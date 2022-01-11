package com.example.teamder.activity;

import static com.example.teamder.activity.NotificationActivity.Type.NewRequest;
import static com.example.teamder.activity.RequestActivity.Status.approved;
import static com.example.teamder.activity.RequestActivity.Status.pending;
import static com.example.teamder.model.IntentModel.IntentName.UserId;
import static com.example.teamder.model.IntentModel.IntentName.UserName;
import static com.example.teamder.model.User.parseUser;
import static com.example.teamder.repository.NotificationRepository.createNotification;
import static com.example.teamder.repository.RequestRepository.createRequest;
import static com.example.teamder.repository.RequestRepository.getRequestOfCourseByParties;
import static com.example.teamder.repository.UserRepository.getUserById;
import static com.example.teamder.util.DateTimeUtil.getToday;
import static com.example.teamder.util.ValidationUtil.validateMessage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamder.R;
import com.example.teamder.model.CurrentUser;
import com.example.teamder.model.Notification;
import com.example.teamder.model.Request;
import com.example.teamder.model.User;

import java.util.ArrayList;

public class RequestActivity extends AppCompatActivity {

    public enum Status {
        pending,
        approved,
        rejected,
        canceled,
    }

    private final User currentUser = CurrentUser.getInstance().getUser();
    private final ArrayList<String> courseNames = new ArrayList<>();
    private User user = null;
    private TextView name;
    private EditText message;
    private String userName = null;
    private String userID = null;
    private Button cancelButton, sendButton;
    private LinearLayout coursesList;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        initialiseVariables();
        checkIntent();
        getUserById(userID, (document) -> {
            user = parseUser(document);
            setUpScreen();
            setUpListeners();
        });
    }

    private void initialiseVariables() {
        name = findViewById(R.id.name);
        cancelButton = findViewById(R.id.cancel_button);
        sendButton = findViewById(R.id.send_button);
        coursesList = findViewById(R.id.courses_list);
        message = findViewById(R.id.message);
        inflater = LayoutInflater.from(this);
    }

    private void checkIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            userName = bundle.getString(UserName.toString());
            userID = bundle.getString(UserId.toString());
        }
    }

    private void setUpScreen() {
        name.setText(userName);
        setUpCourseList();
    }

    private void setUpCourseList() {
        coursesList.removeAllViews();
        for (String course : currentUser.getCourses()) {
            ArrayList<String> parties = new ArrayList<>();
            parties.add(user.getId());
            parties.add(currentUser.getId());
            if (user.getCourses().contains(course)) {
                getRequestOfCourseByParties(parties, course, pending.toString(), (snapshot) -> {
                    if (snapshot.getDocuments().size() == 0) {
                        getRequestOfCourseByParties(parties, course, approved.toString(), (documentSnapshots) -> {
                            if (documentSnapshots.getDocuments().size() == 0) {
                                View itemView = inflater.inflate(R.layout.request_course_row, null, false);
                                TextView courseName = itemView.findViewById(R.id.name);
                                CheckBox checkBox = itemView.findViewById(R.id.checkbox);
                                courseName.setText(course);
                                coursesList.addView(itemView);
                                checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                                    if (isChecked) {
                                        courseNames.add(courseName.getText().toString());
                                    } else {
                                        courseNames.remove(courseName.getText().toString());
                                    }
                                });
                                courseName.setOnClickListener((View view) -> checkBox.setChecked(!checkBox.isChecked()));
                            }
                        });
                    }
                });
            }
        }
    }

    private void setUpListeners() {
        cancelButton.setOnClickListener((View view) -> cancel());
        sendButton.setOnClickListener((View view) -> sendRequest());
    }

    private void cancel() {
        Intent intent = new Intent(RequestActivity.this, ProfileActivity.class);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void sendRequest() {
        String messageText = validateMessage(message);
        if (messageText != null) {
            if (courseNames.size() > 0) {
                for (String course : courseNames) {
                    createRequestAndNotificationInDb(course, messageText);
                }
                navigateUser();
            } else {
                Toast.makeText(this, "Please select some courses.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void createRequestAndNotificationInDb(String course, String messageText) {
        Request request = new Request(course, getToday(), currentUser.getId(), messageText, user.getId());
        createRequest(request, (documentReference) -> {
            Notification notification = new Notification(
                    currentUser.getName() + " sends you a request for course " + course + ".",
                    user.getId(),
                    NewRequest,
                    documentReference.getId()
            );
            createNotification(notification);
        });
    }

    private void navigateUser() {
        Intent intent = new Intent(RequestActivity.this, ProfileActivity.class);
        setResult(RESULT_OK, intent);
        finish();
    }
}