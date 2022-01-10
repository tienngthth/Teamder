package com.example.teamder.activity;

import static com.example.teamder.activity.ProfileActivity.Action.Explore;
import static com.example.teamder.model.Request.parseRequest;
import static com.example.teamder.model.User.parseUser;
import static com.example.teamder.repository.RequestRepository.getPendingRequestByFieldValue;
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
import com.example.teamder.model.Request;
import com.example.teamder.model.ToVisitUserList;
import com.example.teamder.model.User;
import com.example.teamder.repository.AuthenticationRepository;
import com.example.teamder.repository.NotificationRepository;
import com.example.teamder.repository.UserRepository;
import com.example.teamder.service.NotificationService;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;


public class HomeActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST = 99;
    private final ToVisitUserList toVisitUserList = ToVisitUserList.getInstance();
    private final User currentUser = CurrentUser.getInstance().getUser();
    private TextView userNameView, coursesTitle;
    private ImageButton logoutButton, notificationButton, profileButton, exploreButton;
    private LinearLayout receivedRequestListView, courseListView, sentRequestListView, receivedRequests, sentRequests;
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

    private void registerService(){
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
        coursesTitle = findViewById(R.id.all_courses);
        courseListView = findViewById(R.id.courses_list);
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
        getPendingRequestByFieldValue("requesteeID", currentUser.getId(), (snapshot) -> {
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
        receivedRequestListView.setVisibility(View.VISIBLE);
        receivedRequestListView.removeAllViews();
        receivedRequestListView.addView(itemView);
    }

    @SuppressLint("SetTextI18n, InflateParams")
    private void setupCustomReceivedRequestView(Request request, int index) {
        getUserById(request.getRequesterID(), (documentSnapshot -> {
            User user = parseUser(documentSnapshot);
            View itemView = inflater.inflate(R.layout.requests_row, null, false);
            ImageButton nextBtn = itemView.findViewById(R.id.next_button);
            ((TextView) itemView.findViewById(R.id.name)).setText(user.getName());
            ((TextView) itemView.findViewById(R.id.time)).setText(request.getCreatedTime());
            ((TextView) itemView.findViewById(R.id.course)).setText(request.getCourseName());
            if (index % 2 == 0) {
                itemView.findViewById(R.id.row_linear).setBackgroundColor(getResources().getColor(R.color.blue_grey_050));
            }
            receivedRequestListView.addView(itemView);
            itemView.setOnClickListener((View view) -> toReviewRequest(request, "received"));
            nextBtn.setOnClickListener((View view) -> toReviewRequest(request, "received"));
        }));
    }

    @SuppressLint("SetTextI18n")
    private void generateSentRequestsListView() {
        getPendingRequestByFieldValue("requesterID", currentUser.getId(), (snapshot) -> {
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
        sentRequestListView.setVisibility(View.VISIBLE);
        sentRequestListView.removeAllViews();
        sentRequestListView.addView(itemView);
    }

    @SuppressLint("SetTextI18n, InflateParams")
    private void setupCustomSentRequestView(Request request, int index) {
        getUserById(request.getRequesteeID(), (documentSnapshot -> {
            User user = parseUser(documentSnapshot);
            View itemView = inflater.inflate(R.layout.requests_row, null, false);
            ImageButton nextBtn = itemView.findViewById(R.id.next_button);
            ((TextView) itemView.findViewById(R.id.name)).setText(user.getName());
            ((TextView) itemView.findViewById(R.id.time)).setText(request.getCreatedTime());
            ((TextView) itemView.findViewById(R.id.course)).setText(request.getCourseName());
            if (index % 2 == 0) {
                itemView.findViewById(R.id.row_linear).setBackgroundColor(getResources().getColor(R.color.blue_grey_050));
            }
            sentRequestListView.addView(itemView);
            itemView.setOnClickListener((View view) -> toReviewRequest(request, "sent"));
            nextBtn.setOnClickListener((View view) -> toReviewRequest(request, "sent"));
        }));
    }

    @SuppressLint("SetTextI18n")
    private void generateCoursesListView() {
        ArrayList<String> courses = currentUser.getCourses();
        int total = courses.size();
        if (total == 0) {
            coursesTitle.setText("No courses found");
            courseListView.setVisibility(View.GONE);
        } else {
            prepareCourseListView();
            for (int index = 0; index < total; ++index) {
                setupCustomCourseItemView(courses.get(index), index);
            }
        }
    }

    @SuppressLint("SetTextI18n, InflateParams")
    private void prepareCourseListView() {
        View itemView = inflater.inflate(R.layout.courses_header, null, false);
        courseListView.setVisibility(View.VISIBLE);
        courseListView.removeAllViews();
        courseListView.addView(itemView);
        coursesTitle.setText("All courses");
    }

    @SuppressLint("SetTextI18n, InflateParams")
    private void setupCustomCourseItemView(String course, int index) {
        View itemView = inflater.inflate(R.layout.courses_row, null, false);
        ImageButton nextBtn = itemView.findViewById(R.id.next_button);
        ((TextView) itemView.findViewById(R.id.course)).setText(course);
        if (index % 2 == 0) {
            itemView.findViewById(R.id.row_linear).setBackgroundColor(getResources().getColor(R.color.blue_grey_050));
        }
        courseListView.addView(itemView);
        itemView.setOnClickListener((View view) -> toCourse(course));
        nextBtn.setOnClickListener((View view) -> toCourse(course));
    }

    private void toReviewRequest(Request request, String source) {
        Intent intent = new Intent(HomeActivity.this, ReviewActivity.class);
        intent.putExtra("id", request.getId());
        intent.putExtra("message", request.getMessage());
        intent.putExtra("course", request.getCourseName());
        intent.putExtra("source", source);
        intent.putExtra("requesteeID", request.getRequesteeID());
        intent.putExtra("requesterID", request.getRequesterID());
        startActivity(intent);
    }

    private void toCourse(String course) {
        Intent intent = new Intent(HomeActivity.this, CourseActivity.class);
        intent.putExtra("course", course);
        startActivity(intent);
    }

    private void navigateUser(Class activity) {
        Intent intent = new Intent(HomeActivity.this, activity);
        startActivity(intent);
    }

    private void explore() {
        if (currentUser.getCourses().size() > 0) {
            UserRepository.getUsersByCourse(currentUser.getCourses(),
                    currentUser.getUid(),
                    (QuerySnapshot) -> {
                        for (DocumentSnapshot document : QuerySnapshot.getDocuments()) {
                            String userID = document.getId();
                            if (!toVisitUserList.getUserIDs().contains(userID) && !userID.equals(currentUser.getId()) && !currentUser.getVisitedTeameeIDs().contains(userID)) {
                                toVisitUserList.addUserID(userID);
                            }
                        }
                        if (toVisitUserList.getUserIDs().size() > 0) {
                            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                            intent.putExtra("action", Explore);
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "No potential teammate found", Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Let's update your profile and courses first", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onBackPressed() {
    }
}