package com.example.final_project_group5.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project_group5.R;
import com.example.final_project_group5.entity.User;
import com.example.final_project_group5.repository.UserRepo;

import java.time.LocalDateTime;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword extends AppCompatActivity {
    private EditText etEmail, etNewPassword, etConfirmPassword;
    private Button btnResetPassword;
    private TextView tvBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        // Initialize UI components
        etEmail = findViewById(R.id.etEmail);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        // Back to Login click listener
        tvBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPassword.this, Login.class);
            startActivity(intent);
            finish();
        });

        // Reset Password button click listener
        btnResetPassword.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (!validateInputs(email, newPassword, confirmPassword)) {
                return;
            }

            resetPassword(email, newPassword);
        });
    }

    private boolean validateInputs(String email, String newPassword, String confirmPassword) {
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            return false;
        }
        if (newPassword.isEmpty()) {
            etNewPassword.setError("Password is required");
            return false;
        }
        if (newPassword.length() < 6) {
            etNewPassword.setError("Password must be at least 6 characters");
            return false;
        }
        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return false;
        }
        return true;
    }

    private void resetPassword(String email, String newPassword) {
        UserRepo.getUserService().getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                Log.d("ForgotPassword", "Get all users response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body();
                    Log.d("ForgotPassword", "Number of users found: " + users.size());

                    User userToUpdate = null;
                    for (User user : users) {
                        Log.d("ForgotPassword", "Checking user: " + user.getEmail());
                        if (user.getEmail().equals(email)) {
                            userToUpdate = user;
                            break;
                        }
                    }

                    if (userToUpdate != null) {
                        Log.d("ForgotPassword", "User found: " + userToUpdate.getEmail() + ", ID: " + userToUpdate.getId());
                        if (!userToUpdate.isActive()) {
                            Toast.makeText(ForgotPassword.this, "This account is banned", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Update password
                        userToUpdate.setPassword(newPassword);
                        userToUpdate.setUpdateAt(LocalDateTime.now().toString());
                        updatePasswordInMockAPI(userToUpdate);
                    } else {
                        Toast.makeText(ForgotPassword.this, "Email not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgotPassword.this, "Failed to fetch users: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e("ForgotPassword", "Error fetching users: " + t.getMessage());
                Toast.makeText(ForgotPassword.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePasswordInMockAPI(User user) {
        Log.d("ForgotPassword", "Updating user ID: " + user.getId() + " with new password: " + user.getPassword());
        UserRepo.getUserService().updateUser(user.getId(), user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d("ForgotPassword", "Update response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ForgotPassword.this, "Password reset successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ForgotPassword.this, Login.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ForgotPassword.this, "Failed to reset password: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("ForgotPassword", "Error updating password: " + t.getMessage());
                Toast.makeText(ForgotPassword.this, "Error updating password: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}