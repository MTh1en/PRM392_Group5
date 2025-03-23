package com.example.final_project_group5.fragment.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.final_project_group5.R;
import com.example.final_project_group5.activity.Config;
import com.example.final_project_group5.activity.Login;
import com.example.final_project_group5.activity.PaymentResult;
import com.example.final_project_group5.activity.VNPay;
import com.example.final_project_group5.adapter.CartAdapter;
import com.example.final_project_group5.entity.Cart;
import com.example.final_project_group5.entity.Order;
import com.example.final_project_group5.entity.OrderDetail;
import com.example.final_project_group5.entity.Product;
import com.example.final_project_group5.repository.CartRepo;
import com.example.final_project_group5.repository.OrderRepo;
import com.example.final_project_group5.repository.ProductRepo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {
    private ListView listViewCart;
    private TextView tvTotal, tvEmptyCart;
    private Button btnCheckout;
    private LinearLayout footerLayout;
    private WebView webView;
    private CartAdapter cartAdapter;
    private List<Cart> cartItems = new ArrayList<>();
    private List<Product> productList = new ArrayList<>();
    private String userId;
    private boolean isViewInitialized = false;
    private Order pendingOrder;
    private String currentOrderId;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        if (getArguments() != null) {
            userId = getArguments().getString("USER_ID");
        }
        Log.d("CartFragment", "userId: " + userId);

        listViewCart = view.findViewById(R.id.listViewCart);
        tvTotal = view.findViewById(R.id.tvTotal);
        tvEmptyCart = view.findViewById(R.id.tvEmptyCart);
        btnCheckout = view.findViewById(R.id.btnCheckout);
        footerLayout = view.findViewById(R.id.footerLayout);
        webView = view.findViewById(R.id.webview_payment);

        isViewInitialized = true;

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains("myapp://vnpay_return")) { // Sửa thành myapp://vnpay_return
                    handlePaymentResult(url);
                }
            }

            private void handlePaymentResult(String url) {
                webView.setVisibility(View.GONE);
                listViewCart.setVisibility(View.VISIBLE);
                footerLayout.setVisibility(View.VISIBLE);

                String paymentStatus = "FAILED";
                String transactionNo = "N/A";
                String orderId = currentOrderId;
                String vnp_SecureHash = null;

                if (url.contains("vnp_ResponseCode=00")) {
                    paymentStatus = "SUCCESS";
                    transactionNo = getQueryParam(url, "vnp_TransactionNo");
                    orderId = getQueryParam(url, "vnp_TxnRef");
                    vnp_SecureHash = getQueryParam(url, "vnp_SecureHash");

                    Map<String, String> responseParams = getAllQueryParams(url);

                    if (vnp_SecureHash != null && validateSignature(responseParams, vnp_SecureHash)) {
                        Log.d("CartFragment", "Chữ ký hợp lệ");
                        // Thanh toán thành công, xóa giỏ hàng trên MockAPI
                        deleteCartAfterCreateOrder(cartItems);
                        // Gửi đơn hàng lên server
                        if (pendingOrder != null) {
                            submitOrder(pendingOrder);
                        }
                    } else {
                        Log.e("CartFragment", "Chữ ký không hợp lệ");
                        paymentStatus = "FAILED";
                    }
                } else if (url.contains("vnp_ResponseCode") || url.contains("error") || url.contains("cancel")) {
                    paymentStatus = "FAILED";
                }

                Intent intent = new Intent(getActivity(), PaymentResult.class);
                intent.putExtra("PAYMENT_STATUS", paymentStatus);
                intent.putExtra("TRANSACTION_NO", transactionNo != null ? transactionNo : "N/A");
                intent.putExtra("ORDER_ID", orderId != null ? orderId : currentOrderId);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                webView.setVisibility(View.GONE);
                listViewCart.setVisibility(View.VISIBLE);
                footerLayout.setVisibility(View.VISIBLE);

                Intent intent = new Intent(getActivity(), PaymentResult.class);
                intent.putExtra("PAYMENT_STATUS", "ERROR");
                intent.putExtra("TRANSACTION_NO", "N/A");
                intent.putExtra("ORDER_ID", currentOrderId);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            }
        });

        btnCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(getContext(), "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
            } else {
                pendingOrder = createOrderFromCart();
                if (pendingOrder != null) {
                    double originalTotal = pendingOrder.getTotalAmount() / 100; // Tính giá trị gốc từ totalAmount
                    showBillDialog(pendingOrder, originalTotal);
                }
            }
        });

        fetchProducts();
        return view;
    }

    private void deleteCartAfterCreateOrder(List<Cart> cartItems) {
        for (Cart cart : cartItems) {
            CartRepo.getCartService().deleteCart(cart.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d("CartFragment", "Đã xóa mục giỏ hàng trên MockAPI: " + cart.getId());
                    } else {
                        Log.e("CartFragment", "Lỗi khi xóa mục giỏ hàng trên MockAPI: " + cart.getId());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("CartFragment", "Lỗi kết nối khi xóa mục giỏ hàng: " + t.getMessage());
                }
            });
        }

        // Xóa giỏ hàng trên giao diện
        cartItems.clear();
        updateCartView();
    }

    private void submitOrder(Order order) {
        Call<Order> call = OrderRepo.getOrderService().createOrder(order);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Order created successfully!", Toast.LENGTH_SHORT).show();
                    // Chuyển đến OrderFragmentUser
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
                Toast.makeText(getContext(), "Lỗi kết nối khi tạo đơn hàng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getQueryParam(String url, String param) {
        try {
            String[] urlParts = url.split("\\?");
            if (urlParts.length > 1) {
                String query = urlParts[1];
                for (String paramPair : query.split("&")) {
                    String[] pair = paramPair.split("=");
                    if (pair.length > 1 && pair[0].equals(param)) {
                        return pair[1];
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isViewInitialized) {
            if (userId == null || userId.isEmpty()) {
                Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
                getActivity().finish();
                return;
            }
            fetchCartByUserId(Integer.parseInt(userId));
            updateCartView();
        }
    }

    private void fetchCartByUserId(int userId) {
        Call<List<Cart>> call = CartRepo.getCartService().getCartsByUser(userId);
        call.enqueue(new Callback<List<Cart>>() {
            @Override
            public void onResponse(Call<List<Cart>> call, Response<List<Cart>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cartItems.clear();
                    cartItems.addAll(response.body());
                    updateCartView();
                } else {
                    Toast.makeText(getContext(), "Giỏ hàng chưa có gì", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Cart>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối khi lấy giỏ hàng", Toast.LENGTH_SHORT).show();
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

        double originalTotal = 0.0; // Giá trị gốc
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
                originalTotal += cart.getQuantity() * product.getDiscountedPrice(); // Tính giá trị gốc
            }
        }

        order.setOrderDetails(orderDetails);
        order.setTotalAmount(originalTotal * 100); // Lưu giá trị đã nhân 100 vào Order

        return order;
    }

    private void showBillDialog(Order order, double originalTotal) {
        if (order == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Order Summary");

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_order_details, null);
        EditText etShippingAddress = dialogView.findViewById(R.id.etShippingAddress);
        builder.setView(dialogView);

        StringBuilder billContent = new StringBuilder();
        billContent.append("Order Date: ").append(order.getOrderDate()).append("\n");
        billContent.append("Total Amount: ").append(String.format("%.0fđ", originalTotal)).append("\n"); // Hiển thị giá trị gốc
        billContent.append("Items:\n");
        for (OrderDetail detail : order.getOrderDetails()) {
            billContent.append("- ").append(detail.getProductName())
                    .append(" (x").append(detail.getQuantity()).append("): ")
                    .append(String.format("%.0fđ", detail.getDiscountedPrice() * detail.getQuantity())).append("\n"); // Hiển thị giá trị gốc
        }
        builder.setMessage(billContent.toString());

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            order.setShippingAddress(etShippingAddress.getText().toString());
            order.setShippingFee(0.0f);
            order.setDiscountAmount(0.0f);
            order.setPaymentStatus("Pending");
            order.setShippingStatus("Pending");
            initiateVNPayPayment(order);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void initiateVNPayPayment(Order order) {
        try {
            long amount = (long) order.getTotalAmount();
            String orderInfo = "Thanh toán đơn hàng của " + userId;
            currentOrderId = "ORDER_" + System.currentTimeMillis();
            String paymentUrl = VNPay.createPaymentUrl(amount, orderInfo, currentOrderId);
            Log.d("CartFragment", "Payment URL: " + paymentUrl);
            webView.loadUrl(paymentUrl);
            webView.setVisibility(View.VISIBLE);
            listViewCart.setVisibility(View.GONE);
            footerLayout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi khi tạo URL thanh toán: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private Map<String, String> getAllQueryParams(String url) {
        Map<String, String> params = new HashMap<>();
        try {
            String[] urlParts = url.split("\\?");
            if (urlParts.length > 1) {
                String query = urlParts[1];
                for (String paramPair : query.split("&")) {
                    String[] pair = paramPair.split("=");
                    if (pair.length > 1) {
                        params.put(pair[0], pair[1]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    private boolean validateSignature(Map<String, String> params, String vnp_SecureHash) {
        try {
            String vnp_HashSecret = Config.VNPAY_HASH_SECRET;

            params.remove("vnp_SecureHash");

            List<String> fieldNames = new ArrayList<>(params.keySet());
            Collections.sort(fieldNames);

            StringBuilder dataToHash = new StringBuilder();
            for (String fieldName : fieldNames) {
                String fieldValue = params.get(fieldName);
                if (dataToHash.length() > 0) {
                    dataToHash.append("&");
                }
                dataToHash.append(fieldName).append("=").append(fieldValue);
            }

            String generatedHash = VNPay.hmacSHA512(vnp_HashSecret, dataToHash.toString());

            return generatedHash.equals(vnp_SecureHash);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}