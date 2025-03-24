package com.example.final_project_group5.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project_group5.R;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Executors;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ForgotPassword extends AppCompatActivity {

    private EditText etEmail;
    private Button btnSendOTP;
    private TextView tvBackToLogin;
    public static String generatedOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.etEmail);
        btnSendOTP = findViewById(R.id.btnSendOTP);
        tvBackToLogin = findViewById(R.id.tvBackLogin);

        btnSendOTP.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email!", Toast.LENGTH_SHORT).show();
                return;
            }

            generatedOTP = generateOTP();
            sendOTPEmail(email, generatedOTP);

            Intent intent = new Intent(this, ResetPassword.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        tvBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        });
    }

    private String generateOTP() {
        int otp = 100000 + new Random().nextInt(900000);
        return String.valueOf(otp);
    }
    private void sendOTPEmail(String recipientEmail, String otp) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String senderEmail = "mt12122003@gmail.com"; // Thay bằng email của bạn
                String senderPassword = "updv aemy gffc xpat"; // Thay bằng App Password từ Gmail

                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");

                Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new javax.mail.PasswordAuthentication(senderEmail, senderPassword);
                    }
                });

                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(senderEmail));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
                message.setSubject("Your OTP Code");
                message.setText("Your OTP code is: " + otp);

                Transport.send(message);

                runOnUiThread(() -> Toast.makeText(this, "OTP has been sent!", Toast.LENGTH_LONG).show());

            } catch (MessagingException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Failed to send OTP: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }
}