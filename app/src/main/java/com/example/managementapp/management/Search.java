package com.example.managementapp.management;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.managementapp.R;
import com.example.managementapp.databinding.ActivityManagementBinding;
import com.example.managementapp.databinding.ActivitySearchBinding;

public class Search extends AppCompatActivity {
    private ActivitySearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ProductDB Products = new ProductDB(this);
        SQLiteDatabase db = Products.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM products", null);

        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            int price = cursor.getInt(1);
            int quantity = cursor.getInt(2);
            String bKey = cursor.getString(3);
            String category = cursor.getString(4);

            Log.d("DB_TEST", name + " / " +  price + " / " + quantity + " / " + bKey + " / " + category);
        }

        cursor.close();
        db.close();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}