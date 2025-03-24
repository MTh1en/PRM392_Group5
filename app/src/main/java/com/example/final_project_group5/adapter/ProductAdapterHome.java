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

public class ProductAdapterHome extends RecyclerView.Adapter<ProductAdapterHome.ProductViewHolder> {
    private List<Product> productList;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductAdapterHome(List<Product> productList) {
        this.productList = productList;
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_home, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Hiển thị thông tin sản phẩm
        holder.tvProductName.setText(product.getName());
        holder.tvDiscountedPrice.setText("$" + String.format("%.2f", product.getDiscountedPrice()));
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

        // Xử lý sự kiện click vào sản phẩm
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvDiscountedPrice, tvStock;
        ImageView ivProductImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvDiscountedPrice = itemView.findViewById(R.id.tvDiscountedPrice);
            tvStock = itemView.findViewById(R.id.tvStock);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
        }
    }
}