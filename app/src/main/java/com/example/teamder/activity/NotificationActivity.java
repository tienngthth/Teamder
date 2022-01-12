package com.example.teamder.activity;

import static com.example.teamder.activity.NotificationActivity.Type.ApproveRequest;
import static com.example.teamder.activity.NotificationActivity.Type.CancelRequest;
import static com.example.teamder.activity.NotificationActivity.Type.Feedback;
import static com.example.teamder.activity.NotificationActivity.Type.GroupChange;
import static com.example.teamder.activity.NotificationActivity.Type.NewRequest;
import static com.example.teamder.activity.NotificationActivity.Type.RejectRequest;
import static com.example.teamder.activity.NotificationActivity.Type.Suggestion;
import static com.example.teamder.activity.ProfileActivity.Action.Explore;
import static com.example.teamder.model.IntentModel.IntentName.ActionType;
import static com.example.teamder.model.IntentModel.IntentName.GroupId;
import static com.example.teamder.model.IntentModel.IntentName.Id;
import static com.example.teamder.model.IntentModel.IntentName.Position;
import static com.example.teamder.model.IntentModel.IntentName.UserId;
import static com.example.teamder.model.Notification.parseNotification;
import static com.example.teamder.repository.UtilRepository.updateFieldToDb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamder.R;
import com.example.teamder.model.CurrentUser;
import com.example.teamder.model.Notification;
import com.example.teamder.repository.NotificationRepository;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    public enum Type {
        Feedback,
        Suggestion,
        Message,
        GroupChange,
        ApproveRequest,
        RejectRequest,
        CancelRequest,
        NewRequest,
    }
    private final ArrayList<String> newNotificationIDs = new ArrayList<>();
    private final String currentUserID = CurrentUser.getInstance().getUser().getId();
    private LinearLayout pastNotificationList, newNotificationList, pastNotificationGroup, newNotificationGroup;
    private LayoutInflater inflater;
    private TextView noNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initialiseVariables();
    }

    @Override
    public void onResume() {
        super.onResume();
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

    private void getPastNotifications() {
        NotificationRepository.getNotificationByUserIdAndSeenValue(
                currentUserID,
                true,
                (querySnapshot) -> {
                    updateNotificationView("PAST", querySnapshot);
                    getNewNotifications();
                }
        );
    }

    private void getNewNotifications() {
        NotificationRepository.getNotificationByUserIdAndSeenValue(
                currentUserID,
                false,
                (querySnapshot) -> {
                    updateNotificationView("NEW", querySnapshot);
                    noNotificationFound();
                }
        );
    }

    private void updateNotificationView(String notificationGroup, QuerySnapshot documents) {
        if (notificationGroup.equals("PAST")) {
            pastNotificationGroup.setVisibility(documents.size() < 1 ? View.GONE : View.VISIBLE);
            for (QueryDocumentSnapshot document : documents) {
                setupCustomItemView(pastNotificationList, parseNotification(document));
            }
        } else {
            newNotificationGroup.setVisibility(documents.size() < 1 ? View.GONE : View.VISIBLE);
            for (QueryDocumentSnapshot document : documents) {
                Notification notification = parseNotification(document);
                setupCustomItemView(newNotificationList, notification);
                newNotificationIDs.add(notification.getId());
            }
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
        ((TextView) itemView.findViewById(R.id.timestamp)).setText(notification.getTimeStamp().substring(0, notification.getTimeStamp().length() - 7));
        list.addView(itemView);
        itemView.setOnClickListener((view -> navigateUser(notification)));
    }

    private void navigateUser(Notification notification) {
        Type notificationType = notification.getType();
        if (notificationType.equals(Feedback)) {
            Intent intent = new Intent(NotificationActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else if (notificationType.equals(Suggestion)) {
            Intent intent = new Intent(NotificationActivity.this, ProfileActivity.class);
            intent.putExtra(ActionType.toString(), Explore);
            intent.putExtra(UserId.toString(), notification.getObjectId());
            startActivity(intent);
        } else if (notificationType.equals(NewRequest) || notificationType.equals(CancelRequest) ) {
            Intent intent = new Intent(NotificationActivity.this, ReviewActivity.class);
            intent.putExtra(Id.toString(), notification.getObjectId());
            intent.putExtra(Position.toString(), "received");
            startActivity(intent);
        } else if (notificationType.equals(RejectRequest)) {
            Intent intent = new Intent(NotificationActivity.this, ReviewActivity.class);
            intent.putExtra(Id.toString(), notification.getObjectId());
            intent.putExtra(Position.toString(), "sent");
            startActivity(intent);
        } else if (notificationType.equals(ApproveRequest)) {
            Intent intent = new Intent(NotificationActivity.this, GroupActivity.class);
            intent.putExtra(GroupId.toString(), notification.getObjectId());
            startActivity(intent);
        } else if (notificationType.equals(GroupChange)) {
            Intent intent = new Intent(NotificationActivity.this, GroupActivity.class);
            intent.putExtra(GroupId.toString(), notification.getObjectId());
            startActivity(intent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        seenAllNotification();
    }

    private void seenAllNotification() {
        for (String notificationID : newNotificationIDs) {
            updateFieldToDb("notifications", notificationID, "seen", true);
        }
    }
}