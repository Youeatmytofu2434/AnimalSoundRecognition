package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfilePage extends AppCompatActivity implements View.OnClickListener {

    private Button logout_button;
    private FirebaseUser DataBaseUser;
    private DatabaseReference reference;

    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilepage);

        // Initializing functionalities
        logout_button = (Button) findViewById(R.id.profilepage_logout_button);
        logout_button.setOnClickListener(this);

        // Place holder for info from database
        TextView displayNameTextView = (TextView) findViewById(R.id.profilepage_name);
        TextView displayEmailTextView = (TextView) findViewById(R.id.profilepage_email);

        // Initializing users + references
        DataBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = DataBaseUser.getUid();

        // Gets info from database
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) 
            {
                // save user's info from database to our User class
                User userInfo = snapshot.getValue(User.class);
                if (userInfo != null)
                {
                    String fName = userInfo.firstName;
                    String lName = userInfo.lastName;
                    String email = userInfo.email;
                    // TODO: add address + show address

                    // Displays info
                    displayNameTextView.setText(fName + " " + lName);
                    displayEmailTextView.setText(email);
                }
            }
            // if fails
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfilePage.this, "Something went wrong! :(", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.profilepage_logout_button:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(ProfilePage.this, "Signed out successfully", Toast.LENGTH_LONG).show();
                startActivity(new Intent(ProfilePage.this, Login.class));
        }
    }
}