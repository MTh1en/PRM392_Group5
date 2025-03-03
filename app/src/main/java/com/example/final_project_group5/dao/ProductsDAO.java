package com.example.final_project_group5.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.final_project_group5.entity.Products;

import java.util.List;

@Dao
public interface ProductsDAO {

    @Insert
    void insertProduct(Products product);

    @Update
    void updateProduct(Products product);

    @Delete
    void deleteProduct(Products product);

    @Query("SELECT * FROM products WHERE id = :productId")
    Products getProductById(int productId);

    @Query("SELECT * FROM products")
    List<Products> getAllProducts();

    @Query("SELECT * FROM Products WHERE category_id = :categoryId")
    List<Products> getProductsByCategory(int categoryId);

    @Query("SELECT * FROM Products WHERE price < :maxPrice")
    List<Products> getProductsByMaxPrice(double maxPrice);
}
