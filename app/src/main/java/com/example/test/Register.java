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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity implements View.OnClickListener {
    private TextView banner;
    private EditText editTextFirstName, editTextLastName, editTextEmail, editTextPassword;
    private Button registerUser;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Gets ID of all functionalities in activity_register
        banner = (TextView) findViewById(R.id.register_companyName);
        banner.setOnClickListener(this);

        registerUser = (Button) findViewById(R.id.register_button);
        registerUser.setOnClickListener(this);

        editTextFirstName = (EditText) findViewById(R.id.register_first_name);
        editTextLastName = (EditText) findViewById(R.id.register_last_name);
        editTextEmail = (EditText) findViewById(R.id.register_email);
        editTextPassword = (EditText) findViewById(R.id.register_password);

        progressBar = (ProgressBar) findViewById(R.id.register_progressBar);

        // Firebase stuff
        mAuth = FirebaseAuth.getInstance();
    }

    // Tap/Click recognition function
    @Override
    public void onClick (View v)
    {
        switch (v.getId())
        {
            case R.id.register_companyName:
                startActivity(new Intent(this, Login.class));
                break;
            case R.id.register_button:
                registerUser();
                break;
        }
    }

    public void registerUser ()
    {
        // Getting user's inputs & converts them to string
        String fName = editTextFirstName.getText().toString().trim();
        String lName = editTextLastName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Checks if users provide valid inputs
        if (fName.isEmpty())
        {
            editTextFirstName.setError("First Name is required!");
            editTextFirstName.requestFocus();
            return;
        }
        if (lName.isEmpty())
        {
            editTextLastName.setError("Last Name is required!");
            editTextLastName.requestFocus();
            return;
        }
        if (email.isEmpty())
        {
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }
        // Checks if users provide a valid email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editTextEmail.setError("Please provide a valid email!");
            editTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty())
        {
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        // Checks for password security
        if (password.length() < 6)
        {
            editTextPassword.setError("Please provide a valid password!");
            editTextPassword.requestFocus();
            return;
        }

        // Set invisibility of progress bar
        progressBar.setVisibility(View.VISIBLE);
        // Save user's info to firebase's database
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                // Creates a new user and checks if user has been registered
                if (task.isSuccessful())
                {

                    User user = new User(fName, lName, email);
                    // TODO: split address to Street address, City, State, and Zip Code
                    user.setAddress("");
                    // Creates a root instance in Firebase called Users and each child will get a unique ID with .getUid()
                    // Inserts Data to Firebase
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {

                         // Checks if successfully added a user
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(Register.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(Register.this, Login.class));
                            }
                            // if fails
                            else
                            {
                                Toast.makeText(Register.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
                else
                {
                    Toast.makeText(Register.this, "Failed to register!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

}