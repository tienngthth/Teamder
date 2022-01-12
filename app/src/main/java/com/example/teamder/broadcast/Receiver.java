package com.example.teamder.broadcast;

import static android.telephony.TelephonyManager.EXTRA_STATE_IDLE;
import static com.example.teamder.activity.NotificationActivity.Type.Suggestion;
import static com.example.teamder.activity.RequestActivity.Status.approved;
import static com.example.teamder.activity.RequestActivity.Status.pending;
import static com.example.teamder.model.User.parseUser;
import static com.example.teamder.repository.NotificationRepository.createNotification;
import static com.example.teamder.repository.NotificationRepository.getNotificationByUserIdAndMessage;
import static com.example.teamder.repository.RequestRepository.getRequestsByPartiesAndStatus;
import static com.example.teamder.repository.UserRepository.getOtherUserByFieldValue;
import static com.example.teamder.repository.UserRepository.getUserById;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.example.teamder.model.CurrentUser;
import com.example.teamder.model.Notification;
import com.example.teamder.model.User;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Receiver extends BroadcastReceiver {

    private final User currentUser = CurrentUser.getInstance().getUser();
    private String previousPhoneState = EXTRA_STATE_IDLE;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.PHONE_STATE")){
            reportPhoneState(context, intent);
        }
    }

    private void reportPhoneState(Context context, Intent intent){
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        if (phoneNumber != null) {
            if (!previousPhoneState.equals(EXTRA_STATE_IDLE) && state.equals(EXTRA_STATE_IDLE)) {
                String message = "Check out your friend profile with phone number " + phoneNumber + ".";
                getNotificationByUserIdAndMessage(currentUser.getId(), message, (querySnapshot) ->  {
                    int size = querySnapshot.getDocuments().size();
                    if (size == 0) {
                        getOtherUserByFieldValue("phone", phoneNumber, (documentSnapshots) -> {
                            List<DocumentSnapshot> documents = documentSnapshots.getDocuments();
                            if (documents.size() > 0) {
                                checkIntersectCourses(message, documents.get(0).getString("id"));
                            }
                        });
                    }
                });
            }
            previousPhoneState = state;
        }
    }

    private void checkIntersectCourses(String message, String userId) {
        getUserById(userId, (documentSnapshot -> {
            User user = parseUser(documentSnapshot);
            ArrayList<String> parties = new ArrayList<>(Arrays.asList(userId, currentUser.getId()));
            getRequestsByPartiesAndStatus(pending.toString(), parties, (snapshot) -> {
                        final int[] requestNo = {snapshot.getDocuments().size()};
                        getRequestsByPartiesAndStatus(approved.toString(), parties, (documentSnapshots) -> {
                            requestNo[0] += documentSnapshots.getDocuments().size();
                            int courseAvailable = (countIntersectCourses(user) - requestNo[0]);
                            if (courseAvailable > 0) {
                                createNotification(new Notification(message, currentUser.getId(), Suggestion, userId));
                            }
                        });
                    }
            );
        }));
    }

    public int countIntersectCourses(User user) {
        ArrayList<String> intersectCourses = new ArrayList<>(user.getCourses());
        intersectCourses.retainAll(currentUser.getCourses());
        return intersectCourses.size();
    }

}
