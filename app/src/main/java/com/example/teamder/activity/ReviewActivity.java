package com.example.teamder.activity;

import static com.example.teamder.activity.NotificationActivity.Type.ApproveRequest;
import static com.example.teamder.activity.NotificationActivity.Type.CancelRequest;
import static com.example.teamder.activity.NotificationActivity.Type.RejectRequest;
import static com.example.teamder.activity.ProfileActivity.Action.Profile;
import static com.example.teamder.activity.ProfileActivity.Action.Review;
import static com.example.teamder.activity.RequestActivity.Status.pending;
import static com.example.teamder.model.IntentModel.IntentName.ActionType;
import static com.example.teamder.model.IntentModel.IntentName.GroupId;
import static com.example.teamder.model.IntentModel.IntentName.Id;
import static com.example.teamder.model.IntentModel.IntentName.Position;
import static com.example.teamder.model.IntentModel.IntentName.UserId;
import static com.example.teamder.model.Request.parseRequest;
import static com.example.teamder.model.User.parseUser;
import static com.example.teamder.repository.NotificationRepository.createNotification;
import static com.example.teamder.repository.RequestRepository.getRequestListenerById;
import static com.example.teamder.repository.UserRepository.getUserById;
import static com.example.teamder.repository.UtilRepository.updateFieldToDb;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teamder.R;
import com.example.teamder.model.CurrentUser;
import com.example.teamder.model.Group;
import com.example.teamder.model.Notification;
import com.example.teamder.model.Request;
import com.example.teamder.model.User;
import com.example.teamder.repository.GroupRepository;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {

    public static ListenerRegistration requestListenerRegistration;
    private final User currentUser = CurrentUser.getInstance().getUser();
    private TextView requester, requestee, message, course, status;
    private Button rejectButton, cancelButton, approveButton;
    private String position;
    private LinearLayout actions;
    private Request request;
    private ImageView requesterAvatar, requesteeAvatar;
    private LinearLayout requesterGroup, requesteeGroup;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        initialiseVariables();
        setupRegistrationListeners();
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
        requesterAvatar = findViewById(R.id.avatar_requester);
        requesteeAvatar = findViewById(R.id.avatar_requestee);
        requesterGroup = findViewById(R.id.requester_group);
        requesteeGroup = findViewById(R.id.requestee_group);
        status = findViewById(R.id.status);
    }

    private void setupRegistrationListeners() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        position = bundle.getString(Position.toString());
        requestListenerRegistration = getRequestListenerById(
                bundle.getString(Id.toString()), (documentSnapshot) -> {
                    request = parseRequest(documentSnapshot);
                    message.setText(request.getMessage());
                    course.setText(request.getCourseName());
                    setUpScreen();
                    setUpListeners();
                }
        );
    }

    private void setUpScreen() {
        getUserById(request.getRequesteeID(), (documentSnapshot -> {
            User user = parseUser(documentSnapshot);
            requestee.setText(user.getName());
            updateUserAvatar(user.getId(), requesteeAvatar);
        }));
        getUserById(request.getRequesterID(), (documentSnapshot -> {
            User user = parseUser(documentSnapshot);
            requester.setText(user.getName());
            updateUserAvatar(user.getId(), requesterAvatar);
        }));
        status.setText(request.getStatus());
        cancelButton.setVisibility(position.equals("sent") && request.getStatus().equals(pending.toString()) ? View.VISIBLE : View.GONE);
        actions.setVisibility(position.equals("received") && request.getStatus().equals(pending.toString()) ? View.VISIBLE : View.GONE);
    }

    private void updateUserAvatar(String userId, ImageView view) {
        FirebaseStorage
                .getInstance()
                .getReference()
                .child(userId)
                .getDownloadUrl()
                .addOnSuccessListener(
                        uri -> Picasso.get().load(uri).into(view)
                );
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
                .setNegativeButton("Yes", (dialog, which) -> processAction(action));
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));
        dialog.getButton(dialog.BUTTON_NEGATIVE).setBackgroundColor(getResources().getColor(R.color.red_300));
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue_grey_500));
    }

    private void processAction(String action) {
        if (action.equals("approve")) {
            GroupRepository.getGroupByCourseNameByUserIds(request.getCourseName(), Arrays.asList(request.getRequesteeID(), request.getRequesterID()),
                    querySnapshot -> {
                        int groupsCount = querySnapshot.getDocuments().size();
                        switch (groupsCount) {
                            case 2:
                                mergeGroup(querySnapshot.getDocuments());
                                break;
                            case 1:
                                joinExistingGroup(querySnapshot.getDocuments());
                                break;
                            default:
                                newGroup();
                                break;
                        }
                    }
            );
        } else {
            updateRequestStatus(action, null);
        }
    }

    private void newGroup() {
        Group group = new Group(
                new ArrayList<String>() {{
                    add(request.getRequesteeID());
                    add(request.getRequesterID());
                }},
                request.getCourseName());
        GroupRepository.createGroup(group, (documentReference) -> toGroup(documentReference.getId()), () -> {
        });
    }

    private void joinExistingGroup(List<DocumentSnapshot> documentSnapshots) {
        Group group = Group.parseGroup(documentSnapshots.get(0));
        group.getUserIds().add(group.getUserIds().contains(request.getRequesterID()) ? request.getRequesteeID() : request.getRequesterID());
        updateFieldToDb("groups", group.getId(), "userIds", group.getUserIds(), (v) -> {
            toGroup(group.getId());
        });
    }

    private void mergeGroup(List<DocumentSnapshot> documentSnapshots) {
        Group group = Group.parseGroup(documentSnapshots.get(0));
        Group group2 = Group.parseGroup(documentSnapshots.get(1));
        group.getUserIds().addAll(group2.getUserIds());
        updateFieldToDb("groups", group.getId(), "userIds", group.getUserIds(), (v) -> {
            toGroup(group.getId());
        });
        updateFieldToDb("groups", group2.getId(), "isActive", false);
    }

    private void toGroup(String groupId) {
        Intent intent = new Intent(ReviewActivity.this, GroupActivity.class);
        intent.putExtra(GroupId.toString(), groupId);
        startActivity(intent);
        finish();
        updateRequestStatus("approve", groupId);
    }

    private void updateRequestStatus(String action, String groupId) {
        updateFieldToDb("requests", request.getId(), "status", getPastTense(action), (v) -> {
            createNotificationInDb(action, groupId);
        });
        Toast.makeText(this, "Request " + getPastTense(action) + ".", Toast.LENGTH_LONG).show();
        if (!action.equals("approve")) {
            finish();
        }
    }

    private String getPastTense(String action) {
        return action.equals("approve") ? "approved" : (action + "ed");
    }

    private String getMessageHead(String action) {
        return action.equals("cancel") ? "Request from " : "Your request to ";
    }

    private void createNotificationInDb(String action, String groupId) {
        String message = getMessageHead(action) + currentUser.getName() + " for course " + request.getCourseName() + " has been " + getPastTense(action) + ".";
        Notification notification = new Notification(
                message,
                request.getRequesteeID().equals(currentUser.getId()) ? request.getRequesterID() : request.getRequesteeID(),
                action.equals("approve") ? ApproveRequest : action.equals("reject") ? RejectRequest : CancelRequest,
                action.equals("approve") ? groupId : request.getId());
        createNotification(notification);
    }

    private void toReviewProfile(String pagePosition) {
        Intent intent = new Intent(ReviewActivity.this, ProfileActivity.class);
        intent.putExtra(ActionType.toString(), isToProfile(pagePosition) ? Profile : Review);
        intent.putExtra(UserId.toString(), pagePosition.equals("requestee") ? request.getRequesteeID() : request.getRequesterID());
        startActivity(intent);
    }

    private boolean isToProfile(String pagePosition) {
        return (position.equals("sent") && pagePosition.equals("requester")) || (position.equals("received") && pagePosition.equals("requestee"));
    }

}