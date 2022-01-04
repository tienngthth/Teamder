package com.example.teamder.model;

import static com.example.teamder.util.DateTimeUtil.getCurrentTime;

import com.example.teamder.repository.NotificationRepository;
import com.google.firebase.firestore.DocumentSnapshot;

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
        NotificationRepository.createNotification(new Notification(message, userId));
    }

    public static Notification parseNotification(DocumentSnapshot document) {
        Notification notification = new Notification();
        notification.setMessage(document.getString("message"));
        notification.setTimeStamp(document.getString("timeStamp"));
        notification.setId(document.getId());
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
