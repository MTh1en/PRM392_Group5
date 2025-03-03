package com.example.final_project_group5.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.final_project_group5.entity.OrderDetails;

import java.util.List;

@Dao
public interface OrderDetailsDAO {
    @Insert
    long insertOrderDetail(OrderDetails orderDetail);

    @Query("SELECT * FROM order_details")
    List<OrderDetails> getAllOrderDetails();

    @Query("SELECT * FROM order_details WHERE order_id = :orderId")
    List<OrderDetails> getOrderDetailsByOrderId(int orderId);

    @Update
    int updateOrderDetail(OrderDetails orderDetail);

    @Delete
    int deleteOrderDetail(OrderDetails orderDetail);
}
