package com.example.final_project_group5;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import com.example.final_project_group5.databinding.ActivityAdminDashboardBinding;
import com.example.final_project_group5.entity.AppDatabase;
import com.example.final_project_group5.entity.AppExecutors;
import com.example.final_project_group5.entity.Users;


import java.util.List;

public class AdminDashboard extends AppCompatActivity {
    ActivityAdminDashboardBinding binding;
    AppDatabase appDatabase;
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
        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "electronics_store.dn").build();
        AppExecutors.getInstance().diskIO().execute(new Runnable() {



            @Override
            public void run() {
                List<Users> users = appDatabase.usersDao().getAllUsers();
                for (Users user : users) {
                    Log.d("User", "ID: " + user.getId() +
                            ", Name: " + user.getName() +
                            ", Email: " + user.getEmail() +
                            ", Address: " + user.getAddress() +
                            ", Phone: " + user.getPhone() +
                            ", Role: " + user.getRole());
                }

            }

        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

}