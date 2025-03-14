package com.example.final_project_group5.repository;

import com.example.final_project_group5.api.ApiClient;
import com.example.final_project_group5.api.ProductService;

public class ProductRepo {
    public static ProductService getProductService() {
        return ApiClient.getClient().create(ProductService.class);
    }
}
