package com.example.teamder.model;

import static com.example.teamder.util.DateTimeUtil.getCurrentTime;

import static com.example.teamder.activity.NotificationActivity.Type;
import com.google.firebase.firestore.DocumentSnapshot;

public class Notification {

    private String message;
    private String timeStamp = getCurrentTime();
    private String userId;
    private String id;
    private Type type;
    private boolean hasPushed = false;
    private boolean isSeen = false;

    public Notification() {
    }

    public Notification(String message, String userId, Type type) {
        this.message = message;
        this.userId = userId;
        this.type = type;
    }

    public static Notification parseNotification(DocumentSnapshot document) {
        Notification notification = new Notification();
        notification.setMessage(document.getString("message"));
        notification.setTimeStamp(document.getString("timeStamp"));
        notification.setType(Type.valueOf(document.getString("type")));
        notification.setId(document.getId());
        return notification;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isHasPushed() {
        return hasPushed;
    }

    public void setHasPushed(boolean hasPushed) {
        this.hasPushed = hasPushed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
