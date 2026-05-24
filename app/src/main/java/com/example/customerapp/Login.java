package com.example.customerapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.graphics.Insets;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.customerapp.Customer.Customer;
import com.example.customerapp.DataModel.ApiResponse;
import com.example.customerapp.DataModel.CartManager;
import com.example.customerapp.DataModel.LoginRequest;
import com.example.customerapp.DataModel.RetrofitClient;
import com.example.customerapp.DataModel.UserAuthResponse;
import com.example.customerapp.databinding.ActivityLoginBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {
    private static final String BIOMETRIC_PREFS = "biometric_login";
    private static final String KEY_ENABLED = "enabled";
    private static final String KEY_LOGIN_ID = "login_id";
    private static final String KEY_PASSWORD = "password";

    private ActivityLoginBinding binding;
    private SharedPreferences biometricPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        biometricPrefs = createBiometricPrefs();
        if (biometricPrefs == null) {
            binding.cbBiometricLogin.setChecked(false);
            binding.cbBiometricLogin.setEnabled(false);
        } else {
            binding.cbBiometricLogin.setChecked(biometricPrefs.getBoolean(KEY_ENABLED, false));
        }

        binding.btnLoginSubmit.setOnClickListener(v -> {
            String uId = binding.etLoginId.getText().toString().trim();
            String uPassword = binding.etLoginPw.getText().toString().trim();

            if (uId.isEmpty()) {
                Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (uPassword.isEmpty()) {
                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            loginWithCredentials(uId, uPassword, true);
        });

        tryBiometricLogin();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private SharedPreferences createBiometricPrefs() {
        try {
            MasterKey masterKey = new MasterKey.Builder(this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            return EncryptedSharedPreferences.create(
                    this,
                    BIOMETRIC_PREFS,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            Toast.makeText(this, "암호화 저장소 초기화 실패로 생체로그인을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void tryBiometricLogin() {
        if (biometricPrefs == null) {
            return;
        }
        if (!biometricPrefs.getBoolean(KEY_ENABLED, false)) {
            return;
        }

        String savedId = biometricPrefs.getString(KEY_LOGIN_ID, "");
        String savedPassword = biometricPrefs.getString(KEY_PASSWORD, "");
        if (savedId.isBlank() || savedPassword.isBlank()) {
            return;
        }

        BiometricManager biometricManager = BiometricManager.from(this);
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(this, "사용 가능한 지문 인증이 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        BiometricPrompt prompt = new BiometricPrompt(
                (FragmentActivity) this,
                ContextCompat.getMainExecutor(this),
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        loginWithCredentials(savedId, savedPassword, false);
                    }
                }
        );

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("생체로그인")
                .setSubtitle("지문으로 로그인합니다")
                .setNegativeButtonText("취소")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .build();
        prompt.authenticate(promptInfo);
    }

    private void loginWithCredentials(String uId, String uPassword, boolean updateBiometricPreference) {
        LoginRequest request = new LoginRequest(uId, uPassword);
        RetrofitClient.getInstance().getApiService().login(request)
                .enqueue(new Callback<ApiResponse<UserAuthResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<UserAuthResponse>> call, Response<ApiResponse<UserAuthResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<UserAuthResponse> result = response.body();
                            if (result.isSuccess() && result.getData() != null) {
                                if (updateBiometricPreference) {
                                    saveBiometricPreference(uId, uPassword);
                                }
                                UserAuthResponse auth = result.getData();
                                CartManager.getInstance().setLoggedInUserId(auth.getLoginId());
                                CartManager.getInstance().setAccessToken(auth.getAccessToken());
                                Toast.makeText(Login.this, "로그인 성공!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Login.this, Customer.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(Login.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Login.this, "서버 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<UserAuthResponse>> call, Throwable t) {
                        Toast.makeText(Login.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveBiometricPreference(String uId, String uPassword) {
        if (biometricPrefs == null) {
            return;
        }
        if (binding.cbBiometricLogin.isChecked()) {
            biometricPrefs.edit()
                    .putBoolean(KEY_ENABLED, true)
                    .putString(KEY_LOGIN_ID, uId)
                    .putString(KEY_PASSWORD, uPassword)
                    .apply();
        } else {
            biometricPrefs.edit().clear().apply();
        }
    }
}
