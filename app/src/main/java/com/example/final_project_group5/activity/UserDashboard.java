    package com.example.final_project_group5.activity;

    import android.graphics.Color;
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
        String userId, userName;
        ImageView btnCart;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);

            // Lấy userId từ Intent nếu có (khi chuyển từ Login)
            userId = getIntent().getStringExtra("USER_ID");
            userName = getIntent().getStringExtra("USER_NAME");
            Log.d("UserDashboard", "Received userId from Intent: " + userId);

            binding = ActivityUserDashboardBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            btnCart = findViewById(R.id.btn_cart);
            String userName = "User"; // Thay bằng tên thực tế nếu có
            binding.tvGreeting.setText("Xin chào " + userName + "!      --- Home ---");
            replaceFragment(HomeFragment.newInstance(userId));

            btnCart.setOnClickListener(v -> {
                binding.tvGreeting.setText("Xin chào " + userName + "!      --- Your Cart ---");
                Log.d("UserDashboard", "Opening CartFragment with userId: " + userId);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout1, CartFragment.newInstance(userId))
                        .addToBackStack(null)
                        .commit();
            });

            binding.bottomNavigationViewUser.setOnItemSelectedListener(item -> {
                if (item.getItemId() == R.id.home) {
                    replaceFragment(HomeFragment.newInstance(userId));
                    binding.tvGreeting.setText("Xin chào " + userName + "!      --- Home ---");
                    return true;
                } else if (item.getItemId() == R.id.categories) {
                    replaceFragment(CategoriesFragment.newInstance(userId));
                    binding.tvGreeting.setText("Xin chào " + userName + "!      --- Category ---");
                    return true;
                } else if (item.getItemId() == R.id.order) {
                    replaceFragment(OrderFragmentUser.newInstance(userId));
                    binding.tvGreeting.setText("Xin chào " + userName + "!      --- Order ---");
                    return true;
                } else if (item.getItemId() == R.id.profile) {
                    replaceFragment(ProfileFragment.newInstance(userId));
                    binding.tvGreeting.setText("Xin chào " + userName + "!      --- Profile ---");
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
