package com.example.teamder.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.teamder.model.CurrentUser;
import com.example.teamder.repository.NotificationRepository;
import com.example.teamder.repository.UtilRepository;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

public class NotificationService extends Service {

    public static ListenerRegistration listenerRegistration;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        listenForUpdate();
        return super.onStartCommand(intent, flags, startId);
    }

    public void listenForUpdate() {
        listenerRegistration = NotificationRepository.addSnapshotListenerForNotificationByUserIdAndHasPushedValue(
                CurrentUser.getInstance().getUser().getId(),
                false,
                (List<DocumentChange> documentChangeList) -> {
                    for (DocumentChange documentChange : documentChangeList) {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                            String id = documentChange.getDocument().getId();
                            String message = documentChange.getDocument().getString("message");
                            NotificationPublisher.pushNotification(NotificationService.this, message);
                            UtilRepository.updateFieldToDb("notifications", id, "hasPushed", true);
                        }
                    }
                }
        );
    }
}
