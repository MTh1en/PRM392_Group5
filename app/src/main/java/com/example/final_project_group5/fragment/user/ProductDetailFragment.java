package com.example.final_project_group5.fragment.user;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.final_project_group5.R;
import com.example.final_project_group5.adapter.FeedbackAdapter;
import com.example.final_project_group5.api.ApiClient;
import com.example.final_project_group5.api.CartService;
import com.example.final_project_group5.api.FeedbackService;
import com.example.final_project_group5.api.ProductService;
import com.example.final_project_group5.entity.Cart;
import com.example.final_project_group5.entity.Feedback;
import com.example.final_project_group5.entity.Product;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailFragment extends Fragment {

    private ImageView productImage;
    private TextView productName, ratingCount, productPrice, originalPrice, discountPercentage, brand, stock, productDescription;
    private RatingBar ratingBar;
    private Button addToCartButton, btnSubmitFeedback;
    private EditText etFeedbackTitle, etFeedbackComment;
    private RatingBar rbFeedbackRating;
    private RecyclerView rvFeedbacks;
    private FeedbackAdapter feedbackAdapter;
    private String productId, userId;
    private Product currentProduct;
    private List<Feedback> feedbackList = new ArrayList<>();

    public ProductDetailFragment() {
    }

    public static ProductDetailFragment newInstance(String userId, String productId) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putString("USER_ID", userId);
        args.putString("PRODUCT_ID", productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString("USER_ID");
            productId = getArguments().getString("PRODUCT_ID");
            Log.d("ProductDetailFragment", "onCreate - Received userId: " + userId);
            Log.d("ProductDetailFragment", "onCreate - Received productId: " + productId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        // Ánh xạ các thành phần UI
        productImage = view.findViewById(R.id.productImage);
        productName = view.findViewById(R.id.productName);
        ratingCount = view.findViewById(R.id.ratingCount);
        ratingBar = view.findViewById(R.id.ratingBar);
        productPrice = view.findViewById(R.id.productPrice);
        originalPrice = view.findViewById(R.id.originalPrice);
        discountPercentage = view.findViewById(R.id.discountPercentage);
        brand = view.findViewById(R.id.brand);
        stock = view.findViewById(R.id.stock);
        productDescription = view.findViewById(R.id.productDescription);
        addToCartButton = view.findViewById(R.id.addToCartButton);
        rvFeedbacks = view.findViewById(R.id.rvFeedbacks);
        etFeedbackTitle = view.findViewById(R.id.etFeedbackTitle);
        etFeedbackComment = view.findViewById(R.id.etFeedbackComment);
        rbFeedbackRating = view.findViewById(R.id.rbFeedbackRating);
        btnSubmitFeedback = view.findViewById(R.id.btnSubmitFeedback);

        // Khởi tạo RecyclerView cho feedback
        rvFeedbacks.setLayoutManager(new LinearLayoutManager(getContext()));
        feedbackAdapter = new FeedbackAdapter(getContext(), feedbackList);
        rvFeedbacks.setAdapter(feedbackAdapter);

        // Xử lý sự kiện nút Add to Cart
        addToCartButton.setOnClickListener(v -> {
            if (currentProduct != null && userId != null) {
                CartService cartService = ApiClient.getClient().create(CartService.class);

                boolean exists = false;
                for (Cart cart : ProductFragment.cartItems) {
                    if (cart.getProductId() == Integer.parseInt(currentProduct.getId())) {
                        cart.setQuantity(cart.getQuantity() + 1);
                        cart.setUserId(Integer.parseInt(userId));
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
                                Log.e("ProductDetailFragment", "Lỗi cập nhật giỏ hàng: " + t.getMessage());
                            }
                        });
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    Cart newCart = new Cart("", Integer.parseInt(userId), Integer.parseInt(currentProduct.getId()), 1);
                    Call<Cart> call = cartService.createCart(newCart);
                    call.enqueue(new Callback<Cart>() {
                        @Override
                        public void onResponse(Call<Cart> call, Response<Cart> response) {
                            if (response.isSuccessful()) {
                                ProductFragment.cartItems.add(response.body());
                                Toast.makeText(getContext(), "Thêm " + currentProduct.getName() + " vào giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Cart> call, Throwable t) {
                            Log.e("ProductDetailFragment", "Lỗi thêm giỏ hàng: " + t.getMessage());
                        }
                    });
                }
            } else {
                Toast.makeText(getContext(), "Sản phẩm hoặc tài khoản chưa được tải!", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý sự kiện gửi feedback
        btnSubmitFeedback.setOnClickListener(v -> {
            String title = etFeedbackTitle.getText().toString().trim();
            String comment = etFeedbackComment.getText().toString().trim();
            float rating = rbFeedbackRating.getRating();

            if (!title.isEmpty() && !comment.isEmpty() && rating > 0 && userId != null && productId != null) {
                submitFeedback(title, comment, rating);
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ tiêu đề, nội dung và đánh giá!", Toast.LENGTH_SHORT).show();
            }
        });

        // Load chi tiết sản phẩm và feedback
        if (productId != null) {
            loadProductDetails(productId);
            loadFeedbacks(productId);
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
                Log.e("ProductDetailFragment", "API call failed: " + t.getMessage());
            }
        });
    }

    private void displayProductDetails(Product product) {
        if (product != null) {
            if (product.getImage() != null && !product.getImage().isEmpty()) {
                Glide.with(getContext())
                        .load(product.getImage())
                        .placeholder(R.drawable.app_logo)
                        .error(R.drawable.button_background)
                        .into(productImage);
            } else {
                productImage.setImageResource(R.drawable.button_background);
            }

            productName.setText(product.getName() != null ? product.getName() : "Không có tên");
            ratingCount.setText("(" + (product.getRatingCount() >= 0 ? product.getRatingCount() : 0) + "+ đánh giá)");
            float ratingValue = Math.min((float) (product.getRatingCount() >= 0 ? product.getRatingCount() : 0), 5);
            ratingBar.setRating(ratingValue);
            productPrice.setText(product.getDiscountedPrice() >= 0
                    ? String.format("%,.0fđ", product.getDiscountedPrice())
                    : "Liên hệ");
            originalPrice.setText(product.getOriginalPrice() >= 0
                    ? String.format("%,.0fđ", product.getOriginalPrice())
                    : "");
            discountPercentage.setText(product.getDiscountPercentage() >= 0
                    ? "-" + (int) product.getDiscountPercentage() + "%"
                    : "");
            brand.setText(product.getBrand() != null && !product.getBrand().isEmpty()
                    ? product.getBrand()
                    : "N/A");
            stock.setText(product.getStock() >= 0
                    ? String.valueOf(product.getStock())
                    : "Hết hàng");
            productDescription.setText(product.getDescription() != null && !product.getDescription().isEmpty()
                    ? product.getDescription()
                    : "Không có mô tả");

            addToCartButton.setVisibility(View.VISIBLE);
            Log.d("ProductDetailFragment", "addToCartButton visibility set to VISIBLE");
        } else {
            Log.e("ProductDetailFragment", "Product is null!");
            Toast.makeText(getContext(), "Không thể tải thông tin sản phẩm!", Toast.LENGTH_SHORT).show();
            productName.setText("Không có tên");
            ratingCount.setText("(0+ đánh giá)");
            ratingBar.setRating(0);
            productPrice.setText("Liên hệ");
            originalPrice.setText("");
            discountPercentage.setText("");
            brand.setText("N/A");
            stock.setText("Hết hàng");
            productDescription.setText("Không có mô tả");
        }
    }

    private void loadFeedbacks(String productId) {
        FeedbackService feedbackService = ApiClient.getClient().create(FeedbackService.class);
        Call<List<Feedback>> call = feedbackService.getFeedbacksByProduct(Integer.parseInt(productId));

        call.enqueue(new Callback<List<Feedback>>() {
            @Override
            public void onResponse(Call<List<Feedback>> call, Response<List<Feedback>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    feedbackList.clear();
                    feedbackList.addAll(response.body());
                    feedbackAdapter.updateFeedbacks(feedbackList);
                    Log.d("ProductDetailFragment", "Loaded " + feedbackList.size() + " feedbacks");
                } else {
                    Log.e("ProductDetailFragment", "Failed to fetch feedbacks: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Feedback>> call, Throwable t) {
                Log.e("ProductDetailFragment", "API call failed: " + t.getMessage());
            }
        });
    }

    private void submitFeedback(String title, String comment, float rating) {
        FeedbackService feedbackService = ApiClient.getClient().create(FeedbackService.class);
        String createAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        Feedback newFeedback = new Feedback("", Integer.parseInt(userId), Integer.parseInt(productId), title, comment, rating, createAt);
        Call<Feedback> call = feedbackService.createFeedback(newFeedback);

        call.enqueue(new Callback<Feedback>() {
            @Override
            public void onResponse(Call<Feedback> call, Response<Feedback> response) {
                if (response.isSuccessful() && response.body() != null) {
                    feedbackList.add(response.body());
                    feedbackAdapter.notifyDataSetChanged();
                    etFeedbackTitle.setText("");
                    etFeedbackComment.setText("");
                    rbFeedbackRating.setRating(0);
                    Toast.makeText(getContext(), "Gửi bình luận thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Gửi bình luận thất bại!", Toast.LENGTH_SHORT).show();
                    Log.e("ProductDetailFragment", "Feedback submission failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Feedback> call, Throwable t) {
                Log.e("ProductDetailFragment", "API call failed: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}