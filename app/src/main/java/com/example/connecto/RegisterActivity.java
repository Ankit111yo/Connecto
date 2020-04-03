package com.example.connecto;

import android.app.AppComponentFactory;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText nameView = findViewById(R.id.signup_name);
        final EditText emailView = findViewById(R.id.signup_email);
        final EditText mobileView = findViewById(R.id.signup_mobile);
        final EditText passwordView = findViewById(R.id.signup_password);
        final EditText confirmPasswordView = findViewById(R.id.confirm_signup_password);
        Toolbar toolbar = findViewById(R.id.toolbar);
        Button signUpButton = findViewById(R.id.signup_button);
        TextView signInButton = findViewById(R.id.back_button);
        progressBar = findViewById(R.id.progressBar);

       setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Create Account");

        firebaseAuth=FirebaseAuth.getInstance();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String name = nameView.getText().toString().trim();
                String email = emailView.getText().toString().trim();
                String password1 = passwordView.getText().toString().trim();
                String password2 = confirmPasswordView.getText().toString().trim();
                String mobile = mobileView.getText().toString().trim();

                if (!check(name, email, password1, password2))
                    return;
                else
                    signup(name,email,password1,mobile);


            }

        });
    }

    void signup(final String name, String email, String password, final String mobile)
    {
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if(user==null)
                        return;

                    String uid= user.getUid();

                    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
                    databaseReference.child(uid).child("name").setValue(name);
                    databaseReference.child(uid).child("mobile").setValue(mobile);
                    databaseReference.child(uid).child("numberfollowers").setValue(0);
                    databaseReference.child(uid).child("numberfollowing").setValue(0);
                    databaseReference.child("users").child(uid).setValue(uid);
                    databaseReference.child("mobile").child(mobile).setValue(uid);

                    UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();
                    user.updateProfile(request);

                    Toast.makeText(RegisterActivity.this,"Successfully registered: Verify email",Toast.LENGTH_LONG).show();
                    verifyemail();
                    startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                }
                else
                {
                    Toast.makeText(RegisterActivity.this,"Invalid credentials",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    void verifyemail()
    {
        FirebaseUser user= firebaseAuth.getCurrentUser();
        if(user!=null)
            user.sendEmailVerification();
    }




    private boolean check(String name, String email, String password1, String password2) {
        if(name.length() == 0){
            Toast.makeText(this,"Name cannot be empty", Toast.LENGTH_LONG).show();
            return false;
        }

        if(!email.contains("@") || !email.contains(".")){
            Toast.makeText(this,"Invalid Email", Toast.LENGTH_LONG).show();
            return false;
        }

        if(password1.length() < 6){
            Toast.makeText(this,"Password should be minimum 8 characters", Toast.LENGTH_LONG).show();
            return false;
        }

        if(!password1.equals(password2)){
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
