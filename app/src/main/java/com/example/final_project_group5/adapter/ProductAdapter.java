package com.example.final_project_group5.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.final_project_group5.R;
import com.example.final_project_group5.entity.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_admin, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Hiển thị thông tin sản phẩm
        holder.tvProductName.setText(product.getName());
        holder.tvProductDescription.setText(product.getDescription());
        holder.tvOriginalPrice.setText("$" + String.format("%.2f", product.getOriginalPrice()));
        holder.tvDiscountedPriceAdmin.setText("$" + String.format("%.2f", product.getDiscountedPrice()));
        holder.tvDiscountPercentage.setText(product.getDiscountPercentage() + "% OFF");
        holder.tvAverageRating.setText(String.valueOf(product.getAverageRating()));
        holder.tvRatingCount.setText("(" + product.getRatingCount() + " reviews)");
        holder.tvBrand.setText("Brand: " + product.getBrand());
        holder.tvCategory.setText("Category: " + product.getCategory());
        holder.tvStock.setText("Stock: " + product.getStock());

        // Tải hình ảnh bằng Glide
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(product.getImage())
                    .placeholder(R.drawable.app_logo)
                    .error(R.drawable.banner)
                    .into(holder.ivProductImage);
        } else {
            holder.ivProductImage.setImageResource(R.drawable.app_logo);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductDescription, tvOriginalPrice, tvDiscountedPriceAdmin,
                tvDiscountPercentage, tvRatingCount, tvAverageRating, tvBrand, tvStock, tvCategory;
        ImageView ivProductImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductDescription = itemView.findViewById(R.id.tvProductDescription);
            tvOriginalPrice = itemView.findViewById(R.id.tvOriginalPrice);
            tvDiscountedPriceAdmin = itemView.findViewById(R.id.tvDiscountedPriceAdmin);
            tvDiscountPercentage = itemView.findViewById(R.id.tvDiscountPercentage);
            tvRatingCount = itemView.findViewById(R.id.tvRatingCount);
            tvAverageRating = itemView.findViewById(R.id.tvAverageRating);
            tvBrand = itemView.findViewById(R.id.tvBrand);
            tvStock = itemView.findViewById(R.id.tvStock);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
        }
    }
}