package com.example.teamder.activity;

import static com.example.teamder.model.IntentModel.IntentName.Email;
import static com.example.teamder.model.IntentModel.IntentName.Password;
import static com.example.teamder.util.ScreenUtil.clearFocus;
import static com.example.teamder.util.ScreenUtil.setViewAndChildrenEnabled;
import static com.example.teamder.util.ValidationUtil.validateEmailInput;
import static com.example.teamder.util.ValidationUtil.validatePasswordInput;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teamder.R;
import com.example.teamder.model.CurrentUser;
import com.example.teamder.model.ToVisitUserList;
import com.example.teamder.repository.AuthenticationRepository;
import com.example.teamder.repository.UserRepository;
import com.example.teamder.service.NotificationService;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class LoginActivity extends AppCompatActivity {

    private final CurrentUser currentUser = CurrentUser.getInstance();
    private EditText emailInput, passwordInput;
    private ImageButton signUpLink;
    private Button loginButton;
    private LinearLayout fullscreenConstraint;
    private ActivityResultLauncher<Intent> activityResultLauncher = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialiseVariables();
        setViewAndChildrenEnabled(fullscreenConstraint, false);
        setupListener();
        setupScreen(getIntent());
    }

    @Override
    public void onResume() {
        super.onResume();
        verifyUserType();
    }

    private void initialiseVariables() {
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        signUpLink = findViewById(R.id.sign_up_link);
        fullscreenConstraint = findViewById(R.id.activity_login);
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    setupScreen(result.getData());
                }
        );
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupListener() {
        loginButton.setOnClickListener((View view) -> login());
        signUpLink.setOnClickListener((View view) -> toSignUp());
        fullscreenConstraint.setOnTouchListener((view, event) -> clearInputFieldsFocus(view));
    }

    public void setupScreen(Intent intent) {
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                emailInput.setText(bundle.get(Email.toString()).toString());
                passwordInput.setText(bundle.get(Password.toString()).toString());
            }
        }
    }

    private void verifyUserType() {
        FirebaseUser user = AuthenticationRepository.getCurrentUser();
        if (user != null) {
            navigateUser(user.getUid());
        } else {
            fullscreenConstraint.setVisibility(View.VISIBLE);
            setViewAndChildrenEnabled(fullscreenConstraint, true);
        }
    }

    private void navigateUser(String uid) {
        UserRepository.getUserByFieldValue("uid", uid, querySnapshot -> {
            if (querySnapshot.size() != 1) {
                setViewAndChildrenEnabled(fullscreenConstraint, true);
                Toast.makeText(this, "Account is not valid", Toast.LENGTH_SHORT).show();
            } else {
                for (QueryDocumentSnapshot document : querySnapshot) {
                    currentUser.updateUser(document);
                    ToVisitUserList.getInstance().resetList();
                    toHome();
                }
                if (currentUser.getUser() != null) {
                    startNotificationService();
                }
            }
        });
    }

    private void startNotificationService() {
        Intent intent = new Intent(LoginActivity.this, NotificationService.class);
        startService(intent);
    }

    public void login() {
        String email = validateEmailInput(emailInput);
        String password = validatePasswordInput(passwordInput);
        if (email != null && password != null) {
            setViewAndChildrenEnabled(fullscreenConstraint, false);
            AuthenticationRepository.signInWithEmailAndPassword(
                    email,
                    password,
                    this::verifyUserType,
                    () -> {
                        setViewAndChildrenEnabled(fullscreenConstraint, true);
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void toHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void toSignUp() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        activityResultLauncher.launch(intent);
    }

    private boolean clearInputFieldsFocus(View view) {
        clearFocus(view, emailInput, this);
        clearFocus(view, passwordInput, this);
        return true;
    }

}