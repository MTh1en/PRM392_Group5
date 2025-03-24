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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword extends AppCompatActivity {
    private EditText etEmail;
    private Button btnRequestOtp;
    private TextView tvBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        // Initialize UI components
        etEmail = findViewById(R.id.etEmail);
        btnRequestOtp = findViewById(R.id.btnRequestOtp);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        // Back to Login click listener
        tvBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPassword.this, Login.class);
            startActivity(intent);
            finish();
        });

        // Request OTP button click listener
        btnRequestOtp.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (!validateEmail(email)) {
                return;
            }
            checkEmailAndRequestOtp(email);
        });
    }

    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            return false;
        }
        return true;
    }

    private void checkEmailAndRequestOtp(String email) {
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
                        if (!userToUpdate.isActive()) {
                            Toast.makeText(ForgotPassword.this, "This account is banned", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // Email exists, proceed to OTP screen
                        Intent intent = new Intent(ForgotPassword.this, OtpVerification.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ForgotPassword.this, "Email not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgotPassword.this, "Failed to fetch users: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(ForgotPassword.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}