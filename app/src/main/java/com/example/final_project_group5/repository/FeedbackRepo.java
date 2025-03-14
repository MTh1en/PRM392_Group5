package com.example.final_project_group5.repository;

import com.example.final_project_group5.api.ApiClient;
import com.example.final_project_group5.api.FeedbackService;

public class FeedbackRepo {
    public static FeedbackService getFeedbackService() {
        return ApiClient.getClient().create(FeedbackService.class);
    }
}
