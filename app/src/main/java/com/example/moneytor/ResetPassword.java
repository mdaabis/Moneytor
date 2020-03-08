package com.example.moneytor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {
    EditText currentPwd;
    Button sendRstEmail;
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        back = (Button) findViewById(R.id.back_button_settings);
        currentPwd = (EditText) findViewById(R.id.currentPassword);
        sendRstEmail = (Button) findViewById(R.id.sendResetEmail);

        sendRstEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPwd.getText().toString().equals(MainActivity.pwd)){
                    Toast.makeText(ResetPassword.this,"Correct password",Toast.LENGTH_SHORT).show();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.sendPasswordResetEmail(MainActivity.email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ResetPassword.this,"Email sent", Toast.LENGTH_SHORT).show();
                                changeActivity(ResetPassword.this, Settings.class);
                            } else {
                                Toast.makeText(ResetPassword.this,"Error occurred", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ResetPassword.this,"Incorrect password",Toast.LENGTH_SHORT).show();
                    System.out.println("Main page pwd: " + MainActivity.pwd);
                    System.out.println("This pwd: " + currentPwd.getText());
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

    }

    public void changeActivity(Activity Current, Class Target){
        Intent intent = new Intent(Current, Target);
        startActivity(intent);
    }
}
