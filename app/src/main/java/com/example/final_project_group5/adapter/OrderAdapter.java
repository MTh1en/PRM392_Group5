package com.example.final_project_group5.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.final_project_group5.R;
import com.example.final_project_group5.entity.Order;
import com.example.final_project_group5.entity.OrderDetail;

import java.util.List;

public class OrderAdapter extends BaseAdapter {
    private Context context;
    private List<Order> orderList;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @Override
    public int getCount() {
        return orderList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate layout nếu convertView là null
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        }

        // Lấy đơn hàng tại vị trí position
        Order order = orderList.get(position);

        // Ánh xạ các view
        ImageView ivProductImage = convertView.findViewById(R.id.ivProductImage);
        TextView tvOrderIdDate = convertView.findViewById(R.id.tvOrderIdDate);
        TextView tvTotalAmount = convertView.findViewById(R.id.tvTotalAmount);
        TextView tvPaymentStatus = convertView.findViewById(R.id.tvPaymentStatus);
        TextView tvShippingStatus = convertView.findViewById(R.id.tvShippingStatus);

        // Tính toán lại totalAmount từ orderDetails
        double calculatedTotalAmount = 0;
        List<OrderDetail> orderDetails = order.getOrderDetails();
        if (orderDetails != null && !orderDetails.isEmpty()) {
            for (OrderDetail detail : orderDetails) {
                calculatedTotalAmount += detail.getDiscountedPrice() * detail.getQuantity();
            }
        }

        // Đặt thông tin đơn hàng
        tvOrderIdDate.setText("Order #" + order.getId() + " - " + order.getOrderDate());
        tvTotalAmount.setText("Total: " + String.format("%.0fđ", calculatedTotalAmount));
        tvPaymentStatus.setText("Payment Status: " + (order.getPaymentStatus() != null ? order.getPaymentStatus() : "N/A"));
        tvShippingStatus.setText("Shipping Status: " + (order.getShippingStatus() != null ? order.getShippingStatus() : "N/A"));

        // Tải hình ảnh sản phẩm (lấy hình ảnh của sản phẩm đầu tiên trong orderDetails)
        if (orderDetails != null && !orderDetails.isEmpty()) {
            OrderDetail firstItem = orderDetails.get(0); // Lấy sản phẩm đầu tiên
            if (firstItem.getProductImage() != null && !firstItem.getProductImage().isEmpty()) {
                Glide.with(context)
                        .load(firstItem.getProductImage())
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_report_image)
                        .into(ivProductImage);
            }
        } else {
            ivProductImage.setImageResource(android.R.drawable.ic_menu_report_image); // Hình ảnh mặc định nếu không có sản phẩm
        }
        if ("Success".equals(order.getPaymentStatus())) {
            tvPaymentStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvPaymentStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }

        if ("Pending".equals(order.getShippingStatus())) {
            tvShippingStatus.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        }

        return convertView;
    }
}