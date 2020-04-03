package com.example.connecto.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.connecto.Adapters.PostAdapter;
import com.example.connecto.EditProfileActivity;
import com.example.connecto.ImageViewActivity;
import com.example.connecto.Models.PostModel;
import com.example.connecto.PostActivity;
import com.example.connecto.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.zip.Inflater;

public class Profile extends Fragment {
    private ArrayList<PostModel> posts;
    private RecyclerView recyclerView;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private ImageView coverView;
    private ImageView profilePicView;
    private TextView nameView;
    private TextView aboutView;
    private TextView followerView;
    private TextView followingView;
    private Context context;

    public Profile()
    {

    }

    public static Profile newInstance()
    {
        return new Profile();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view=inflater.inflate(R.layout.fragment_profile,container,false);
        recyclerView = view.findViewById(R.id.recyclerView);
        coverView = view.findViewById(R.id.coverPic);
        profilePicView = view.findViewById(R.id.profilePic);
        nameView = view.findViewById(R.id.name);
        aboutView = view.findViewById(R.id.about);
        followerView = view.findViewById(R.id.followers);
        followingView = view.findViewById(R.id.following);
        Button editProfile = view.findViewById(R.id.editProfile);
        Button postButton = view.findViewById(R.id.postButton);

        context=view.getContext();
        posts=new ArrayList<>();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setNestedScrollingEnabled(false);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent= new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);*/
                startActivity(new Intent(getActivity(), EditProfileActivity.class));


            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getActivity(), PostActivity.class);
                startActivity(intent);
            }
        });

        loadIntro();

        getAllPosts();

        return view;

    }


    private void loadIntro()
    {
        final String uid=firebaseUser.getUid();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String coverurl= (String) dataSnapshot.child(uid).child("cover_pic").getValue();
                if(coverurl!=null)
                {
                    Uri uri=Uri.parse(coverurl);
                    coverView.setBackground(null);

                    Glide.with(context).load(uri).into(coverView);

                    coverView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent= new Intent(getActivity(), ImageViewActivity.class);
                            intent.putExtra("url",coverurl);
                            startActivity(intent);

                        }
                    });
                }
                else
                {
                    Glide.with(context).load(R.drawable.header).into(coverView);
                }

                final String profileurl= (String) dataSnapshot.child(uid).child("profile_pic").getValue();
                if(coverurl!=null)
                {
                    Uri uri=Uri.parse(profileurl);
                    profilePicView.setBackground(null);

                    Glide.with(context).load(uri).into(profilePicView);

                    profilePicView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent= new Intent(getActivity(), ImageViewActivity.class);
                            intent.putExtra("url",profileurl);
                            startActivity(intent);

                        }
                    });
                }
                else
                {
                    Glide.with(context).load(R.drawable.header).into(profilePicView);
                }

                String name = (String) dataSnapshot.child(uid).child("name").getValue();
                nameView.setText(name);

                String about = (String) dataSnapshot.child(uid).child("about").getValue();
                if(about != null)
                    aboutView.setText(about);

                long numberOfFollowers = (long) dataSnapshot.child(uid).child("numberfollowers").getValue();
                String followers = Long.toString(numberOfFollowers).concat(" Followers");
                followerView.setText(followers);

                long numberOfFollowing = (long) dataSnapshot.child(uid).child("numberfollowing").getValue();
                String following = Long.toString(numberOfFollowing).concat(" Following");
                followingView.setText(following);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAllPosts()
    {
        final String uid= firebaseUser.getUid();

      databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              DataSnapshot data = dataSnapshot.child(uid).child("posts");
              String author= (String) dataSnapshot.child(uid).child("name").getValue();
              String profileUrl= (String) dataSnapshot.child(uid).child("profile_pic").getValue();
                  for(DataSnapshot ds:data.getChildren())
                  {
                      PostModel post= ds.getValue(PostModel.class);
                      assert post!=null;
                      post.setAuthor(author);
                      post.setProfileImageUrl(profileUrl);
                      posts.add(post);
                  }
              Collections.sort(posts, new Comparator<PostModel>() {
                  @Override
                  public int compare(PostModel o1, PostModel o2) {
                      return o2.getDate().compareTo(o1.getDate());
                  }
              });

              PostAdapter postAdapter=new PostAdapter(posts);
              recyclerView.setAdapter(postAdapter);
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });
    }


}
