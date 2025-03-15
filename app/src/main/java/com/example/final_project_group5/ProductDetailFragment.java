package com.example.final_project_group5;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.final_project_group5.api.ApiClient;
import com.example.final_project_group5.api.ProductService;
import com.example.final_project_group5.entity.Product;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailFragment extends Fragment {

    private ImageView productImage;
    private TextView productName;
    private TextView ratingCount;
    private TextView productPrice;
    private TextView originalPrice;
    private TextView discountPercentage;
    private TextView brand;
    private TextView stock;
    private TextView productDescription;
    private String productId;

    public ProductDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        productImage = view.findViewById(R.id.productImage);
        productName = view.findViewById(R.id.productName);
        ratingCount = view.findViewById(R.id.ratingCount);
        productPrice = view.findViewById(R.id.productPrice);
        originalPrice = view.findViewById(R.id.originalPrice);
        discountPercentage = view.findViewById(R.id.discountPercentage);
        brand = view.findViewById(R.id.brand);
        stock = view.findViewById(R.id.stock);
        productDescription = view.findViewById(R.id.productDescription);

        if (getArguments() != null) {
            productId = getArguments().getString("productId");
            loadProductDetails(productId);
        }

        return view;
    }

    private void loadProductDetails(String productId) {
        ProductService productService = ApiClient.getClient().create(ProductService.class);
        Call<Product> call = productService.getProduct(productId);

        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Product product = response.body();
                    displayProductDetails(product);
                } else {
                    Log.e("ProductDetailFragment", "Failed to fetch product detail");
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                t.printStackTrace();
                Log.e("ProductDetailFragment", "API call failed: " + t.getMessage());
            }
        });
    }

    private void displayProductDetails(Product product) {
        if (product != null) {
            // Cập nhật UI với thông tin chi tiết sản phẩm
            Glide.with(getContext()).load(product.getImage()).into(productImage);
            productName.setText(product.getName());

            ratingCount.setText("(" + product.getRatingCount() + "+ đánh giá)");
            productPrice.setText(String.format("%,.0fđ", product.getDiscountedPrice()));
            originalPrice.setText(String.format("%,.0fđ", product.getOriginalPrice()));
            // Sử dụng discountPercentage từ API thay vì tính lại
            discountPercentage.setText("-" + (int) product.getDiscountPercentage() + "%");
            brand.setText(product.getBrand());
            stock.setText(String.valueOf(product.getStock())); // Chuyển int thành String
            productDescription.setText(product.getDescription());
        }
    }
}