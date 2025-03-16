package com.example.final_project_group5.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.final_project_group5.R;
import com.example.final_project_group5.entity.Cart;
import com.example.final_project_group5.entity.Product;

import java.util.List;

public class CartAdapter extends BaseAdapter {
    private Context context;
    private List<Cart> cartList;
    private List<Product> productList;
    private OnCartUpdateListener cartUpdateListener;

    public CartAdapter(Context context, List<Cart> cartList, List<Product> productList, OnCartUpdateListener cartUpdateListener) {
        this.context = context;
        this.cartList = cartList;
        this.productList = productList;
        this.cartUpdateListener = cartUpdateListener;
    }

    @Override
    public int getCount() {
        return cartList.size();
    }

    @Override
    public Object getItem(int position) {
        return cartList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Cart cart = cartList.get(position);
        Product product = getProductById(cart.getProductId());

        if (product != null) {
            // Hiển thị dữ liệu sản phẩm trong giỏ hàng
            Glide.with(context).load(product.getImage()).into(holder.ivProductImage);
            holder.tvProductName.setText(product.getName());
            holder.tvProductSpecs.setText(product.getDescription());
            holder.tvOriginalPrice.setText(String.format("%.0fđ", product.getOriginalPrice()));
            holder.tvDiscountedPrice.setText(String.format("%.0fđ", product.getDiscountedPrice()));
            holder.tvDiscount.setText(String.format("-%d%%", (int) (product.getDiscountPercentage() * 100)));
            holder.tvQuantity.setText(String.valueOf(cart.getQuantity()));

            // Sự kiện giảm số lượng
            holder.btnDecrease.setOnClickListener(v -> {
                if (cart.getQuantity() > 1) {
                    cart.setQuantity(cart.getQuantity() - 1);
                    notifyDataSetChanged();
                    cartUpdateListener.onCartUpdated();
                }
            });

            // Sự kiện tăng số lượng
            holder.btnIncrease.setOnClickListener(v -> {
                cart.setQuantity(cart.getQuantity() + 1);
                notifyDataSetChanged();
                cartUpdateListener.onCartUpdated();
            });

            // Xóa sản phẩm khỏi giỏ hàng
            holder.btnDelete.setOnClickListener(v -> {
                cartList.remove(position);
                notifyDataSetChanged();
                cartUpdateListener.onCartUpdated();
            });
        }

        return convertView;
    }

    // Tìm sản phẩm theo ID
    private Product getProductById(int productId) {
        for (Product product : productList) {
            if (Integer.parseInt(product.getId()) == productId) {
                return product;
            }
        }
        return null;
    }


    // ViewHolder giúp tối ưu hiệu suất ListView
    private static class ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductSpecs, tvOriginalPrice, tvDiscountedPrice, tvDiscount, tvQuantity;
        Button btnDecrease, btnIncrease;
        ImageButton btnDelete;

        ViewHolder(View view) {
            ivProductImage = view.findViewById(R.id.ivProductImage);
            tvProductName = view.findViewById(R.id.tvProductName);
            tvProductSpecs = view.findViewById(R.id.tvProductSpecs);
            tvOriginalPrice = view.findViewById(R.id.tvOriginalPrice);
            tvDiscountedPrice = view.findViewById(R.id.tvDiscountedPrice);
            tvDiscount = view.findViewById(R.id.tvDiscount);
            tvQuantity = view.findViewById(R.id.tvQuantity);
            btnDecrease = view.findViewById(R.id.btnDecrease);
            btnIncrease = view.findViewById(R.id.btnIncrease);
            btnDelete = view.findViewById(R.id.btnDelete);
        }
    }

    public interface OnCartUpdateListener {
        void onCartUpdated();
    }
}
