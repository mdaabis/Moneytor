package com.example.moneytor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterPage extends AppCompatActivity {
    Button btnSignIn,btnSignUp;
    EditText emailId, password, cnfrmpassword;
    FirebaseAuth mFirebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        btnSignIn = (Button) findViewById(R.id.BTNlogin_button);
        btnSignUp = (Button) findViewById(R.id.BTNregister);
        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = (EditText) findViewById(R.id.ETusername);
        password = (EditText) findViewById(R.id.ETpassword);
        cnfrmpassword = (EditText) findViewById(R.id.ETconfirm);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (RegisterPage.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString().trim();
                String pwd = password.getText().toString();
                String cnfrmPwd = cnfrmpassword.getText().toString();
                String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
                if (email.isEmpty() && pwd.isEmpty()) {
                    Toast.makeText(RegisterPage.this, "Fields Are Empty.", Toast.LENGTH_SHORT).show();
                } else if (pwd.isEmpty()) {
                    password.setError("Please enter your password.");
                    password.requestFocus();
                } else if (email.isEmpty()) {
                    emailId.setError("Please enter email.");
                    emailId.requestFocus();
                } else if (!pwd.equals(cnfrmPwd)) {
                    Toast.makeText(RegisterPage.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                } else if (!email.matches(regex)) {
                    Toast.makeText(RegisterPage.this, "Invalid email address.", Toast.LENGTH_SHORT).show();
                } else if(pwd.length()<8){
                    password.setError("Password must be at least 8 characters long.");
                    password.requestFocus();
                } else  if(!(email.isEmpty() && pwd.isEmpty())){
                    mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(RegisterPage.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(RegisterPage.this,"Sign up unsuccessful, please try again.",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                startActivity(new Intent(RegisterPage.this,HomePage.class));
                            }
                        }
                    });
                } else {
                    Toast.makeText(RegisterPage.this,"Error occurred.",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
