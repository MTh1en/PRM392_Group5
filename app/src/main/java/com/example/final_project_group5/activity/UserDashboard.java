package com.example.final_project_group5.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.final_project_group5.R;
import com.example.final_project_group5.databinding.ActivityUserDashboardBinding;
import com.example.final_project_group5.fragment.user.CartFragment;
import com.example.final_project_group5.fragment.user.CategoriesFragment;
import com.example.final_project_group5.fragment.user.HomeFragment;
import com.example.final_project_group5.fragment.user.OrderFragmentUser;
import com.example.final_project_group5.fragment.user.ProfileFragment;

public class UserDashboard extends AppCompatActivity {
    ActivityUserDashboardBinding binding;
    String userId;
    ImageView btnCart; // Thêm biến btnCart

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        userId = getIntent().getStringExtra("USER_ID");
        binding = ActivityUserDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Ánh xạ btn_cart từ layout
        btnCart = findViewById(R.id.btn_cart);

        // Mặc định hiển thị HomeFragment
        replaceFragment(new HomeFragment());

        // Bắt sự kiện click cho btn_cart để mở CartFragment
        btnCart.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout1, new CartFragment())
                    .addToBackStack(null) // Cho phép quay lại màn hình trước đó
                    .commit();
        });

        // Xử lý bottom navigation
        binding.bottomNavigationViewUser.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
                return true;
            } else if (item.getItemId() == R.id.categories) {
                replaceFragment(new CategoriesFragment());
                return true;
            } else if (item.getItemId() == R.id.order) {
                replaceFragment(new OrderFragmentUser()); // Hiển thị OrderFragment thay vì CartFragment
                return true;
            } else if (item.getItemId() == R.id.profile) {
                replaceFragment(ProfileFragment.newInstance(userId));
                return true;
            }
            return false;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout1, fragment);
        fragmentTransaction.commit();
    }
}
