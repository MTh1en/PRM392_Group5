package com.example.final_project_group5.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.final_project_group5.DatabaseHelper;
import com.example.final_project_group5.entity.Categories;

import java.util.ArrayList;
import java.util.List;

public class CategoriesDAO {
    private SQLiteDatabase db;

    public CategoriesDAO(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        this.db = dbHelper.getWritableDatabase();
    }

    public long insertCategory(String name) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        return db.insert("Categories", null, values);
    }

    public List<Categories> getAllCategories() {
        List<Categories> categories = new ArrayList<>();
        Cursor cursor = db.query("Categories", null, null, null, null, null, "id ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));

                categories.add(new Categories(id, name));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return categories;
    }
    public int updateCategory(int id, String newName) {
        ContentValues values = new ContentValues();
        values.put("name", newName);
        return db.update("Categories", values, "id = ?", new String[]{String.valueOf(id)});
    }

    public int deleteCategory(int id) {
        // Xóa danh mục dựa trên id
        return db.delete("Categories", "id = ?", new String[]{String.valueOf(id)});
    }
}
