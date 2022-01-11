package com.example.teamder.util;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {

    public static String formatDate(Calendar cal) {
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(cal.getTime());
    }

    @SuppressLint("SimpleDateFormat")
    public static Date getDateFromString(String date) throws ParseException {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MMM dd, yyyy");
        return dateTimeFormat.parse(date);
    }

    public static String getToday() {
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date());
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrentTime() {
        Date time = new Date();
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss.SSS");
        return dateTimeFormat.format(time);
    }

}
