package com.example.final_project_group5.repository;

import com.example.final_project_group5.api.ApiClient;
import com.example.final_project_group5.api.UserService;

public class UserRepo {
    public static UserService getUserService() {
        return ApiClient.getClient().create(UserService.class);
    }
}
