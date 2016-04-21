package com.objectivetruth.uoitlibrarybooking.data.Models;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class CalendarModel {
    private SharedPreferences calendarSharedPreferences;
    private SharedPreferences.Editor calendarSharedPreferencesEditor;
    final static private String CALENDAR_SHARED_PREFERENCES_NAME = "CALENDAR";

    @SuppressLint("CommitPrefEdits")
    public CalendarModel(Application mApplication) {
        calendarSharedPreferences = mApplication.getSharedPreferences(CALENDAR_SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        calendarSharedPreferencesEditor = calendarSharedPreferences.edit();
    }
}
