package com.example.final_project_group5.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.final_project_group5.DatabaseHelper;
import com.example.final_project_group5.entity.Carts;

import java.util.ArrayList;
import java.util.List;

public class CartsDAO {
    private SQLiteDatabase db;
    public CartsDAO(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        this.db = dbHelper.getWritableDatabase();
    }

    public long insertCart(Carts cart) {
        ContentValues values = new ContentValues();
        values.put("user_id", cart.getUserId());
        values.put("product_id", cart.getProductId());
        values.put("quantity", cart.getQuantity());
        values.put("addedAt", cart.getAddedAt());
        return db.insert("Carts", null, values);
    }

    public List<Carts> getCartsByUserId(int userId) {
        List<Carts> carts = new ArrayList<>();
        Cursor cursor = db.query(
                "Carts",
                null,
                "user_id = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                "addedAt DESC" // Sắp xếp theo thời gian thêm sản phẩm
        );
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int productId = cursor.getInt(cursor.getColumnIndexOrThrow("product_id"));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                String addedAt = cursor.getString(cursor.getColumnIndexOrThrow("addedAt"));
                carts.add(new Carts(id, userId, productId, quantity, addedAt));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return carts;
    }
    public int deleteCartById(int cartId) {
        return db.delete("Carts", "id = ?", new String[]{String.valueOf(cartId)});
    }
    public int updateCartQuantity(int cartId, int quantity) {
        ContentValues values = new ContentValues();
        values.put("quantity", quantity);
        return db.update("Carts", values, "id = ?", new String[]{String.valueOf(cartId)});
    }
}
