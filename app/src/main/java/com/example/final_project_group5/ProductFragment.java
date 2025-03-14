package com.example.final_project_group5;

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
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.final_project_group5.api.ApiClient;
import com.example.final_project_group5.api.ProductService;
import com.example.final_project_group5.entity.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductFragment extends Fragment {

    private String categoryName;
    private GridLayout productGridLayout; // Sử dụng GridLayout

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        if (getArguments() != null) {
            categoryName = getArguments().getString("category");
        }

        TextView toolbarTitle = view.findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(categoryName);

        ImageView btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        productGridLayout = view.findViewById(R.id.productGridLayout); // Lấy GridLayout

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
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                t.printStackTrace();
                Log.e("ProductFragment", "API call failed: " + t.getMessage());
            }
        });
    }

    private void displayProducts(List<Product> products) {
        productGridLayout.removeAllViews(); // Xóa tất cả View cũ
        productGridLayout.setColumnCount(2); // Đặt số cột = 2

        int margin = 10; // Khoảng cách giữa các sản phẩm
        int cardWidth = getResources().getDisplayMetrics().widthPixels / 2 - (margin * 2); // Đảm bảo 2 cột bằng nhau

        for (Product product : products) {
            // Tạo LinearLayout chứa sản phẩm (Card)
            LinearLayout productCard = new LinearLayout(getContext());
            productCard.setOrientation(LinearLayout.VERTICAL);
            productCard.setPadding(16, 16, 16, 16);
            productCard.setBackgroundResource(R.drawable.card_background); // Đặt background bo góc

            // Thiết lập LayoutParams để giữ kích thước bằng nhau và tạo khoảng cách giữa các sản phẩm
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = cardWidth; // Đặt chiều rộng cố định cho mỗi item
            params.setMargins(margin, margin, margin, margin); // Đặt khoảng cách 10dp giữa các sản phẩm
            productCard.setLayoutParams(params);

            // Tạo ImageView cho sản phẩm
            ImageView productImageView = new ImageView(getContext());
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 300);
            productImageView.setLayoutParams(imgParams);
            productImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(getContext()).load(product.getImage()).into(productImageView);
            productCard.addView(productImageView);

            // Tạo TextView cho tên sản phẩm
            TextView productNameTextView = new TextView(getContext());
            productNameTextView.setText(product.getName());
            productNameTextView.setTypeface(null, Typeface.BOLD);
            productNameTextView.setPadding(8, 8, 8, 0);
            productCard.addView(productNameTextView);

            // Tạo TextView cho giá sản phẩm
            TextView productPriceTextView = new TextView(getContext());
            productPriceTextView.setText(product.getDiscountedPrice() + "đ");
            productPriceTextView.setTextColor(Color.RED);
            productPriceTextView.setPadding(8, 4, 8, 0);
            productCard.addView(productPriceTextView);

            // Tạo RatingBar
            RatingBar ratingBar = new RatingBar(getContext(), null, android.R.attr.ratingBarStyleSmall);
            ratingBar.setNumStars(5); // Giới hạn tối đa 5 sao
            ratingBar.setStepSize(1f); // Chỉ hiển thị số nguyên (1, 2, 3, 4, 5)
            ratingBar.setIsIndicator(true); // Không cho phép người dùng thay đổi
            ratingBar.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));


            float ratingValue = Math.min((float) product.getRatingCount(), 5);
            ratingBar.setRating(ratingValue); // Hiển thị số sao theo ratingCount
            productCard.addView(ratingBar);

            // Tạo Button "Thêm vào giỏ"
            Button addToCartButton = new Button(getContext());
            addToCartButton.setText("Add to cart");
            addToCartButton.setBackgroundResource(R.drawable.button_background);
            addToCartButton.setTextAppearance(getContext(), R.style.WhiteButtonText); // Áp dụng style
            productCard.addView(addToCartButton);

            // Thêm sản phẩm vào GridLayout
            productGridLayout.addView(productCard);
        }
    }

}