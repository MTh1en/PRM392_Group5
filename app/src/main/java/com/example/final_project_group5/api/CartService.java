package com.example.final_project_group5.api;

import com.example.final_project_group5.entity.Cart;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CartService {
    String CARTS = "Cart"; // "carts" là tên endpoint trong MockAPI.io

    @GET(CARTS)
    Call<List<Cart>> getAllCarts();

    @GET(CARTS + "/{id}")
    Call<Cart> getCart(@Path("id") String id);

    @POST(CARTS)
    Call<Cart> createCart(@Body Cart cart);

    @PUT(CARTS + "/{id}")
    Call<Cart> updateCart(@Path("id") String id, @Body Cart cart);

    @DELETE(CARTS + "/{id}")
    Call<Void> deleteCart(@Path("id") String id);

    @GET(CARTS)
    Call<List<Cart>> getCartsByUser(@Query("userId") int userId);
}
