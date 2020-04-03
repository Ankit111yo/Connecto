package com.example.connecto.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.connecto.ImageViewActivity;
import com.example.connecto.Models.CommentsModel;
import com.example.connecto.Models.PostModel;
import com.example.connecto.ProfileActivity;
import com.example.connecto.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


import java.util.Date;
import java.util.Map;
import java.util.Random;

import static java.lang.StrictMath.max;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {


    private ArrayList<PostModel> postModels;
    private ArrayList<CommentsModel> commentModels;
    private Context context;
    private CommentAdapter commentAdapter;
    private String userUid;

    public PostAdapter(ArrayList<PostModel> posts) {
        
        this.postModels=posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.post, viewGroup, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        userUid= FirebaseAuth.getInstance().getUid();

        setProfilepic(viewHolder,i);
        setAuthor(viewHolder,i);
        setDate(viewHolder, i);
        setCaption(viewHolder,i);

        setInfo(viewHolder, i);
        setLike(viewHolder, i);
        setComment(viewHolder, i);
        setShare(viewHolder, i);
        setImage(viewHolder, i);
    }


    private void setProfilepic(ViewHolder viewHolder,int i)
    {
        final String profilepic=postModels.get(i).getProfileImageUrl();
        if(profilepic!=null)
        {
            Uri uri = Uri.parse(profilepic);

            Glide.with(context).load(uri).into(viewHolder.profileImageView);
        }
        else
        {
            Glide.with(context).load(R.drawable.profile_pic).into(viewHolder.profileImageView);
        }

        viewHolder.profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(profilepic!=null)
                {
                    Intent intent= new Intent(context, ImageViewActivity.class);
                    intent.putExtra("url",profilepic);
                    context.startActivity(intent);
                }
            }
        });
    }

    private void setAuthor(ViewHolder viewHolder,int i) {

        String author = postModels.get(i).getAuthor();
        final String uid = postModels.get(i).getUid();

        viewHolder.authorView.setText(author);
        viewHolder.authorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userUid!=uid)
                {
                    Intent intent= new Intent(context, ProfileActivity.class);
                    intent.putExtra("uid",uid);
                    intent.putExtra("follows",true);
                    context.startActivity(intent);
                }
            }
        });
    }

    private void setDate(ViewHolder viewHolder,int i)
    {

        Date date = postModels.get(i).getDate();

        if(date!=null) {
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd");
            String strDate = formatter.format(date);
            SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");

            String strTime = timeFormatter.format(date);
            String time = strDate.concat(" at ").concat(strTime);
            viewHolder.dateView.setText(time);
        }

    }

    private void setCaption(ViewHolder viewHolder,int i)
    {
        if(postModels.get(i).getCaption() != null)
            viewHolder.captionView.setText(postModels.get(i).getCaption());
        else
            viewHolder.captionView.setText(null);
    }

    private void setImage(ViewHolder viewHolder, final int i)
    {
        if(postModels.get(i).getImageUrl() != null){
            String url = postModels.get(i).getImageUrl();
            Uri uri = Uri.parse(url);
            Glide.with(context)
                    .load(uri)
                    .into(viewHolder.imageView);
        }
        else{
            String urr=postModels.get(i).getProfileImageUrl();
            Uri urs= Uri.parse(urr);
            Glide.with(context)
                    .load(R.drawable.profile_pic)
                    .into(viewHolder.imageView);
        }

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(postModels.get(i).getImageUrl() != null){
                    Intent intent = new Intent(context, ImageViewActivity.class);
                    intent.putExtra("url", postModels.get(i).getImageUrl());
                    context.startActivity(intent);
                }
            }
        });
    }



    private  void setInfo(ViewHolder viewHolder,int i)
    {
        int numberOfLikes = postModels.get(i).getNumberOfLikes();
        int numberOfComments = postModels.get(i).getNumberOfComments();
        String likes = Integer.toString(numberOfLikes);
        String comments = Integer.toString(numberOfComments);

        viewHolder.infoView.setText(likes.concat(" likes ").concat(comments).concat(" comments "));
    }

   private void setLike(final ViewHolder viewHolder, final int i)
   {
       Map<String, Boolean> likes = postModels.get(i).getLikes();
       if(likes.containsKey(userUid)){
           viewHolder.likeButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_liked));
       }else{
           viewHolder.likeButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like));
       }

       final boolean[] liked = new boolean[1];
       viewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String uid = postModels.get(i).getUid();
               DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(uid);
               String id = postModels.get(i).getId();
               DatabaseReference postRef = databaseReference.child("posts").child(id);
               postRef.runTransaction(new Transaction.Handler() {
                   @NonNull
                   @Override
                   public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                       PostModel p = mutableData.getValue(PostModel.class);
                       if(p == null) {
                           return Transaction.success(mutableData);
                       }

                       if(p.likes.containsKey(userUid)){
                           p.numberOfLikes = p.numberOfLikes - 1;
                           p.likes.remove(userUid);
                           liked[0] = false;
                       }
                       else {
                           p.numberOfLikes = p.numberOfLikes + 1;
                           assert userUid != null;
                           p.likes.put(userUid, true);
                           liked[0] = true;
                       }

                       mutableData.setValue(p);
                       return Transaction.success(mutableData);
                   }

                   @Override
                   public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                       Map<String, Boolean> likes = postModels.get(i).getLikes();
                       if(liked[0]){
                           viewHolder.likeButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_liked));
                           int numberOfLikes = postModels.get(i).getNumberOfLikes() + 1;
                           postModels.get(i).setNumberOfLikes(numberOfLikes);
                           assert userUid != null;
                           likes.put(userUid, true);
                       }
                       else{
                           viewHolder.likeButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like));
                           int numberOfLikes = postModels.get(i).getNumberOfLikes() - 1;
                           postModels.get(i).setNumberOfLikes(max(0,numberOfLikes));
                           likes.remove(userUid);
                       }
                       postModels.get(i).setLikes(likes);
                       setInfo(viewHolder, i);
                   }
               });
           }
       });
   }


   private void setComment(final ViewHolder viewHolder, final int i)
   {
       final String uid = postModels.get(i).getUid();
//       Log.e("uid in set comment",uid);
       final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child(uid);
       commentModels= new ArrayList<>();

       viewHolder.commentButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               AlertDialog.Builder builder = new AlertDialog.Builder(context);
               LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
               final View view = inflater.inflate(R.layout.layout_comment, null);
               builder.setView(view);
               final Dialog dialog = builder.create();
               dialog.setContentView(R.layout.layout_comment);
               dialog.show();


               final RecyclerView recyclerView=view.findViewById(R.id.recyclerView);
               recyclerView.setLayoutManager(new LinearLayoutManager(context));
               final EditText newcomment = view.findViewById(R.id.comment);
               Button postButton = view.findViewById(R.id.postButton);

               final String id = postModels.get(i).getId();
               final DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference();
               commentModels.clear();
               commentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       DataSnapshot data= dataSnapshot.child(uid).child("posts").child(id).child("comments");
                       for(DataSnapshot ds:data.getChildren())
                       {
                           CommentsModel comment = ds.getValue(CommentsModel.class);
                           assert comment != null;
                           String authorUid = comment.getUid();
                           String author = (String) dataSnapshot.child(authorUid).child("name").getValue();
                           String profileUrl = (String) dataSnapshot.child(authorUid).child("profile_pic").getValue();
                           comment.setAuthor(author);
                           comment.setProfileImageUrl(profileUrl);
                           commentModels.add(comment);
                       }



                     Collections.sort(commentModels, new Comparator<CommentsModel>() {
                         @Override
                         public int compare(CommentsModel o1, CommentsModel o2) {
                             return o1.getDate().compareTo(o2.getDate());
                         }
                     });


                       commentAdapter = new CommentAdapter(commentModels);
                       recyclerView.setAdapter(commentAdapter);
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });


               postButton.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       final String comment = newcomment.getText().toString().trim();

                       if (!comment.equals(""))
                       {
                           final String randomString;
                           Random random =  new Random();
                           long rand = random.nextLong();
                           randomString = Long.toString(rand);
                           final Date date = new Date();

                           commentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                   CommentsModel commentsModel= new CommentsModel(randomString,userUid,comment,date);
                                   String author= (String) dataSnapshot.child(userUid).child("name").getValue();
                                   String profileUrl = (String) dataSnapshot.child(userUid).child("profile_pic").getValue();
                                   databaseReference.child("posts").child(id).child("comments").child(randomString).setValue(commentsModel);
                                   commentsModel.setAuthor(author);
                                   commentsModel.setProfileImageUrl(profileUrl);
                                   commentModels.add(commentsModel);
                                   newcomment.setText("");
                                   commentAdapter.notifyDataSetChanged();



                                   String id = postModels.get(i).getId();
                                   DatabaseReference postRef = databaseReference.child("posts").child(id);
                                   postRef.runTransaction(new Transaction.Handler() {
                                       @NonNull
                                       @Override
                                       public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                           PostModel p = mutableData.getValue(PostModel.class);
                                           if(p == null) {
                                               return Transaction.success(mutableData);
                                           }
                                           Date date = new Date();

                                           p.numberOfComments = p.numberOfComments + 1;
                                           p.lastModifiedDate = date;
                                           mutableData.setValue(p);
                                           return Transaction.success(mutableData);
                                       }

                                       @Override
                                       public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                           int numberOfComments = postModels.get(i).getNumberOfComments() + 1;
                                           postModels.get(i).setNumberOfComments(numberOfComments);
                                           setInfo(viewHolder, i);
                                       }
                                   });

                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError databaseError) {

                               }
                           });





                       }

                   }
               });

           }
       });
   }


   private void setShare(ViewHolder viewHolder,int i)
   {
       final String caption = postModels.get(i).getCaption();
       final String imageUrl = postModels.get(i).getImageUrl();
       if(imageUrl != null) {
           final StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
           try {
               final File localFile = File.createTempFile("images", ".jpg");
               viewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       final ProgressDialog progressDialog = new ProgressDialog(context);
                       progressDialog.setCancelable(false);
                       progressDialog.setMessage("Opening Menu");
                       progressDialog.show();
                       storageRef.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                           @Override
                           public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                               progressDialog.dismiss();
                               Uri uri = FileProvider.getUriForFile(context, "com.example.congenialtelegram.provider", localFile);
                               Intent share = new Intent(Intent.ACTION_SEND);
                               share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                               share.putExtra(Intent.EXTRA_STREAM, uri);
                               share.setType("image/*");
                               share.putExtra(Intent.EXTRA_TEXT, caption);

                               context.startActivity(Intent.createChooser(share, "Share"));
                           }
                       });
                   }
               });
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
       else{
           viewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   String message = caption;
                   Intent share = new Intent(Intent.ACTION_SEND);
                   share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                   share.setType("text/*");
                   share.putExtra(Intent.EXTRA_TEXT, message);

                   context.startActivity(Intent.createChooser(share, "Share"));
               }
           });
       }
   }

    @Override
    public int getItemCount() {
        return postModels.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView authorView;
        private TextView dateView;
        private TextView captionView;
        private ImageView imageView;
        private ImageView profileImageView;
        private TextView infoView;
        private ImageButton likeButton;
        private ImageButton commentButton;
        private ImageButton shareButton;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            authorView = itemView.findViewById(R.id.author);
            dateView = itemView.findViewById(R.id.date);
            captionView = itemView.findViewById(R.id.caption);
            imageView = itemView.findViewById(R.id.image);
            profileImageView = itemView.findViewById(R.id.profileImage);
            infoView = itemView.findViewById(R.id.info);
            likeButton = itemView.findViewById(R.id.like);
            commentButton = itemView.findViewById(R.id.comment);
            shareButton = itemView.findViewById(R.id.share);
        }
    }

}
