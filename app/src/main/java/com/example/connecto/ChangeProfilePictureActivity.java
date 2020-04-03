package com.example.connecto;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.connecto.Models.PostModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.Objects;
import java.util.Random;

public class ChangeProfilePictureActivity extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    private ImageView profileImageView;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;
    private String uid;
    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_picture);

        Button uploadButton = findViewById(R.id.uploadButton);
        Button removeButton = findViewById(R.id.removeButton);
        profileImageView = findViewById(R.id.profileImage);
        progressBar = findViewById(R.id.progressBar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Change Profile Picture");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null)
            uid = firebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(uid);
        setImage();

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("profile_pic").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                Glide.with(ChangeProfilePictureActivity.this).load(R.drawable.profile_pic).into(profileImageView);
                Toast.makeText(ChangeProfilePictureActivity.this,"Profile pic removed",Toast.LENGTH_SHORT).show();
            }
        });



    }


    public void setImage()
    {
        progressBar.setVisibility(View.VISIBLE);

        databaseReference.child("profile_pic").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url =(String)dataSnapshot.getValue();
                if(url==null) {
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                Uri uri=Uri.parse(url);
                Glide.with(ChangeProfilePictureActivity.this).
                        load(uri).
                        centerCrop().
                        placeholder(R.drawable.profile_pic).
                        into(profileImageView);

                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void selectImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE&&data!=null)
        {
            imageUpload(data);
        }
    }

    private void imageUpload(final Intent data)
    {
        final Uri selectedImageuri;
        if(data!=null)
            selectedImageuri=data.getData();
        else
            selectedImageuri=null;

        if(selectedImageuri!=null)
        {
            final String randomString;
            Random random =  new Random();
            long rand = random.nextLong();
            randomString = Long.toString(rand);

            final StorageReference storageReference= FirebaseStorage.getInstance().
                                               getReference().child("profile_pic").
                                               child(uid).child(randomString);

            progressBar.setVisibility(View.VISIBLE);

            storageReference.putFile(selectedImageuri).
                    addOnSuccessListener(ChangeProfilePictureActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Glide.with(ChangeProfilePictureActivity.this).
                            load(selectedImageuri).centerCrop().
                            placeholder(R.drawable.profile_pic).into(profileImageView);


                    storageReference.getDownloadUrl().addOnSuccessListener(ChangeProfilePictureActivity.this, new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Date date =new Date();
                            PostModel postModel=new PostModel(randomString,firebaseUser.getUid(),null,uri.toString(),date);
                            databaseReference.child("posts").child(randomString).setValue(postModel);
                            databaseReference.child("profile_pic").setValue(uri.toString());
                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(uri)
                                    .build();
                            firebaseUser.updateProfile(request);
                        }
                    });

                    Toast.makeText(ChangeProfilePictureActivity.this, "Profile Picture Changed Successfully", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }).addOnFailureListener(ChangeProfilePictureActivity.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ChangeProfilePictureActivity.this, "Error in changing Profile Pic", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });

        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        //return super.onSupportNavigateUp();
        Intent intent = getIntent();
        if(intent.getExtras() == null)
            onBackPressed();
        else
            startActivity(new Intent(ChangeProfilePictureActivity.this, MainActivity.class));
        return true;
    }
}
