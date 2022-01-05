package com.example.teamder.activity;

import static com.example.teamder.model.Review.parseReview;
import static com.example.teamder.repository.ReviewRepository.getReviewByUserId;
import static com.example.teamder.repository.UtilRepository.updateFieldToDb;
import static com.example.teamder.util.ScreenUtil.clearFocus;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teamder.R;
import com.example.teamder.model.CurrentUser;
import com.example.teamder.model.Review;
import com.example.teamder.model.User;
import com.example.teamder.util.ValidationUtil;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private final User currentUser = CurrentUser.getInstance().getUser();
    private EditText name, major, sID, GPA, introduction, phone;
    private ImageButton addCourseButton;
    private LayoutInflater inflater;
    private LinearLayout reviewList, courseList, fullscreenConstraint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initialiseVariables();
        setUpListeners();
        setUpScreen();
    }

    private void initialiseVariables() {
        name = findViewById(R.id.name);
        major = findViewById(R.id.major);
        sID = findViewById(R.id.sID);
        GPA = findViewById(R.id.GPA);
        introduction = findViewById(R.id.introduction);
        phone = findViewById(R.id.phone);
        addCourseButton = findViewById(R.id.add_course);
        courseList = findViewById(R.id.courses_list);
        reviewList = findViewById(R.id.reviews_list);
        fullscreenConstraint = findViewById(R.id.full_screen);
        inflater = LayoutInflater.from(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpListeners() {
        fullscreenConstraint.setOnTouchListener((view, event) -> clearInputFieldsFocus(view));
        addCourseButton.setOnClickListener((View view) -> openCourseInputForm());
    }

    private boolean clearInputFieldsFocus(View view) {
        clearFocus(view, name, this);
        clearFocus(view, major, this);
        clearFocus(view, GPA, this);
        clearFocus(view, phone, this);
        clearFocus(view, introduction, this);
        clearFocus(view, sID, this);
        return true;
    }

    private void setUpScreen() {
        name.setText(currentUser.getName());
        major.setText(currentUser.getMajor());
        sID.setText(currentUser.getsId());
        phone.setText(currentUser.getPhone());
        introduction.setText(currentUser.getIntroduction());
        GPA.setText(String.valueOf(currentUser.getGPA()));
        setUpCoursesList();
        setUpReviewsList();
    }

    @SuppressLint("SetTextI18n")
    private void setUpReviewsList() {
        reviewList.removeAllViews();
        getReviewByUserId(currentUser.getId(), (querySnapshot) -> {
            int size = querySnapshot.size();
            if (size > 0) {
                findViewById(R.id.reviews).setVisibility(View.VISIBLE);
                for (QueryDocumentSnapshot snapshot : querySnapshot) {
                    setUpCustomReviewView(snapshot);
                }
                ((TextView) findViewById(R.id.reviews_count)).setText("(" + size + ")");
                ((TextView) findViewById(R.id.reviews_count)).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.reviews).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.reviews_count)).setVisibility(View.GONE);
            }
        });
    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    private void setUpCustomReviewView(QueryDocumentSnapshot snapshot) {
        Review review = parseReview(snapshot);
        View itemView = inflater.inflate(R.layout.notification_row, null, false);
        ((TextView) itemView.findViewById(R.id.message)).setText(review.getComment());
        ((TextView) itemView.findViewById(R.id.timestamp)).setText(review.getTimeStamp());
        reviewList.addView(itemView);
    }

    @SuppressLint("SetTextI18n")
    private void setUpCoursesList() {
        ArrayList<String> courses = currentUser.getCourses();
        int size = courses.size();
        courseList.removeAllViews();
        if (size > 0) {
            for (int index = 0; index < size; ++index) {
                setupCustomCourseView(courses.get(index), index);
            }
            ((TextView) findViewById(R.id.enrolling_courses_count)).setText("(" + size + ")");
            ((TextView) findViewById(R.id.enrolling_courses_count)).setVisibility(View.VISIBLE);
        } else {
            ((TextView) findViewById(R.id.enrolling_courses_count)).setVisibility(View.GONE);
        }
    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    private void setupCustomCourseView(String name, int index) {
        View itemView = inflater.inflate(R.layout.enrolled_courses_row, null, false);
        ((TextView) itemView.findViewById(R.id.course)).setText(name);
        ((ImageButton) itemView.findViewById(R.id.remove_course)).setOnClickListener((View view) -> {
            currentUser.removeCourse(index);
            updateFieldToDb("users", currentUser.getId(), "courses", currentUser.getCourses());
            setUpCoursesList();
        });
        courseList.addView(itemView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateFieldToDb("users", currentUser.getId(), "name", name.getText().toString());
        updateFieldToDb("users", currentUser.getId(), "major", major.getText().toString());
        updateFieldToDb("users", currentUser.getId(), "sID", sID.getText().toString());
        updateFieldToDb("users", currentUser.getId(), "GPA", GPA.getText().toString());
        updateFieldToDb("users", currentUser.getId(), "introduction", introduction.getText().toString());
        updateFieldToDb("users", currentUser.getId(), "phone", phone.getText().toString());
    }

    @SuppressLint("SetTextI18n")
    private void openCourseInputForm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.course_input_form, null);
        EditText editText = view.findViewById(R.id.course_name);
        builder.setView(view)
                .setPositiveButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Confirm", (dialog, id) -> {});
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));
        dialog.getButton(dialog.BUTTON_NEGATIVE).setBackgroundColor(getResources().getColor(R.color.red_300));
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue_grey_500));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> {
            String name = ValidationUtil.validateNameInput(editText);
            if (name != null) {
                currentUser.addCourse(name);
                dialog.dismiss();
                setUpCoursesList();
                updateFieldToDb("users", currentUser.getId(), "courses", currentUser.getCourses());
            }
        });
    }

}