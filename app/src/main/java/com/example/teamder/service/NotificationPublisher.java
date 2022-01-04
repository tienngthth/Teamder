package com.example.teamder.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.teamder.R;

public class NotificationPublisher {

    private static NotificationManager notificationManager;

    public static void pushNotification(Context context, String message) {
        final int NOTIFY_ID = 0;
        String appId = context.getString(R.string.app_name);
        String title = context.getString(R.string.app_name);
        if (notificationManager == null) {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        registerChannel(appId, title);
        notificationManager.notify(NOTIFY_ID, createNotification(context, appId, message));
    }

    @SuppressLint("ObsoleteSdkInt")
    private static void registerChannel(String id, String title) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notificationManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notificationManager.createNotificationChannel(mChannel);
            }
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private static Notification createNotification(Context context, String appId, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, appId);
        Intent intent = new Intent(context, NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        builder.setContentTitle(message)
                .setSmallIcon(R.drawable.ic_avatar)
                .setContentText(context.getString(R.string.app_name))
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                .setPriority(Notification.PRIORITY_HIGH);
        return builder.build();
    }

}
