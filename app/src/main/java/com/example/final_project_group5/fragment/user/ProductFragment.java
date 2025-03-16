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
import com.example.final_project_group5.api.ProductService;
import com.example.final_project_group5.entity.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductFragment extends Fragment {

    private String categoryName;
    private GridLayout productGridLayout;

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
        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

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
                } else {
                    Log.e("ProductFragment", "API response failed: " + response.code());
                    Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                t.printStackTrace();
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
            productPriceTextView.setText(product.getDiscountedPrice() + "đ");
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

            productCard.setOnClickListener(v -> {
                if (product.getId() == null) {
                    Toast.makeText(getContext(), "Product ID is missing", Toast.LENGTH_SHORT).show();
                    Log.e("ProductFragment", "Product ID is null for product: " + product.getName());
                    return;
                }

                ProductDetailFragment productDetailFragment = new ProductDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("productId", product.getId());
                productDetailFragment.setArguments(bundle);

                FragmentManager fragmentManager = getParentFragmentManager();
                try {
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_layout1, productDetailFragment)
                            .addToBackStack(null)
                            .commit();
                    Log.d("ProductFragment", "Navigating to ProductDetailFragment with ID: " + product.getId());
                } catch (Exception e) {
                    Log.e("ProductFragment", "Fragment transaction failed: " + e.getMessage());
                    Toast.makeText(getContext(), "Error navigating to product details", Toast.LENGTH_SHORT).show();
                }
            });

            productGridLayout.addView(productCard);
        }
    }
}