package com.example.connecto.Models;

public class NotificationModel {

    public String userName;
    public Boolean notify;

    public NotificationModel(String userName, Boolean notify) {
        this.userName = userName;
        this.notify = notify;
    }

    public NotificationModel() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Boolean getNotify() {
        return notify;
    }

    public void setNotify(Boolean notify) {
        this.notify = notify;
    }
}
