package com.example.moneytor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterPage extends AppCompatActivity {
    Button btnSignIn, btnSignUp;
    EditText emailId, password, cnfrmpassword, first_name, surname;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference current_user_db;
    TextView haveAccount;
    private String email, pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);


        btnSignIn = findViewById(R.id.BTNlogin_button);
        btnSignUp = findViewById(R.id.BTNregister);
        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.ETusername);
        password = findViewById(R.id.ETpassword);
        cnfrmpassword = findViewById(R.id.ETconfirm);
        first_name = findViewById(R.id.ETname);
        surname = findViewById(R.id.ETsurname);
        haveAccount = findViewById(R.id.TVno_account);

        haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegisterPage.this, "Press the 'Login' button", Toast.LENGTH_SHORT).show();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterPage.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailId.getText().toString().trim();
                pwd = password.getText().toString();
                String cnfrmPwd = cnfrmpassword.getText().toString();
                String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
                if (email.isEmpty() && pwd.isEmpty()) {
                    Toast.makeText(RegisterPage.this, "Fields Are Empty", Toast.LENGTH_SHORT).show();
                } else if (pwd.isEmpty()) {
                    password.setError("Please enter your password");
                    password.requestFocus();
                } else if (email.isEmpty()) {
                    emailId.setError("Please enter email");
                    emailId.requestFocus();
                } else if (!pwd.equals(cnfrmPwd)) {
                    Toast.makeText(RegisterPage.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else if (!email.matches(regex)) {
                    Toast.makeText(RegisterPage.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                } else if (pwd.length() < 8) {
                    password.setError("Password must be at least 8 characters long");
                    password.requestFocus();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterPage.this);
                    dialog.setTitle("Terms and Conditions");
                    dialog.setMessage("This application, once authorised and authenticated, will retrieve data from your Monzo account " +
                            "and store it in a separate, cloud-based database. This data includes your: transactions, balance and name. " +
                            "You can delete your account from the Settings page at any time and all of your data will be removed from the  " +
                            "database.");
                    dialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(RegisterPage.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(RegisterPage.this, "Sign up unsuccessful, please try again", Toast.LENGTH_SHORT).show();
                                    } else {
                                        String user_id = mFirebaseAuth.getCurrentUser().getUid();
                                        String firstName = first_name.getText().toString();
                                        String surName = surname.getText().toString();

                                        current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                                        Map<String, String> newPost = new HashMap<>();
                                        newPost.put("First name", firstName);
                                        newPost.put("Surname", surName);
                                        current_user_db.setValue(newPost);


                                        FirebaseUser user = mFirebaseAuth.getCurrentUser();
                                        user.sendEmailVerification()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            startActivity(new Intent(RegisterPage.this, MainActivity.class));
                                                            Toast.makeText(RegisterPage.this, "Registration successful, please verify your email address", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });
                            changeActivity(RegisterPage.this, MainActivity.class);

                        }
                    });
                    dialog.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            changeActivity(RegisterPage.this, MainActivity.class);
                            Toast.makeText(RegisterPage.this, "Registration failed as you rejected terms and conditions", Toast.LENGTH_SHORT).show();

                        }
                    });

                    AlertDialog alertDialog = dialog.create();
                    alertDialog.show();
                }

            }
        });
    }

    public void changeActivity(Activity Current, Class Target) {
        Intent intent = new Intent(Current, Target);
        startActivity(intent);
    }
}
