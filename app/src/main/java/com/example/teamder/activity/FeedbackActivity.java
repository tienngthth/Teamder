package com.example.teamder.activity;

import static com.example.teamder.model.User.parseUser;
import static com.example.teamder.repository.NotificationRepository.createNotification;
import static com.example.teamder.repository.RequestRepository.createRequest;
import static com.example.teamder.repository.ReviewRepository.createReview;
import static com.example.teamder.repository.UserRepository.getUserById;
import static com.example.teamder.util.DateTimeUtil.getToday;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.teamder.R;
import com.example.teamder.model.CurrentUser;
import com.example.teamder.model.Notification;
import com.example.teamder.model.Review;
import com.example.teamder.model.User;

public class FeedbackActivity extends AppCompatActivity {

    private final User currentUser = CurrentUser.getInstance().getUser();
    private User user = null;
    private TextView name, feedback;
    private String userName = null;
    private String userID = null;
    private Button cancelButton;
    private ImageButton sendButton;
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initialiseVariables();
        checkIntent();
        getUserById(userID, (document) -> {
            user = parseUser(document);
            setUpScreen();
            setUpListeners();
        });
    }

    private void initialiseVariables() {
        name = findViewById(R.id.name);
        cancelButton = findViewById(R.id.cancel_button);
        sendButton = findViewById(R.id.send_button);
        feedback = findViewById(R.id.feedback);
    }

    private void checkIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            userName = bundle.getString("userName");
            userID = bundle.getString("userID");
            action = bundle.getString("action");
        }
    }

    private void setUpScreen() {
        name.setText(userName);
    }

    private void setUpListeners() {
        cancelButton.setOnClickListener((View view) -> cancel());
        sendButton.setOnClickListener((View view) -> sendFeedback());
    }

    private void cancel() {
        Intent intent = new Intent(FeedbackActivity.this, ProfileActivity.class);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void sendFeedback() {
        String messageText = feedback.getText().toString();
        Review review = new Review(userID, messageText, getToday());
        Notification notification = new Notification(currentUser.getName() + " sends you a feedback", user.getId());
        createNotification(notification);
        createReview(review);
        Intent intent;
        if (action.equals("explore")) {
            intent = new Intent(FeedbackActivity.this, ProfileActivity.class);
        }
        else {
            intent = new Intent(FeedbackActivity.this, CourseActivity.class);
        }
        setResult(RESULT_OK, intent);
        finish();
    }
}