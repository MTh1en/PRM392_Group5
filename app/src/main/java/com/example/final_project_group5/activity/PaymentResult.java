package com.example.final_project_group5.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project_group5.R;

public class PaymentResult extends AppCompatActivity {
    private TextView tvPaymentStatus, tvTransactionNo, tvOrderId;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_result);

        // Ánh xạ các view
        tvPaymentStatus = findViewById(R.id.tv_payment_status);
        tvTransactionNo = findViewById(R.id.tv_transaction_no);
        tvOrderId = findViewById(R.id.tv_order_id);
        btnBack = findViewById(R.id.btn_back);

        // Lấy dữ liệu từ Intent extra
        Intent intent = getIntent();
        String paymentStatus = intent.getStringExtra("PAYMENT_STATUS");
        String transactionNo = intent.getStringExtra("TRANSACTION_NO");
        String orderId = intent.getStringExtra("ORDER_ID");

        Log.d("PaymentResult", "Payment Status: " + paymentStatus);
        Log.d("PaymentResult", "Transaction No: " + transactionNo);
        Log.d("PaymentResult", "Order ID: " + orderId);

        // Kiểm tra và hiển thị dữ liệu
        if (paymentStatus != null) {
            if ("SUCCESS".equals(paymentStatus)) {
                tvPaymentStatus.setText("Thanh toán thành công!");
                tvTransactionNo.setText("Mã giao dịch: " + (transactionNo != null ? transactionNo : "N/A"));
                tvOrderId.setText("Mã đơn hàng: " + (orderId != null ? orderId : "N/A"));
                Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();
            } else if ("FAILED".equals(paymentStatus)) {
                tvPaymentStatus.setText("Thanh toán thất bại!");
                tvTransactionNo.setText("Mã giao dịch: " + (transactionNo != null ? transactionNo : "N/A"));
                tvOrderId.setText("Mã đơn hàng: " + (orderId != null ? orderId : "N/A"));
                Toast.makeText(this, "Thanh toán thất bại hoặc bị hủy!", Toast.LENGTH_LONG).show();
            } else if ("ERROR".equals(paymentStatus)) {
                tvPaymentStatus.setText("Lỗi thanh toán!");
                tvTransactionNo.setText("Mã giao dịch: " + (transactionNo != null ? transactionNo : "N/A"));
                tvOrderId.setText("Mã đơn hàng: " + (orderId != null ? orderId : "N/A"));
                Toast.makeText(this, "Có lỗi xảy ra trong quá trình thanh toán!", Toast.LENGTH_LONG).show();
            }
        } else {
            tvPaymentStatus.setText("Lỗi: Không nhận được dữ liệu!");
            tvTransactionNo.setText("Mã giao dịch: N/A");
            tvOrderId.setText("Mã đơn hàng: N/A");
            Toast.makeText(this, "Không nhận được dữ liệu thanh toán!", Toast.LENGTH_LONG).show();
        }

        // Xử lý nút quay lại để về HomeFragment
        btnBack.setOnClickListener(v -> {
            Intent backIntent = new Intent(PaymentResult.this, UserDashboard.class);
            String userId = getIntent().getStringExtra("USER_ID");
            backIntent.putExtra("USER_ID", userId);
            backIntent.putExtra("SHOW_HOME_FRAGMENT", true);
            backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(backIntent);
            finish();
        });
    }
}