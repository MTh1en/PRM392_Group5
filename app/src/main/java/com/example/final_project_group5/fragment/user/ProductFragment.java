package com.example.final_project_group5.fragment.user;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.final_project_group5.R;
import com.example.final_project_group5.api.ApiClient;
import com.example.final_project_group5.api.CartService;
import com.example.final_project_group5.api.ProductService;
import com.example.final_project_group5.entity.Cart;
import com.example.final_project_group5.entity.Product;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductFragment extends Fragment {

    private String categoryName;
    private GridLayout productGridLayout;
    public static List<Cart> cartItems = new ArrayList<>();
    private String userId;
    public static ProductFragment newInstance(String userId, String categoryName) {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        args.putString("USER_ID", userId);
        args.putString("CATEGORY_NAME", categoryName); // Lưu categoryName vào Bundle
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        if (getArguments() != null) {
            userId = getArguments().getString("USER_ID");
            categoryName = getArguments().getString("CATEGORY_NAME");
            Log.d("ProductFragment", "onCreate - Received userId: " + userId);
            Log.d("ProductFragment", "onCreate - Received categoryName: " + categoryName);
        }

        productGridLayout = view.findViewById(R.id.productGridLayout);
        productGridLayout.setColumnCount(2);

        fetchProductsByCategory();

        return view;
    }

    private void fetchProductsByCategory() {
        ProductService productService = ApiClient.getClient().create(ProductService.class);
        Call<List<Product>> call = productService.getProductsByCategory(categoryName);

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayProducts(response.body());
                    Log.d("ProductFragment", "API response successful");
                } else {
                    Log.e("ProductFragment", "API response failed: " + response.code());
                    Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("ProductFragment", "API call failed: " + t.getMessage());
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayProducts(List<Product> products) {
        productGridLayout.removeAllViews();

        int margin = 10;
        int cardWidth = getResources().getDisplayMetrics().widthPixels / 2 - (margin * 2);

        for (Product product : products) {
            LinearLayout productCard = new LinearLayout(getContext());
            productCard.setOrientation(LinearLayout.VERTICAL);
            productCard.setPadding(16, 16, 16, 16);
            productCard.setBackgroundResource(R.drawable.card_background);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = cardWidth;
            params.setMargins(margin, margin, margin, margin);
            productCard.setLayoutParams(params);

            ImageView productImageView = new ImageView(getContext());
            productImageView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 300));
            productImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(getContext()).load(product.getImage()).into(productImageView);
            productCard.addView(productImageView);

            TextView productNameTextView = new TextView(getContext());
            productNameTextView.setText(product.getName());
            productNameTextView.setTypeface(null, Typeface.BOLD);
            productNameTextView.setPadding(8, 8, 8, 0);
            productCard.addView(productNameTextView);

            TextView productPriceTextView = new TextView(getContext());
            productPriceTextView.setText(String.format("%.0f", product.getDiscountedPrice()) + " VND");
            productPriceTextView.setTextColor(Color.RED);
            productPriceTextView.setPadding(8, 4, 8, 0);
            productCard.addView(productPriceTextView);

            RatingBar ratingBar = new RatingBar(getContext(), null, android.R.attr.ratingBarStyleSmall);
            ratingBar.setNumStars(5);
            ratingBar.setStepSize(1f);
            ratingBar.setIsIndicator(true);
            ratingBar.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            float ratingValue = Math.min((float) product.getRatingCount(), 5);
            ratingBar.setRating(ratingValue);
            productCard.addView(ratingBar);

            Button addToCartButton = new Button(getContext());
            addToCartButton.setText("Add to cart");
            addToCartButton.setBackgroundResource(R.drawable.button_background);
            addToCartButton.setTextAppearance(getContext(), R.style.WhiteButtonText);
            productCard.addView(addToCartButton);


            addToCartButton.setOnClickListener(v -> {
                if (product != null) {
                    CartService cartService = ApiClient.getClient().create(CartService.class);

                    // Kiểm tra xem sản phẩm đã có trong giỏ hàng hay chưa
                    boolean exists = false;
                    for (Cart cart : cartItems) {
                        if (cart.getProductId() == Integer.parseInt(product.getId())) {
                            cart.setQuantity(cart.getQuantity() + 1);
                            cart.setUserId(Integer.parseInt(userId));
                            // Gọi API cập nhật số lượng giỏ hàng
                            Call<Cart> call = cartService.updateCart(cart.getId(), cart);
                            call.enqueue(new Callback<Cart>() {
                                @Override
                                public void onResponse(Call<Cart> call, Response<Cart> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(getContext(), "Cập nhật giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Cart> call, Throwable t) {
                                    Log.e("ProductFragment", "Lỗi cập nhật giỏ hàng: " + t.getMessage());
                                }
                            });

                            exists = true;
                            break;
                        }
                    }

                    // Nếu sản phẩm chưa có trong giỏ hàng, thêm mới
                    if (!exists) {
                        Cart newCart = new Cart("", Integer.parseInt(userId), Integer.parseInt(product.getId()), 1);
                        Call<Cart> call = cartService.createCart(newCart);
                        call.enqueue(new Callback<Cart>() {
                            @Override
                            public void onResponse(Call<Cart> call, Response<Cart> response) {
                                if (response.isSuccessful()) {
                                    cartItems.add(response.body()); // Cập nhật danh sách giỏ hàng
                                    Toast.makeText(getContext(), "Thêm vào giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Cart> call, Throwable t) {
                                Log.e("ProductFragment", "Lỗi thêm giỏ hàng: " + t.getMessage());
                            }
                        });
                    }
                }
            });

            productCard.setOnClickListener(v -> {
                if (product.getId() == null) {
                    Toast.makeText(getContext(), "Product ID is missing", Toast.LENGTH_SHORT).show();
                    return;
                }
                ProductDetailFragment productDetailFragment = ProductDetailFragment.newInstance(userId, product.getId()); // Sử dụng userId trực tiếp
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout1, productDetailFragment)
                        .addToBackStack(null)
                        .commit();
            });

            productGridLayout.addView(productCard);
        }
    }
}