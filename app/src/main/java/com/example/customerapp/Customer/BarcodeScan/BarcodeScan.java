package com.example.customerapp.Customer.BarcodeScan;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.widget.Toast;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.customerapp.Customer.Shoppingbasket.ShoppingBasket;
import com.example.customerapp.databinding.ActivityBarcodeScanBinding;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.io.Serializable;

public class BarcodeScan extends AppCompatActivity {

    private ActivityBarcodeScanBinding binding;
    private static final int CAMERA_PERMISSION = 1001;
    private boolean isDetected = false; // 중복 스캔 방지
    private String scannedBarcode = null;

    // 스캔된 바코드 저장 데이터
    private Map<String, Integer> scannedItems = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBarcodeScanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 카메라 권한 체크
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

        // 뒤로가기 버튼
        binding.btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, ShoppingBasket.class);
            intent.putExtra("scannedItems", (Serializable) scannedItems);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // 프리뷰 설정
                Preview preview = new Preview.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .build();

                preview.setSurfaceProvider(binding.cameraPreview.getSurfaceProvider());

                // 이미지 분석기
                ImageAnalysis analysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                BarcodeScanner scanner = BarcodeScanning.getClient();

                analysis.setAnalyzer(ContextCompat.getMainExecutor(this), imageProxy -> {
                    scanBarcode(scanner, imageProxy);
                });

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        analysis
                );

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void scanBarcode(BarcodeScanner scanner, ImageProxy imageProxy) {
        if (imageProxy.getImage() == null) {
            imageProxy.close();
            return;
        }

        @SuppressWarnings("UnsafeOptInUsageError")
        InputImage image = InputImage.fromMediaImage(
                imageProxy.getImage(),
                imageProxy.getImageInfo().getRotationDegrees()
        );

        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    if (!barcodes.isEmpty() && !isDetected) {
                        isDetected = true;

                        Barcode barcode = barcodes.get(0);
                        String value = barcode.getRawValue();

                        // 🔥 바코드 변수에 저장
                        scannedBarcode = value;

                        // HashMap에 저장
                        if (scannedItems.containsKey(value)) {
                            scannedItems.put(value, scannedItems.get(value) + 1);
                        } else {
                            scannedItems.put(value, 1);
                        }

                        // 🔥 로그로 확인
                        Log.d("SCAN_MAP", "현재 스캔 목록: " + scannedItems.toString());
                        Log.d("BARCODE_SCAN", "인식된 바코드: " + value);
                        Toast.makeText(this, "바코드: " + value, Toast.LENGTH_SHORT).show();

                        // XML 안내 텍스트 변경
                        binding.tvScanMessage.setText("스캔된 바코드: " + value);

                        // 1초 후 다시 스캔 가능
                        binding.cameraPreview.postDelayed(() -> isDetected = false, 1000);
                    }
                })
                .addOnCompleteListener(task -> imageProxy.close());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            startCamera();

        } else {
            Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}