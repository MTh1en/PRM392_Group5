package com.example.final_project_group5.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.final_project_group5.fragment.admin.OrderFragment;
import com.example.final_project_group5.fragment.admin.ProductAdminFragment;
import com.example.final_project_group5.fragment.user.ProductFragment;
import com.example.final_project_group5.R;
import com.example.final_project_group5.fragment.admin.UserFragment;
import com.example.final_project_group5.databinding.ActivityAdminDashboardBinding;

public class AdminDashboard extends AppCompatActivity {
    private ActivityAdminDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Khởi tạo View Binding
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Thiết lập Toolbar
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Ẩn tiêu đề mặc định
        }

        // Ánh xạ và xử lý nút Logout
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển về màn hình Login và kết thúc Activity hiện tại
                Intent intent = new Intent(AdminDashboard.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        // Thiết lập Fragment mặc định
        replaceFragment(new UserFragment());

        // Xử lý BottomNavigationView
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.users) {
                replaceFragment(new UserFragment());
                return true;
            } else if (item.getItemId() == R.id.orders) {
                replaceFragment(new OrderFragment());
                return true;
            } else if (item.getItemId() == R.id.products) {
                replaceFragment(new ProductAdminFragment());
                return true;
            }
            return false;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}