package com.example.connecto.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.connecto.ImageViewActivity;
import com.example.connecto.MessageActivity;
import com.example.connecto.Models.ChatModel;
import com.example.connecto.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private ArrayList<ChatModel> chatModel;
    Context context;
    DatabaseReference databaseReference;
    String userUid;


    public ChatAdapter(ArrayList<ChatModel> chatModel)
    {
        this.chatModel=chatModel;
    }
    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context=viewGroup.getContext();
        View view= LayoutInflater.from(context).inflate(R.layout.chat_card,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatAdapter.ViewHolder viewHolder, final int i) {
        final String uid =chatModel.get(i).getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String name= (String) dataSnapshot.child("name").getValue();
                viewHolder.textView.setText(name);
                final String profUrl= (String) dataSnapshot.child("profile_pic").getValue();

                if(profUrl!=null)
                {
                    Uri uri=Uri.parse(profUrl);

                    Glide.with(context).load(uri).into(viewHolder.imageView);
                }
                viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(context, ImageViewActivity.class);
                        intent.putExtra("url",profUrl);
                        context.startActivity(intent);
                    }
                });

                viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(context, MessageActivity.class);
                        intent.putExtra("name",name);
                        intent.putExtra("uid",uid);
                        context.startActivity(intent);
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return chatModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;
        ImageButton imageButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageButton=itemView.findViewById(R.id.chatButton);
            imageView=itemView.findViewById(R.id.profileImage);
            textView=itemView.findViewById(R.id.author);
        }
    }
}
