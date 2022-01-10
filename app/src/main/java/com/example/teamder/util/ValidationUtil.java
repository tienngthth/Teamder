package com.example.teamder.util;

import android.widget.EditText;

// need to import these two
//import org.apache.commons..routines.EmailValidator;
//import org.apache.commons.validator.routines.EmailValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtil {

    public static Long isNumeric(EditText input) {
        String value = input.getText().toString();
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException nfe) {
            input.setError("Please enter a number");
            return null;
        }
    }

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
//        if (EmailValidator.getInstance().isValid(email)) {
        if (true) {
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

    public static String validateOptionalNameInput(EditText nameInput) {
        String name = nameInput.getText().toString().trim();
        String regex = "[A-Za-z]{0,10}";
        if (isValidRegex(name, regex)) {
            return name;
        } else {
            nameInput.setError("Name can contain less than 10 alphabetical characters only");
            return null;
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

    public static Long validateMaxSlots(EditText maxSlots) {
        try {
            long maxSlotsValue = Long.parseLong(maxSlots.getText().toString());
            if (maxSlotsValue < 1) {
                maxSlots.setError("Slots must be greater than 0");
                return null;
            } else {
                return maxSlotsValue;
            }
        } catch (Exception e) {
            maxSlots.setError("Please enter a number");
            return null;
        }
    }

    public static Long validateOptionalMaxSlots(EditText maxSlots) {
        String maxSlotsString = maxSlots.getText().toString().trim();
        try {
            long maxSlotsValue = Long.parseLong(maxSlotsString);
            if (maxSlotsValue < 1) {
                maxSlots.setError("Slots must be greater than 0");
                return null;
            } else {
                return maxSlotsValue;
            }
        } catch (Exception e) {
            if (maxSlotsString.equals("")) {
                return -1L;
            } else {
                maxSlots.setError("Please enter a number");
                return null;
            }
        }
    }
}
