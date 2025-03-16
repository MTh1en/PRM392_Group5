package com.example.final_project_group5.fragment.user;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.final_project_group5.R;
import com.example.final_project_group5.adapter.CartAdapter;
import com.example.final_project_group5.entity.Cart;
import com.example.final_project_group5.entity.Product;
import com.example.final_project_group5.repository.ProductRepo;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {
    private ListView listViewCart;

    private TextView tvSubtotal, tvShippingFee, tvTotal, tvEmptyCart;

    private Button btnCheckout;
    private CartAdapter cartAdapter;
    private List<Cart> cartItems = ProductFragment.cartItems; // Lấy danh sách từ ProductFragment
    private List<Product> productList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        listViewCart = view.findViewById(R.id.listViewCart);
        tvSubtotal = view.findViewById(R.id.tvSubtotal);
        tvShippingFee = view.findViewById(R.id.tvShippingFee);
        tvTotal = view.findViewById(R.id.tvTotal);
        tvEmptyCart = view.findViewById(R.id.tvEmptyCart);
        btnCheckout = view.findViewById(R.id.btnCheckout);



        fetchProducts();

        btnCheckout.setOnClickListener(v -> Toast.makeText(getContext(), "Chuyển đến trang thanh toán", Toast.LENGTH_SHORT).show());

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d("CartFragment", "Cart Items: " + cartItems.size());
        for (Cart cart : cartItems) {
            Log.d("CartFragment", "Product ID: " + cart.getProductId() + ", Quantity: " + cart.getQuantity());
        }
        updateCartView();
    }


    private void fetchProducts() {
        Call<List<Product>> call = ProductRepo.getProductService().getAllProducts();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());


                    // Log danh sách sản phẩm đã lấy được
                    for (Product product : productList) {
                        Log.d("CartFragment", "Fetched Product -> ID: " + product.getId() + ", Name: " + product.getName());
                    }

                    updateCartView();

                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {

                Toast.makeText(getContext(), "Lỗi kết nối khi lấy sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateCartView() {
        if (productList.isEmpty()) {
            Log.e("CartFragment", "Product list is empty, waiting for update...");
            return;

        }

        Log.d("CartFragment", "Updating cart view with products: " + productList.size());

        cartAdapter = new CartAdapter(getContext(), cartItems, productList, this::updateTotal);
        listViewCart.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();
        updateTotal();
    }

    private void updateTotal() {
        double subtotal = 0.0;
        for (Cart cart : cartItems) {

            subtotal += cart.getQuantity() * 50000; // Giá mặc định

        }
        tvSubtotal.setText(subtotal + "đ");
        tvShippingFee.setText("50000đ");
        tvTotal.setText((subtotal + 50000) + "đ");
    }
}