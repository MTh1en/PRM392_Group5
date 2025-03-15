package com.example.final_project_group5.fragment.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.final_project_group5.R;

public class ProductDetailFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView productName = view.findViewById(R.id.productName);
        TextView productPrice = view.findViewById(R.id.productPrice);
        TextView originalPrice = view.findViewById(R.id.originalPrice);
        TextView productDescription = view.findViewById(R.id.productDescription);
        ImageView productImage = view.findViewById(R.id.productImage);

        // Gán dữ liệu giả lập (sau này thay bằng API)
        productName.setText("NVIDIA GeForce RTX 4070");
        productPrice.setText("13.999.000đ");
        originalPrice.setText("15.999.000đ");
        productDescription.setText("NVIDIA GeForce RTX 4070 mang đến hiệu năng tuyệt vời...");
        productImage.setImageResource(R.drawable.banner);
    }
}