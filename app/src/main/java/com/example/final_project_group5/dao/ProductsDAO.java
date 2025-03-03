package com.example.final_project_group5.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.final_project_group5.DatabaseHelper;
import com.example.final_project_group5.entity.Products;

import java.util.ArrayList;
import java.util.List;

public class ProductsDAO {
    private SQLiteDatabase db;
    public ProductsDAO(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        this.db = dbHelper.getWritableDatabase();
    }

    public long insertProduct(Products products) {
        ContentValues values = new ContentValues();
        values.put("name", products.getName());
        values.put("description", products.getDescription());
        values.put("price", products.getPrice());
        values.put("image", products.getImage());
        values.put("rating", products.getRating());
        values.put("discount", products.getDiscount());
        values.put("cores", products.getCores());
        values.put("boost_clock", products.getBoostClock());
        values.put("memories", products.getMemories());
        values.put("tdp", products.getTdp());
        values.put("category_id", products.getCategoryId());
        return db.insert("Products", null, values);
    }

    public List<Products> getAllProducts() {
        List<Products> productList = new ArrayList<>();
        Cursor cursor = db.query("Products", null, null, null, null, null, "id ASC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Products product = new Products(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getFloat(cursor.getColumnIndexOrThrow("price")),
                        cursor.getString(cursor.getColumnIndexOrThrow("image")),
                        cursor.getFloat(cursor.getColumnIndexOrThrow("rating")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("discount")),
                        cursor.getString(cursor.getColumnIndexOrThrow("cores")),
                        cursor.getString(cursor.getColumnIndexOrThrow("boost_clock")),
                        cursor.getString(cursor.getColumnIndexOrThrow("memories")),
                        cursor.getString(cursor.getColumnIndexOrThrow("tdp")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("category_id"))
                );
                productList.add(product);
            }
            cursor.close();
        }
        return productList;
    }

    public int updateProduct(Products product) {
        ContentValues values = new ContentValues();
        values.put("name", product.getName());
        values.put("description", product.getDescription());
        values.put("price", product.getPrice());
        values.put("image", product.getImage());
        values.put("rating", product.getRating());
        values.put("discount", product.getDiscount());
        values.put("cores", product.getCores());
        values.put("boost_clock", product.getBoostClock());
        values.put("memories", product.getMemories());
        values.put("tdp", product.getTdp());
        values.put("category_id", product.getCategoryId());
        return db.update("Products", values, "id = ?", new String[]{String.valueOf(product.getId())});
    }

    public int deleteProduct(int productId) {
        return db.delete("Products", "id = ?", new String[]{String.valueOf(productId)});
    }
    public Products getProductById(int productId) {
        Cursor cursor = db.query("Products", null, "id = ?", new String[]{String.valueOf(productId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Products product = new Products(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    cursor.getFloat(cursor.getColumnIndexOrThrow("price")),
                    cursor.getString(cursor.getColumnIndexOrThrow("image")),
                    cursor.getFloat(cursor.getColumnIndexOrThrow("rating")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("discount")),
                    cursor.getString(cursor.getColumnIndexOrThrow("cores")),
                    cursor.getString(cursor.getColumnIndexOrThrow("boost_clock")),
                    cursor.getString(cursor.getColumnIndexOrThrow("memories")),
                    cursor.getString(cursor.getColumnIndexOrThrow("tdp")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("category_id"))
            );
            cursor.close();
            return product;
        }
        return null;
    }
}
