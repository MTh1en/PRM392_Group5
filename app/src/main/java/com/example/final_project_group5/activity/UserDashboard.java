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
            Fragment fragment = null;
            if (item.getItemId() == R.id.home) {
                fragment = new HomeFragment();
            } else if (item.getItemId() == R.id.categories) {
                fragment = new CategoriesFragment();
            } else if (item.getItemId() == R.id.cart) {
                fragment = new CartFragment();
            } else if (item.getItemId() == R.id.profile) {
                fragment = new ProfileFragment();
            }

            if (fragment != null) {
                // Xóa backstack trước khi thay thế fragment mới
                clearBackStack();
                replaceFragment(fragment);
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

    private void clearBackStack() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            fragmentManager.popBackStack();
        }
    }
}