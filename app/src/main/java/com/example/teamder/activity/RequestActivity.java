package com.example.teamder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamder.R;
import com.example.teamder.model.CurrentUser;
import com.example.teamder.model.ToVisitUserList;
import com.example.teamder.model.User;

public class RequestActivity extends AppCompatActivity {

    private final ToVisitUserList toVisitUserList = ToVisitUserList.getInstance();
    private User currentUser = CurrentUser.getInstance().getUser();
    private TextView name;
    private String userName, userID;
    private Button cancelButton;
    private ImageButton sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        initialiseVariables();
        checkIntent();
        setUpScreen();
        setUpListeners();
    }

    private void initialiseVariables() {
        name = findViewById(R.id.name);
        cancelButton = findViewById(R.id.cancel_button);
        sendButton = findViewById(R.id.send_button);
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
        //Do something
        Intent intent = new Intent(RequestActivity.this, ProfileActivity.class);
        setResult(RESULT_OK, intent);
        finish();
    }
}