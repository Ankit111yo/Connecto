package com.example.connecto.Models;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Date;

public class CommentsModel {

    public String id;
    public String uid;
    public String author;
    public String profileImageUrl;
    public String comment;
    public Date date;

    public CommentsModel() {

    }

    public CommentsModel(String id, String uid,  String comment, Date date) {


        this.id = id;
        this.uid = uid;
        this.author = author;

         this.comment=comment;
        this.date = date;
    }




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


}
