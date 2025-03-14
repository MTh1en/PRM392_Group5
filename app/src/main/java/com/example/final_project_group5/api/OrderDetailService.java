package com.example.final_project_group5.api;

import com.example.final_project_group5.entity.OrderDetail;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrderDetailService {
    String ORDER_DETAILS = "OrderDetail";

    @GET(ORDER_DETAILS)
    Call<List<OrderDetail>> getAllOrderDetails();

    @GET(ORDER_DETAILS + "/{id}")
    Call<OrderDetail> getOrderDetail(@Path("id") String id);

    @GET(ORDER_DETAILS)
    Call<List<OrderDetail>> getOrderDetailsByOrder(@Query("orderId") int orderId);
    @POST(ORDER_DETAILS)
    Call<OrderDetail> createOrderDetail(@Body OrderDetail orderDetail);

    @PUT(ORDER_DETAILS + "/{id}")
    Call<OrderDetail> updateOrderDetail(@Path("id") String id, @Body OrderDetail orderDetail);

    @DELETE(ORDER_DETAILS + "/{id}")
    Call<Void> deleteOrderDetail(@Path("id") String id);
}
