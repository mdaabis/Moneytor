package com.example.moneytor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    TextView TVemail;
    Button forgotPassword;
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        TVemail = findViewById(R.id.userEmail);
        forgotPassword = findViewById(R.id.forgot);
        back = findViewById(R.id.back_button_forgot_pwd);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        // When forgot password is clicked, an email is sent with a reset link
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = TVemail.getText().toString().trim();

                // if-statement ensures an email is entered
                if (email.equals("")) {
                    Toast.makeText(ForgotPassword.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPassword.this, "Email sent", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgotPassword.this, MainActivity.class));
                            } else {
                                Toast.makeText(ForgotPassword.this, "Error occurred", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
