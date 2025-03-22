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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.final_project_group5.R;
import com.example.final_project_group5.api.ApiClient;
import com.example.final_project_group5.api.CartService;
import com.example.final_project_group5.entity.Cart;
import com.example.final_project_group5.entity.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
            // Hiển thị thông tin sản phẩm trong giỏ hàng
            Glide.with(context).load(product.getImage()).into(holder.ivProductImage);
            holder.tvProductName.setText(product.getName());

            // Tính toán giá giảm
            double originalPrice = product.getOriginalPrice();
            int discountPercentage = getDiscountByProductId(cart.getProductId());
            double discountedPrice = originalPrice * (1 - discountPercentage / 100.0);
            Log.d("CartAdapter", "Product: " + product.getName() +
                    ", Original Price: " + originalPrice +
                    ", Discount: " + discountPercentage + "%" +
                    ", Discounted Price: " + discountedPrice);

            holder.tvOriginalPrice.setText(String.format("%.0fđ", originalPrice));
            holder.tvDiscountedPrice.setText(String.format("%.0fđ", discountedPrice));
            holder.tvDiscount.setText(String.format("-%d%%", discountPercentage));

            holder.tvQuantity.setText(String.valueOf(cart.getQuantity()));

            // Giảm số lượng
            holder.btnDecrease.setOnClickListener(v -> {
                if (cart.getQuantity() > 1) {
                    cart.setQuantity(cart.getQuantity() - 1);
                    updateCartQuantity(cart);
                }
            });

            // Tăng số lượng
            holder.btnIncrease.setOnClickListener(v -> {
                cart.setQuantity(cart.getQuantity() + 1);
                updateCartQuantity(cart);
            });

            // Xóa sản phẩm khỏi giỏ hàng
            holder.btnDelete.setOnClickListener(v -> {
                Cart cartItem = cartList.get(position);
                CartService cartService = ApiClient.getClient().create(CartService.class);
                Call<Void> call = cartService.deleteCart(cartItem.getId());

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            cartList.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Xóa sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                            if (cartUpdateListener != null) {
                                cartUpdateListener.onCartUpdated();
                            }
                        } else {
                            Toast.makeText(context, "Lỗi khi xóa sản phẩm!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, "Lỗi kết nối mạng!", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }

        return convertView;
    }

    // Lấy sản phẩm theo ID
    private Product getProductById(int productId) {
        for (Product product : productList) {
            if (Integer.parseInt(product.getId()) == productId) {
                return product;
            }
        }
        return null;
    }

    // Lấy discount theo ID sản phẩm
    public int getDiscountByProductId(int productId) {
        Product product = getProductById(productId);
        if (product != null) {
            return (int) product.getDiscountPercentage();
        }
        return 0; // Trả về 0 nếu không tìm thấy sản phẩm
    }

    // Cập nhật số lượng giỏ hàng qua API
    private void updateCartQuantity(Cart cart) {
        CartService cartService = ApiClient.getClient().create(CartService.class);
        Call<Cart> call = cartService.updateCart(cart.getId(), cart);

        call.enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful()) {
                    notifyDataSetChanged();
                    cartUpdateListener.onCartUpdated();
                    Toast.makeText(context, "Cập nhật số lượng thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Lỗi khi cập nhật số lượng!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ViewHolder giúp tối ưu hiệu suất ListView
    private static class ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvOriginalPrice, tvDiscountedPrice, tvDiscount, tvQuantity;
        Button btnDecrease, btnIncrease;
        ImageButton btnDelete;

        ViewHolder(View view) {
            ivProductImage = view.findViewById(R.id.ivProductImage);
            tvProductName = view.findViewById(R.id.tvProductName);
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