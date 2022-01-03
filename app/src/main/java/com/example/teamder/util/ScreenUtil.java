package com.example.teamder.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

public class ScreenUtil {

    public static void hideKeyboard(View view, Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void clearFocus(View view, View focusedView, Context context) {
        focusedView.clearFocus();
        hideKeyboard(view, context);
    }

    public static void clearFocus(View view, View[] focusedViews, Context context) {
        for (View focusView : focusedViews) {
            focusView.clearFocus();
        }
        hideKeyboard(view, context);
    }

    public static void setViewAndChildrenEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                setViewAndChildrenEnabled(child, enabled);
            }
        }
    }
}
