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
import androidx.room.Room;

import com.example.final_project_group5.dao.UsersDAO;
import com.example.final_project_group5.entity.AppDatabase;
import com.example.final_project_group5.entity.AppExecutors;
import com.example.final_project_group5.entity.Users;

public class SignUp extends AppCompatActivity {
    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private CheckBox cbTerms;
    private Button btnCreateAccount;
    private TextView tvSignIn;
    private AppDatabase appDatabase;
    private UsersDAO usersDAO;

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

        // Initialize Room database
        appDatabase = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "electronic_store.db")
                .build();
        usersDAO = appDatabase.usersDao();

        // Sign In text click listener
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
                final Users newUser = new Users();
                newUser.setName(fullName);
                newUser.setEmail(email);
                newUser.setPassword(password); // Note: Should be hashed in production
                newUser.setRole(0); // Default role (e.g., 0 for regular user)

                // Insert user in background thread
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        // Check for existing email
                        Users existingUser = usersDAO.loginUser(email, password);
                        if (existingUser != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SignUp.this,
                                            "Email already registered",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }

                        long userId = usersDAO.insertUser(newUser);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (userId > 0) {
                                    Toast.makeText(SignUp.this,
                                            "Account created successfully",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignUp.this, Login.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(SignUp.this,
                                            "Failed to create account",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
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