package com.example.final_project_group5.fragment.user;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.final_project_group5.R;
import com.example.final_project_group5.adapter.CartAdapter;
import com.example.final_project_group5.entity.Cart;
import com.example.final_project_group5.entity.Order;
import com.example.final_project_group5.entity.OrderDetail;
import com.example.final_project_group5.entity.Product;
import com.example.final_project_group5.repository.OrderRepo;
import com.example.final_project_group5.repository.ProductRepo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {
    private ListView listViewCart;
    private TextView tvTotal, tvEmptyCart;
    private Button btnCheckout;
    private ImageView backButton;
    private LinearLayout footerLayout;
    private CartAdapter cartAdapter;
    private List<Cart> cartItems = ProductFragment.cartItems;
    private List<Product> productList = new ArrayList<>();
    private String userId;
    private boolean isViewInitialized = false;

    public CartFragment() {
    }

    public static CartFragment newInstance(String userId) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        args.putString("USER_ID", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString("USER_ID");
            Log.d("CartFragment", "onCreate - Received userId from Bundle: " + userId); // Thêm log này
        }
        Log.d("CartFragment", "onCreate - Received userId: " + userId);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        listViewCart = view.findViewById(R.id.listViewCart);
        tvTotal = view.findViewById(R.id.tvTotal);
        tvEmptyCart = view.findViewById(R.id.tvEmptyCart);
        btnCheckout = view.findViewById(R.id.btnCheckout);
        backButton = view.findViewById(R.id.backButton);
        footerLayout = view.findViewById(R.id.footerLayout);

        isViewInitialized = true;

        backButton.setOnClickListener(v -> {
            if (getFragmentManager() != null) {
                getFragmentManager().popBackStack();
            }
        });

        btnCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(getContext(), "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
            } else {
                Order order = createOrderFromCart();
                showBillDialog(order);
            }
        });

        fetchProducts();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isViewInitialized) {
            updateCartView();
        }
    }

    private void fetchProducts() {
        Call<List<Product>> call = ProductRepo.getProductService().getAllProducts();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    if (isViewInitialized) {
                        updateCartView();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối khi lấy sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCartView() {
        if (!isViewInitialized || tvTotal == null) {
            Log.e("CartFragment", "View chưa được khởi tạo hoặc bị null");
            return;
        }

        if (cartItems.isEmpty()) {
            tvEmptyCart.setVisibility(View.VISIBLE);
            listViewCart.setVisibility(View.GONE);
            footerLayout.setVisibility(View.GONE);
        } else {
            tvEmptyCart.setVisibility(View.GONE);
            listViewCart.setVisibility(View.VISIBLE);
            footerLayout.setVisibility(View.VISIBLE);

            cartAdapter = new CartAdapter(getContext(), cartItems, productList, this::updateTotal);
            listViewCart.setAdapter(cartAdapter);
            cartAdapter.notifyDataSetChanged();
            updateTotal();
        }
    }

    private Product getProductById(int productId) {
        for (Product product : productList) {
            if (Integer.parseInt(product.getId()) == productId) {
                return product;
            }
        }
        return null;
    }

    private void updateTotal() {
        if (!isViewInitialized || tvTotal == null) {
            Log.e("CartFragment", "updateTotal: tvTotal là null");
            return;
        }

        double total = 0.0;
        for (Cart cart : cartItems) {
            Product product = getProductById(cart.getProductId());
            if (product != null) {
                total += cart.getQuantity() * product.getDiscountedPrice();
            }
        }
        tvTotal.setText(String.format("%.0fđ", total));
    }

    private Order createOrderFromCart() {
        Order order = new Order();

        Log.d("CartFragment", "createOrderFromCart - userId: " + userId);
        if (userId == null) {
            Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            return null;
        }
        order.setUserId(Integer.parseInt(userId));

        order.setOrderDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        double total = 0.0;
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (Cart cart : cartItems) {
            Product product = getProductById(cart.getProductId());
            if (product != null) {
                OrderDetail detail = new OrderDetail();
                detail.setId(product.getId());
                detail.setProductName(product.getName());
                detail.setProductDescription(product.getDescription());
                detail.setProductImage(product.getImage());
                detail.setQuantity(cart.getQuantity());
                detail.setOriginalPrice(product.getOriginalPrice());
                detail.setDiscountedPrice(product.getDiscountedPrice());
                detail.setDiscountAmount((int) (product.getOriginalPrice() - product.getDiscountedPrice()));
                orderDetails.add(detail);
                total += cart.getQuantity() * product.getDiscountedPrice();
            }
        }

        order.setOrderDetails(orderDetails);
        order.setTotalAmount(total);

        return order;
    }

    private void showBillDialog(Order order) {
        if (order == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Order Summary");

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_order_details, null);

        EditText etShippingAddress = dialogView.findViewById(R.id.etShippingAddress);

        builder.setView(dialogView);

        StringBuilder billContent = new StringBuilder();
        billContent.append("Order Date: ").append(order.getOrderDate()).append("\n");
        billContent.append("Total Amount: ").append(String.format("%.0fđ", order.getTotalAmount())).append("\n");
        billContent.append("Items:\n");
        for (OrderDetail detail : order.getOrderDetails()) {
            billContent.append("- ").append(detail.getProductName())
                    .append(" (x").append(detail.getQuantity()).append("): ")
                    .append(String.format("%.0fđ", detail.getDiscountedPrice() * detail.getQuantity())).append("\n");
        }
        builder.setMessage(billContent.toString());

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            order.setShippingAddress(etShippingAddress.getText().toString());
            order.setShippingFee(0.0f); // Giá trị mặc định hoặc bỏ qua
            order.setDiscountAmount(0.0f); // Giá trị mặc định hoặc bỏ qua
            order.setPaymentStatus("Pending"); // Giá trị mặc định hoặc bỏ qua
            order.setShippingStatus("Pending"); // Giá trị mặc định hoặc bỏ qua
            submitOrder(order);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void submitOrder(Order order) {
        Call<Order> call = OrderRepo.getOrderService().createOrder(order);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Order created successfully!", Toast.LENGTH_SHORT).show();
                    cartItems.clear();
                    updateCartView();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout1, OrderFragmentUser.newInstance(userId))
                            .addToBackStack(null)
                            .commit();
                } else {
                    Toast.makeText(getContext(), "Failed to create order", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}