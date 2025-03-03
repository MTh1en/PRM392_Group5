package com.example.final_project_group5.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.final_project_group5.entity.Orders;

import java.util.List;

@Dao
public interface  OrdersDAO {
    @Insert
    long insertOrder(Orders order);

    @Query("SELECT * FROM Orders WHERE user_id = :userId ORDER BY order_date DESC")
    List<Orders> getOrdersByUserId(int userId);

    @Query("SELECT * FROM Orders ORDER BY order_date DESC")
    List<Orders> getAllOrders();

    @Update
    int updateOrder(Orders order);

    @Delete
    int deleteOrder(Orders order);

    @Query("SELECT * FROM Orders WHERE id = :orderId")
    Orders getOrderById(int orderId);

    @Query("SELECT * FROM Orders WHERE status = :status ORDER BY order_date DESC")
    List<Orders> getOrdersByStatus(String status);
}
