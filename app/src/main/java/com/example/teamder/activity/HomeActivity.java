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
import com.example.teamder.repository.AuthenticationRepository;

import java.util.ArrayList;


public class HomeActivity extends AppCompatActivity {

    private TextView userNameView, requestsTitle, coursesTitle;
    private ImageButton logoutButton, notificationButton;
    private LinearLayout requestListView, courseListView;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initialiseVariables();
        setupListener();
        setupScreen();
    }

    private void initialiseVariables() {
        userNameView = findViewById(R.id.name);
        logoutButton = findViewById(R.id.logout_btn);
        notificationButton = findViewById(R.id.notification_button);
        requestsTitle = findViewById(R.id.all_requests);
        requestListView = findViewById(R.id.requests_list);
        coursesTitle = findViewById(R.id.all_courses);
        courseListView = findViewById(R.id.courses_list);
        inflater = LayoutInflater.from(this);
    }

    private void setupListener() {
        logoutButton.setOnClickListener((View view) -> openConfirmationDialog());
        notificationButton.setOnClickListener((View view) -> toNotification());
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
        // Cancel listening to notification service
//        NotificationService.listenerRegistration.remove();
        finish();
    }

    @SuppressLint("SetTextI18n")
    public void setupScreen() {
//        userNameView.setText(", " + CurrentUser.getInstance().getUser().getName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
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
            coursesTitle.setText("No course found");
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
        coursesTitle.setText("All requests");
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

    private void toNotification() {
        Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
    }
}