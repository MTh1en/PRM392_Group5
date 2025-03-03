package com.example.final_project_group5.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.final_project_group5.DatabaseHelper;
import com.example.final_project_group5.entity.OrderDetails;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailsDAO {
    private SQLiteDatabase db;
    public OrderDetailsDAO(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        this.db = dbHelper.getWritableDatabase();
    }

    public long insertOrderDetail(int orderId, int productId, int quantity, double price) {
        ContentValues values = new ContentValues();
        values.put("order_id", orderId);
        values.put("product_id", productId);
        values.put("quantity", quantity);
        values.put("price", price);
        return db.insert("OrderDetails", null, values);
    }

    public List<OrderDetails> getAllOrderDetails() {
        List<OrderDetails> orderDetails = new ArrayList<>();
        Cursor cursor = db.query(
                "OrderDetails",
                null,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int orderId = cursor.getInt(cursor.getColumnIndexOrThrow("order_id"));
                int productId = cursor.getInt(cursor.getColumnIndexOrThrow("product_id"));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

                orderDetails.add(new OrderDetails(id, orderId, productId, quantity, price));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return orderDetails;
    }
    public List<OrderDetails> getOrderDetailsByOrderId(int orderId) {
        List<OrderDetails> orderDetails = new ArrayList<>();
        Cursor cursor = db.query(
                "OrderDetails",
                null,
                "order_id = ?",
                new String[]{String.valueOf(orderId)},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int productId = cursor.getInt(cursor.getColumnIndexOrThrow("product_id"));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

                orderDetails.add(new OrderDetails(id, orderId, productId, quantity, price));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return orderDetails;
    }

    public int updateOrderDetail(int id, int quantity, double price) {
        ContentValues values = new ContentValues();
        values.put("quantity", quantity);
        values.put("price", price);
        return db.update("OrderDetails", values, "id = ?", new String[]{String.valueOf(id)});
    }

    public int deleteOrderDetail(int id) {
        return db.delete("OrderDetails", "id = ?", new String[]{String.valueOf(id)});
    }
}
