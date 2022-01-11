package com.example.teamder.activity;

import static com.example.teamder.model.IntentModel.IntentName.CourseName;
import static com.example.teamder.model.IntentModel.IntentName.GroupId;
import static com.example.teamder.repository.MessageRepository.addSnapshotListenerForMessageByGroupId;
import static com.example.teamder.repository.MessageRepository.createMessage;
import static com.example.teamder.util.ScreenUtil.clearFocus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamder.R;
import com.example.teamder.model.CurrentUser;
import com.example.teamder.model.Message;
import com.example.teamder.model.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    private final ArrayList<String> newNotificationIDs = new ArrayList<>();
    private final User currentUser = CurrentUser.getInstance().getUser();
    private LinearLayout messageList, messageGroup, fullScreen;
    private ScrollView messages;
    private LayoutInflater inflater;
    private TextView noNotification, courseName;
    private EditText messageEditText;
    private ImageView sendButton;
    public static ListenerRegistration listenerRegistration;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        initialiseVariables();
        getMessages();
        setUpListeners();
    }

    @Override
    public void onBackPressed() {
        listenerRegistration.remove();
        super.onBackPressed();
    }

    private void initialiseVariables() {
        inflater = LayoutInflater.from(this);
        messageList = findViewById(R.id.message_list);
        messageGroup = findViewById(R.id.messages_group);
        noNotification = findViewById(R.id.no_notification);
        sendButton = findViewById(R.id.sendButton);
        messageEditText = findViewById(R.id.message);
        courseName = findViewById(R.id.course_name);
        fullScreen = findViewById(R.id.message_activity);
        messages = findViewById(R.id.messages);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        groupId = bundle.getString(GroupId.toString());
        courseName.setText(bundle.getString(CourseName.toString()));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void getMessages() {
        listenerRegistration = addSnapshotListenerForMessageByGroupId(
                groupId,
                documentSnapshots -> {
                    updateMessageView(documentSnapshots);
                    noMessageFound();
                }
        );
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpListeners() {
        fullScreen.setOnTouchListener((view, event) -> clearInputFieldsFocus(view, true));
        messages.setOnTouchListener((view, event) -> clearInputFieldsFocus(view, false));
        sendButton.setOnClickListener((View view) -> sendMessage());
    }

    private boolean clearInputFieldsFocus(View view, Boolean returnValue) {
        clearFocus(view, messageEditText, this);
        return returnValue;

    }

    private void sendMessage() {
        String content = messageEditText.getText().toString().trim();
        if (!content.equals("")) {
            Message message = new Message(
                    content,
                    groupId,
                    currentUser.getId()
            );
            messageEditText.setText("");
            createMessage(message);
        }
    }

    private void updateMessageView(List<DocumentSnapshot> documents) {
        messageGroup.setVisibility(documents.size() < 1 ? View.GONE : View.VISIBLE);
        messageList.removeAllViews();
        for (DocumentSnapshot document : documents) {
            setupCustomItemView(messageList, Message.parseMessage(document));
        }
        messages.post(() -> messages.fullScroll(ScrollView.FOCUS_DOWN));
    }

    private void noMessageFound() {
        noNotification.setVisibility(
                messageGroup.getVisibility() == View.GONE
                        ? View.VISIBLE
                        : View.GONE);
    }

    @SuppressLint({"SetTextI18n", "InflateParams", "RtlHardcoded"})
    private void setupCustomItemView(LinearLayout list, Message message) {
        View itemView = inflater.inflate(R.layout.message_row, null, false);
        ((TextView) itemView.findViewById(R.id.message)).setText(message.getContent());
        ((TextView) itemView.findViewById(R.id.timestamp)).setText(message.getTimeStamp().substring(0, message.getTimeStamp().length() - 7));
        list.addView(itemView);
        itemView.findViewById(R.id.avatar).setVisibility(message.getUserId().equals(currentUser.getId()) ? View.GONE : View.VISIBLE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity =  message.getUserId().equals(currentUser.getId()) ? Gravity.RIGHT : Gravity.LEFT;
        itemView.setLayoutParams(params);
    }
}