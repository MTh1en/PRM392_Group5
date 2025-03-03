package com.example.final_project_group5.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.final_project_group5.entity.Feedbacks;

import java.util.List;

@Dao
public interface FeedbacksDAO {

    @Insert
    long insertFeedback(Feedbacks feedback);

    @Query("SELECT * FROM feedbacks ORDER BY created_at DESC")
    List<Feedbacks> getAllFeedbacks();

    @Query("SELECT * FROM feedbacks WHERE product_id = :productId ORDER BY created_at DESC")
    List<Feedbacks> getFeedbacksByProductId(int productId);

    @Query("UPDATE feedbacks SET response = :response, response_by = :responseBy WHERE id = :feedbackId")
    int responseFeedback(int feedbackId, String response, int responseBy);

    @Query("DELETE FROM feedbacks WHERE id = :id")
    int deleteFeedback(int id);

    @Update
    int updateFeedback(Feedbacks feedback);

    @Delete
    int deleteFeedback(Feedbacks feedback);
}
