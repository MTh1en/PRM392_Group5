package com.example.final_project_group5.api;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://67cda0a2125cd5af75785649.mockapi.io/";
    private static Retrofit retrofit;

    // Hàm khởi tạo Retrofit
    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Service cho User
    public static UserService getUserService() {
        return getClient().create(UserService.class);
    }

    // Service cho Cart
    public static CartService getCartService() {
        return getClient().create(CartService.class);
    }
}
