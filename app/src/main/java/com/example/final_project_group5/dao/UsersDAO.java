package com.example.final_project_group5.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.final_project_group5.entity.Users;

import java.util.List;

@Dao
public interface UsersDAO {

    @Insert
    long insertUser(Users user);

    @Query("SELECT * FROM users WHERE id = :id")
    Users getUserById(int id);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    Users loginUser(String email, String password);

    @Query("SELECT * FROM users ORDER BY name ASC")
    List<Users> getAllUsers();

    @Update
    int updateUser(Users user);

    @Delete
    int deleteUser(Users user);
}
