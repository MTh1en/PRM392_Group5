package com.example.final_project_group5.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
    ImageView btnCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Lấy userId từ Intent nếu có (khi chuyển từ Login)
        userId = getIntent().getStringExtra("USER_ID");
        Log.d("UserDashboard", "Received userId from Intent: " + userId);

        binding = ActivityUserDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        btnCart = findViewById(R.id.btn_cart);

        replaceFragment(new HomeFragment());

        btnCart.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            String userId = sharedPreferences.getString("userId", null);

            Log.d("UserDashboard", "Opening CartFragment with userId: " + userId);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout1, CartFragment.newInstance(userId)) // Kiểm tra dòng này
                    .addToBackStack(null)
                    .commit();
        });

        binding.bottomNavigationViewUser.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(HomeFragment.newInstance(userId));
                return true;
            } else if (item.getItemId() == R.id.categories) {
                replaceFragment(CategoriesFragment.newInstance(userId));
                return true;
            } else if (item.getItemId() == R.id.order) {
                replaceFragment(OrderFragmentUser.newInstance(userId));
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