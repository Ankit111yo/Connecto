package com.example.connecto;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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

public class EditProfileActivity extends AppCompatActivity {

   private static final int REQ_CODE_IMAGE_INPUT = 1;
    private EditText nameEdit;
    private EditText aboutEdit;
    private ProgressBar progressBar;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private String uid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        Button updateCoverButton = findViewById(R.id.coverPic);
        Button updateProfileButton = findViewById(R.id.updateButton);
        Button updateProfilePictureButton = findViewById(R.id.profilePic);
        nameEdit = findViewById(R.id.name);
        aboutEdit = findViewById(R.id.about);
        progressBar = findViewById(R.id.progressBar);

    setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

       firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser!=null;
         uid=firebaseUser.getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference().child(uid);

        loadData();

        updateProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EditProfileActivity.this,ChangeProfilePictureActivity.class);
                startActivity(intent);
            }
        });

        updateCoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseCoverImage();
            }
        });

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });


    }

    private void loadData()
    {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             String name = (String) dataSnapshot.child("name").getValue();
             nameEdit.setText(name);

                String about = (String) dataSnapshot.child("about").getValue();
                if(about != null)
                    aboutEdit.setText(about);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void chooseCoverImage()
    {
        Intent cover=new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(cover,REQ_CODE_IMAGE_INPUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQ_CODE_IMAGE_INPUT&&data!=null)
            loadCoverImage(data);
    }


    private void loadCoverImage(@Nullable Intent coverdata)
    {
        final Uri selectedImage;
        if(coverdata!=null)
        {
             selectedImage= coverdata.getData();
        }
        else
            selectedImage=null;


        if(selectedImage!=null)
        {
            final String randomString;
            Random random =  new Random();
            long rand = random.nextLong();
            randomString = Long.toString(rand);

            final StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("cover_pic").child(uid).child(randomString);

            progressBar.setVisibility(View.VISIBLE);
            nameEdit.setEnabled(false);
            aboutEdit.setEnabled(false);

            storageReference.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Date date=new Date();
                            PostModel postModel= new PostModel(randomString,firebaseUser.getUid(),null,uri.toString(),date);
                            databaseReference.child("posts").child(randomString).setValue(postModel);
                            databaseReference.child("cover_pic").setValue(uri.toString());
                        }

                    });
                    Toast.makeText(EditProfileActivity.this, "Cover Picture Changed Successfully", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    nameEdit.setEnabled(true);
                    aboutEdit.setEnabled(true);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfileActivity.this, "Error while changing Cover Picture", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    nameEdit.setEnabled(true);
                    aboutEdit.setEnabled(true);
                }
            });
        }
    }

    private void updateProfile()
    {
        String name = nameEdit.getText().toString().trim();
        String about = aboutEdit.getText().toString().trim();
        databaseReference.child("name").setValue(name);
        databaseReference.child("about").setValue(about);


        UserProfileChangeRequest userProfileChangeRequest= new UserProfileChangeRequest.Builder().setDisplayName(name).build();
        firebaseUser.updateProfile(userProfileChangeRequest);

        Toast.makeText(EditProfileActivity.this, "Profile Updated", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
        intent.putExtra("key", true);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
        intent.putExtra("key", true);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
       Intent intent=new Intent(EditProfileActivity.this,MainActivity.class);
       intent.putExtra("key",true);
       startActivity(intent);
       return true;

    }
}

