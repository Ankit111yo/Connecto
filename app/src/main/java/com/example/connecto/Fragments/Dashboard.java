package com.example.connecto.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.connecto.Adapters.PostAdapter;
import com.example.connecto.Models.PostModel;
import com.example.connecto.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Dashboard extends Fragment {

    private ArrayList<PostModel> posts;
    private RecyclerView recyclerView;
    private ArrayList<String> uids;
    private SwipeRefreshLayout swipeRefreshLayout;

    public Dashboard() {
    }

    public static Dashboard newInstance() {
        return new Dashboard();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view=inflater.inflate(R.layout.fragment_dashboard,container,false);
        recyclerView=view.findViewById(R.id.recycler);
        swipeRefreshLayout=view.findViewById(R.id.swipe_refresh);
        posts=new ArrayList<>();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPosts();
            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        getPosts();
 return view;
    }

    public void getPosts()
    {
    swipeRefreshLayout.setRefreshing(true);
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();

        assert firebaseUser!=null;
        uids=new ArrayList<>();
        final String uid=firebaseUser.getUid();
        uids.add(uid);

        databaseReference.child(uid).child("following").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d:dataSnapshot.getChildren())
                {
                    String followingid= (String) d.getValue();
                    uids.add(followingid);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                posts.clear();
                for(String userids:uids)
                {
                    String user= (String) dataSnapshot.child(userids).child("name").getValue();
                    DataSnapshot d=  dataSnapshot.child(userids).child("posts");
                    String profilepic= (String) dataSnapshot.child(userids).child("profile_pic").getValue();

                    for(DataSnapshot ds:d.getChildren())
                    {
                        PostModel post=ds.getValue(PostModel.class);
                        assert post!=null;
                        post.setAuthor(user);
                        post.setProfileImageUrl(profilepic);
                        posts.add(post);

                    }

                }



                    Collections.sort(posts, new Comparator<PostModel>() {
                        @Override
                        public int compare(PostModel o1, PostModel o2) {
                            return o2.getLastModifiedDate().compareTo(o1.getLastModifiedDate());
                        }
                    });


                PostAdapter postAdapter=new PostAdapter(posts);
                recyclerView.setAdapter(postAdapter);
                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
 swipeRefreshLayout.setRefreshing(false);
            }
        });

    }
}
