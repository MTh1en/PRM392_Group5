package com.example.final_project_group5.fragment.user;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.final_project_group5.R;
import com.example.final_project_group5.api.ApiClient;
import com.example.final_project_group5.api.CartService;
import com.example.final_project_group5.api.ProductService;
import com.example.final_project_group5.entity.Cart;
import com.example.final_project_group5.entity.Product;
import com.example.final_project_group5.fragment.user.ProductFragment;

import java.util.ArrayList;
import java.util.List;

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
    private ImageView backButton;
    private Button addToCartButton;
    private String productId;
    private Product currentProduct;

    public ProductDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        // Ánh xạ các thành phần UI
        productImage = view.findViewById(R.id.productImage);
        productName = view.findViewById(R.id.productName);
        ratingCount = view.findViewById(R.id.ratingCount);
        productPrice = view.findViewById(R.id.productPrice);
        originalPrice = view.findViewById(R.id.originalPrice);
        discountPercentage = view.findViewById(R.id.discountPercentage);
        brand = view.findViewById(R.id.brand);
        stock = view.findViewById(R.id.stock);
        productDescription = view.findViewById(R.id.productDescription);
        backButton = view.findViewById(R.id.backButton);
        addToCartButton = view.findViewById(R.id.addToCartButton);

        // Xử lý sự kiện nút Back
        backButton.setOnClickListener(v -> {
            if (getFragmentManager() != null) {
                getFragmentManager().popBackStack();
            }
        });

        // Xử lý sự kiện nút Add to Cart
        addToCartButton.setOnClickListener(v -> {
            if (currentProduct != null) {
                CartService cartService = ApiClient.getClient().create(CartService.class);

                // Kiểm tra xem sản phẩm đã có trong giỏ hàng hay chưa
                boolean exists = false;
                for (Cart cart : ProductFragment.cartItems) {
                    if (cart.getProductId() == Integer.parseInt(currentProduct.getId())) {
                        cart.setQuantity(cart.getQuantity() + 1);

                        // Gọi API cập nhật số lượng giỏ hàng
                        Call<Cart> call = cartService.updateCart(cart.getId(), cart);
                        call.enqueue(new Callback<Cart>() {
                            @Override
                            public void onResponse(Call<Cart> call, Response<Cart> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getContext(), "Cập nhật giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
                                    Log.d("ProductDetailFragment", "Cart updated: " + response.body());
                                } else {
                                    Toast.makeText(getContext(), "Cập nhật giỏ hàng thất bại!", Toast.LENGTH_SHORT).show();
                                    Log.e("ProductDetailFragment", "Update failed: " + response.code());
                                }
                            }

                            @Override
                            public void onFailure(Call<Cart> call, Throwable t) {
                                Log.e("ProductDetailFragment", "Lỗi cập nhật giỏ hàng: " + t.getMessage());
                                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                            }
                        });

                        exists = true;
                        break;
                    }
                }

                // Nếu sản phẩm chưa có trong giỏ hàng, thêm mới
                if (!exists) {
                    Cart newCart = new Cart("", 1, Integer.parseInt(currentProduct.getId()), 1); // userId = 1 (giả định)
                    Call<Cart> call = cartService.createCart(newCart);
                    call.enqueue(new Callback<Cart>() {
                        @Override
                        public void onResponse(Call<Cart> call, Response<Cart> response) {
                            if (response.isSuccessful()) {
                                ProductFragment.cartItems.add(response.body()); // Cập nhật danh sách giỏ hàng
                                Toast.makeText(getContext(), "Thêm " + currentProduct.getName() + " vào giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
                                Log.d("ProductDetailFragment", "Cart item added: " + response.body());
                            } else {
                                Toast.makeText(getContext(), "Thêm giỏ hàng thất bại!", Toast.LENGTH_SHORT).show();
                                Log.e("ProductDetailFragment", "Add failed: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<Cart> call, Throwable t) {
                            Log.e("ProductDetailFragment", "Lỗi thêm giỏ hàng: " + t.getMessage());
                            Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(getContext(), "Sản phẩm chưa được tải!", Toast.LENGTH_SHORT).show();
            }
        });

        // Load chi tiết sản phẩm
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
                    currentProduct = response.body();
                    displayProductDetails(currentProduct);
                } else {
                    Log.e("ProductDetailFragment", "Failed to fetch product detail: " + response.code());
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
            Glide.with(getContext()).load(product.getImage()).into(productImage);
            productName.setText(product.getName());
            ratingCount.setText("(" + product.getRatingCount() + "+ đánh giá)");
            productPrice.setText(String.format("%,.0fđ", product.getDiscountedPrice()));
            originalPrice.setText(String.format("%,.0fđ", product.getOriginalPrice()));
            discountPercentage.setText("-" + (int) product.getDiscountPercentage() + "%");
            brand.setText(product.getBrand());
            stock.setText(String.valueOf(product.getStock()));
            productDescription.setText(product.getDescription());
        }
    }
}