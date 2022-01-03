package com.example.teamder.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamder.R;
import com.example.teamder.model.Notification;

public class NotificationActivity extends AppCompatActivity {

    private LinearLayout pastNotificationList, newNotificationList, pastNotificationGroup, newNotificationGroup;
    private LayoutInflater inflater;
    private TextView noNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initialiseVariables();
        getPastNotifications();
    }

    private void initialiseVariables() {
        inflater = LayoutInflater.from(this);
        pastNotificationList = findViewById(R.id.past_notification_list);
        newNotificationList = findViewById(R.id.new_notification_list);
        pastNotificationGroup = findViewById(R.id.past_notification_group);
        newNotificationGroup = findViewById(R.id.new_notification_group);
        noNotification = findViewById(R.id.no_notification);
    }

    // get all pass notification
    private void getPastNotifications() {
        // 1. get pass notification
        // 2. call updateNotificationView
        // 3. call getNewNotifications
    }

    // get new notification
    private void getNewNotifications() {
        // 1. get new notification
        // 2. call updateNotificationView
    }

    // update notification view, update parameters to pass in results type ()
    private void updateNotificationView(String notificationGroup) {
        if (notificationGroup.equals("PAST")) {
            // 1. set pastNotificationGroup visibility based on notification lengths
            // 2. call setupCustomItemView
        } else {
            // 1. set newNotificationGroup visibility based on notification lengths
            // 2. call setupCustomItemView
        }
    }

    private void noNotificationFound() {
        noNotification.setVisibility(
                (pastNotificationGroup.getVisibility() == View.GONE && newNotificationGroup.getVisibility() == View.GONE)
                        ? View.VISIBLE
                        : View.GONE);
    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    private void setupCustomItemView(LinearLayout list, Notification notification) {
        View itemView = inflater.inflate(R.layout.notification_row, null, false);
        ((TextView) itemView.findViewById(R.id.message)).setText(notification.getMessage());
        ((TextView) itemView.findViewById(R.id.timestamp)).setText(notification.getTimeStamp());
        list.addView(itemView);
    }

    @Override
    public void onPause() {
        super.onPause();
        seenAllNotification();
    }

    //  mark all notification to seen
    private void seenAllNotification() {

    }
}