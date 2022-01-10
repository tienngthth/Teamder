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
import com.example.teamder.model.Notification;
import com.example.teamder.model.Request;
import com.example.teamder.model.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CourseActivity extends AppCompatActivity {

    private final User currentUser = CurrentUser.getInstance().getUser();
    private TextView course;
    private Button doneButton;
    private LinearLayout teameeList;
    private LayoutInflater inflater;

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
            getApproveRequestByCourseName(courseName, (querySnapshot) -> {
                generateTeameeListView(getTeameeIDs(querySnapshot));
            });
            doneButton.setOnClickListener((View view) -> removeGroup(courseName));
        }
    }

    private ArrayList<String> getTeameeIDs(QuerySnapshot querySnapshot) {
        ArrayList<String> teameesIDs = new ArrayList<>();
        teameesIDs.add(currentUser.getId());
        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
            ArrayList<String> parties = parseRequest(documentSnapshot).getParties();
            for (String id : parties) {
                if (!teameesIDs.contains(id)) {
                    teameesIDs.add(id);
                }
            }
        }
        return teameesIDs;
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

    private void removeGroup(String courseName) {
        getApproveRequestByCourseName(courseName, (snapshot) -> {
            if (currentUser.getId().equals(snapshot.getDocuments().get(0).get("requesterID"))) {
                for (int index = 0; index < snapshot.getDocuments().size(); ++index) {
                    Request request = parseRequest(snapshot.getDocuments().get(index));
                    updateFieldToDb("requests", request.getId(), "status", "cancelled");
                    Notification notification = new Notification("Group " + courseName + " has been removed", request.getRequesteeID(), DoneGroup);
                    createNotification(notification);
                }
                Toast.makeText(CourseActivity.this, "Group has been removed", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(CourseActivity.this, "Only group leader can remove the group", Toast.LENGTH_SHORT).show();
            }
        });
        finish();
    }
}