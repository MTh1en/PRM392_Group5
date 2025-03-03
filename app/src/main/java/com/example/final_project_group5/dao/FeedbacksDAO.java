package com.example.final_project_group5.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.final_project_group5.DatabaseHelper;
import com.example.final_project_group5.entity.Feedbacks;

import java.util.ArrayList;
import java.util.List;

public class FeedbacksDAO {
    private SQLiteDatabase db;
    public FeedbacksDAO(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        this.db = dbHelper.getWritableDatabase();
    }

    public long insertFeedback(Feedbacks feedback) {
        ContentValues values = new ContentValues();
        values.put("user_id", feedback.getUserId());
        values.put("product_id", feedback.getProductId());
        values.put("content", feedback.getContent());
        values.put("rating", feedback.getRating());
        values.put("created_at", feedback.getCreatedAt());
        return db.insert("Feedbacks", null, values);
    }

    public List<Feedbacks> getAllFeedbacks() {
        List<Feedbacks> feedbackList = new ArrayList<>();
        Cursor cursor = db.query("Feedbacks", null, null, null, null, null, "created_at DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Feedbacks feedback = new Feedbacks(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("product_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("content")),
                        cursor.getFloat(cursor.getColumnIndexOrThrow("rating")),
                        cursor.getString(cursor.getColumnIndexOrThrow("created_at")),
                        cursor.getString(cursor.getColumnIndexOrThrow("response")),
                        cursor.isNull(cursor.getColumnIndexOrThrow("response_by")) ? null : cursor.getInt(cursor.getColumnIndexOrThrow("response_by"))
                );
                feedbackList.add(feedback);
            }
            cursor.close();
        }
        return feedbackList;
    }

    public List<Feedbacks> getFeedbacksByProductId(int productId) {
        List<Feedbacks> feedbacks = new ArrayList<>();
        Cursor cursor = db.query(
                "Feedbacks",
                null,
                "product_id = ?",
                new String[]{String.valueOf(productId)},
                null,
                null,
                "created_at DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
                String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                double rating = cursor.getDouble(cursor.getColumnIndexOrThrow("rating"));
                String createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"));
                String response = cursor.getString(cursor.getColumnIndexOrThrow("response"));
                int responseBy = cursor.getInt(cursor.getColumnIndexOrThrow("response_by"));

                feedbacks.add(new Feedbacks(id, userId, productId, content, rating, createdAt, response, responseBy));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return feedbacks;
    }
    public int responseFeedback(int feedbackId, String response, int responseBy) {
        ContentValues values = new ContentValues();
        values.put("response", response);
        values.put("response_by", responseBy);
        return db.update("Feedbacks", values, "id = ?", new String[]{String.valueOf(feedbackId)});
    }
    public int deleteFeedback(int id) {
        return db.delete("Feedbacks", "id = ?", new String[]{String.valueOf(id)});
    }
}
