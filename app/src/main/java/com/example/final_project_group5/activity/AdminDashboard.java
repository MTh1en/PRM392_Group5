package com.example.final_project_group5.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.final_project_group5.fragment.admin.OrderFragment;
import com.example.final_project_group5.fragment.user.ProductFragment;
import com.example.final_project_group5.R;
import com.example.final_project_group5.fragment.admin.UserFragment;
import com.example.final_project_group5.databinding.ActivityAdminDashboardBinding;

public class AdminDashboard extends AppCompatActivity {
    ActivityAdminDashboardBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        replaceFragment(new UserFragment());
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.users) {

                replaceFragment(new UserFragment());
                return true;
            } else if (item.getItemId() == R.id.orders) {
                replaceFragment(new OrderFragment());
                return true;
            } else if (item.getItemId() == R.id.products) {
                replaceFragment(new ProductFragment());
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