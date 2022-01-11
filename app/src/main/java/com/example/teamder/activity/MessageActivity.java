package com.example.teamder.activity;

import static com.example.teamder.repository.UtilRepository.updateFieldToDb;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamder.R;
import com.example.teamder.model.CurrentUser;
import com.example.teamder.model.Message;
import com.example.teamder.model.User;
import com.example.teamder.repository.MessageRepository;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    private final ArrayList<String> newNotificationIDs = new ArrayList<>();
    private final User currentUser = CurrentUser.getInstance().getUser();
    private LinearLayout messageList, messageGroup;
    private LayoutInflater inflater;
    private TextView noNotification;
    private EditText messageEditText;
    private ImageView sendButton;
    public static ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        initialiseVariables();
        getMessages();
        setUpOnClickListener();
    }

    @Override
    public void onBackPressed() {
        listenerRegistration.remove();
        super.onBackPressed();
    }

    private void initialiseVariables() {
        inflater = LayoutInflater.from(this);
        messageList = findViewById(R.id.past_notification_list);
        messageGroup = findViewById(R.id.past_notification_group);
        noNotification = findViewById(R.id.no_notification);
        sendButton = findViewById(R.id.sendButton);
        messageEditText = findViewById(R.id.messageEditText);
    }

    private void getMessages() {
        listenerRegistration = MessageRepository.addSnapshotListenerForMessageByGroupId(
                "TrqiTVoaeRh9JioDxZU0",
                documentSnapshots -> {
                    updateMessageView(documentSnapshots);
                    noMessageFound();
                }
        );
    }

    private void setUpOnClickListener(){
        sendButton.setOnClickListener(view -> {
            Message message = new Message();
            message.setGroupId("TrqiTVoaeRh9JioDxZU0");
            message.setUserId(currentUser.getId());
            message.setContent(messageEditText.getText().toString());
            messageEditText.setText("");
            MessageRepository.createMessage(message,task -> {});
        });
    }

    private void updateMessageView(List<DocumentSnapshot> documents) {
        messageGroup.setVisibility(documents.size() < 1 ? View.GONE : View.VISIBLE);
        messageList.removeAllViews();
        for (DocumentSnapshot document : documents) {
            setupCustomItemView(messageList, Message.parseMessage(document));
        }
    }

    private void noMessageFound() {
        noNotification.setVisibility(
                messageGroup.getVisibility() == View.GONE
                        ? View.VISIBLE
                        : View.GONE);
    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    private void setupCustomItemView(LinearLayout list, Message message) {
        View itemView = inflater.inflate(R.layout.message_row, null, false);
        ((TextView) itemView.findViewById(R.id.message)).setText(message.getContent());
        ((TextView) itemView.findViewById(R.id.timestamp)).setText(message.getTimeStamp());
        list.addView(itemView);
    }
}