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
import com.example.connecto.Fragments.Profile;
import com.example.connecto.ImageViewActivity;
import com.example.connecto.Models.UserModel;
import com.example.connecto.ProfileActivity;
import com.example.connecto.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private ArrayList<UserModel> userModels;
    private String userUid;
    DatabaseReference databaseReference;
    Context context;

    public UserAdapter(ArrayList<UserModel> userModels) {
        this.userModels = userModels;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       context=viewGroup.getContext();
        View view= LayoutInflater.from(context).inflate(R.layout.user_card,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserAdapter.ViewHolder viewHolder, final int i) {

        final String uid= userModels.get(i).getUid();
        userUid= FirebaseAuth.getInstance().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String author = (String) dataSnapshot.child("name").getValue();
                final String profilepic = (String) dataSnapshot.child("profile_pic").getValue();
                viewHolder.authorView.setText(author);

                if (profilepic != null)
                {
                    Uri uri= Uri.parse(profilepic);
                    Glide.with(context).load(uri).into(viewHolder.profileImageView);

                    viewHolder.profileImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent= new Intent(context, ImageViewActivity.class);
                            intent.putExtra("url",profilepic);
                            context.startActivity(intent);
                        }
                    });
                }
                else
                {
                    Glide.with(context).load(R.drawable.profile_pic).into(viewHolder.profileImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final Boolean bool = userModels.get(i).isFriend();
        if(bool){
            viewHolder.followButton.setVisibility(View.GONE);
            viewHolder.followButton.setEnabled(false);
            viewHolder.checkButton.setVisibility(View.VISIBLE);
            viewHolder.checkButton.setEnabled(true);
        }

        viewHolder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                follow(viewHolder, uid, i);
            }
        });

        viewHolder.checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unfollow(viewHolder, uid, i);
            }
        });

        viewHolder.authorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context, ProfileActivity.class);
                intent.putExtra("uid",uid);
                intent.putExtra("follows",bool);
                context.startActivity(intent);
            }
        });
    }







    private void follow(@NonNull final UserAdapter.ViewHolder viewHolder, final String uid, int index){
        viewHolder.followButton.setVisibility(View.GONE);
        viewHolder.followButton.setEnabled(false);
        viewHolder.checkButton.setVisibility(View.VISIBLE);
        viewHolder.checkButton.setEnabled(true);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                databaseReference.child(userUid).child("following").child(uid).setValue(uid);
                databaseReference.child(uid).child("followers").child(userUid).setValue(userUid);
                long number = (long) dataSnapshot.child(userUid).child("numberfollowing").getValue();
                databaseReference.child(userUid).child("numberfollowing").setValue(number + 1);
                long number2 = (long) dataSnapshot.child(uid).child("numberfollowers").getValue();
                databaseReference.child(uid).child("numberfollowers").setValue(number2 + 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        userModels.set(index, new UserModel(uid, true));
    }

    private void unfollow(@NonNull final UserAdapter.ViewHolder viewHolder, final String uid, int index){
        viewHolder.followButton.setVisibility(View.VISIBLE);
        viewHolder.followButton.setEnabled(true);
        viewHolder.checkButton.setVisibility(View.GONE);
        viewHolder.checkButton.setEnabled(false);
        userModels.set(index, new UserModel(uid, false));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.child(userUid).child("following").child(uid).getRef().removeValue();
                dataSnapshot.child(uid).child("followers").child(userUid).getRef().removeValue();
                long number = (long) dataSnapshot.child(userUid).child("numberfollowing").getValue();
                databaseReference.child(userUid).child("numberfollowing").setValue(number - 1);
                long number2 = (long) dataSnapshot.child(uid).child("numberfollowers").getValue();
                databaseReference.child(uid).child("numberfollowers").setValue(number2 - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView profileImageView;
        private TextView authorView;
        private ImageButton followButton;
        private ImageButton checkButton;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            authorView = itemView.findViewById(R.id.author);
            profileImageView = itemView.findViewById(R.id.profileImage);
            followButton = itemView.findViewById(R.id.followButton);
            checkButton = itemView.findViewById(R.id.checkButton);
            checkButton.setEnabled(false);
        }
    }
}
