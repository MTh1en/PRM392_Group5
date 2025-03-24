package com.example.final_project_group5.api;

import com.example.final_project_group5.entity.Product;
import com.example.final_project_group5.entity.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {
    String USERS = "User";
    @GET(USERS)
    Call<List<User>> getAllUsers();
    @GET(USERS + "/{id}")
    Call<User> getUser(@Path("id") String id);
    @POST(USERS)
    Call<User> createUser(@Body User user);
    @PUT(USERS + "/{id}")
    Call<User> updateUser(@Path("id") String id, @Body User user);
    @DELETE(USERS + "/{id}")
    Call<User> deleteUser(@Path("id") String id);

    @GET(USERS)
    Call<List<User>> login(@Query("email") String email, @Query("password") String password);

    @GET(USERS)
    Call<List<User>> loginGoogle(@Query("email") String email);

}
