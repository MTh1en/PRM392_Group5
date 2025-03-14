package com.example.final_project_group5;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project_group5.api.ApiClient;
import com.example.final_project_group5.api.UserService;
import com.example.final_project_group5.entity.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

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
    private UserService userService;

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

        // Initialize Retrofit
        userService = ApiClient.getUserService();

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
            Toast.makeText(Login.this, "Attempting MockAPI login...", Toast.LENGTH_SHORT).show();
            authenticateWithMockAPI(email, password);
        });

        // Google Sign-In button click listener
        btnGoogle.setOnClickListener(v -> {
            Toast.makeText(Login.this, "Starting Google Sign-In...", Toast.LENGTH_SHORT).show();
            signInWithGoogle();
        });

        // Forgot Password click listener
        tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(Login.this, "Forgot Password functionality to be implemented", Toast.LENGTH_SHORT).show();
        });
    }

    private void authenticateWithMockAPI(String email, String password) {
        Toast.makeText(Login.this, "Calling MockAPI with email: " + email, Toast.LENGTH_SHORT).show();
        Call<List<User>> call = userService.getAllUsers();
        Toast.makeText(Login.this, "Request enqueued", Toast.LENGTH_SHORT).show();
        android.util.Log.d("LoginDebug", "Enqueuing request to " + call.request().url());
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                Toast.makeText(Login.this, "MockAPI response received, code: " + response.code(), Toast.LENGTH_SHORT).show();
                android.util.Log.d("LoginDebug", "Response received: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body();
                    Toast.makeText(Login.this, "Users fetched: " + users.size(), Toast.LENGTH_SHORT).show();
                    User matchedUser = null;
                    for (User user : users) {
                        if (user.getEmail().equals(email)) {
                            matchedUser = user;
                            break;
                        }
                    }
                    if (matchedUser != null) {
                        Toast.makeText(Login.this, "User found: " + matchedUser.getEmail(), Toast.LENGTH_SHORT).show();
                        if (matchedUser.getPassword().equals(password)) {
                            Toast.makeText(Login.this, "MockAPI login successful", Toast.LENGTH_SHORT).show();
                            handleSuccessfulLogin(matchedUser);
                        } else {
                            Toast.makeText(Login.this, "Invalid password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Login.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Login.this, "Failed to fetch users: " + response.code(), Toast.LENGTH_SHORT).show();
                    android.util.Log.e("LoginDebug", "Unsuccessful response: " + response.code() + ", body: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(Login.this, "MockAPI Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                android.util.Log.e("LoginDebug", "Request failed: " + t.getMessage(), t);
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

        userService.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body();
                    User matchedUser = null;
                    for (User user : users) {
                        if (user.getEmail().equals(email)) {
                            matchedUser = user;
                            break;
                        }
                    }
                    if (matchedUser != null) {
                        Toast.makeText(Login.this, "Google user found in MockAPI", Toast.LENGTH_SHORT).show();
                        handleSuccessfulLogin(matchedUser);
                    } else {
                        Toast.makeText(Login.this, "Google user not found, creating new user", Toast.LENGTH_SHORT).show();
                        User newUser = new User();
                        newUser.setEmail(email);
                        newUser.setName(name);
                        newUser.setRole("CUSTOMER");
                        newUser.setActive(true);
                        createUserInMockAPI(newUser);
                    }
                } else {
                    Toast.makeText(Login.this, "Failed to fetch users: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(Login.this, "Google Check Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createUserInMockAPI(User user) {
        userService.createUser(user).enqueue(new Callback<User>() {
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
        Toast.makeText(Login.this, "Welcome " + user.getName() + ", starting MainActivity", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Login.this, MainActivity.class);
        intent.putExtra("USER_EMAIL", user.getEmail());
        intent.putExtra("USER_NAME", user.getName());
        intent.putExtra("USER_ROLE", "admin".equalsIgnoreCase(user.getRole()) ? 1 : 0);
        startActivity(intent);
        finish();
    }
}
//package com.example.final_project_group5;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.final_project_group5.entity.User;
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInClient;
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.android.gms.common.api.ApiException;
//import com.google.android.gms.tasks.Task;
//
//public class Login extends AppCompatActivity {
//    private static final int RC_SIGN_IN = 9001;
//
//    private TextView tvSignUp;
//    private EditText etEmail, etPassword;
//    private Button btnSignIn, btnGoogle;
//    private TextView tvForgotPassword;
//    private GoogleSignInClient mGoogleSignInClient;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_login);
//
//        // Configure Google Sign-In
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//
//        // Initialize UI components
//        tvSignUp = findViewById(R.id.DntSignUp);
//        etEmail = findViewById(R.id.etEmail);
//        etPassword = findViewById(R.id.etPassword);
//        btnSignIn = findViewById(R.id.btnSignIn);
//        btnGoogle = findViewById(R.id.btnGoogle);
//        tvForgotPassword = findViewById(R.id.tvForgotPassword);
//
//        // Initialize Room database
//
//        // Sign Up text click listener
//        tvSignUp.setOnClickListener(v -> {
//            Intent intent = new Intent(Login.this, SignUp.class);
//            startActivity(intent);
//        });
//
//        // Sign In button click listener
//        btnSignIn.setOnClickListener(v -> {
//            String email = etEmail.getText().toString().trim();
//            String password = etPassword.getText().toString().trim();
//
//            if (email.isEmpty() || password.isEmpty()) {
//                Toast.makeText(Login.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        });
//
//        // Google Sign-In button click listener
//        btnGoogle.setOnClickListener(v -> signInWithGoogle());
//
//        // Forgot Password click listener
//        tvForgotPassword.setOnClickListener(v -> {
//            Toast.makeText(Login.this, "Forgot Password functionality to be implemented", Toast.LENGTH_SHORT).show();
//        });
//    }
//
//    private void signInWithGoogle() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RC_SIGN_IN) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                handleGoogleSignIn(account);
//            } catch (ApiException e) {
//                Toast.makeText(this, "Google Sign-In failed: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void handleGoogleSignIn(GoogleSignInAccount account) {
//        String email = account.getEmail();
//        String name = account.getDisplayName();
//
//        // For this version, we'll just proceed with Google Sign-In without database integration
//        Toast.makeText(Login.this, "Welcome " + name, Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(Login.this, MainActivity.class);
//        // Since we're not modifying DAO, we'll pass minimal user info
//        intent.putExtra("USER_EMAIL", email);
//        intent.putExtra("USER_NAME", name);
//        intent.putExtra("USER_ROLE", 0); // Default role
//        startActivity(intent);
//        finish();
//    }
//
//    private void handleSuccessfulLogin(User user) {
//        Toast.makeText(Login.this, "Welcome " + user.getName(), Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(Login.this, MainActivity.class);
//        intent.putExtra("USER_ID", user.getId());
//        intent.putExtra("USER_ROLE", user.getRole());
//        startActivity(intent);
//        finish();
//    }
//}