package com.example.teamder.activity;

import static com.example.teamder.activity.NotificationActivity.Type.GroupChange;
import static com.example.teamder.activity.ProfileActivity.Action.Review;
import static com.example.teamder.activity.RequestActivity.Status.approved;
import static com.example.teamder.model.Group.parseGroup;
import static com.example.teamder.model.IntentModel.IntentName.ActionType;
import static com.example.teamder.model.IntentModel.IntentName.CourseName;
import static com.example.teamder.model.IntentModel.IntentName.GroupId;
import static com.example.teamder.model.IntentModel.IntentName.UserId;
import static com.example.teamder.model.User.parseUser;
import static com.example.teamder.repository.GroupRepository.getGroupById;
import static com.example.teamder.repository.NotificationRepository.createNotification;
import static com.example.teamder.repository.RequestRepository.getRequestByUserIdStatusAndCourseName;
import static com.example.teamder.repository.UserRepository.getUserById;
import static com.example.teamder.repository.UtilRepository.updateFieldToDb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teamder.R;
import com.example.teamder.model.CurrentUser;
import com.example.teamder.model.Group;
import com.example.teamder.model.Notification;
import com.example.teamder.model.User;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {

    private final User currentUser = CurrentUser.getInstance().getUser();
    private TextView course, status;
    private Button closeButton, leaveButton, feedbackButton;
    private ImageButton messageButton;
    private LinearLayout teameeList, actions;
    private LayoutInflater inflater;
    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        initialiseVariables();
        setUpScreen();
        setUpListeners();
    }

    private void initialiseVariables() {
        course = findViewById(R.id.course);
        teameeList = findViewById(R.id.teamee_list);
        closeButton = findViewById(R.id.close_button);
        leaveButton = findViewById(R.id.leave_button);
        messageButton = findViewById(R.id.message);
        status = findViewById(R.id.status);
        feedbackButton = findViewById(R.id.feedback_button);
        actions = findViewById(R.id.actions);
        inflater = LayoutInflater.from(this);
    }

    private void setUpScreen() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String groupId = bundle.getString(GroupId.toString());
            getGroupById(groupId, (documentSnapshot) -> {
                group = parseGroup(documentSnapshot);
                course.setText(group.getCourseName());
                status.setText(group.getIsActive() ? "Active" : "Inactive");
                actions.setVisibility(enableActions() ? View.VISIBLE : View.GONE);
                feedbackButton.setVisibility(enableFeedback() ? View.VISIBLE : View.GONE);
                generateTeameeListView(group.getUserIds());
            });
        }
    }

    private boolean enableActions() {
        return (group.getIsActive() && group.getUserIds().contains(currentUser.getId()));
    }

    private boolean enableFeedback() {
        return !enableActions() && group.getUserIds().size() > 0 && (group.getUserIds().size() > 1 || !group.getUserIds().contains(currentUser.getId()));
    }

    private void setUpListeners() {
        closeButton.setOnClickListener((View view) -> openCloseConfirmationDialog());
        leaveButton.setOnClickListener((View view) -> openLeaveConfirmationDialog());
        feedbackButton.setOnClickListener((View view) -> toFeedbackActivity());
        messageButton.setOnClickListener((View view) -> toMessage());
    }


    private void openLeaveConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(GroupActivity.this, R.style.AlertDialogCustom))
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to leave this group?")
                .setPositiveButton("No", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Yes", (dialog, which) -> leaveGroup());
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));
        dialog.getButton(dialog.BUTTON_NEGATIVE).setBackgroundColor(getResources().getColor(R.color.red_300));
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue_grey_500));
    }

    private void leaveGroup() {
        group.getUserIds().remove(currentUser.getId());
        updateFieldToDb("groups", group.getId(), "userIds", group.getUserIds());
        pushNotification(currentUser.getName() + " has left your group for course " + group.getCourseName() + ".");
        if (group.getUserIds().size() > 0) {
            toFeedbackActivity();
        } else {
            closeGroup();
        }
        updateRequestStatus(currentUser.getId());
        Toast.makeText(this,"Left group successfully.", Toast.LENGTH_LONG).show();
        finish();
    }

    @SuppressLint("SetTextI18n")
    private void generateTeameeListView(ArrayList<String> teameeIDs) {
        int total = teameeIDs.size();
        teameeList.removeAllViews();
        for (int index = 0; index < total; ++index) {
            setupItemView(teameeIDs.get(index));
        }
    }

    @SuppressLint("SetTextI18n, InflateParams")
    private void setupItemView(String id) {
        getUserById(id, (documentSnapshot) -> {
            User user = parseUser(documentSnapshot);
            View itemView = inflater.inflate(R.layout.group_member, null, false);
            TextView nameView = itemView.findViewById(R.id.name);
            nameView.setText(user.getName());
            itemView.setOnClickListener((View view) -> reviewProfile(user.getId()));
            teameeList.addView(itemView);
        });
    }

    private void reviewProfile(String userID) {
        Intent intent = new Intent(GroupActivity.this, ProfileActivity.class);
        if (!userID.equals(currentUser.getId())) {
            intent.putExtra(ActionType.toString(), Review.toString());
            intent.putExtra(UserId.toString(), userID);
        }
        startActivity(intent);
    }

    private void toMessage() {
        Intent intent = new Intent(GroupActivity.this, MessageActivity.class);
        intent.putExtra(GroupId.toString(), group.getId());
        intent.putExtra(CourseName.toString(), group.getCourseName());
        startActivity(intent);
    }

    private void openCloseConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(GroupActivity.this, R.style.AlertDialogCustom))
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to close this group?")
                .setPositiveButton("No", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Yes", (dialog, which) -> closeGroup());
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));
        dialog.getButton(dialog.BUTTON_NEGATIVE).setBackgroundColor(getResources().getColor(R.color.red_300));
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue_grey_500));
    }

    private void closeGroup() {
        updateFieldToDb("groups", group.getId(), "isActive", false);
        for (String userId : group.getUserIds()) {
            updateRequestStatus(userId);
        }
        if (group.getUserIds().size() > 1) {
            toFeedbackActivity();
        }
        pushNotification("Your group for course " + group.getCourseName() + " has been closed by " + currentUser.getName() + ".");
        Toast.makeText(this,"Group closed.", Toast.LENGTH_LONG).show();
        finish();
    }

    private void updateRequestStatus(String userId) {
        getRequestByUserIdStatusAndCourseName(userId, approved.toString(), group.getCourseName(), (documentSnapshots) -> {
            List<DocumentSnapshot> documents = documentSnapshots.getDocuments();
            for (DocumentSnapshot documentSnapshot : documents) {
                updateFieldToDb("requests", documentSnapshot.getId(), "status", "done");
            }
        });
    }

    private void pushNotification(String message) {
        for (String userId : group.getUserIds()) {
            if (!currentUser.getId().equals(userId)) {
                createNotification(new Notification(message, userId, GroupChange, group.getId()));
            }
        }
    }

    private void toFeedbackActivity() {
        Intent intent = new Intent(GroupActivity.this, FeedbackActivity.class);
        intent.putExtra(GroupId.toString(), group.getId());
        startActivity(intent);
    }
}