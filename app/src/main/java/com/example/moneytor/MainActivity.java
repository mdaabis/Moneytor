package com.example.moneytor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    TextView forgotPassword;
    Button btnSignUp, btnSignIn;
    EditText emailId, password;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        forgotPassword = (TextView) findViewById(R.id.forgot_password);
        btnSignUp = (Button)findViewById(R.id.register);
        btnSignIn = (Button) findViewById(R.id.login_button);
        emailId = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if (mFirebaseUser != null && mFirebaseUser.isEmailVerified()) {
//                    Toast.makeText(MainActivity.this,"You are logged in", Toast.LENGTH_SHORT).show();
                    changeActivity(MainActivity.this, HomePage.class);
                } else {
                    Toast.makeText(MainActivity.this,"Please Login",Toast.LENGTH_SHORT).show();
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
                String email = emailId.getText().toString().trim();
                String pwd = password.getText().toString();
                if (email.isEmpty() && pwd.isEmpty()) {
                    Toast.makeText(MainActivity.this,"Fields Are Empty!",Toast.LENGTH_SHORT).show();
                } else if (pwd.isEmpty()) {
                    password.setError("Please enter your password.");
                    password.requestFocus();
                } else if(email.isEmpty()) {
                    emailId.setError("Please enter your email id.");
                    emailId.requestFocus();
                } else  if(!(email.isEmpty() && pwd.isEmpty())){
                    mFirebaseAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(MainActivity.this,"Login error, please try again.",Toast.LENGTH_SHORT).show();
                            } else{
                                if(mFirebaseAuth.getCurrentUser().isEmailVerified()){
                                    changeActivity(MainActivity.this,HomePage.class);
                                    Toast.makeText(MainActivity.this,"Login successful.",Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this,"Please verify your email address.",Toast.LENGTH_SHORT).show();

                                }
                            }
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this,"Error Occurred!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(MainActivity.this,"Forgot password - To be completed",Toast.LENGTH_SHORT).show();
                changeActivity(MainActivity.this, ForgotPassword.class);
                //To be completed
            }
        });
    }

    public void changeActivity(Activity Current, Class Target){
        Intent intent = new Intent(Current, Target);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
}
