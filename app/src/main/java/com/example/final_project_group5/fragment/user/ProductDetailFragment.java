package com.example.final_project_group5.fragment.user;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.final_project_group5.R;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productId = getArguments().getString("productId");
            Log.d("ProductDetailFragment", "onCreate: productId = " + productId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);
        Log.d("ProductDetailFragment", "onCreateView: View inflated");

        productImage = view.findViewById(R.id.productImage);
        productName = view.findViewById(R.id.productName);
        ratingCount = view.findViewById(R.id.ratingCount);
        productPrice = view.findViewById(R.id.productPrice);
        originalPrice = view.findViewById(R.id.originalPrice);
        discountPercentage = view.findViewById(R.id.discountPercentage);
        brand = view.findViewById(R.id.brand);
        stock = view.findViewById(R.id.stock);
        productDescription = view.findViewById(R.id.productDescription);

        if (productId != null) {
            Log.d("ProductDetailFragment", "Loading product details for ID: " + productId);
            loadProductDetails(productId);
        } else {
            Log.e("ProductDetailFragment", "productId is null");
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
                    Log.d("ProductDetailFragment", "Product loaded: " + product.toString());
                    displayProductDetails(product);
                } else {
                    Log.e("ProductDetailFragment", "Failed to fetch product detail, Code: " + response.code() + ", Message: " + response.message());
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
        if (product != null && isAdded()) {
            try {
                Log.d("ProductDetailFragment", "Updating UI with product: " + product.getName());
                Log.d("ProductDetailFragment", "Context: " + getContext());
                Glide.with(getContext())
                        .load(product.getImage())
//                        .placeholder(R.drawable.placeholder_image)
//                        .error(R.drawable.placeholder_image)
                        .into(productImage);
                productName.setText(product.getName());
                ratingCount.setText("(" + product.getRatingCount() + "+ đánh giá)");
                productPrice.setText(String.format("%,.0fđ", product.getDiscountedPrice()));
                originalPrice.setText(String.format("%,.0fđ", product.getOriginalPrice()));
                discountPercentage.setText("-" + (int) product.getDiscountPercentage() + "%");
                brand.setText(product.getBrand());
                stock.setText(String.valueOf(product.getStock()));
                productDescription.setText(product.getDescription());

                // Debug visibility
                Log.d("ProductDetailFragment", "productName visibility: " + productName.getVisibility());
                Log.d("ProductDetailFragment", "productName text: " + productName.getText());
            } catch (Exception e) {
                Log.e("ProductDetailFragment", "Error updating UI: " + e.getMessage());
            }
        } else {
            Log.e("ProductDetailFragment", "Product data is null or fragment not added");
        }
    }
}