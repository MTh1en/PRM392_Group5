package com.example.final_project_group5.adapter;

import android.content.Context;
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

    public CartAdapter(Context context, List<Cart> cartList, List<Product> productList) {
        this.context = context;
        this.cartList = cartList;
        this.productList = productList;
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
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        }

        ImageView ivProductImage = convertView.findViewById(R.id.ivProductImage);
        TextView tvProductName = convertView.findViewById(R.id.tvProductName);
        TextView tvProductSpecs = convertView.findViewById(R.id.tvProductSpecs);
        TextView tvOriginalPrice = convertView.findViewById(R.id.tvOriginalPrice);
        TextView tvDiscountedPrice = convertView.findViewById(R.id.tvDiscountedPrice);
        TextView tvDiscount = convertView.findViewById(R.id.tvDiscount);
        TextView tvQuantity = convertView.findViewById(R.id.tvQuantity);
        Button btnDecrease = convertView.findViewById(R.id.btnDecrease);
        Button btnIncrease = convertView.findViewById(R.id.btnIncrease);
        ImageButton btnDelete = convertView.findViewById(R.id.btnDelete);

        Cart cart = cartList.get(position);
        Product product = getProductById(cart.getProductId());

        if (product != null) {
            Glide.with(context).load(product.getImage()).into(ivProductImage);
            tvProductName.setText(product.getName());
            tvProductSpecs.setText(product.getDescription());
            tvOriginalPrice.setText(String.format("%.0fđ", product.getOriginalPrice()));
            tvDiscountedPrice.setText(String.format("%.0fđ", product.getDiscountedPrice()));
            tvDiscount.setText(String.format("-%d%%", (int) (product.getDiscountPercentage() * 100)));
            tvQuantity.setText(String.valueOf(cart.getQuantity()));

            btnDecrease.setOnClickListener(v -> {
                if (cart.getQuantity() > 1) {
                    cart.setQuantity(cart.getQuantity() - 1);
                    tvQuantity.setText(String.valueOf(cart.getQuantity()));
                    notifyDataSetChanged();
                }
            });

            btnIncrease.setOnClickListener(v -> {
                cart.setQuantity(cart.getQuantity() + 1);
                tvQuantity.setText(String.valueOf(cart.getQuantity()));
                notifyDataSetChanged();
            });

            btnDelete.setOnClickListener(v -> {
                cartList.remove(position);
                notifyDataSetChanged();
            });
        }

        return convertView;
    }

    private Product getProductById(int productId) {
        for (Product product : productList) {
            if (product.getId().equals(String.valueOf(productId))) {
                return product;
            }
        }
        return null;
    }
}