package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener
{
    private TextView registerUser, forgotPassword;
    private EditText editTextEmail, editTextPassword;
    private Button LogIn_Button;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initializing all buttons + editText + onclick functionalities
        registerUser = (TextView) findViewById(R.id.login_register);
        registerUser.setOnClickListener(this);

        forgotPassword = (TextView) findViewById(R.id.login_forgot_password);
        forgotPassword.setOnClickListener(this);

        LogIn_Button = (Button) findViewById(R.id.login_button);
        LogIn_Button.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.login_email);
        editTextPassword = (EditText) findViewById(R.id.login_password);

        progressBar = (ProgressBar) findViewById(R.id.login_progressBar);
        mAuth = FirebaseAuth.getInstance();
    }

    // Tap/Click recognition function
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            // Move to activity_register when users tap on "register" in login menu
            case R.id.login_register:
                startActivity(new Intent(this, Register.class));
                break;
            // Clicks on log in button
            case R.id.login_button:
                userLogin();
                break;
            // Move to forgot password activity
            case R.id.login_forgot_password:
                startActivity(new Intent(Login.this, ForgotPassword.class));
                break;
        }
    }

    public void userLogin()
    {
        // Get user's inputs and converts to string
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Check if user provides a valid email
        if (email.isEmpty())
        {
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editTextEmail.setError("Please enter a valid email!");
            editTextEmail.requestFocus();
            return;
        }
        // Check if user provides a password
        if (password.isEmpty())
        {
            editTextPassword.setError("Please enter a password!");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6)
        {
            editTextPassword.setError("Please enter a valid password!");
            editTextPassword.requestFocus();
            return;
        }

        // Sets progress bar to visible til confirm login
        progressBar.setVisibility(View.VISIBLE);

        // sign in w/ email + password & check if task has been complete
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    // check if email has been verified
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user.isEmailVerified())
                    {
                        // redirect to profile page
                        startActivity(new Intent(Login.this, MainActivity.class));
                    }
                    else
                    {
                        user.sendEmailVerification();
                        Toast.makeText(Login.this, "Check your email to verify your account", Toast.LENGTH_LONG).show();
                    }
                }
                // user provides wrong credentials
                else
                {
                    Toast.makeText(Login.this,"Please check your email and password!", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}