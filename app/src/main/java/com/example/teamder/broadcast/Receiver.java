package com.example.teamder.broadcast;

import static android.telephony.TelephonyManager.EXTRA_STATE_IDLE;
import static com.example.teamder.activity.NotificationActivity.Type.Suggestion;
import static com.example.teamder.repository.NotificationRepository.createNotification;
import static com.example.teamder.repository.NotificationRepository.getNotificationByUserIdAndMessage;
import static com.example.teamder.repository.UserRepository.getUserByFieldValue;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.example.teamder.model.CurrentUser;
import com.example.teamder.model.Notification;
import com.example.teamder.model.User;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class Receiver extends BroadcastReceiver {

    private final User currentUser = CurrentUser.getInstance().getUser();
    private String previousPhoneState = EXTRA_STATE_IDLE;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.PHONE_STATE")){
            reportPhoneState(context, intent);
        }
        // should we do it for message as well?
    }

    private void reportPhoneState(Context context, Intent intent){
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        if (phoneNumber != null) {
            if (!previousPhoneState.equals(EXTRA_STATE_IDLE) && state.equals(EXTRA_STATE_IDLE)) {
                String message = "Check out your friend profile with phone number " + phoneNumber + ".";
                // only suggest this once?
                getNotificationByUserIdAndMessage(currentUser.getId(), message, (querySnapshot) ->  {
                    if (querySnapshot.getDocuments().size() == 0) {
                        getUserByFieldValue("phone", phoneNumber, (documentSnapshots) -> {
                            List<DocumentSnapshot> documents = documentSnapshots.getDocuments();
                            if (documents.size() > 0) {
                                createNotification(new Notification(message, currentUser.getId(), Suggestion, documents.get(0).getString("id")));
                            }
                        });
                    }
                });
            }
            previousPhoneState = state;
        }
    }

}
