package com.example.final_project_group5;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.final_project_group5.adapter.CartAdapter;
import com.example.final_project_group5.entity.Cart;
import com.example.final_project_group5.entity.Product;
import com.example.final_project_group5.repository.CartRepo;
import com.example.final_project_group5.repository.ProductRepo;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {

    private ListView listViewCart;
    private TextView tvSubtotal;
    private TextView tvShippingFee;
    private TextView tvTotal;
    private Button btnCheckout;
    private CartAdapter cartAdapter;
    private List<Cart> cartItems = new ArrayList<>();
    private List<Product> productList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        listViewCart = view.findViewById(R.id.listViewCart);
        tvSubtotal = view.findViewById(R.id.tvSubtotal);
        tvShippingFee = view.findViewById(R.id.tvShippingFee);
        tvTotal = view.findViewById(R.id.tvTotal);
        btnCheckout = view.findViewById(R.id.btnCheckout);

        fetchCartItems();
        fetchProducts();

        btnCheckout.setOnClickListener(v -> Toast.makeText(getContext(), "Chuyển đến trang thanh toán", Toast.LENGTH_SHORT).show());

        return view;
    }

    private void fetchCartItems() {
        Call<List<Cart>> call = CartRepo.getCartService().getAllCarts();
        call.enqueue(new Callback<List<Cart>>() {
            @Override
            public void onResponse(Call<List<Cart>> call, Response<List<Cart>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cartItems.clear();
                    cartItems.addAll(response.body());
                    updateAdapter();
                    updateTotal();
                } else {
                    Toast.makeText(getContext(), "Không thể lấy dữ liệu giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Cart>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchProducts() {
        Call<List<Product>> call = ProductRepo.getProductService().getAllProducts();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    updateAdapter();
                } else {
                    Toast.makeText(getContext(), "Không thể lấy danh sách sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối khi lấy sản phẩm: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAdapter() {
        if (cartAdapter == null) {
            cartAdapter = new CartAdapter(getContext(), cartItems, productList);
            listViewCart.setAdapter(cartAdapter);
        } else {
            cartAdapter.notifyDataSetChanged();
        }
    }

    private void updateTotal() {
        double subtotal = 0.0;
        for (Cart cart : cartItems) {
            Product product = getProductById(cart.getProductId());
            if (product != null) {
                subtotal += product.getDiscountedPrice() * cart.getQuantity();
            }
        }
        double shippingFee = 50000.0;
        double total = subtotal + shippingFee;

        tvSubtotal.setText(String.format("%.0fđ", subtotal));
        tvShippingFee.setText(String.format("%.0fđ", shippingFee));
        tvTotal.setText(String.format("%.0fđ", total));
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