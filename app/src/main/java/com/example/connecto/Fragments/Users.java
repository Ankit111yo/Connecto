package com.example.connecto.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.connecto.Adapters.UserAdapter;
import com.example.connecto.Models.UserModel;
import com.example.connecto.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Users extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<UserModel> userModels;
    private Context context;

    public Users()
    {

    }

    public static Users newInstance()
    {
        return new Users();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_users,container,false);
        context=view.getContext();
        recyclerView=view.findViewById(R.id.recyclerView);
        userModels=new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        getAllUSers();

        return view;
    }

    private void getAllUSers()
    {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();

        assert firebaseUser!=null;
        final String uid= firebaseUser.getUid();
        final Map<String,Boolean> map = new HashMap<>();

        databaseReference.child(uid).child("following").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds:dataSnapshot.getChildren())
                    {
                        String usid= (String) ds.getValue();
                        assert usid!=null;
                        map.put(usid,true);

                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot data= dataSnapshot.child("users");
                for(DataSnapshot ds:data.getChildren())
                {
                    String uidd= (String) ds.getValue();
                    if(uidd.equals(uid))
                        continue;
                    else if(map.containsKey(uidd))
                        userModels.add(new UserModel(uidd,true));
                    else
                        userModels.add(new UserModel(uidd,false));

                }
                UserAdapter userAdapter= new UserAdapter(userModels);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
