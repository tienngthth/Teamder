package com.example.teamder.model;

import static com.example.teamder.util.DateTimeUtil.getCurrentTime;

import com.google.firebase.firestore.DocumentSnapshot;

public class Message {

    private String timeStamp = getCurrentTime();
    private String content;
    private String groupId;
    private boolean isSeen = false;
    private String userId;

    public Message(String content, String groupId, String userId) {
        this.content = content;
        this.groupId = groupId;
        this.userId = userId;
    }

    public Message() {
    }

    public static Message parseMessage(DocumentSnapshot document) {
        Message message = new Message();
        message.setContent(document.getString("content"));
        message.setTimeStamp(document.getString("timeStamp"));
        message.setUserId(document.getString("userId"));
        message.setIsSeen(document.getBoolean("isSeen"));
        return message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String userID) {
        this.groupId = userID;
    }

    public boolean getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(boolean seen) {
        isSeen = seen;
    }
}
