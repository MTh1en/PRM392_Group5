package com.example.final_project_group5.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.final_project_group5.fragment.user.CartFragment;
import com.example.final_project_group5.fragment.user.CategoriesFragment;
import com.example.final_project_group5.fragment.user.HomeFragment;
import com.example.final_project_group5.fragment.user.ProfileFragment;
import com.example.final_project_group5.R;
import com.example.final_project_group5.databinding.ActivityUserDashboardBinding;

public class UserDashboard extends AppCompatActivity {
    ActivityUserDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityUserDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Mặc định hiển thị HomeFragment
        replaceFragment(new HomeFragment());

        binding.bottomNavigationViewUser.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
                return true;
            } else if (item.getItemId() == R.id.categories) {
                replaceFragment(new CategoriesFragment());
                return true;
            } else if (item.getItemId() == R.id.cart) {
                replaceFragment(new CartFragment());
                return true;
            } else if (item.getItemId() == R.id.profile) {
                replaceFragment(new ProfileFragment());
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
