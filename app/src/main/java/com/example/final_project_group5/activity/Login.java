package com.example.final_project_group5.activity;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;

    private TextView tvSignUp, tvForgotPassword;
    private EditText etEmail, etPassword;
    private Button btnSignIn, btnGoogle;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize UI components
        tvSignUp = findViewById(R.id.DntSignUp);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnGoogle = findViewById(R.id.btnGoogle);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // Sign Up text click listener
        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, SignUp.class);
            startActivity(intent);
        });

        // Sign In button click listener (MockAPI)
        btnSignIn.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(Login.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            authenticateWithMockAPI(email, password);
        });

        // Google Sign-In button click listener
        btnGoogle.setOnClickListener(v -> {
            Toast.makeText(Login.this, "Starting Google Sign-In...", Toast.LENGTH_SHORT).show();
            signInWithGoogle();
        });

        // Forgot Password click listener
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, ForgotPassword.class);
            startActivity(intent);
        });
    }

    private void authenticateWithMockAPI(String email, String password) {
        Log.d("Login", "Email: " + email + ", Password: " + password);
        UserRepo.getUserService().login(email, password).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> user = response.body();
                    User matchedUser = user.get(0);
                    if (user.get(0).isActive()) {
                        if ((user.get(0).getPassword().equals(password))) {
                            handleSuccessfulLogin(matchedUser);
                        } else {
                            Toast.makeText(Login.this, "Invalid password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Login.this, "User is banned", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Login.this, "Login Failed " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(Login.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(this, "Google Sign-In successful", Toast.LENGTH_SHORT).show();
                handleGoogleSignIn(account);
            } catch (ApiException e) {
                Toast.makeText(this, "Google Sign-In failed: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleGoogleSignIn(GoogleSignInAccount account) {
        String email = account.getEmail();
        String name = account.getDisplayName();

        UserRepo.getUserService().loginGoogle(email).enqueue(new Callback<List<User>>() {

            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                Log.d("Login", "Email: " + email);
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body();
                    handleSuccessfulLogin(users.get(0));
                } else {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setPassword("google");
                    newUser.setName(name);
                    newUser.setRole("CUSTOMER");
                    newUser.setActive(true);
                    newUser.setCreateAt(LocalDateTime.now().toString());
                    createUserInMockAPI(newUser);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(Login.this, "Google Check Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createUserInMockAPI(User user) {
        UserRepo.getUserService().createUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(Login.this, "New Google user created", Toast.LENGTH_SHORT).show();
                    handleSuccessfulLogin(response.body());
                } else {
                    Toast.makeText(Login.this, "Failed to create Google user: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(Login.this, "Create User Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleSuccessfulLogin(User user) {
        Intent intent;
        if (user.getRole().equals("ADMIN")) {
            intent = new Intent(Login.this, AdminDashboard.class);
        } else {
            intent = new Intent(Login.this, UserDashboard.class);
        }
        intent.putExtra("USER_ID", user.getId());
        intent.putExtra("USER_EMAIL", user.getEmail());
        intent.putExtra("USER_NAME", user.getName());

        // Lưu userId vào SharedPreferences
        Log.d("UserDashboard", "Lấy userId từ SharedPreferences..."); // Thêm log này
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);
        Log.d("UserDashboard", "userId từ SharedPreferences: " + userId); // Thêm log này

        Log.d("LoginActivity", "userId sau khi lưu: " + sharedPreferences.getString("userId", null));

        startActivity(intent);
        finish();
    }


}