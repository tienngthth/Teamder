package com.example.teamder.activity;

import static com.example.teamder.activity.ProfileActivity.Action.Explore;
import static com.example.teamder.activity.RequestActivity.Status.pending;
import static com.example.teamder.model.IntentModel.IntentName.ActionType;
import static com.example.teamder.model.IntentModel.IntentName.GroupId;
import static com.example.teamder.model.IntentModel.IntentName.Id;
import static com.example.teamder.model.IntentModel.IntentName.Position;
import static com.example.teamder.model.Request.parseRequest;
import static com.example.teamder.model.User.parseUser;
import static com.example.teamder.repository.RequestRepository.getRequestByStatusAndFieldValue;
import static com.example.teamder.repository.UserRepository.getUserById;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.teamder.R;
import com.example.teamder.broadcast.Receiver;
import com.example.teamder.model.CurrentUser;
import com.example.teamder.model.Group;
import com.example.teamder.model.Request;
import com.example.teamder.model.ToVisitUserList;
import com.example.teamder.model.User;
import com.example.teamder.repository.AuthenticationRepository;
import com.example.teamder.repository.GroupRepository;
import com.example.teamder.repository.NotificationRepository;
import com.example.teamder.service.NotificationService;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;


public class HomeActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST = 99;
    private final ToVisitUserList toVisitUserList = ToVisitUserList.getInstance();
    private final User currentUser = CurrentUser.getInstance().getUser();
    private TextView userNameView, groupsTitle;
    private ImageButton logoutButton, notificationButton, profileButton, exploreButton;
    private LinearLayout receivedRequestListView, groupListView, sentRequestListView, receivedRequests, sentRequests;
    private LayoutInflater inflater;
    private TextView newNotificationCount;
    protected Receiver myReceiver;
    protected IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initialiseVariables();
        setupListener();
        registerService();
    }

    private void registerService() {
        myReceiver = new Receiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(myReceiver, intentFilter);
        ActivityCompat.requestPermissions(HomeActivity.this, new String[]{
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG}, MY_PERMISSIONS_REQUEST);
    }

    private void initialiseVariables() {
        userNameView = findViewById(R.id.name);
        logoutButton = findViewById(R.id.logout_btn);
        notificationButton = findViewById(R.id.notification_button);
        receivedRequests = findViewById(R.id.all_received_requests);
        sentRequests = findViewById(R.id.all_sent_requests);
        receivedRequestListView = findViewById(R.id.requests_list);
        sentRequestListView = findViewById(R.id.sent_requests_list);
        groupsTitle = findViewById(R.id.all_groups);
        groupListView = findViewById(R.id.groups_list);
        newNotificationCount = findViewById(R.id.new_notification_count);
        profileButton = findViewById(R.id.profile_button);
        exploreButton = findViewById(R.id.explore_button);
        inflater = LayoutInflater.from(this);
    }

    private void setupListener() {
        logoutButton.setOnClickListener((View view) -> openConfirmationDialog());
        notificationButton.setOnClickListener((View view) -> navigateUser(NotificationActivity.class));
        profileButton.setOnClickListener((View view) -> navigateUser(ProfileActivity.class));
        exploreButton.setOnClickListener((View view) -> explore());
    }

    private void openConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(HomeActivity.this, R.style.AlertDialogCustom))
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("No", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Yes", (dialog, which) -> logout());
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));
        dialog.getButton(dialog.BUTTON_NEGATIVE).setBackgroundColor(getResources().getColor(R.color.red_300));
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue_grey_500));
    }

    private void logout() {
        CurrentUser.getInstance().setUser(null);
        AuthenticationRepository.signOut();
        NotificationService.listenerRegistration.remove();
        navigateUser(LoginActivity.class);
        finish();
    }

    @SuppressLint("SetTextI18n")
    public void setupScreen() {
        countNewNotification();
        userNameView.setText(", " + CurrentUser.getInstance().getUser().getName());
    }

    @SuppressLint("SetTextI18n")
    private void countNewNotification() {
        NotificationRepository.getNotificationByUserIdAndSeenValue(
                currentUser.getId(),
                false,
                (querySnapshot) -> {
                    int newNotifications = querySnapshot.size();
                    newNotificationCount.setText(" (+" + newNotifications + ")");
                    newNotificationCount.setVisibility(newNotifications < 1 ? View.GONE : View.VISIBLE);
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
        setupScreen();
    }

    private void updateList() {
        generateReceivedRequestsListView();
        generateSentRequestsListView();
        generateCoursesListView();
    }

    @SuppressLint("SetTextI18n")
    private void generateReceivedRequestsListView() {
        getRequestByStatusAndFieldValue(pending.toString(), "requesteeID", currentUser.getId(), (snapshot) -> {
            int size = snapshot.getDocuments().size();
            receivedRequests.setVisibility(size == 0 ? View.GONE : View.VISIBLE);
            prepareReceivedRequestListView();
            for (int index = 0; index < size; ++index) {
                Request request = parseRequest(snapshot.getDocuments().get(index));
                setupCustomReceivedRequestView(request, index);
            }
        });
    }

    @SuppressLint("SetTextI18n, InflateParams")
    private void prepareReceivedRequestListView() {
        View itemView = inflater.inflate(R.layout.requests_header, null, false);
        ((TextView) itemView.findViewById(R.id.position)).setText("From");
        receivedRequestListView.removeAllViews();
        receivedRequestListView.addView(itemView);
    }

    @SuppressLint("SetTextI18n, InflateParams")
    private void setupCustomReceivedRequestView(Request request, int index) {
        View itemView = inflater.inflate(R.layout.requests_row, null, false);
        ImageButton nextBtn = itemView.findViewById(R.id.next_button);
        ((TextView) itemView.findViewById(R.id.time)).setText(request.getTimeStamp());
        ((TextView) itemView.findViewById(R.id.course)).setText(request.getCourseName());
        if (index % 2 == 0) {
            itemView.findViewById(R.id.row_linear).setBackgroundColor(getResources().getColor(R.color.blue_grey_050));
        }
        receivedRequestListView.addView(itemView);
        itemView.setOnClickListener((View view) -> toReviewRequest(request, "received"));
        nextBtn.setOnClickListener((View view) -> toReviewRequest(request, "received"));
        getUserById(request.getRequesterID(), (documentSnapshot -> {
            User user = parseUser(documentSnapshot);
            ((TextView) itemView.findViewById(R.id.name)).setText(user.getName());
        }));
    }

    @SuppressLint("SetTextI18n")
    private void generateSentRequestsListView() {
        getRequestByStatusAndFieldValue(pending.toString(), "requesterID", currentUser.getId(), (snapshot) -> {
            int size = snapshot.getDocuments().size();
            sentRequests.setVisibility(size == 0 ? View.GONE : View.VISIBLE);
            prepareSentRequestListView();
            for (int index = 0; index < size; ++index) {
                Request request = parseRequest(snapshot.getDocuments().get(index));
                setupCustomSentRequestView(request, index);
            }
        });
    }


    @SuppressLint("SetTextI18n, InflateParams")
    private void prepareSentRequestListView() {
        View itemView = inflater.inflate(R.layout.requests_header, null, false);
        ((TextView) itemView.findViewById(R.id.position)).setText("To");
        sentRequestListView.removeAllViews();
        sentRequestListView.addView(itemView);
    }

    @SuppressLint("SetTextI18n, InflateParams")
    private void setupCustomSentRequestView(Request request, int index) {
        View itemView = inflater.inflate(R.layout.requests_row, null, false);
        ImageButton nextBtn = itemView.findViewById(R.id.next_button);
        ((TextView) itemView.findViewById(R.id.time)).setText(request.getTimeStamp());
        ((TextView) itemView.findViewById(R.id.course)).setText(request.getCourseName());
        if (index % 2 == 0) {
            itemView.findViewById(R.id.row_linear).setBackgroundColor(getResources().getColor(R.color.blue_grey_050));
        }
        sentRequestListView.addView(itemView);
        itemView.setOnClickListener((View view) -> toReviewRequest(request, "sent"));
        nextBtn.setOnClickListener((View view) -> toReviewRequest(request, "sent"));
        getUserById(request.getRequesteeID(), (documentSnapshot -> {
            User user = parseUser(documentSnapshot);
            ((TextView) itemView.findViewById(R.id.name)).setText(user.getName());
        }));
    }

    @SuppressLint("SetTextI18n")
    private void generateCoursesListView() {
        GroupRepository.getAllActiveGroupOfUser(currentUser.getId(),
                querySnapshot -> {
                    ArrayList<Group> groups = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                        groups.add(Group.parseGroup(documentSnapshot));
                    }
                    int total = groups.size();
                    if (total == 0) {
                        groupsTitle.setText(
                                (currentUser.getCourses().size() == 0)
                                        ? "Let's add some courses to your Profile."
                                        : "No group found. Let's explore and review requests."
                        );
                        groupListView.setVisibility(View.GONE);
                    } else {
                        prepareCourseListView();
                        for (int index = 0; index < total; ++index) {
                            setupCustomCourseItemView(groups.get(index).getCourseName(), groups.get(index).getId(), index);
                        }
                    }
                });
    }

    @SuppressLint("SetTextI18n, InflateParams")
    private void prepareCourseListView() {
        View itemView = inflater.inflate(R.layout.courses_header, null, false);
        groupListView.setVisibility(View.VISIBLE);
        groupListView.removeAllViews();
        groupListView.addView(itemView);
        groupsTitle.setText("All groups");
    }

    @SuppressLint("SetTextI18n, InflateParams")
    private void setupCustomCourseItemView(String course, String groupId, int index) {
        View itemView = inflater.inflate(R.layout.courses_row, null, false);
        ImageButton nextBtn = itemView.findViewById(R.id.next_button);
        ((TextView) itemView.findViewById(R.id.course)).setText(course);
        if (index % 2 == 0) {
            itemView.findViewById(R.id.row_linear).setBackgroundColor(getResources().getColor(R.color.blue_grey_050));
        }
        groupListView.addView(itemView);
        itemView.setOnClickListener((View view) -> toGroup(groupId));
        nextBtn.setOnClickListener((View view) -> toGroup(groupId));
    }

    private void toReviewRequest(Request request, String position) {
        Intent intent = new Intent(HomeActivity.this, ReviewActivity.class);
        intent.putExtra(Id.toString(), request.getId());
        intent.putExtra(Position.toString(), position);
        startActivity(intent);
    }

    private void toGroup(String groupId) {
        Intent intent = new Intent(HomeActivity.this, GroupActivity.class);
        intent.putExtra(GroupId.toString(), groupId);
        startActivity(intent);
    }

    private void navigateUser(Class activity) {
        Intent intent = new Intent(HomeActivity.this, activity);
        startActivity(intent);
    }

    private void explore() {
        if (currentUser.getCourses().size() > 0) {
            if (toVisitUserList.getUserIDs().size() > 0) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                intent.putExtra(ActionType.toString(), Explore.toString());
                startActivity(intent);
            } else {
                toVisitUserList.resetList();
                Toast.makeText(this, "No potential teammate found", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Let's update your profile and enrolling courses first", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
    }
}