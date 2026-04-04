package com.example.customerapp.Customer.Shoppingbasket;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.customerapp.Customer.BarcodeScan.BarcodeScan;
import com.example.customerapp.R;
import com.example.customerapp.databinding.ActivityShoppingBasketBinding;

import java.util.HashMap;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ShoppingBasket extends AppCompatActivity {
    private static final String BASE_URL = "https://server-jc54.onrender.com/";
    private ActivityShoppingBasketBinding binding;

    // 스캔 결과 저장용 런처
    private ActivityResultLauncher<Intent> barcodeLauncher;
    private CartAdapter cartAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShoppingBasketBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // recyclerView 초기화
        cartAdapter = new CartAdapter();
        binding.rvCartList.setAdapter(cartAdapter);

        // 런처 초기화
        barcodeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            HashMap<String, Integer> scannedItems =
                                    (HashMap<String, Integer>) data.getSerializableExtra("scannedItems");

                            if (scannedItems != null && !scannedItems.isEmpty()) {
                                Log.d("SHOPPING_BASKET", "받은 바코드 목록: " + scannedItems.toString());
                                cartAdapter.setData(scannedItems);
                                // TODO: 여기서 장바구니 UI 업데이트 or 서버 전송 가능
                            } else {
                                Log.d("SHOPPING_BASKET", "스캔된 바코드 없음");
                            }
                        }
                    }
                }
        );

        // 바코드 스캔 액티비티 실행
        binding.btnBarcodeScan.setOnClickListener(v -> {
            Intent intent = new Intent(ShoppingBasket.this, BarcodeScan.class);
            barcodeLauncher.launch(intent);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}