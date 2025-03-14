package com.example.final_project_group5;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project_group5.entity.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class Login extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;

    private TextView tvSignUp;
    private EditText etEmail, etPassword;
    private Button btnSignIn, btnGoogle;
    private TextView tvForgotPassword;
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

        // Initialize Room database

        // Sign Up text click listener
        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, SignUp.class);
            startActivity(intent);
        });

        // Sign In button click listener
        btnSignIn.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(Login.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
        });

        // Google Sign-In button click listener
        btnGoogle.setOnClickListener(v -> signInWithGoogle());

        // Forgot Password click listener
        tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(Login.this, "Forgot Password functionality to be implemented", Toast.LENGTH_SHORT).show();
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
                handleGoogleSignIn(account);
            } catch (ApiException e) {
                Toast.makeText(this, "Google Sign-In failed: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleGoogleSignIn(GoogleSignInAccount account) {
        String email = account.getEmail();
        String name = account.getDisplayName();

        // For this version, we'll just proceed with Google Sign-In without database integration
        Toast.makeText(Login.this, "Welcome " + name, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Login.this, MainActivity.class);
        // Since we're not modifying DAO, we'll pass minimal user info
        intent.putExtra("USER_EMAIL", email);
        intent.putExtra("USER_NAME", name);
        intent.putExtra("USER_ROLE", 0); // Default role
        startActivity(intent);
        finish();
    }

    private void handleSuccessfulLogin(User user) {
        Toast.makeText(Login.this, "Welcome " + user.getName(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Login.this, MainActivity.class);
        intent.putExtra("USER_ID", user.getId());
        intent.putExtra("USER_ROLE", user.getRole());
        startActivity(intent);
        finish();
    }
}