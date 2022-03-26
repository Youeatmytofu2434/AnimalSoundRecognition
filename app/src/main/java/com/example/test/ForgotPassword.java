package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ForgotPassword extends AppCompatActivity
{

    private EditText emailEditText;
    private Button forgotPasswordButton;
    private ProgressBar progressBar;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        auth = FirebaseAuth.getInstance();

        emailEditText = (EditText) findViewById(R.id.forgotPassword_email);
        forgotPasswordButton = (Button) findViewById(R.id.forgotPassword_button);
        progressBar = (ProgressBar) findViewById(R.id.forgotPassword_progressBar);

        forgotPasswordButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                reset_password();
            }
        });
    }

    private void reset_password()
    {
        String email = emailEditText.getText().toString().trim();
        // Check for valid email
        if (email.isEmpty())
        {
            emailEditText.setError("Email is required!");
            emailEditText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailEditText.setError("Please provide a valid email!");
            emailEditText.requestFocus();
            return;
        }
        // set progress bar to visible
        progressBar.setVisibility(View.VISIBLE);

        // call sendPasswordResetEmail to reset email
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(ForgotPassword.this, "Please check your email!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ForgotPassword.this, Login.class));
                }
                else
                {
                    Toast.makeText(ForgotPassword.this, "Something went wrong! Please try again! :(", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}