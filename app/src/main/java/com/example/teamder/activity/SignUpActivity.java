package com.example.teamder.activity;

import static com.example.teamder.model.IntentModel.IntentName.Email;
import static com.example.teamder.model.IntentModel.IntentName.Password;
import static com.example.teamder.repository.UtilRepository.updateFieldToDb;
import static com.example.teamder.util.ScreenUtil.clearFocus;
import static com.example.teamder.util.ValidationUtil.confirmPassword;
import static com.example.teamder.util.ValidationUtil.validateEmailInput;
import static com.example.teamder.util.ValidationUtil.validateNameInput;
import static com.example.teamder.util.ValidationUtil.validatePasswordInput;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.teamder.R;
import com.example.teamder.model.User;
import com.example.teamder.repository.AuthenticationRepository;
import com.example.teamder.repository.UserRepository;
import com.example.teamder.util.ScreenUtil;

public class SignUpActivity extends AppCompatActivity {

    private ConstraintLayout fullscreenConstraint;
    private EditText emailInput, passwordInput, confirmPasswordInput, nameInput;
    private ImageButton loginLink;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initialiseVariables();
        setupListener();
    }

    private void initialiseVariables() {
        nameInput = findViewById(R.id.name_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password);
        loginLink = findViewById(R.id.login_link);
        signUpButton = findViewById(R.id.sign_up_button);
        fullscreenConstraint = findViewById(R.id.activity_sign_up);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupListener() {
        fullscreenConstraint.setOnTouchListener((view, event) -> clearInputFieldsFocus(view));
        signUpButton.setOnClickListener((View view) -> signUp());
        loginLink.setOnClickListener((View view) -> toLogin());
    }

    private boolean clearInputFieldsFocus(View view) {
        clearFocus(view, nameInput, this);
        clearFocus(view, emailInput, this);
        clearFocus(view, passwordInput, this);
        clearFocus(view, confirmPasswordInput, this);
        return true;
    }

    private void signUp() {
        String email = validateEmailInput(emailInput);
        String password = validatePasswordInput(passwordInput);
        String name = validateNameInput(nameInput);
        if (confirmPassword(confirmPasswordInput, password) && password != null && email != null && name != null) {
            ScreenUtil.setViewAndChildrenEnabled(fullscreenConstraint, false);
            AuthenticationRepository.createUserWithEmailAndPassword(
                    email,
                    password,
                    (firebaseUser) -> storeNewUserToDb(name, email, firebaseUser.getUid(), password),
                    () -> {
                        Toast.makeText(SignUpActivity.this, "Sign up failed.", Toast.LENGTH_SHORT).show();
                        ScreenUtil.setViewAndChildrenEnabled(fullscreenConstraint, true);
                    });
        }
    }

    private void storeNewUserToDb(String name, String email, String uid, String password) {
        UserRepository.addUser(
                new User(name, email, uid),
                (docRefCallBack) -> {
                    updateFieldToDb("users", docRefCallBack.getId(), "id", docRefCallBack.getId(), (v) -> {});
                    Toast.makeText(SignUpActivity.this, "Sign up successfully.", Toast.LENGTH_SHORT).show();
                    toLogin(email, password);
                },
                () -> {
                    Toast.makeText(SignUpActivity.this, "Can not sign up at the moment.", Toast.LENGTH_SHORT).show();
                    ScreenUtil.setViewAndChildrenEnabled(fullscreenConstraint, true);
                });
    }

    private void toLogin(String email, String password) {
        AuthenticationRepository.signOut();
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        intent.putExtra(Email.toString(), email);
        intent.putExtra(Password.toString(), password);
        startActivity(intent);
        finish();
    }

    private void toLogin() {
        finish();
    }
}