package com.example.connecto;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailNotVerifedActivity extends AppCompatActivity {

    FirebaseAuth firebase=FirebaseAuth.getInstance();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_not_verified);

        Button signin = (Button)findViewById(R.id.login_button);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser firebaseUser = firebase.getCurrentUser();

                if(firebaseUser!=null)
                    firebaseUser.sendEmailVerification();

                startActivity(new Intent(EmailNotVerifedActivity.this,LoginActivity.class));
            }
        });

    }
}
