package com.example.final_project_group5;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Welcome extends AppCompatActivity {
Button btnGetStarted;
TextView tvHaveAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
btnGetStarted = (Button) findViewById(R.id.getStartedButton);
btnGetStarted.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Welcome.this, SignUp.class);
        startActivity(intent);
    }
});
tvHaveAccount = (TextView) findViewById(R.id.haveAccountTextView);
tvHaveAccount.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Welcome.this, Login.class);
        startActivity(intent);
    }
});
    }
}