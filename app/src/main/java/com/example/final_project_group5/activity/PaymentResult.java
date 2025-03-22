package com.example.final_project_group5.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project_group5.R;

public class PaymentResult extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_result); // Đảm bảo có layout này

        Uri uri = getIntent().getData();
        if (uri != null) {
            String responseCode = uri.getQueryParameter("vnp_ResponseCode");
            String transactionNo = uri.getQueryParameter("vnp_TransactionNo");
            String orderId = uri.getQueryParameter("vnp_TxnRef");

            if ("00".equals(responseCode)) {
                Toast.makeText(this, "Thanh toán thành công! Mã giao dịch: " + transactionNo, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Thanh toán thất bại hoặc bị hủy!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Không nhận được dữ liệu thanh toán!", Toast.LENGTH_LONG).show();
        }
    }
}