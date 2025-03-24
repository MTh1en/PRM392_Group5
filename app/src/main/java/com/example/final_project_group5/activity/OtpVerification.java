package com.example.final_project_group5.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project_group5.R;
import com.example.final_project_group5.entity.User;
import com.example.final_project_group5.repository.UserRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerification extends AppCompatActivity {
    private EditText etOtp, etNewPassword, etConfirmPassword;
    private Button btnResetPassword;
    private String email, generatedOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp_verification);

        // Get email from intent
        email = getIntent().getStringExtra("email");

        // Initialize UI components
        etOtp = findViewById(R.id.etOtp);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        // Generate and "send" OTP
        generatedOtp = generateOtp();
        Toast.makeText(this, "Your OTP is: " + generatedOtp, Toast.LENGTH_LONG).show();

        // Reset Password button click listener
        btnResetPassword.setOnClickListener(v -> {
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            String enteredOtp = etOtp.getText().toString().trim();

            if (!validateInputs(newPassword, confirmPassword, enteredOtp)) {
                return;
            }

            if (!enteredOtp.equals(generatedOtp)) {
                etOtp.setError("Invalid OTP");
                return;
            }

            resetPassword(email, newPassword);
        });
    }

    private boolean validateInputs(String newPassword, String confirmPassword, String otp) {
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
        if (otp.isEmpty()) {
            etOtp.setError("OTP is required");
            return false;
        }
        return true;
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // 6-digit OTP
        return String.valueOf(otp);
    }

    private void resetPassword(String email, String newPassword) {
        UserRepo.getUserService().getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body();
                    User userToUpdate = null;
                    for (User user : users) {
                        if (user.getEmail().equals(email)) {
                            userToUpdate = user;
                            break;
                        }
                    }

                    if (userToUpdate != null) {
                        userToUpdate.setPassword(newPassword);
                        userToUpdate.setUpdateAt(LocalDateTime.now().toString());
                        updatePasswordInMockAPI(userToUpdate);
                    }
                } else {
                    Toast.makeText(OtpVerification.this, "Failed to fetch users: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(OtpVerification.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePasswordInMockAPI(User user) {
        UserRepo.getUserService().updateUser(user.getId(), user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(OtpVerification.this, "Password reset successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OtpVerification.this, Login.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(OtpVerification.this, "Failed to reset password: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(OtpVerification.this, "Error updating password: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}