package com.example.final_project_group5.api;

import com.example.final_project_group5.entity.Order;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrderService {
    String ORDERS = "Order"; // "orders" là tên endpoint trong MockAPI.io

    @GET(ORDERS)
    Call<List<Order>> getAllOrders();

    @GET(ORDERS + "/{id}")
    Call<Order> getOrder(@Path("id") String id);

    @POST(ORDERS)
    Call<Order> createOrder(@Body Order order);

    @PUT(ORDERS + "/{id}")
    Call<Order> updateOrder(@Path("id") String id, @Body Order order);

    @DELETE(ORDERS + "/{id}")
    Call<Void> deleteOrder(@Path("id") String id);

    @GET(ORDERS)
    Call<List<Order>> getOrdersByUser(@Query("userId") int userId);
}
