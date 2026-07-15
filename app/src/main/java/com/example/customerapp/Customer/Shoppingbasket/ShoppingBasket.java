package com.example.customerapp.Customer.Shoppingbasket;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.customerapp.Customer.BarcodeScan.BarcodeScan;
import com.example.customerapp.Customer.Customer;
import com.example.customerapp.Customer.Household_Ledger.household_Ledger;
import com.example.customerapp.Customer.MyInfo;
import com.example.customerapp.DataModel.ApiResponse;
import com.example.customerapp.DataModel.ApiService;
import com.example.customerapp.DataModel.CartManager;
import com.example.customerapp.DataModel.Product;
import com.example.customerapp.DataModel.RetrofitClient;
import com.example.customerapp.R;
import com.example.customerapp.databinding.ActivityShoppingBasketBinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShoppingBasket extends AppCompatActivity {

    private ActivityShoppingBasketBinding binding;

    private ActivityResultLauncher<Intent> barcodeLauncher;
    private CartAdapter cartAdapter;
    private final List<CartItem> cartItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShoppingBasketBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ApiService api = RetrofitClient.getInstance().getApiService();

        cartAdapter = new CartAdapter();
        cartAdapter.setOnCartChangeListener(this::updateTotalPrice);
        binding.rvCartList.setAdapter(cartAdapter);
        syncCartFromManager();

        barcodeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != RESULT_OK || result.getData() == null) {
                        return;
                    }

                    HashMap<String, Integer> scannedItems =
                            (HashMap<String, Integer>) result.getData().getSerializableExtra("scannedItems");

                    if (scannedItems == null || scannedItems.isEmpty()) {
                        Log.d("SHOPPING_BASKET", "스캔된 바코드 없음");
                        return;
                    }

                    Log.d("SHOPPING_BASKET", "받은 바코드 목록: " + scannedItems);
                    for (String barcode : scannedItems.keySet()) {
                        Integer qty = scannedItems.get(barcode);
                        addScannedBarcodeToCart(api, barcode, qty == null ? 1 : qty);
                    }
                }
        );

        binding.btnBarcodeScan.setOnClickListener(v -> {
            Intent intent = new Intent(ShoppingBasket.this, BarcodeScan.class);
            barcodeLauncher.launch(intent);
        });

        binding.btnOrder.setOnClickListener(v -> {
            int totalPrice = CartManager.getInstance().getScannedTotalPrice();
            if (totalPrice <= 0) {
                Toast.makeText(ShoppingBasket.this, "바코드 스캔된 상품이 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(ShoppingBasket.this, Payment.class);
            intent.putExtra("total_price", totalPrice);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.bottomNavigation.setSelectedItemId(R.id.nav_cart);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent = null;

            if (id == R.id.nav_cart) {
                return true;
            } else if (id == R.id.nav_shopping) {
                intent = new Intent(this, Customer.class);
            } else if (id == R.id.nav_ledger) {
                intent = new Intent(this, household_Ledger.class);
            } else if (id == R.id.nav_my_info) {
                intent = new Intent(this, MyInfo.class);
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    private void addScannedBarcodeToCart(ApiService api, String barcode, int qty) {
        String normalizedBarcode = barcode == null ? "" : barcode.trim();
        if (normalizedBarcode.isEmpty()) {
            return;
        }

        api.searchByBarcode(normalizedBarcode).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()
                        && response.body().getData() != null) {
                    addProductToCart(response.body().getData(), qty);
                    return;
                }

                findProductByBarcodeFromProductList(api, normalizedBarcode, qty);
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                Log.e("SERVER", "바코드 상품 조회 실패: " + t.getMessage());
                findProductByBarcodeFromProductList(api, normalizedBarcode, qty);
            }
        });
    }

    private void findProductByBarcodeFromProductList(ApiService api, String barcode, int qty) {
        api.searchProducts("").enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (!response.isSuccessful()
                        || response.body() == null
                        || !response.body().isSuccess()
                        || response.body().getData() == null) {
                    showBarcodeNotFound(barcode);
                    return;
                }

                for (Product product : response.body().getData()) {
                    String productBarcode = product.getBKey();
                    if (productBarcode != null && productBarcode.trim().equals(barcode)) {
                        addProductToCart(product, qty);
                        return;
                    }
                }

                showBarcodeNotFound(barcode);
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                Log.e("SERVER", "전체 상품 조회 실패: " + t.getMessage());
                showBarcodeNotFound(barcode);
            }
        });
    }

    private void addProductToCart(Product product, int qty) {
        CartManager.getInstance().addScannedItem(product, qty);
        syncCartFromManager();
        Toast.makeText(this, product.getPName() + " 장바구니 추가", Toast.LENGTH_SHORT).show();
    }

    private void showBarcodeNotFound(String barcode) {
        Toast.makeText(this, "등록되지 않은 상품입니다: " + barcode, Toast.LENGTH_SHORT).show();
    }

    private void updateTotalPrice() {
        int total = CartManager.getInstance().getTotalPrice();
        int scannedTotal = CartManager.getInstance().getScannedTotalPrice();
        binding.tvTotalPrice.setText(formatWon(total));
        binding.tvScannedPrice.setText(formatWon(scannedTotal));
    }

    private int calculateTotalPrice() {
        int total = 0;
        for (CartItem item : cartItems) {
            int price = item.getProduct().getPPrice();
            int qty = item.getQuantity();
            total += price * qty;
        }
        return total;
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncCartFromManager();
    }

    private void syncCartFromManager() {
        cartItems.clear();
        for (Product product : CartManager.getInstance().getCartItems()) {
            cartItems.add(new CartItem(product, product.getCartQuantity()));
        }
        cartItems.sort(Comparator
                .comparing(CartItem::isScannedInStore)
                .thenComparing(item -> {
                    String name = item.getProduct().getPName();
                    return name == null ? "" : name;
                }));
        cartAdapter.setData(cartItems);
        updateTotalPrice();
    }

    private String formatWon(int amount) {
        return String.format(Locale.KOREA, "₩%,d", amount);
    }
}
