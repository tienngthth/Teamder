package com.example.teamder.model;

import static com.example.teamder.util.DateTimeUtil.getCurrentTime;

public class Notification {

    private String message;
    private String timeStamp;
    private String userId;
    private String id;
    private boolean hasPushed = false;
    private boolean isSeen = false;

    public Notification() {
    }

    public Notification(String message, String userId) {
        this.message = message;
        this.timeStamp = getCurrentTime();
        this.userId = userId;
    }

    public static void broadcastNotification(String message, String userId) {

    }

    // parse from some type to notification
    public static Notification parseNotification() {
        Notification notification = new Notification();
        return notification;
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