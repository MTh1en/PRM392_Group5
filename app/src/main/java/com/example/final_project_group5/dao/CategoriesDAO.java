package com.example.final_project_group5.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.final_project_group5.entity.Categories;

import java.util.List;

@Dao
public interface CategoriesDAO {

    @Insert
    long insertCategory(Categories category);

    @Update
    int updateCategory(Categories category);

    @Delete
    int deleteCategory(Categories category);

    @Query("SELECT * FROM categories ORDER BY name ASC")
    List<Categories> getAllCategories();

    @Query("SELECT * FROM categories WHERE id = :categoryId LIMIT 1")
    Categories getCategoryById(int categoryId);

    @Query("SELECT * FROM categories WHERE name LIKE '%' || :name || '%' ORDER BY name ASC")
    List<Categories> searchCategoriesByName(String name);
}
