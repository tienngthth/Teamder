package com.example.teamder.activity;

import static com.example.teamder.model.User.parseUser;
import static com.example.teamder.repository.RequestRepository.createRequest;
import static com.example.teamder.repository.RequestRepository.getPendingRequestOfCourseByParties;
import static com.example.teamder.repository.UserRepository.getUserById;
import static com.example.teamder.repository.NotificationRepository.createNotification;
import static com.example.teamder.util.DateTimeUtil.getToday;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
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

    private final User currentUser = CurrentUser.getInstance().getUser();
    private final ArrayList<String> courseNames = new ArrayList<>();
    private User user = null;
    private TextView name, message;
    private String userName = null;
    private String userID = null;
    private Button cancelButton;
    private ImageButton sendButton;
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
            userName = bundle.getString("userName");
            userID = bundle.getString("userID");
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
                getPendingRequestOfCourseByParties(parties, course, (snapshot) -> {
                    if (snapshot.getDocuments().size() == 0) {
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
        String messageText = message.getText().toString();
        if (courseNames.size() > 0) {
            for (String course : courseNames) {
                Request request = new Request(course, getToday(), currentUser.getId(), messageText, user.getId());
                Notification notification = new Notification(messageText, user.getId());
                createRequest(request);
                createNotification(notification);
            }
            Intent intent = new Intent(RequestActivity.this, ProfileActivity.class);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(this, "Please select some courses", Toast.LENGTH_LONG).show();
        }
    }
}