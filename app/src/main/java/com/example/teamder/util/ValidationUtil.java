package com.example.teamder.util;

import android.widget.EditText;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtil {

    public static String validatePasswordInput(EditText passwordInput) {
        String password = passwordInput.getText().toString();
        String regex = "[A-Za-z0-9!@#$]{6,15}";
        if (isValidRegex(password, regex)) {
            return password;
        } else {
            passwordInput.setError("Password must have 6 to 15 characters with alphanumerical and !@#$ only");
            return null;
        }
    }

    public static String validateEmailInput(EditText emailInput) {
        String email = emailInput.getText().toString();
        if (EmailValidator.getInstance().isValid(email)) {
            return email;
        } else {
            emailInput.setError("Please enter a valid email");
            return null;
        }
    }

    public static String validateNameInput(EditText nameInput) {
        String name = nameInput.getText().toString().trim();
        String regex = "[A-Za-z0-9\\s]{1,10}";
        if (isValidRegex(name, regex)) {
            return name;
        } else {
            nameInput.setError("Name can not be empty and contains less than 10 alphanumerical characters or spaces only");
            return null;
        }
    }

    public static String validateMessage(EditText messageInput) {
        String message = messageInput.getText().toString().trim();
        if (message.equals("")) {
            messageInput.setError("Message can not be empty");
            return null;
        } else {
            return message;
        }
    }

    private static boolean isValidRegex(String value, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public static boolean confirmPassword(EditText confirmPasswordInput, String password) {
        String confirmPassword = confirmPasswordInput.getText().toString();
        if (confirmPassword.equals(password)) {
            return true;
        } else {
            confirmPasswordInput.setError("Password does not match");
            return false;
        }
    }

}
