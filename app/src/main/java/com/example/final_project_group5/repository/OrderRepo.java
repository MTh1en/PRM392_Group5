package com.example.final_project_group5.repository;

import com.example.final_project_group5.api.ApiClient;
import com.example.final_project_group5.api.OrderService;

public class OrderRepo {
    public static OrderService getOrderService() {
        return ApiClient.getClient().create(OrderService.class);
    }
}
