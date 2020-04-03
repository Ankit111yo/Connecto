package com.example.connecto.Models;

import java.util.Date;

public class MessageModel {

    public String message;
    public String imageUrl;
    public Date date;
    public Boolean isRead;
    public Boolean isSender;

    public MessageModel(String message, String imageUrl, Date date) {
        this.message = message;
        this.imageUrl = imageUrl;
        this.date = date;
        this.isRead=false;
    }
    public MessageModel()
    {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public Boolean getSender() {
        return isSender;
    }

    public void setSender(Boolean sender) {
        isSender = sender;
    }
}
