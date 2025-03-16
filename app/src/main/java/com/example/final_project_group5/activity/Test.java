package com.example.final_project_group5.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project_group5.entity.Order;
import com.example.final_project_group5.entity.OrderDetail;
import com.example.final_project_group5.entity.Product;
import com.example.final_project_group5.repository.OrderRepo;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Test extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. Tạo một đối tượng Order mới
        Order order = new Order();

        // 2. Tạo danh sách Product (nếu cần)
        List<OrderDetail> orderDetails = new ArrayList<>();

        // Tạo một sản phẩm
        OrderDetail orderDetail1 = new OrderDetail();
        orderDetail1.setId("P001");
        orderDetail1.setProductName("Laptop");
        orderDetail1.setProductImage("Laptop Description");
        orderDetail1.setQuantity(3);
        orderDetail1.setOriginalPrice(100);
        orderDetail1.setDiscountAmount(50);
        orderDetail1.setDiscountedPrice(50);
        orderDetails.add(orderDetail1);

        // Tạo một sản phẩm khác
        OrderDetail orderDetail2 = new OrderDetail();
        orderDetail2.setId("P002");
        orderDetail2.setProductName("Macbook");
        orderDetail2.setProductImage("Macbook Description");
        orderDetail2.setQuantity(2);
        orderDetail2.setOriginalPrice(1000);
        orderDetail2.setDiscountAmount(50);
        orderDetail2.setDiscountedPrice(950);
        orderDetails.add(orderDetail2);

        order.setId("O001");
        order.setUserId(123);
        order.setOrderDate("2023-10-27");
        order.setTotalAmount(1500.0);
        order.setShippingAddress("123 Main St");
        order.setShippingFee(10.0f);
        order.setDiscountAmount(50.0f);
        order.setPaymentStatus("Paid");
        order.setShippingStatus("Shipped");
        order.setOrderDetails(orderDetails); // Thêm danh sách sản phẩm vào đơn hàng

        //Gọi API để tạo đơn hàng
        OrderRepo.getOrderService().createOrder(order).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {

            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {

            }
        });
    }
}
