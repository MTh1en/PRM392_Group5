package com.example.final_project_group5.repository;

import com.example.final_project_group5.api.ApiClient;
import com.example.final_project_group5.api.OrderDetailService;

public class OrderDetailRepo {
    public static OrderDetailService getOrderDetailService() {
        return ApiClient.getClient().create(OrderDetailService.class);
    }
}
