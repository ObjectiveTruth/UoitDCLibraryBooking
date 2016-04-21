package com.objectivetruth.uoitlibrarybooking.data.Models;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class UserModel {
    private SharedPreferences userSharedPreferences;
    private SharedPreferences.Editor userPreferencesEditor;
    final static private String USER_SHARED_PREFERENCES_NAME = "USER_INFO";

    @SuppressLint("CommitPrefEdits")
    public UserModel(Application mApplication) {
        userSharedPreferences = mApplication.getSharedPreferences(USER_SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        userPreferencesEditor = userSharedPreferences.edit();
    }
}
