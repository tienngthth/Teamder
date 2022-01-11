package com.example.teamder.model;

import static com.example.teamder.util.DateTimeUtil.getCurrentTime;

import com.example.teamder.repository.MessageRepository;
import com.google.firebase.firestore.DocumentSnapshot;

public class Message {
    private String uid;
    private String timeStamp = getCurrentTime();
    private String content;
    private String groupId;
    private boolean isSeen;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String userID) {
        this.groupId = userID;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public static Message parseMessage(DocumentSnapshot document){
        Message message = new Message();
        message.setContent(document.getString("content"));
        message.setTimeStamp(document.getString("timeStamp"));
        message.setUid(document.getId());
        message.setUserId(document.getString("userId"));
        return message;
    }
}
