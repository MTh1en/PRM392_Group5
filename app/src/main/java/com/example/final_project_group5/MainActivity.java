package com.example.final_project_group5;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnLogout;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Google Sign-In client
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize UI components
        tvWelcome = findViewById(R.id.tvWelcome);
        btnLogout = findViewById(R.id.btnLogout);

        // Get data from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String email = extras.getString("USER_EMAIL");
            String name = extras.getString("USER_NAME");
            int role = extras.getInt("USER_ROLE");

            // Display welcome message
            tvWelcome.setText("Welcome, " + name + "!\nEmail: " + email + "\nRole: " + role);
        } else {
            tvWelcome.setText("Welcome! No user data received.");
        }

        // Set up logout button
        btnLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
        // Check if the user signed in with Google
        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            // Sign out from Google
            mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
                // Google Sign-Out complete, proceed to Login screen
                goToLoginActivity();
            });
        } else {
            // Regular logout (non-Google), just go to Login screen
            goToLoginActivity();
        }
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish(); // Close MainActivity
    }
}