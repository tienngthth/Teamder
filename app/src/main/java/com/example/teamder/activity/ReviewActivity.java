package com.example.teamder.activity;

import static com.example.teamder.model.User.parseUser;
import static com.example.teamder.repository.UserRepository.getUserById;
import static com.example.teamder.repository.UtilRepository.updateFieldToDb;
import static com.example.teamder.repository.NotificationRepository.createNotification;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teamder.R;
import com.example.teamder.model.Notification;
import com.example.teamder.model.User;

public class ReviewActivity extends AppCompatActivity {

    private TextView requester, requestee, message, course;
    private Button rejectButton, cancelButton;
    private ImageButton approveButton;
    private String id, requesteeID, requesterID, source, courseName;
    private LinearLayout actions, requesterAvatar, requesteeAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        initialiseVariables();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkIntent();
        setUpScreen();
        setUpListeners();
    }

    private void initialiseVariables() {
        requestee = findViewById(R.id.requestee);
        message = findViewById(R.id.message);
        requester = findViewById(R.id.requester);
        course = findViewById(R.id.course);
        cancelButton = findViewById(R.id.cancel_button);
        rejectButton = findViewById(R.id.reject_button);
        approveButton = findViewById(R.id.approve_button);
        actions = findViewById(R.id.actions);
        requesterAvatar = findViewById(R.id.requester_avatar);
        requesteeAvatar = findViewById(R.id.requestee_avatar);
    }

    private void checkIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        id = bundle.getString("id");
        courseName = bundle.getString("course");
        requesteeID = bundle.getString("requesteeID");
        requesterID = bundle.getString("requesterID");
        source = bundle.getString("source");
        message.setText(bundle.getString("message"));
        course.setText(courseName);
    }

    private void setUpScreen() {
        getUserById(requesteeID, (documentSnapshot -> {
            User user = parseUser(documentSnapshot);
            requestee.setText(user.getName());
        }));
        getUserById(requesterID, (documentSnapshot -> {
            User user = parseUser(documentSnapshot);
            requester.setText(user.getName());
        }));
        cancelButton.setVisibility(source.equals("sent") ? View.VISIBLE : View.GONE);
        actions.setVisibility(source.equals("received") ? View.VISIBLE : View.GONE);
    }

    private void setUpListeners() {
        cancelButton.setOnClickListener((View view) -> openConfirmationDialog("cancel"));
        rejectButton.setOnClickListener((View view) -> openConfirmationDialog("reject"));
        approveButton.setOnClickListener((View view) -> openConfirmationDialog("approve"));
        requesterAvatar.setOnClickListener((View view) -> toReviewProfile("requester"));
        requesteeAvatar.setOnClickListener((View view) -> toReviewProfile("requestee"));
    }

    private void openConfirmationDialog(String action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(ReviewActivity.this, R.style.AlertDialogCustom))
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to " + action + " this request?")
                .setPositiveButton("No", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Yes", (dialog, which) -> updateRequestStatus(action));
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));
        dialog.getButton(dialog.BUTTON_NEGATIVE).setBackgroundColor(getResources().getColor(R.color.red_300));
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue_grey_500));
    }

    private void updateRequestStatus(String action) {
        updateFieldToDb("requests", id, "status", action.equals("approve") ? "approved" : (action + "ed"));
        if (action.equals("approve")) {
            toCourse();
        }
        Notification notification = new Notification("Your request has been " + action + "ed", requesterID);
        createNotification(notification);
        finish();
    }

    private void toCourse() {
        Intent intent = new Intent(ReviewActivity.this, CourseActivity.class);
        intent.putExtra("course", courseName);
        startActivity(intent);
        finish();
    }

    private void toReviewProfile(String position) {
        Intent intent = new Intent(ReviewActivity.this, ProfileActivity.class);
        intent.putExtra("action", isToProfile(position) ? "profile" : "review");
        intent.putExtra("userID", position.equals("requestee") ? requesteeID : requesterID);
        startActivity(intent);
    }

    private boolean isToProfile(String position) {
        return (source.equals("sent") && position.equals("requester")) || (source.equals("received") && position.equals("requestee"));
    }

}