package com.example.connecto.Adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.connecto.ImageViewActivity;
import com.example.connecto.Models.CommentsModel;
import com.example.connecto.ProfileActivity;
import com.example.connecto.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {


    private ArrayList<CommentsModel>commentsModels;
    private Context context;

    public CommentAdapter(ArrayList<CommentsModel> commentModels)
    {
        this.commentsModels=commentModels;
    }
    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context=viewGroup.getContext();
        View view= LayoutInflater.from(context).inflate(R.layout.comment_card,viewGroup,false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentAdapter.ViewHolder viewHolder, int i) {
              viewHolder.authorView.setText(commentsModels.get(i).getAuthor());
              viewHolder.commentView.setText(commentsModels.get(i).getComment());

              final String uid = commentsModels.get(i).getUid();
              viewHolder.authorView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      String userId= FirebaseAuth.getInstance().getUid();
                      if(!userId.equals(uid))
                      {
                          Intent intent= new Intent(context, ProfileActivity.class);
                          intent.putExtra("uid",uid);
                          intent.putExtra("follows",true);
                          context.startActivity(intent);
                      }

                  }
              });

              final String picurl=commentsModels.get(i).getProfileImageUrl();


              if(picurl!=null)
              {
                  Uri uri=Uri.parse(picurl);
                  Glide.with(context).load(uri).into(viewHolder.imageView);
              }
              else
              {
                  Glide.with(context).load(R.drawable.profile_pic).into(viewHolder.imageView);
              }


              viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      if(picurl!=null)
                      {
                          Intent intent=new Intent(context,ImageViewActivity.class);
                          intent.putExtra("url",picurl);
                          context.startActivity(intent);
                      }
                  }
              });
    }

    @Override
    public int getItemCount() {
        return commentsModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView authorView;
        private TextView commentView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.profileImage);
            authorView=itemView.findViewById(R.id.author);
            commentView=itemView.findViewById(R.id.commentText);
        }
    }
}
