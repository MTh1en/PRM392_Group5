package com.example.final_project_group5.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.final_project_group5.DatabaseHelper;
import com.example.final_project_group5.entity.Users;

import java.util.ArrayList;
import java.util.List;

public class UsersDAO {
    private final SQLiteDatabase db;
    public UsersDAO(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        this.db = dbHelper.getWritableDatabase();
    }

    public long insertUser(Users users) {
        ContentValues values = new ContentValues();
        values.put("name", users.getName());
        values.put("email", users.getEmail());
        values.put("password", users.getPassword());
        values.put("address", users.getAddress());
        values.put("phone", users.getPhone());
        values.put("role", users.getRole());

        return db.insert("Users", null, values);
    }
    public List<Users> getAllUsers() {
        List<Users> userList = new ArrayList<>();
        Cursor cursor = db.query("Users", null, null, null, null, null, "id ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Users user = new Users(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("password")),
                        cursor.getString(cursor.getColumnIndexOrThrow("address")),
                        cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("role"))
                );
                userList.add(user);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return userList;
    }

    public int updateUser(Users user) {
        ContentValues values = new ContentValues();
        values.put("name", user.getName());
        values.put("email", user.getEmail());
        values.put("password", user.getPassword());
        values.put("address", user.getAddress());
        values.put("phone", user.getPhone());
        values.put("role", user.getRole());

        return db.update("Users", values, "id = ?", new String[]{String.valueOf(user.getId())});
    }
    public int deleteUser(int userId) {
        return db.delete("Users", "id = ?", new String[]{String.valueOf(userId)});
    }
}
