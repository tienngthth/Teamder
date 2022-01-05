package com.example.teamder.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teamder.R;
import com.example.teamder.model.Course;
import com.example.teamder.model.CurrentUser;
import com.example.teamder.model.Request;
import com.example.teamder.model.User;
import com.example.teamder.repository.AuthenticationRepository;
import com.example.teamder.repository.NotificationRepository;
import com.example.teamder.service.NotificationService;

import java.util.ArrayList;


public class HomeActivity extends AppCompatActivity {

    private final User currentUser = CurrentUser.getInstance().getUser();
    private TextView userNameView, requestsTitle, coursesTitle;
    private ImageButton logoutButton, notificationButton, profileButton, exploreButton;
    private LinearLayout requestListView, courseListView;
    private LayoutInflater inflater;
    private TextView newNotificationCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initialiseVariables();
        setupListener();
    }

    private void initialiseVariables() {
        userNameView = findViewById(R.id.name);
        logoutButton = findViewById(R.id.logout_btn);
        notificationButton = findViewById(R.id.notification_button);
        requestsTitle = findViewById(R.id.all_requests);
        requestListView = findViewById(R.id.requests_list);
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
        // 1. get all requests, courses
        generateRequestsListView();
        generateCoursesListView();
    }

    @SuppressLint("SetTextI18n")
    private void generateRequestsListView() {
        ArrayList<Request> requests = new ArrayList<>();
        int total = requests.size();
        if (total == 0) {
            requestsTitle.setVisibility(View.GONE);
            requestListView.setVisibility(View.GONE);
        } else {
            prepareRequestListView();
            for (int index = 0; index < total; ++index) {
                setupCustomRequestView(requests.get(index), index);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void generateCoursesListView() {
        ArrayList<Course> courses = new ArrayList<>();
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
    private void prepareRequestListView() {
        View itemView = inflater.inflate(R.layout.requests_header, null, false);
        requestListView.setVisibility(View.VISIBLE);
        requestListView.removeAllViews();
        requestListView.addView(itemView);
        requestsTitle.setText("All requests");
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
    private void setupCustomRequestView(Request request, int index) {
        View itemView = inflater.inflate(R.layout.requests_row, null, false);
        ImageButton nextBtn = itemView.findViewById(R.id.next_button);
        ((TextView) itemView.findViewById(R.id.name)).setText(request.getName());
        ((TextView) itemView.findViewById(R.id.time)).setText(request.getCreatedTime());
        ((TextView) itemView.findViewById(R.id.course)).setText(request.getCourse());
        if (index % 2 == 0) {
            itemView.findViewById(R.id.row_linear).setBackgroundColor(getResources().getColor(R.color.blue_grey_050));
        }
        requestListView.addView(itemView);
        itemView.setOnClickListener((View view) -> toReviewRequest(index));
        nextBtn.setOnClickListener((View view) -> toReviewRequest(index));
    }

    @SuppressLint("SetTextI18n, InflateParams")
    private void setupCustomCourseItemView(Course course, int index) {
        View itemView = inflater.inflate(R.layout.courses_row, null, false);
        ImageButton nextBtn = itemView.findViewById(R.id.next_button);
        ((TextView) itemView.findViewById(R.id.name)).setText(course.getName());
        if (index % 2 == 0) {
            itemView.findViewById(R.id.row_linear).setBackgroundColor(getResources().getColor(R.color.blue_grey_050));
        }
        courseListView.addView(itemView);
        itemView.setOnClickListener((View view) -> toCourse(index));
        nextBtn.setOnClickListener((View view) -> toCourse(index));
    }

    private void toReviewRequest(int index) {
        Intent intent = new Intent(HomeActivity.this, ReviewActivity.class);
        intent.putExtra("index", index);
        startActivity(intent);
    }

    private void toCourse(int index) {
        Intent intent = new Intent(HomeActivity.this, CourseActivity.class);
        intent.putExtra("index", index);
        startActivity(intent);
    }

    private void navigateUser(Class activity) {
        Intent intent = new Intent(HomeActivity.this, activity);
        startActivity(intent);
    }

    private void explore() {
        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
        intent.putExtra("action", "explore");
        intent.putExtra("userId", "eCWYH9PXaCCfYVnPRij5");
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
    }
}