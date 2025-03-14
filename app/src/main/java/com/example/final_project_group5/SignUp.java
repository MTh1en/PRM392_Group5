package com.example.final_project_group5;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project_group5.entity.User;

public class SignUp extends AppCompatActivity {
    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private CheckBox cbTerms;
    private Button btnCreateAccount;
    private TextView tvSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        // Initialize UI components with IDs (these need to be added to your XML)
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        cbTerms = findViewById(R.id.cbTerms);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        tvSignIn = findViewById(R.id.alSignIn);
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
            }
        });

        // Create Account button click listener
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = etFullName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();

                // Input validation
                if (!validateInputs(fullName, email, password, confirmPassword)) {
                    return;
                }

                if (!cbTerms.isChecked()) {
                    Toast.makeText(SignUp.this,
                            "Please agree to the Terms of Service and Privacy Policy",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create new user
                final User newUser = new User();
                newUser.setName(fullName);
                newUser.setEmail(email);
                newUser.setPassword(password); // Note: Should be hashed in production
                newUser.setRole("CUSTOMER"); // Default role (e.g., 0 for regular user)

            }
        });
    }

    private boolean validateInputs(String fullName, String email, String password, String confirmPassword) {
        if (fullName.isEmpty()) {
            etFullName.setError("Full name is required");
            return false;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return false;
        }

        return true;
    }
}