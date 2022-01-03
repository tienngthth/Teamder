package com.example.teamder.model;

public class Message {
    private String uid;
    private String timeStamp;
    private String content;
    private String userID;
    private boolean isSeen;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
