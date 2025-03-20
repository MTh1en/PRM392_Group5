package com.example.final_project_group5.fragment.user;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.final_project_group5.R;
import com.example.final_project_group5.adapter.OrderAdapter;
import com.example.final_project_group5.entity.Order;
import com.example.final_project_group5.entity.OrderDetail;
import com.example.final_project_group5.repository.OrderRepo;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderFragmentUser extends Fragment {
    private ListView listViewOrders;
    private List<Order> orderList = new ArrayList<>();
    private OrderAdapter orderAdapter;
    private ImageView btnCart;
    private String userId;

    public OrderFragmentUser() {
    }

    public static OrderFragmentUser newInstance(String userId) {
        OrderFragmentUser fragment = new OrderFragmentUser();
        Bundle args = new Bundle();
        args.putString("USER_ID", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        if (getArguments() != null) {
            userId = getArguments().getString("USER_ID");
            Log.d("CartFragment", "onCreate - Received userId from Bundle: " + userId); // Thêm log này
        }
        Log.d("OrderFragmentUser", "onCreate - Received userId: " + userId);
        listViewOrders = view.findViewById(R.id.listViewOrders);

        listViewOrders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Order selectedOrder = orderList.get(position);
                showOrderDetail(selectedOrder);
            }
        });

        return view;
    }

    private void fetchOrders() {
        if (userId == null) {
            Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<List<Order>> call = OrderRepo.getOrderService().getOrdersByUser(Integer.parseInt(userId));
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderList.clear();
                    orderList.addAll(response.body());
                    orderAdapter = new OrderAdapter(getContext(), orderList);
                    listViewOrders.setAdapter(orderAdapter);
                    orderAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Không tìm thấy đơn hàng!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showOrderDetail(Order order) {
        if (order == null) return;

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Order Details");

            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_order_detail_user, null);

            TextView tvOrderDate = dialogView.findViewById(R.id.tvOrderDate);
            TextView tvShippingAddress = dialogView.findViewById(R.id.tvShippingAddress);
            TextView tvTotalAmount = dialogView.findViewById(R.id.tvTotalAmount);
            TextView tvOrderItems = dialogView.findViewById(R.id.tvOrderItems);

            tvOrderDate.setText("Order Date: " + order.getOrderDate());
            tvShippingAddress.setText("Shipping Address: " + order.getShippingAddress());
            tvTotalAmount.setText("Total Amount: " + order.getTotalAmount());

            StringBuilder orderItems = new StringBuilder();
            List<OrderDetail> orderDetails = order.getOrderDetails(); // Lấy danh sách OrderDetail

            if (orderDetails != null && !orderDetails.isEmpty()) { // Kiểm tra null và rỗng
                for (OrderDetail detail : orderDetails) {
                    orderItems.append("Product: ").append(detail.getProductName()).append("\n");
                    orderItems.append("Quantity: ").append(detail.getQuantity()).append("\n");
                    orderItems.append("Price: ").append(detail.getDiscountedPrice()).append("\n\n");
                }
                tvOrderItems.setText(orderItems.toString());
            } else {
                tvOrderItems.setText("No items in order."); // Hiển thị thông báo khi rỗng
            }

            builder.setView(dialogView);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.show();
        } catch (Exception e) {
            Log.e("OrderFragmentUser", "Error showing order details: " + e.getMessage());
            Toast.makeText(getContext(), "Error showing order details", Toast.LENGTH_SHORT).show();
        }
    }
}