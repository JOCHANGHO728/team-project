package com.example.customerapp.Customer.BarcodeScan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.customerapp.Customer.Customer;
import com.example.customerapp.Customer.Household_Ledger.household_Ledger;
import com.example.customerapp.Customer.MyInfo;
import com.example.customerapp.Customer.Shoppingbasket.ShoppingBasket;
import com.example.customerapp.DataModel.ApiResponse;
import com.example.customerapp.DataModel.ApiService;
import com.example.customerapp.DataModel.Product;
import com.example.customerapp.R;
import com.example.customerapp.databinding.ActivityBarcodeScanBinding;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BarcodeScan extends AppCompatActivity {

    private static final int CAMERA_PERMISSION = 1001;
    private static final String BASE_URL = "https://server-jc54.onrender.com/";

    private ActivityBarcodeScanBinding binding;
    private ApiService apiService;
    private boolean isDetected = false;
    private String pendingBarcode = null;
    private final Map<String, Integer> scannedItems = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBarcodeScanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        binding.scannedProductPanel.setVisibility(View.GONE);
        binding.btnAddScannedProduct.setEnabled(false);
        binding.btnAddScannedProduct.setOnClickListener(v -> addPendingBarcode());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION
            );
        } else {
            startCamera();
        }

        binding.btnBack.setOnClickListener(v -> returnScannedItems());

        binding.bottomNavigation.setSelectedItemId(R.id.nav_cart);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent = null;

            if (id == R.id.nav_cart) {
                returnScannedItems();
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

    private void returnScannedItems() {
        Intent intent = new Intent(this, ShoppingBasket.class);
        intent.putExtra("scannedItems", (Serializable) scannedItems);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void addPendingBarcode() {
        if (pendingBarcode == null || pendingBarcode.isBlank()) {
            return;
        }

        scannedItems.put(pendingBarcode, scannedItems.getOrDefault(pendingBarcode, 0) + 1);
        Log.d("SCAN_MAP", "현재 스캔 목록: " + scannedItems);
        Toast.makeText(this, "상품을 담았습니다.", Toast.LENGTH_SHORT).show();

        pendingBarcode = null;
        isDetected = false;
        binding.scannedProductPanel.setVisibility(View.GONE);
        binding.btnAddScannedProduct.setEnabled(false);
        binding.tvScanMessage.setText("다음 바코드를 스캔해주세요");
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .build();
                preview.setSurfaceProvider(binding.cameraPreview.getSurfaceProvider());

                ImageAnalysis analysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                BarcodeScanner scanner = BarcodeScanning.getClient();
                analysis.setAnalyzer(ContextCompat.getMainExecutor(this), imageProxy -> scanBarcode(scanner, imageProxy));

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        this,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        analysis
                );
            } catch (ExecutionException | InterruptedException e) {
                Log.e("BARCODE_SCAN", "카메라 시작 실패", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void scanBarcode(BarcodeScanner scanner, ImageProxy imageProxy) {
        if (imageProxy.getImage() == null) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(
                imageProxy.getImage(),
                imageProxy.getImageInfo().getRotationDegrees()
        );

        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    if (barcodes.isEmpty() || isDetected) {
                        return;
                    }

                    Barcode barcode = barcodes.get(0);
                    String value = barcode.getRawValue();
                    if (value == null || value.isBlank()) {
                        return;
                    }

                    isDetected = true;
                    pendingBarcode = value.trim();
                    binding.tvScanMessage.setText("스캔된 바코드: " + pendingBarcode);
                    binding.btnAddScannedProduct.setEnabled(false);
                    binding.scannedProductPanel.setVisibility(View.GONE);
                    loadScannedProduct(pendingBarcode);
                })
                .addOnCompleteListener(task -> imageProxy.close());
    }

    private void loadScannedProduct(String barcode) {
        apiService.searchByBarcode(barcode).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()
                        && response.body().getData() != null) {
                    showScannedProduct(response.body().getData(), barcode);
                    return;
                }

                findProductByBarcodeFromProductList(barcode);
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                Log.e("BARCODE_SCAN", "바코드 상품 조회 실패: " + t.getMessage());
                findProductByBarcodeFromProductList(barcode);
            }
        });
    }

    private void findProductByBarcodeFromProductList(String barcode) {
        apiService.searchProducts("").enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (!response.isSuccessful()
                        || response.body() == null
                        || !response.body().isSuccess()
                        || response.body().getData() == null) {
                    showProductNotFound(barcode);
                    return;
                }

                for (Product product : response.body().getData()) {
                    String productBarcode = product.getBKey();
                    if (productBarcode != null && productBarcode.trim().equals(barcode)) {
                        showScannedProduct(product, barcode);
                        return;
                    }
                }

                showProductNotFound(barcode);
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                Log.e("BARCODE_SCAN", "전체 상품 조회 실패: " + t.getMessage());
                showProductNotFound(barcode);
            }
        });
    }

    private void showScannedProduct(Product product, String barcode) {
        pendingBarcode = barcode;
        binding.tvScannedProductName.setText(product.getPName());
        binding.tvScannedProductInfo.setText(
                "가격: " + product.getPPrice() + "원 / 바코드: " + barcode
        );
        binding.scannedProductPanel.setVisibility(View.VISIBLE);
        binding.btnAddScannedProduct.setEnabled(true);
    }

    private void showProductNotFound(String barcode) {
        pendingBarcode = null;
        binding.tvScanMessage.setText("등록되지 않은 상품입니다: " + barcode);
        binding.scannedProductPanel.setVisibility(View.GONE);
        binding.btnAddScannedProduct.setEnabled(false);
        binding.cameraPreview.postDelayed(() -> isDetected = false, 1500);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
