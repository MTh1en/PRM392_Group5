package com.example.final_project_group5.fragment.user;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.final_project_group5.R;
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
                    displayProducts(response.body()); // Hiển thị sản phẩm
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

        for (Product product : products) {
            // Tạo LinearLayout cho mỗi sản phẩm
            LinearLayout productLayout = new LinearLayout(getContext());
            productLayout.setOrientation(LinearLayout.VERTICAL);

            // Tạo ImageView
            ImageView productImageView = new ImageView(getContext());
            Glide.with(getContext())
                    .load(product.getImage())
                    .into(productImageView);
            productLayout.addView(productImageView);

            // Tạo TextView cho tên sản phẩm
            TextView productNameTextView = new TextView(getContext());
            productNameTextView.setText(product.getName());
            productLayout.addView(productNameTextView);

            // Tạo TextView cho giá sản phẩm
            TextView productPriceTextView = new TextView(getContext());
            productPriceTextView.setText(String.valueOf(product.getDiscountedPrice()) + "đ");
            productLayout.addView(productPriceTextView);

            // Tạo Button "Add to cart"
            Button addToCartButton = new Button(getContext());
            addToCartButton.setText("Add to cart");
            productLayout.addView(addToCartButton);

            // Thêm LinearLayout vào GridLayout
            productGridLayout.addView(productLayout);
        }
    }
}