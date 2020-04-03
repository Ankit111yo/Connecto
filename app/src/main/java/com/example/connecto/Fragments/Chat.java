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

import com.example.connecto.Adapters.ChatAdapter;
import com.example.connecto.Models.ChatModel;
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

public class Chat extends Fragment {

    private RecyclerView recyclerView;
    private Context context;
    private ArrayList<ChatModel> chatmodel;

    public Chat()
    {

    }

    public static Chat newInstance()
    {
        return new Chat();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_chat,container,false);
        context=view.getContext();
        recyclerView=view.findViewById(R.id.recyclerView);
        chatmodel =new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        getAllChats();
        return view;
    }
    private void getAllChats()
    {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        String uid=firebaseUser.getUid();
        databaseReference.child(uid).child("message").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    String userUid= (String) ds.getKey();

                    ChatModel chatModel= new ChatModel(userUid,null);
                    chatmodel.add(chatModel);
                }

                ChatAdapter chatAdapter= new ChatAdapter(chatmodel);
                recyclerView.setAdapter(chatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

