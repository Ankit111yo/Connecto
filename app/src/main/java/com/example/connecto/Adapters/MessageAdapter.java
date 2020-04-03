package com.example.connecto.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompatSideChannelService;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.connecto.ImageViewActivity;
import com.example.connecto.Models.MessageModel;
import com.example.connecto.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {


    private Context context;
    ArrayList<MessageModel> messageModel;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    public MessageAdapter(ArrayList<MessageModel> mesageModel)
    {
        this.messageModel=mesageModel;
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel message=messageModel.get(position);
        if(message.getSender())
            return VIEW_TYPE_MESSAGE_SENT;
        else
            return VIEW_TYPE_MESSAGE_RECEIVED;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
        context=viewGroup.getContext();
        if(viewtype==VIEW_TYPE_MESSAGE_SENT)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.message_sent,viewGroup,false);
            return new ViewHolder(view);

        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.message_received,viewGroup,false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        if(messageModel.get(i).getImageUrl()!=null)
        {
            Uri uri=Uri.parse(messageModel.get(i).getImageUrl());
            if(uri!=null)
            {
                Glide.with(context).load(uri).into(viewHolder.imageView);
            }

            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, ImageViewActivity.class);
                    intent.putExtra("url",messageModel.get(i).getImageUrl());
                    context.startActivity(intent);
                }
            });
        }
        else
        {
            Glide.with(context).load((Bitmap) null).into(viewHolder.imageView);
        }


        if(messageModel.get(i).getMessage()!=null)
        {
            viewHolder.textView.setText(messageModel.get(i).getMessage());
        }
        else
            viewHolder.textView.setText(null);

        Date date = messageModel.get(i).getDate();
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, hh:mm a");
        String strDate = formatter.format(date);
        viewHolder.dateView.setText(strDate);
    }

    @Override
    public  int getItemCount() {
        return messageModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;
        private TextView dateView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.image);
            textView=itemView.findViewById(R.id.text);
            dateView=itemView.findViewById(R.id.time);
        }
    }
}
