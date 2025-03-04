package com.example.final_project_group5;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.final_project_group5.dao.UsersDAO;
import com.example.final_project_group5.entity.AppDatabase;
import com.example.final_project_group5.entity.AppExecutors;
import com.example.final_project_group5.entity.Users;

import java.util.List;

public class Welcome extends AppCompatActivity {
    Button btnGetStarted;
    TextView tvHaveAccount;
    private UsersDAO usersDAO;

    AppDatabase appDatabase;

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
        this.deleteDatabase("electronics_store.db");
        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "electronics_store.dn").build();
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<Users> users = appDatabase.usersDao().getAllUsers();
            }
        });
    }
}