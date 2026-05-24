package com.example.customerapp.Customer;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.customerapp.Customer.Shopping.ProductAdapter;
import com.example.customerapp.DataModel.ApiResponse;
import com.example.customerapp.DataModel.CartManager;
import com.example.customerapp.DataModel.Product;
import com.example.customerapp.DataModel.RetrofitClient;
import com.example.customerapp.R;

import com.example.customerapp.Customer.Shoppingbasket.ShoppingBasket;
import com.example.customerapp.Customer.Household_Ledger.household_Ledger;
import com.example.customerapp.Customer.MyInfo;
import android.content.Intent; // Intent 사용을 위해 필요
import com.google.android.material.bottomnavigation.BottomNavigationView; // BottomNavigationView 사용을 위해 필요
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Customer extends AppCompatActivity {

    private RecyclerView rvCategory, rvProductList;
    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;
    private String currentCategory = "";
    private SearchView svProductSearch;
    private final List<Product> allProducts = new ArrayList<>();
    private final List<Product> currentCategoryProducts = new ArrayList<>();
    private final List<Product> currentDisplayProducts = new ArrayList<>();

    public interface OnCategoryClickListener {
        void onCategoryClick(String categoryName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        // 1. 뷰 초기화
        rvCategory = findViewById(R.id.rv_category);
        rvProductList = findViewById(R.id.rv_product_list);
        svProductSearch = findViewById(R.id.sv_product_search);

        rvCategory.setLayoutManager(new LinearLayoutManager(this));
        rvProductList.setLayoutManager(new LinearLayoutManager(this));

        // 2. 초기 어댑터 설정 (DB 로딩 후 교체)
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), categoryName -> {
            currentCategory = categoryName;
            svProductSearch.setQuery("", false);
            loadProductsByCategory(categoryName);
        });
        rvCategory.setAdapter(categoryAdapter);
        productAdapter = createProductAdapter(new ArrayList<>());
        rvProductList.setAdapter(productAdapter);

        // 3. 검색 기능 구현 (현재 카테고리 결과 내 필터)
        svProductSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProducts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText);
                return true;
            }
        });

        // 4. DB에서 카테고리/상품 로드
        loadCategoriesFromServer();

        // 5. 하단 네비게이션 설정
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);

        // 현재 화면인 '상품목록' 아이콘을 활성화 상태로 표시
        bottomNavigation.setSelectedItemId(R.id.nav_shopping);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent = null;

            if (id == R.id.nav_shopping) {
                return true; // 현재 화면이므로 아무것도 하지 않음
            }
            else if (id == R.id.nav_cart) {
                intent = new Intent(this, ShoppingBasket.class);
            }
            else if (id == R.id.nav_ledger) {
                intent = new Intent(this, household_Ledger.class);
            }
            else if (id == R.id.nav_my_info) {
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

    private void loadCategoriesFromServer() {
        RetrofitClient.getInstance().getApiService().searchProducts("")
                .enqueue(new Callback<ApiResponse<List<Product>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                        if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                            Toast.makeText(Customer.this, "카테고리 조회 실패", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<Product> products = response.body().getData();
                        if (products == null || products.isEmpty()) {
                            Toast.makeText(Customer.this, "등록된 상품이 없습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        allProducts.clear();
                        allProducts.addAll(products);

                        Set<String> categorySet = new LinkedHashSet<>();
                        for (Product product : products) {
                            String category = product.getCategory();
                            if (category != null && !category.isBlank()) {
                                categorySet.add(category);
                            }
                        }

                        List<String> categories = new ArrayList<>(categorySet);
                        if (categories.isEmpty()) {
                            Toast.makeText(Customer.this, "카테고리 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        categoryAdapter = new CategoryAdapter(categories, categoryName -> {
                            currentCategory = categoryName;
                            svProductSearch.setQuery("", false);
                            loadProductsByCategory(categoryName);
                        });
                        rvCategory.setAdapter(categoryAdapter);

                        currentCategory = categories.get(0);
                        loadProductsByCategory(currentCategory);
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                        Toast.makeText(Customer.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadProductsByCategory(String categoryName) {
        RetrofitClient.getInstance().getApiService().getProductsByCategory(categoryName)
                .enqueue(new Callback<ApiResponse<List<Product>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                        if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                            applyLocalCategoryFallback(categoryName);
                            return;
                        }

                        List<Product> products = response.body().getData();
                        currentCategoryProducts.clear();
                        if (products != null) {
                            currentCategoryProducts.addAll(products);
                        }
                        refreshDisplayProducts("");
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                        applyLocalCategoryFallback(categoryName);
                    }
                });
    }

    private void applyLocalCategoryFallback(String categoryName) {
        List<Product> filtered = new ArrayList<>();
        for (Product product : allProducts) {
            if (categoryName.equals(product.getCategory())) {
                filtered.add(product);
            }
        }
        currentCategoryProducts.clear();
        currentCategoryProducts.addAll(filtered);
        refreshDisplayProducts("");
    }

    // 검색어에 따른 상품 필터링 로직
    private void filterProducts(String query) {
        String normalized = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        refreshDisplayProducts(normalized);
    }

    private ProductAdapter createProductAdapter(List<Product> products) {
        return new ProductAdapter(
                products,
                product -> {
                    CartManager.getInstance().addItem(product);
                    Toast.makeText(this, "장바구니에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                },
                this::showVariantPicker
        );
    }

    private void showVariantPicker(Product selectedProduct) {
        String baseName = normalizeProductBaseName(selectedProduct.getPName());
        List<Product> sameNameProducts = new ArrayList<>();

        for (Product product : currentCategoryProducts) {
            if (normalizeProductBaseName(product.getPName()).equals(baseName)) {
                sameNameProducts.add(product);
            }
        }

        if (sameNameProducts.size() <= 1) {
            return;
        }

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_product_options, null, false);
        TextView tvTitle = dialogView.findViewById(R.id.tv_option_title);
        TextView tvSelectedName = dialogView.findViewById(R.id.tv_selected_name);
        TextView tvSelectedPrice = dialogView.findViewById(R.id.tv_selected_price);
        RecyclerView rvOptions = dialogView.findViewById(R.id.rv_option_list);
        MaterialButton btnAddSelectedProduct = dialogView.findViewById(R.id.btn_add_selected_product);

        tvTitle.setText(baseName + " 옵션 선택");
        rvOptions.setLayoutManager(new GridLayoutManager(this, 2));

        final Product[] selectedOption = {selectedProduct};
        updateSelectedProductPreview(tvSelectedName, tvSelectedPrice, selectedOption[0]);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        VariantOptionAdapter optionAdapter = new VariantOptionAdapter(baseName, sameNameProducts, product -> {
            selectedOption[0] = product;
            updateSelectedProductPreview(tvSelectedName, tvSelectedPrice, product);
        });
        rvOptions.setAdapter(optionAdapter);
        btnAddSelectedProduct.setOnClickListener(v -> {
            CartManager.getInstance().addItem(selectedOption[0]);
            Toast.makeText(this, selectedOption[0].getPName() + " 추가", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        dialog.show();
    }

    private void updateSelectedProductPreview(TextView tvName, TextView tvPrice, Product product) {
        tvName.setText(product.getPName());
        tvPrice.setText(product.getPPrice() + "원");
    }

    private String normalizeProductBaseName(String name) {
        if (name == null) return "";
        String normalized = name.trim();
        normalized = normalized.replaceAll("\\(.*?\\)", "").trim();
        normalized = normalized.replaceAll("(\\s+\\d+\\s*박스)$", "").trim();
        normalized = normalized.replaceAll("(\\s+대용량|\\s+소용량|\\s+세트|\\s+묶음)$", "").trim();
        return normalized;
    }

    private void refreshDisplayProducts(String normalizedQuery) {
        Map<String, Product> baseProductMap = new LinkedHashMap<>();

        for (Product product : currentCategoryProducts) {
            String name = product.getPName();
            if (name == null || name.isBlank()) continue;

            if (!normalizedQuery.isEmpty() && !name.toLowerCase(Locale.ROOT).contains(normalizedQuery)) {
                continue;
            }

            String baseName = normalizeProductBaseName(name);
            if (!baseProductMap.containsKey(baseName)) {
                baseProductMap.put(baseName, product);
            } else {
                Product existing = baseProductMap.get(baseName);
                if (existing != null && !existing.getPName().equals(baseName) && name.equals(baseName)) {
                    baseProductMap.put(baseName, product);
                }
            }
        }

        currentDisplayProducts.clear();
        currentDisplayProducts.addAll(baseProductMap.values());

        productAdapter = createProductAdapter(new ArrayList<>(currentDisplayProducts));
        rvProductList.setAdapter(productAdapter);
    }



    // --- 카테고리 어댑터 (이미지 제외 및 선택 강조) ---
    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
        private List<String> categories;
        private OnCategoryClickListener listener;
        private int selectedPosition = 0;

        public CategoryAdapter(List<String> categories, OnCategoryClickListener listener) {
            this.categories = categories;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_left, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String name = categories.get(position);
            holder.tvName.setText(name);

            // 선택 상태에 따른 디자인 (배경 화이트/연회색)
            if (selectedPosition == position) {
                holder.itemView.setBackgroundColor(Color.WHITE);
                holder.tvName.setTextColor(Color.BLACK);
                holder.tvName.setTypeface(null, Typeface.BOLD);
            } else {
                holder.itemView.setBackgroundColor(Color.parseColor("#F5F5F5"));
                holder.tvName.setTextColor(Color.GRAY);
                holder.tvName.setTypeface(null, Typeface.NORMAL);
            }

            holder.itemView.setOnClickListener(v -> {
                int previousPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);
                listener.onCategoryClick(name);
            });
        }

        @Override
        public int getItemCount() { return categories.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_category_name);
            }
        }
    }

    private static class VariantOptionAdapter extends RecyclerView.Adapter<VariantOptionAdapter.ViewHolder> {
        interface OnVariantSelectListener {
            void onSelect(Product product);
        }

        private final List<Product> options;
        private final OnVariantSelectListener listener;
        private final String baseName;
        private final int regularPrice;
        private static final int BOX_UNIT_COUNT = 30;

        VariantOptionAdapter(String baseName, List<Product> options, OnVariantSelectListener listener) {
            this.baseName = baseName;
            this.options = options;
            this.listener = listener;
            this.regularPrice = findRegularPrice(baseName, options);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_variant_option, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Product product = options.get(position);
            holder.tvOptionLabel.setText(getOptionLabel(product.getPName()));
            holder.tvOptionPrice.setText(product.getPPrice() + "원");
            bindDiscountBadge(holder, product);
            holder.itemView.setOnClickListener(v -> listener.onSelect(product));
        }

        private void bindDiscountBadge(ViewHolder holder, Product product) {
            String name = product.getPName();
            if (name == null || !isBoxProduct(name) || regularPrice <= 0) {
                holder.tvDiscountBadge.setVisibility(View.GONE);
                return;
            }

            int regularTotal = regularPrice * BOX_UNIT_COUNT;
            int discountPercent = Math.round((regularTotal - product.getPPrice()) * 100f / regularTotal);
            if (discountPercent <= 0) {
                holder.tvDiscountBadge.setText("박스상품");
            } else {
                holder.tvDiscountBadge.setText("일반상품보다 " + discountPercent + "% 저렴");
            }
            holder.tvDiscountBadge.setVisibility(View.VISIBLE);
        }

        private int findRegularPrice(String baseName, List<Product> options) {
            for (Product option : options) {
                String name = option.getPName();
                if (name != null
                        && !isBoxProduct(name)
                        && normalizeOptionBaseName(name).equals(baseName)) {
                    return option.getPPrice();
                }
            }
            return 0;
        }

        private String getOptionLabel(String name) {
            if (name == null || name.isBlank()) {
                return "옵션";
            }
            if (isBoxProduct(name)) return "1박스";
            if (normalizeOptionBaseName(name).equals(baseName)) return "일반상품";
            if (name.contains("대용량")) return "대용량";
            if (name.contains("소용량")) return "소용량";
            if (name.contains("행사")) return "행사상품";
            if (name.contains("세트")) return "세트";
            if (name.contains("묶음")) return "묶음";
            return name;
        }

        private String normalizeOptionBaseName(String name) {
            return name.trim().replaceAll("\\(.*?\\)", "").trim();
        }

        private boolean isBoxProduct(String name) {
            String normalized = name.toUpperCase(Locale.ROOT);
            return normalized.contains("BOX") || name.contains("박스");
        }

        @Override
        public int getItemCount() {
            return options.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvOptionLabel, tvOptionPrice, tvDiscountBadge;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvOptionLabel = itemView.findViewById(R.id.tv_option_label);
                tvOptionPrice = itemView.findViewById(R.id.tv_option_price);
                tvDiscountBadge = itemView.findViewById(R.id.tv_discount_badge);
            }
        }
    }
}
