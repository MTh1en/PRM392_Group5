package com.example.final_project_group5.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.final_project_group5.R;
import com.example.final_project_group5.api.OrderService;
import com.example.final_project_group5.api.UserService;
import com.example.final_project_group5.entity.Order;
import com.example.final_project_group5.entity.OrderDetail;
import com.example.final_project_group5.entity.User;
import com.example.final_project_group5.repository.OrderRepo;
import com.example.final_project_group5.repository.UserRepo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAdapterAdmin extends RecyclerView.Adapter<OrderAdapterAdmin.OrderViewHolder> {

    private List<Order> orderList;
    private Map<Integer, User> userCache; // Cache để lưu thông tin người dùng đã lấy
    private OnStatusUpdateListener statusUpdateListener;
    private Context context; // Thêm Context để sử dụng trong Toast

    // Interface để thông báo khi trạng thái được cập nhật
    public interface OnStatusUpdateListener {
        void onStatusUpdated();
    }

    public OrderAdapterAdmin(Context context, List<Order> orderList) {
        this.context = context; // Lưu Context từ constructor
        this.orderList = orderList;
        this.userCache = new HashMap<>();
    }

    public void setOnStatusUpdateListener(OnStatusUpdateListener listener) {
        this.statusUpdateListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_admin, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Hiển thị các thuộc tính của Order
        holder.tvOrderId.setText("Order ID: #" + order.getId());
        holder.tvOrderDate.setText("Order Date: " + order.getOrderDate());
        holder.tvTotalAmount.setText("Total Amount: " + String.format("%.0f", order.getTotalAmount())+" VND");
        holder.tvShippingAddress.setText("Shipping Address: " + order.getShippingAddress());
        holder.tvShippingFee.setText("Shipping Fee: " + String.format("%.0f", order.getShippingFee())+" VND");
        holder.tvDiscountAmount.setText("Discount: " + String.format("%.0f", order.getDiscountAmount())+" %");
        holder.tvPaymentStatus.setText("Payment Status: " + order.getPaymentStatus());
        holder.tvShippingStatus.setText("Shipping Status: " + order.getShippingStatus());

        // Hiển thị và cấu hình nút Update Status
        String shippingStatus = order.getShippingStatus();
        if ("Pending".equals(shippingStatus)) {
            holder.btnUpdateStatus.setVisibility(View.VISIBLE);
            holder.btnUpdateStatus.setText("Mark as Success");
        } else if ("Success".equals(shippingStatus)) {
            holder.btnUpdateStatus.setVisibility(View.VISIBLE);
            holder.btnUpdateStatus.setText("Mark as Pending");
        } else {
            holder.btnUpdateStatus.setVisibility(View.GONE);
        }

        // Xử lý sự kiện click nút Update Status
        holder.btnUpdateStatus.setOnClickListener(v -> {
            updateShippingStatus(order, position);
        });

        // Lấy thông tin người dùng từ userId
        int userIdInt = order.getUserId();
        String userId = String.valueOf(userIdInt); // Chuyển int thành String để gọi API
        if (userCache.containsKey(userIdInt)) {
            // Nếu đã có trong cache, hiển thị ngay
            User user = userCache.get(userIdInt);
            holder.tvUserName.setText("User Name: " + user.getName());
            holder.tvUserPhone.setText("Phone: " + user.getPhone());
            holder.pbUserLoading.setVisibility(View.GONE); // Ẩn ProgressBar nếu dùng cache
        } else {
            // Nếu chưa có, gọi API để lấy thông tin người dùng
            holder.pbUserLoading.setVisibility(View.VISIBLE); // Hiển thị ProgressBar
            holder.tvUserName.setText("User Name: Loading...");
            holder.tvUserPhone.setText("Phone: Loading...");

            UserService userService = UserRepo.getUserService();
            Call<User> call = userService.getUser(userId); // Gọi API với userId dạng String
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    holder.pbUserLoading.setVisibility(View.GONE); // Ẩn ProgressBar
                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body();
                        userCache.put(userIdInt, user); // Lưu vào cache với key là userIdInt
                        holder.tvUserName.setText("User Name: " + user.getName());
                        holder.tvUserPhone.setText("Phone: " + user.getPhone());
                    } else {
                        holder.tvUserName.setText("User Name: Unknown");
                        holder.tvUserPhone.setText("Phone: Unknown");
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    holder.pbUserLoading.setVisibility(View.GONE); // Ẩn ProgressBar
                    holder.tvUserName.setText("User Name: Error");
                    holder.tvUserPhone.setText("Phone: Error");
                }
            });
        }

        // Hiển thị chi tiết đơn hàng (orderDetails)
        holder.llOrderDetails.removeAllViews(); // Xóa các view cũ trước khi thêm mới
        if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
            for (OrderDetail detail : order.getOrderDetails()) {
                // Tạo layout ngang cho mỗi OrderDetail
                LinearLayout llDetail = new LinearLayout(holder.itemView.getContext());
                llDetail.setOrientation(LinearLayout.HORIZONTAL);
                llDetail.setPadding(0, 8, 0, 8);

                // Tạo ImageView để hiển thị productImage
                ImageView ivProductImage = new ImageView(holder.itemView.getContext());
                LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(100, 100);
                imageParams.setMargins(0, 0, 16, 0); // Khoảng cách giữa hình ảnh và text
                ivProductImage.setLayoutParams(imageParams);
                ivProductImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

                // Tải hình ảnh bằng Glide
                Glide.with(holder.itemView.getContext())
                        .load(detail.getProductImage())
                        .placeholder(R.drawable.app_logo) // Hình ảnh placeholder nếu cần
                        .error(R.drawable.banner) // Hình ảnh lỗi nếu tải thất bại
                        .into(ivProductImage);

                // Tạo TextView để hiển thị thông tin sản phẩm
                TextView tvDetail = new TextView(holder.itemView.getContext());
                tvDetail.setText("- " + detail.getProductName() + "\n" +
                        "  Quantity: " + detail.getQuantity() + "\n" +
                        "  Original Price: " + String.format("%.0f", detail.getOriginalPrice()) +" VND"+ "\n" +
                        "  Discounted Price: " + String.format("%.0f", detail.getDiscountedPrice())+" VND" + "\n" +
                        "  Description: " + detail.getProductDescription());
                tvDetail.setTextSize(14);
                tvDetail.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.black));
                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                tvDetail.setLayoutParams(textParams);

                // Thêm ImageView và TextView vào layout ngang
                llDetail.addView(ivProductImage);
                llDetail.addView(tvDetail);

                // Thêm layout ngang vào llOrderDetails
                holder.llOrderDetails.addView(llDetail);
            }
        } else {
            TextView tvDetail = new TextView(holder.itemView.getContext());
            tvDetail.setText("No order details available");
            tvDetail.setTextSize(14);
            tvDetail.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray));
            holder.llOrderDetails.addView(tvDetail);
        }
    }

    private void updateShippingStatus(Order order, int position) {
        OrderService orderService = OrderRepo.getOrderService();
        String currentStatus = order.getShippingStatus();
        String newStatus = "Pending".equals(currentStatus) ? "Success" : "Pending"; // Chuyển đổi trạng thái
        order.setShippingStatus(newStatus); // Cập nhật trạng thái mới
        Call<Order> call = orderService.updateOrder(order.getId(), order);

        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful()) {
                    // Cập nhật danh sách và thông báo cho fragment
                    orderList.set(position, response.body());
                    notifyItemChanged(position);
                    Toast.makeText(context, "Shipping status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                    if (statusUpdateListener != null) {
                        statusUpdateListener.onStatusUpdated();
                    }
                } else {
                    Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvUserName, tvUserPhone, tvOrderDate, tvTotalAmount, tvShippingAddress,
                tvShippingFee, tvDiscountAmount, tvPaymentStatus, tvShippingStatus, tvOrderDetailsTitle;
        LinearLayout llOrderDetails;
        ProgressBar pbUserLoading;
        Button btnUpdateStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserPhone = itemView.findViewById(R.id.tvUserPhone);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            tvShippingAddress = itemView.findViewById(R.id.tvShippingAddress);
            tvShippingFee = itemView.findViewById(R.id.tvShippingFee);
            tvDiscountAmount = itemView.findViewById(R.id.tvDiscountAmount);
            tvPaymentStatus = itemView.findViewById(R.id.tvPaymentStatus);
            tvShippingStatus = itemView.findViewById(R.id.tvShippingStatus);
            tvOrderDetailsTitle = itemView.findViewById(R.id.tvOrderDetailsTitle);
            llOrderDetails = itemView.findViewById(R.id.llOrderDetails);
            pbUserLoading = itemView.findViewById(R.id.pbUserLoading);
            btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);
        }
    }
}