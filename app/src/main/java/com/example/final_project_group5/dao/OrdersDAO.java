package com.example.final_project_group5.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.final_project_group5.DatabaseHelper;
import com.example.final_project_group5.entity.Orders;

import java.util.ArrayList;
import java.util.List;

public class OrdersDAO {
    private SQLiteDatabase db;
    public OrdersDAO(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        this.db = dbHelper.getWritableDatabase();
    }
    public long insertOrder(Orders order) {
        ContentValues values = new ContentValues();
        values.put("user_id", order.getUserId());
        values.put("order_date", order.getOrderDate());
        values.put("total_price", order.getTotalPrice());
        values.put("status", order.getStatus());

        return db.insert("Orders", null, values);
    }
    public List<Orders> getAllOrders() {
        List<Orders> orders = new ArrayList<>();
        Cursor cursor = db.query(
                "Orders",
                null,
                null,
                null,
                null,
                null,
                "order_date DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
                String orderDate = cursor.getString(cursor.getColumnIndexOrThrow("order_date"));
                double totalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("total_price"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));

                orders.add(new Orders(id, userId, orderDate, totalPrice, status));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return orders;
    }
    public List<Orders> getOrdersByUserId(int userId) {
        List<Orders> orders = new ArrayList<>();
        Cursor cursor = db.query(
                "Orders",
                null,
                "user_id = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                "order_date DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String orderDate = cursor.getString(cursor.getColumnIndexOrThrow("order_date"));
                double totalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("total_price"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));

                orders.add(new Orders(id, userId, orderDate, totalPrice, status));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return orders;
    }

    public int updateOrderStatus(int orderId, String status) {
        ContentValues values = new ContentValues();
        values.put("status", status);
        return db.update("Orders", values, "id = ?", new String[]{String.valueOf(orderId)});
    }
    public int deleteOrder(int orderId) {
        return db.delete("Orders", "id = ?", new String[]{String.valueOf(orderId)});
    }
}
