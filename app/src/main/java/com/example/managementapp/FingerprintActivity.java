package com.example.managementapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class FingerprintActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_auth);

        Toast.makeText(this, "지문 인증 화면입니다.", Toast.LENGTH_SHORT).show();

        MaterialButton toLogin = findViewById(R.id.btn_to_login);
        MaterialButton toMain = findViewById(R.id.btn_to_main);

        toLogin.setOnClickListener(v -> {
            startActivity(new Intent(FingerprintActivity.this, Login.class));
            finish();
        });

        toMain.setOnClickListener(v -> {
            startActivity(new Intent(FingerprintActivity.this, MainActivity.class));
            finish();
        });
    }
}
