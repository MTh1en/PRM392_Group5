package com.example.final_project_group5.api;

import com.example.final_project_group5.entity.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductService {
    String PRODUCTS = "Product";
    @GET(PRODUCTS)
    Call<List<Product>> getAllProducts();
    @GET(PRODUCTS + "/{id}")
    Call<Product> getProduct(@Path("id") String id);
    @POST(PRODUCTS)
    Call<Product> createProduct(@Body Product product);
    @PUT(PRODUCTS + "/{id}")
    Call<Product> updateProduct(@Path("id") String id, @Body Product product);
    @DELETE(PRODUCTS + "/{id}")
    Call<Void> deleteProduct(@Path("id") String id);
    @GET(PRODUCTS)
    Call<List<Product>> getProductsByCategory(@Query("category") String category);

    @GET(PRODUCTS)
    Call<List<Product>> searchProductsByName(@Query("name") String name);


    @GET(PRODUCTS)
    Call<List<Product>> searchProductsByBrand(@Query("brand") String brand);


    @GET(PRODUCTS)
    Call<List<Product>> searchProductsByCategory(@Query("category") String category);
}
