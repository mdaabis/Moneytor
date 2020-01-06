package com.example.moneytor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    TextView TVemail;
    Button forgotPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        TVemail = (TextView) findViewById(R.id.user);
        forgotPassword = findViewById(R.id.forgot);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = TVemail.getText().toString().trim();
                if(email==null) {
                    Toast.makeText(ForgotPassword.this,"Please enter your email.", Toast.LENGTH_SHORT).show();
                } else {
                    String emailAddress = "user@example.com";
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ForgotPassword.this,"Email sent.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ForgotPassword.this, MainActivity.class));
                                    } else {
                                        Toast.makeText(ForgotPassword.this,"Error occurred.", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }

            }
        });


    }
}
