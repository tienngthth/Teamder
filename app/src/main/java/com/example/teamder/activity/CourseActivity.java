package com.example.teamder.activity;

import static com.example.teamder.activity.NotificationActivity.Type.DoneGroup;
import static com.example.teamder.activity.ProfileActivity.Action.Inspect;
import static com.example.teamder.model.Request.parseRequest;
import static com.example.teamder.model.User.parseUser;
import static com.example.teamder.repository.NotificationRepository.createNotification;
import static com.example.teamder.repository.RequestRepository.getApproveRequestByCourseName;
import static com.example.teamder.repository.UserRepository.getUserById;
import static com.example.teamder.repository.UtilRepository.updateFieldToDb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamder.R;
import com.example.teamder.model.CurrentUser;
import com.example.teamder.model.Group;
import com.example.teamder.model.Notification;
import com.example.teamder.model.Request;
import com.example.teamder.model.User;
import com.example.teamder.repository.GroupRepository;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

public class CourseActivity extends AppCompatActivity {

    private final User currentUser = CurrentUser.getInstance().getUser();
    private TextView course;
    private Button doneButton;
    private LinearLayout teameeList;
    private LayoutInflater inflater;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        initialiseVariables();
        setUpScreen();
    }

    private void initialiseVariables() {
        course = findViewById(R.id.course);
        teameeList = findViewById(R.id.teamee_list);
        doneButton = findViewById(R.id.done_button);
        inflater = LayoutInflater.from(this);
    }

    private void setUpScreen() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String courseName = bundle.getString("course");
            course.setText(courseName);
            GroupRepository.getGroup(courseName, Arrays.asList(currentUser.getId()), (querySnapshot) -> {
                groupId = Group.parseGroup(querySnapshot.getDocuments().get(0)).getUid();
                generateTeameeListView(Group.parseGroup(querySnapshot.getDocuments().get(0)).getUserIds());
            });
            doneButton.setOnClickListener((View view) -> deactivateGroup());
        }
    }

    @SuppressLint("SetTextI18n")
    private void generateTeameeListView(ArrayList<String> teameeIDs) {
        int total = teameeIDs.size();
        teameeList.removeAllViews();
        for (int index = 0; index < total; ++index) {
            setupItemView(teameeIDs.get(index));
        }
        if (total < 2) {
            doneButton.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n, InflateParams")
    private void setupItemView(String id) {
        getUserById(id, (documentSnapshot) -> {
            User user = parseUser(documentSnapshot);
            View itemView = inflater.inflate(R.layout.group_member, null, false);
            TextView nameView = itemView.findViewById(R.id.name);
            nameView.setText(user.getName());
            nameView.setOnClickListener((View view) -> inspect(user.getId()));
            teameeList.addView(itemView);
        });
    }

    private void inspect(String userID) {
        Intent intent = new Intent(CourseActivity.this, ProfileActivity.class);
        intent.putExtra("action", Inspect);
        intent.putExtra("teammateID", userID);
        startActivity(intent);
    }

    private void deactivateGroup() {
        GroupRepository.deactivateGroup(groupId);
        finish();
    }
}