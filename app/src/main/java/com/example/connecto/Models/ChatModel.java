package com.example.connecto.Models;

public class ChatModel {


        public String uid;
        public String profurl;

        public ChatModel(String uid, String profurl) {
            this.uid = uid;
            this.profurl = profurl;
        }

        public ChatModel() {
        }

        public String getUid() {
            return uid;
        }

        public String getProfurl() {
            return profurl;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public void setProfurl(String profurl) {
            this.profurl= profurl;
        }



}
