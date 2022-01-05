package com.example.teamder.activity;

import static com.example.teamder.model.Review.parseReview;
import static com.example.teamder.model.User.parseUser;
import static com.example.teamder.repository.ReviewRepository.getReviewByUserId;
import static com.example.teamder.repository.UserRepository.getUserById;
import static com.example.teamder.repository.UtilRepository.updateFieldToDb;
import static com.example.teamder.util.ScreenUtil.clearFocus;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.example.teamder.repository.UserRepository;
import com.example.teamder.util.ValidationUtil;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private User user = null;
    private String userId = null;
    private EditText name, major, sID, GPA, introduction, phone;
    private View phoneLine, nameLine, majorLine, sIDLine, GPALine, introductionLine;
    private ImageButton addCourseButton;
    private LayoutInflater inflater;
    private LinearLayout reviewList, courseList, fullscreenConstraint, actions;
    private String action = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initialiseVariables();
        if (userId != null) {
            getUserById(userId, (document) -> {
                user = parseUser(document);
                setUpListeners();
                setUpScreen();
            });
        } else {
            user = CurrentUser.getInstance().getUser();
            setUpListeners();
            setUpScreen();
        }
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
        phoneLine = findViewById(R.id.phone_line);
        nameLine = findViewById(R.id.name_line);
        majorLine = findViewById(R.id.major_line);
        sIDLine = findViewById(R.id.sID_line);
        GPALine = findViewById(R.id.GPA_line);
        introductionLine = findViewById(R.id.introduction_line);
        actions = findViewById(R.id.actions);
        inflater = LayoutInflater.from(this);
        checkIntent();
    }

    private void checkIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            action = bundle.get("action").toString();
            userId = bundle.get("userId").toString();
        }
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
        name.setText(user.getName());
        major.setText(user.getMajor());
        sID.setText(user.getsId());
        phone.setText(user.getPhone());
        introduction.setText(user.getIntroduction());
        GPA.setText(String.valueOf(user.getGPA()));
        setUpCoursesList();
        setUpReviewsList();
        setEditable();
    }

    private void setEditable() {
        addCourseButton.setVisibility(action.equals("explore") ? View.GONE : View.VISIBLE);
        actions.setVisibility(action.equals("explore") ? View.VISIBLE : View.GONE);
        majorLine.setVisibility(action.equals("explore") ? View.GONE : View.VISIBLE);
        nameLine.setVisibility(action.equals("explore") ? View.GONE : View.VISIBLE);
        phoneLine.setVisibility(action.equals("explore") ? View.GONE : View.VISIBLE);
        sIDLine.setVisibility(action.equals("explore") ? View.GONE : View.VISIBLE);
        introductionLine.setVisibility(action.equals("explore") ? View.GONE : View.VISIBLE);
        GPALine.setVisibility(action.equals("explore") ? View.GONE : View.VISIBLE);
        name.setEnabled(!action.equals("explore"));
        phone.setEnabled(!action.equals("explore"));
        GPA.setEnabled(!action.equals("explore"));
        major.setEnabled(!action.equals("explore"));
        introduction.setEnabled(!action.equals("explore"));
        sID.setEnabled(!action.equals("explore"));
    }

    @SuppressLint("SetTextI18n")
    private void setUpReviewsList() {
        reviewList.removeAllViews();
        getReviewByUserId(user.getId(), (querySnapshot) -> {
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
        ArrayList<String> courses = user.getCourses();
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
        if (action.equals("explore")) {
            itemView.findViewById(R.id.remove_course).setVisibility(View.GONE);
        } else {
            itemView.findViewById(R.id.remove_course).setVisibility(View.VISIBLE);
            ((ImageButton) itemView.findViewById(R.id.remove_course)).setOnClickListener((View view) -> {
                user.removeCourse(index);
                updateFieldToDb("users", user.getId(), "courses", user.getCourses());
                setUpCoursesList();
            });
        }
        courseList.addView(itemView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (CurrentUser.getInstance().getUser().getId().equals(user.getId())) {
            user.setName(name.getText().toString());
            updateFieldToDb("users", user.getId(), "name", name.getText().toString(), (v) -> {
                updateUser();
            });
            updateFieldToDb("users", user.getId(), "major", major.getText().toString(), (v) -> {
                updateUser();
            });
            updateFieldToDb("users", user.getId(), "sID", sID.getText().toString(), (v) -> {
                updateUser();
            });
            updateFieldToDb("users", user.getId(), "GPA", GPA.getText().toString(), (v) -> {
                updateUser();
            });
            updateFieldToDb("users", user.getId(), "introduction", introduction.getText().toString(), (v) -> {
                updateUser();
            });
            updateFieldToDb("users", user.getId(), "phone", phone.getText().toString(), (v) -> {
                updateUser();
            });
        }
        updateUser();
    }

    private void updateUser() {
        UserRepository.getUserById(user.getId(), document -> {
                CurrentUser.getInstance().updateUser(document);
        });
    }

    @SuppressLint("SetTextI18n")
    private void openCourseInputForm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.course_input_form, null);
        EditText editText = view.findViewById(R.id.course_name);
        builder.setView(view)
                .setPositiveButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Confirm", (dialog, id) -> {
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));
        dialog.getButton(dialog.BUTTON_NEGATIVE).setBackgroundColor(getResources().getColor(R.color.red_300));
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue_grey_500));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> {
            String name = ValidationUtil.validateNameInput(editText);
            if (name != null) {
                user.addCourse(name);
                dialog.dismiss();
                setUpCoursesList();
                updateFieldToDb("users", user.getId(), "courses", user.getCourses());
            }
        });
    }

}