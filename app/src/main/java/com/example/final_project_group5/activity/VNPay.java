package com.example.final_project_group5.activity;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class VNPay {
    public static String createPaymentUrl(long amount, String orderInfo, String orderId) throws Exception {
        Log.d("VNPay", "Amount received: " + amount);
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_Amount = String.valueOf(amount);
        String vnp_CurrCode = "VND";
        String vnp_IpAddr = "127.0.0.1";
        String vnp_Locale = "vn";
        String vnp_OrderType = "250000";
        String vnp_TxnRef = orderId;

        // Sử dụng Calendar với timezone GMT+7 như bài mẫu
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        String vnp_Url = Config.VNPAY_URL;
        String vnp_TmnCode = Config.VNPAY_TMN_CODE;
        String vnp_ReturnUrl = "myapp://vnpay_return";
        String vnp_HashSecret = Config.VNPAY_HASH_SECRET;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", vnp_Amount);
        vnp_Params.put("vnp_CurrCode", vnp_CurrCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo); // Không encode ở đây, sẽ encode trong vòng lặp
        vnp_Params.put("vnp_OrderType", vnp_OrderType);
        vnp_Params.put("vnp_Locale", vnp_Locale);
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        // Encode tất cả giá trị như bài mẫu
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append("=")
                        .append(URLEncoder.encode(fieldValue, "UTF-8"))
                        .append("&");

                query.append(fieldName).append("=")
                        .append(URLEncoder.encode(fieldValue, "UTF-8"))
                        .append("&");
            }
        }

        // Loại bỏ ký tự & cuối cùng như bài mẫu
        String queryUrl = query.substring(0, query.length() - 1);
        String vnp_SecureHash = hmacSHA512(vnp_HashSecret, hashData.toString().substring(0, hashData.length() - 1));

        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        String paymentUrl = vnp_Url + "?" + queryUrl;

        Log.d("VNPay", "Data to hash: " + hashData.toString().substring(0, hashData.length() - 1));
        Log.d("VNPay", "Secure Hash: " + vnp_SecureHash);
        Log.d("VNPay", "Payment URL: " + paymentUrl);

        return paymentUrl;
    }

    public static String hmacSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(secretKeySpec);
            byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder();
            for (byte aByte : bytes) {
                hash.append(String.format("%02x", aByte));
            }
            return hash.toString();
        } catch (Exception e) {
            return ""; // Trả về chuỗi rỗng nếu có lỗi, giống bài mẫu
        }
    }
}