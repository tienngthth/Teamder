package com.example.teamder.activity;

import static com.example.teamder.activity.ProfileActivity.Action.Explore;
import static com.example.teamder.activity.ProfileActivity.Action.Inspect;
import static com.example.teamder.activity.ProfileActivity.Action.Profile;
import static com.example.teamder.activity.RequestActivity.Status.approved;
import static com.example.teamder.activity.RequestActivity.Status.pending;
import static com.example.teamder.model.IntentModel.IntentName.ActionType;
import static com.example.teamder.model.IntentModel.IntentName.TeammateId;
import static com.example.teamder.model.IntentModel.IntentName.UserId;
import static com.example.teamder.model.IntentModel.IntentName.UserName;
import static com.example.teamder.model.Review.parseReview;
import static com.example.teamder.model.ToVisitUserList.countIntersectCourses;
import static com.example.teamder.model.User.parseUser;
import static com.example.teamder.repository.RequestRepository.getRequestsByPartiesAndStatus;
import static com.example.teamder.repository.ReviewRepository.getReviewByUserId;
import static com.example.teamder.repository.UserRepository.getOtherUserByFieldValue;
import static com.example.teamder.repository.UserRepository.getUserById;
import static com.example.teamder.repository.UtilRepository.updateFieldToDb;
import static com.example.teamder.util.ScreenUtil.clearFocus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teamder.R;
import com.example.teamder.model.CurrentUser;
import com.example.teamder.model.Review;
import com.example.teamder.model.ToVisitUserList;
import com.example.teamder.model.User;
import com.example.teamder.repository.UserRepository;
import com.example.teamder.util.ValidationUtil;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;

public class ProfileActivity extends AppCompatActivity {

    public enum Action {
        Explore,
        Profile,
        Review,
        Inspect,
    }
    private final ToVisitUserList toVisitUserList = ToVisitUserList.getInstance();
    private final User currentUser = CurrentUser.getInstance().getUser();
    private ActivityResultLauncher<Intent> activityResultLauncher = null;
    private User user = null;
    private String userId = null;
    private EditText name, major, sID, GPA, introduction, phone, email;
    private View phoneLine, nameLine, majorLine, sIDLine, GPALine, introductionLine;
    private ImageButton addCourseButton, feedbackButton;
    private Button passButton, requestButton;
    private LayoutInflater inflater;
    private LinearLayout reviewList, courseList, fullscreenConstraint, actions, emailGroup, sIdGroup, phoneGroup;
    private Action action = Profile;
    private boolean slideAnimation = true;
    private boolean showDetails = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initialiseVariables();
        checkActionType();
    }

    private void checkActionType() {
        if (userId != null && !action.equals(Profile)) {
            getUserById(userId, (document) -> {
                user = parseUser(document);
                if (action.equals(Explore)) {
                    checkIntersectCourses();
                } else {
                    setUpListeners();
                    setUpScreen();
                }
            });
        } else {
            setUpCurrentUserScreen();
        }
    }

    private void checkIntersectCourses() {
        ArrayList<String> parties = new ArrayList<>(Arrays.asList(user.getId(), currentUser.getId()));
        getRequestsByPartiesAndStatus(pending.toString(), parties, (snapshot) -> {
            if (snapshot.getDocuments().size() > 0) {
                slideAnimation = false;
                nextUser();
            } else {
                getRequestsByPartiesAndStatus(approved.toString(), parties, (documentSnapshots) -> {
                    if ((countIntersectCourses(user) - documentSnapshots.getDocuments().size()) > 0) {
                        setUpListeners();
                        setUpScreen();
                    } else {
                        slideAnimation = false;
                        nextUser();
                    }
                });
            }
        });
    }

    private void setUpCurrentUserScreen() {
        user = CurrentUser.getInstance().getUser();
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
        email = findViewById(R.id.email);
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
        passButton = findViewById(R.id.pass_button);
        requestButton = findViewById(R.id.request_button);
        feedbackButton = findViewById(R.id.feedback_button);
        emailGroup = findViewById(R.id.email_group);
        phoneGroup = findViewById(R.id.phone_group);
        sIdGroup = findViewById(R.id.sID_group);
        inflater = LayoutInflater.from(this);
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        nextUser();
                    }
                }
        );
        checkIntent();
    }

    private void checkIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            action = Action.valueOf(bundle.get(ActionType.toString()).toString());
            userId = bundle.getString(UserId.toString());
            if (userId == null) {
                if (action.equals(Explore)) {
                    userId = toVisitUserList.getUserID();
                    showDetails = false;
                } else if (action.equals(Inspect)){
                    userId = bundle.get(TeammateId.toString()).toString();
                } else {
                    userId = bundle.get(UserId.toString()).toString();
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpListeners() {
        fullscreenConstraint.setOnTouchListener((view, event) -> clearInputFieldsFocus(view));
        addCourseButton.setOnClickListener((View view) -> openCourseInputForm());
        passButton.setOnClickListener((View view) -> nextUser());
        requestButton.setOnClickListener((View view) -> toRequest());
        feedbackButton.setOnClickListener((View view) -> toFeedback());
    }

    private void toRequest() {
        Intent intent = new Intent(ProfileActivity.this, RequestActivity.class);
        intent.putExtra(UserName.toString(), user.getName());
        intent.putExtra(UserId.toString(), user.getId());
        activityResultLauncher.launch(intent);
    }

    private void toFeedback() {
        Intent intent = new Intent(ProfileActivity.this, FeedbackActivity.class);
        intent.putExtra(UserName.toString(), user.getName());
        intent.putExtra(UserId.toString(), user.getId());
        intent.putExtra(ActionType.toString(), action);
        startActivity(intent);
    }

    private void nextUser() {
        toVisitUserList.removeUserID();
        currentUser.addVisitedTeameeIDs(userId);
        updateFieldToDb("users", CurrentUser.getInstance().getUser().getId(), "visitedTeameeIDs", currentUser.getVisitedTeameeIDs());
        if (toVisitUserList.getUserIDs().size() > 0) {
            toNextUserProfile();
        } else {
            toVisitUserList.resetList();
            Toast.makeText(this, "No more potential teammate found", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void toNextUserProfile() {
        finish();
        Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
        intent.putExtra(ActionType.toString(), Explore);
        startActivity(intent);
        if (slideAnimation) {
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
        slideAnimation = true;
    }


    private boolean clearInputFieldsFocus(View view) {
        clearFocus(view, name, this);
        clearFocus(view, major, this);
        clearFocus(view, GPA, this);
        clearFocus(view, phone, this);
        clearFocus(view, introduction, this);
        clearFocus(view, sID, this);
        clearFocus(view, email, this);
        return true;
    }

    private void setUpScreen() {
        name.setText(user.getName());
        major.setText(user.getMajor());
        sID.setText(user.getsId());
        phone.setText(user.getPhone());
        email.setText(user.getEmail());
        introduction.setText(user.getIntroduction());
        GPA.setText(String.valueOf(user.getGpa()));
        sIdGroup.setVisibility(showDetails ? View.VISIBLE : View.GONE);
        phoneGroup.setVisibility(showDetails ? View.VISIBLE : View.GONE);
        emailGroup.setVisibility(showDetails ? View.VISIBLE : View.GONE);
        setUpCoursesList();
        setUpReviewsList();
        setEditable();
        fullscreenConstraint.setVisibility(View.VISIBLE);
    }

    private void setEditable() {
        addCourseButton.setVisibility(action.equals(Profile) ? View.VISIBLE : View.GONE);
        majorLine.setVisibility(action.equals(Profile) ? View.VISIBLE : View.GONE);
        nameLine.setVisibility(action.equals(Profile) ? View.VISIBLE : View.GONE);
        phoneLine.setVisibility(action.equals(Profile) ? View.VISIBLE : View.GONE);
        sIDLine.setVisibility(action.equals(Profile) ? View.VISIBLE : View.GONE);
        introductionLine.setVisibility(action.equals(Profile) ? View.VISIBLE : View.GONE);
        GPALine.setVisibility(action.equals(Profile) ? View.VISIBLE : View.GONE);
        name.setEnabled(action.equals(Profile));
        phone.setEnabled(action.equals(Profile));
        GPA.setEnabled(action.equals(Profile));
        major.setEnabled(action.equals(Profile));
        introduction.setEnabled(action.equals(Profile));
        actions.setVisibility(action.equals(Explore) ? View.VISIBLE : View.GONE);
        feedbackButton.setVisibility(!action.equals(Profile) && !userId.equals(currentUser.getId()) ? View.VISIBLE : View.GONE);
        name.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        phone.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        GPA.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        major.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        email.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        introduction.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
            } else {
                findViewById(R.id.reviews).setVisibility(View.GONE);
            }
            findViewById(R.id.reviews_count).setVisibility(size > 0 ? View.VISIBLE : View.GONE);
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
        }
        findViewById(R.id.enrolling_courses_count).setVisibility(size > 0 ? View.VISIBLE : View.GONE);
    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    private void setupCustomCourseView(String name, int index) {
        View itemView = inflater.inflate(R.layout.enrolled_courses_row, null, false);
        ((TextView) itemView.findViewById(R.id.course)).setText(name);
        itemView.findViewById(R.id.remove_course).setVisibility(action.equals(Profile) ? View.VISIBLE : View.GONE);
        if (action.equals(Profile)) {
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
            updateFieldToDb("users", user.getId(), "gpa", GPA.getText().toString(), (v) -> {
                updateUser();
            });
            updateFieldToDb("users", user.getId(), "introduction", introduction.getText().toString(), (v) -> {
                updateUser();
            });
            getOtherUserByFieldValue("sId", sID.getText().toString(), (querySnapshot) -> {
                if (querySnapshot.getDocuments().size() > 0) {
                    Toast.makeText(this, "Can not update sID. This sID already exists.", Toast.LENGTH_LONG).show();
                } else {
                    updateFieldToDb("users", user.getId(), "sId", sID.getText().toString(), (v) -> {
                        updateUser();
                    });
                }
            });
            getOtherUserByFieldValue("email", email.getText().toString(), (querySnapshot) -> {
                if (querySnapshot.getDocuments().size() > 0) {
                    Toast.makeText(this, "Can not update email. This email already exists.", Toast.LENGTH_LONG).show();
                } else {
                    updateFieldToDb("users", user.getId(), "email", email.getText().toString(), (v) -> {
                        updateUser();
                    });
                }
            });
            getOtherUserByFieldValue("phone", phone.getText().toString(), (querySnapshot) -> {
                if (querySnapshot.getDocuments().size() > 0) {
                    Toast.makeText(this, "Can not update phone number. This phone number already exists.", Toast.LENGTH_LONG).show();
                } else {
                    updateFieldToDb("users", user.getId(), "phone", phone.getText().toString(), (v) -> {
                        updateUser();
                    });
                }
            });
            updateUser();
        }
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

    @Override
    public void onBackPressed() {
        finish();
    }

}