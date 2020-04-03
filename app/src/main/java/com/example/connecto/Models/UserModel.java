package com.example.connecto.Models;

public class UserModel {

    public String uid;
    public boolean friend;

    public UserModel(String uid, boolean friend) {
        this.uid = uid;
        this.friend = friend;
    }

    public UserModel() {
    }

    public String getUid() {
        return uid;
    }

    public boolean isFriend() {
        return friend;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setFriend(boolean friend) {
        this.friend = friend;
    }

}
