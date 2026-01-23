package com.example.managementapp.management;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProductDB extends SQLiteOpenHelper {
    public ProductDB(Context context) {
        super(context, "ProductDB.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE products (" +
                "name TEXT PRIMARY KEY, " +
                "price INTEGER, " +
                "quantity INTEGER, " +
                "b_key TEXT, " +
                "category TEXT)");

        //초기 데이터 삽입
        db.execSQL("INSERT INTO products VALUES ('농심 새우깡 90g', 1500, 100, '8801043014801', 'snack')");
        db.execSQL("INSERT INTO products VALUES ('농심 매운새우깡 90g', 1600, 80, '8801043014802', 'snack')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS products");
        onCreate(db);
    }
}