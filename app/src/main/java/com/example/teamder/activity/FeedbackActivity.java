package com.example.teamder.activity;

import static com.example.teamder.activity.NotificationActivity.Type.Feedback;
import static com.example.teamder.model.Group.parseGroup;
import static com.example.teamder.model.IntentModel.IntentName.GroupId;
import static com.example.teamder.model.IntentModel.IntentName.UserId;
import static com.example.teamder.model.User.parseUser;
import static com.example.teamder.repository.GroupRepository.getGroupById;
import static com.example.teamder.repository.NotificationRepository.createNotification;
import static com.example.teamder.repository.ReviewRepository.createReview;
import static com.example.teamder.repository.UserRepository.getUserById;
import static com.example.teamder.util.ScreenUtil.clearFocus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamder.R;
import com.example.teamder.model.CurrentUser;
import com.example.teamder.model.Notification;
import com.example.teamder.model.Review;
import com.example.teamder.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class FeedbackActivity extends AppCompatActivity {

    private final User currentUser = CurrentUser.getInstance().getUser();
    private Button sendButton, cancelButton;
    private LinearLayout feedbackListView, fullConstraint;
    private ArrayList<String> userIds = new ArrayList<>();
    private LayoutInflater inflater;
    private HashMap<String, EditText> feedbackMessages = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initialiseVariables();
        setUpListeners();
        checkIntent();
    }

    private void initialiseVariables() {
        cancelButton = findViewById(R.id.cancel_button);
        feedbackListView = findViewById(R.id.feedback_list);
        sendButton = findViewById(R.id.send_button);
        fullConstraint = findViewById(R.id.feedback_activity);
        inflater = LayoutInflater.from(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpListeners() {
        sendButton.setOnClickListener((View view) -> sendFeedback());
        cancelButton.setOnClickListener((View view) -> finish());
        fullConstraint.setOnTouchListener((view, event) -> clearInputFieldsFocus(view));
    }

    private boolean clearInputFieldsFocus(View view) {
        for(String userId: userIds) {
            if (feedbackMessages.get(userId) != null) {
                clearFocus(view, Objects.requireNonNull(feedbackMessages.get(userId)), this);
            }
        }
        return true;
    }

    private void checkIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String userId = bundle.getString(UserId.toString());
            String groupId = bundle.getString(GroupId.toString());
            if (userId != null) {
                userIds.add(userId);
                generateInputForm();
                fullConstraint.setVisibility(View.VISIBLE);
            } else {
                getGroupById(groupId, (documentSnapshot) -> {
                    userIds = parseGroup(documentSnapshot).getUserIds();
                    if (userIds.size() > 0 && (userIds.size() > 1 || !userIds.contains(currentUser.getId()))) {
                        fullConstraint.setVisibility(View.VISIBLE);
                        generateInputForm();
                    } else {
                        finish();
                    }
                });
            }
        }
    }

    private void generateInputForm() {
        feedbackListView.removeAllViews();
        for (String userId: userIds) {
            if (!currentUser.getId().equals(userId)) {
                getUserById(userId, (documentSnapshot -> {
                    setupCustomReceivedRequestView(parseUser(documentSnapshot));
                }));
            }
        }
    }

    @SuppressLint("SetTextI18n, InflateParams")
    private void setupCustomReceivedRequestView(User user) {
        View itemView = inflater.inflate(R.layout.feedback_input, null, false);
        EditText editText = itemView.findViewById(R.id.feedback);
        ((TextView) itemView.findViewById(R.id.name)).setText("To " + user.getName());
        feedbackListView.addView(itemView);
        feedbackMessages.put(user.getId(), editText);
    }

    private void sendFeedback() {
        for (String userId: userIds) {
            EditText editText = feedbackMessages.get(userId);
            if (editText != null) {
                String message = editText.getText().toString().trim();
                if (!message.equals("")) {
                    createReview(new Review(userId, message));
                    createNotificationInDb(userId);
                }
            }
        }
        displayToast();
        finish();
    }

    private void displayToast() {
        String reviewSuccess = "Thank you for your review!";
        Toast.makeText(this, reviewSuccess, Toast.LENGTH_LONG).show();
    }

    private void createNotificationInDb(String userId) {
        String newFeedbackNotification = "You have a new feedback from your friend.";
        createNotification(new Notification(newFeedbackNotification, userId, Feedback));
    }

}