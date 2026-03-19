package com.example.managementapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import java.util.concurrent.Executor;

public class FingerprintActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_auth); // XML 연결

        showBiometricPrompt();

       // findViewById(R.id.btn_cancel).setOnClickListener(v -> finish());
    }



    private void showBiometricPrompt() {

        BiometricManager biometricManager = BiometricManager.from(this);
        // 지문 검사가 가능한지 체크
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                break; // 인증 가능
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "지문 센서가 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "등록된 지문이 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            default:
                return;
        }
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            // 지문 인식 성공
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(FingerprintActivity.this, "인증 성공", Toast.LENGTH_SHORT).show();

                // 메인 액티비티로 보냄, 이건 확인 필요
                Intent intent = new Intent(FingerprintActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }

            @Override
            // 지문 인식 실패
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                // 유저 캔슬 제외
                if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON &&
                        errorCode != BiometricPrompt.ERROR_USER_CANCELED)
                {
                    Toast.makeText(FingerprintActivity.this, "에러: " + errString, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                // 지문 불일치
                Toast.makeText(FingerprintActivity.this, "지문이 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("지문 인증")
                .setSubtitle("등록된 지문을 스캔하세요.")
                .setNegativeButtonText("비밀번호 사용")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }
}
