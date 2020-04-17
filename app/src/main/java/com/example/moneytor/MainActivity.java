package com.example.moneytor;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.Instant;

public class MainActivity extends AppCompatActivity {
    public static EditText emailId, password;
    public static String email, pwd;
    private static FirebaseUser mFirebaseUser;
    Button btnSignUp, btnSignIn;
    FirebaseAuth mFirebaseAuth;
    private TextView noAccount;
    private TextView forgotPassword;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private int exitCounter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        forgotPassword = findViewById(R.id.forgot_password);
        btnSignUp = findViewById(R.id.register);
        btnSignIn = findViewById(R.id.login_button);
        emailId = findViewById(R.id.username);
        password = findViewById(R.id.password);
        mFirebaseAuth = FirebaseAuth.getInstance();
        noAccount = findViewById(R.id.no_account);

        noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Press the 'Register' button.", Toast.LENGTH_SHORT).show();
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if (mFirebaseUser != null && mFirebaseUser.isEmailVerified()) {
                    Toast.makeText(MainActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();
                    changeActivity(MainActivity.this, HomePage.class);
                } else {
                    Toast.makeText(MainActivity.this, "Please login", Toast.LENGTH_SHORT).show();
                }
            }
        };

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity(MainActivity.this, RegisterPage.class);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailId.getText().toString().trim();
                pwd = password.getText().toString();
                if (email.isEmpty() && pwd.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Fields Are Empty!", Toast.LENGTH_SHORT).show();
                } else if (pwd.isEmpty()) {
                    password.setError("Please enter your password");
                    password.requestFocus();
                } else if (email.isEmpty()) {
                    emailId.setError("Please enter your email id");
                    emailId.requestFocus();
                } else {
                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Incorrect login details, please try again", Toast.LENGTH_SHORT).show();
                            } else {
                                if (mFirebaseAuth.getCurrentUser().isEmailVerified()) {
                                    Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                    // is below line needed
                                    SharedPreferences sharedPreferences = getSharedPreferences(Authentication.SHARED_PREFS, MODE_PRIVATE);
                                    if (!hasTokenExpired() && sharedPreferences.contains(Authentication.ACCESS_TOKEN)) {
                                        changeActivity(MainActivity.this, HomePage.class);
                                    } else {
                                        Toast.makeText(MainActivity.this, "Please authorise your account", Toast.LENGTH_SHORT).show();
                                        changeActivity(MainActivity.this, Authentication.class);
//
                                    }

                                } else {
                                    Toast.makeText(MainActivity.this, "Please verify your email address", Toast.LENGTH_SHORT).show();

                                }
                            }
                        }
                    });
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ResetPassword.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    private boolean hasTokenExpired() {
        SharedPreferences sharedPreferences = getSharedPreferences(Authentication.SHARED_PREFS, MODE_PRIVATE);
        long expireEpoch = sharedPreferences.getLong(Authentication.EXPIRE_DATE, 0);
        Instant instant = Instant.now();
        long timeStampSeconds = instant.getEpochSecond();
        boolean expired = timeStampSeconds > expireEpoch;
        return expired;
    }

    public void changeActivity(Activity Current, Class Target) {
        Intent intent = new Intent(Current, Target);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onBackPressed() {
        if (exitCounter < 1) {
            Toast.makeText(MainActivity.this, "Press back again to exit.", Toast.LENGTH_SHORT).show();
            exitCounter++;
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}
