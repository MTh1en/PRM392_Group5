package com.example.final_project_group5.repository;

import com.example.final_project_group5.api.ApiClient;
import com.example.final_project_group5.api.CartService;
import com.example.final_project_group5.entity.Cart;

import java.util.List;

import retrofit2.Call;

public class CartRepo {
    public static CartService getCartService() {
        return ApiClient.getClient().create(CartService.class);
    }
}
