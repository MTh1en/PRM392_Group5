package com.example.final_project_group5.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.final_project_group5.entity.Carts;

import java.util.List;

@Dao
public interface CartsDAO {

    @Insert
    long insertCart(Carts cart);

    @Update
    int updateCart(Carts cart);

    @Delete
    int deleteCart(Carts cart);

    @Query("DELETE FROM carts WHERE user_id = :userId")
    int clearCartByUserId(int userId);

    @Query("SELECT * FROM carts WHERE user_id = :userId ORDER BY addedAt DESC")
    LiveData<List<Carts>> getCartsByUserId(int userId);

    @Query("SELECT * FROM carts WHERE user_id = :userId AND product_id = :productId LIMIT 1")
    Carts getCartByUserAndProduct(int userId, int productId);

    @Query("UPDATE carts SET quantity = :quantity WHERE user_id = :userId AND product_id = :productId")
    int updateCartQuantity(int userId, int productId, int quantity);

    @Query("SELECT SUM(quantity) FROM carts WHERE user_id = :userId")
    LiveData<Integer> getTotalQuantityByUserId(int userId);
}
