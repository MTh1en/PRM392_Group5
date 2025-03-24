package com.example.final_project_group5.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project_group5.R;
import com.example.final_project_group5.entity.User;
import com.example.final_project_group5.repository.UserRepo;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPassword extends AppCompatActivity {

    private EditText etOTP, etNewPassword;
    private Button btnResetPassword;
    private TextView tvBackToLogin;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        etOTP = findViewById(R.id.etOTP);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        tvBackToLogin = findViewById(R.id.tvBackLogin);

        String email = getIntent().getStringExtra("email");
        extractUserInformationByEmail(email);
        btnResetPassword.setOnClickListener(v -> {
            String inputOTP = etOTP.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();

            if (inputOTP.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (inputOTP.equals(ForgotPassword.generatedOTP)) {
                updatePasswordInMockAPI(email, newPassword);
            } else {
                Toast.makeText(this, "Invalid OTP!", Toast.LENGTH_SHORT).show();
            }
        });

        tvBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        });
    }
    private void extractUserInformationByEmail(String email){
        UserRepo.getUserService().loginGoogle(email).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null){
                    List<User> users = response.body();
                    if (!users.isEmpty()){
                        User user = users.get(0);
                        userId = user.getId();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(ResetPassword.this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updatePasswordInMockAPI(String email, String newPassword) {
        // Tạo đối tượng User để gửi lên API
        User updatedUser = new User();
        updatedUser.setEmail(email);
        updatedUser.setPassword(newPassword);
        updatedUser.setActive(true);
        Log.d("Reset Password", "UserId: " + userId);
        UserRepo.getUserService().updateUser(userId, updatedUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ResetPassword.this, "Password reset successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ResetPassword.this, Login.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ResetPassword.this, "Failed to update password: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ResetPassword.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}