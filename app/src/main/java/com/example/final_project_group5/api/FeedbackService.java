package com.example.final_project_group5.api;

import com.example.final_project_group5.entity.Feedback;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FeedbackService {
    String FEEDBACKS = "Feedback"; // "feedbacks" là tên endpoint trong MockAPI.io

    @GET(FEEDBACKS)
    Call<List<Feedback>> getAllFeedbacks();

    @GET(FEEDBACKS + "/{id}")
    Call<Feedback> getFeedback(@Path("id") String id);

    @POST(FEEDBACKS)
    Call<Feedback> createFeedback(@Body Feedback feedback);

    @PUT(FEEDBACKS + "/{id}")
    Call<Feedback> updateFeedback(@Path("id") String id, @Body Feedback feedback);

    @DELETE(FEEDBACKS + "/{id}")
    Call<Void> deleteFeedback(@Path("id") String id);

    @GET(FEEDBACKS)
    Call<List<Feedback>> getFeedbacksByUser(@Query("userId") int userId);

    @GET(FEEDBACKS)
    Call<List<Feedback>> getFeedbacksByProduct(@Query("productId") int productId);
}
