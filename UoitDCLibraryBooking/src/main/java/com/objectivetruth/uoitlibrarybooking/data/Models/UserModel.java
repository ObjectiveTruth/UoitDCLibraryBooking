package com.objectivetruth.uoitlibrarybooking.data.models;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCES_KEYS.*;

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

    public boolean isUserLoggedIn() {
        String username = userSharedPreferences.getString(USER_USERNAME, null);
        String password = userSharedPreferences.getString(USER_PASSWORD, null);
        String institution = userSharedPreferences.getString(USER_INSTITUTION, null);
        boolean returnIsUserLoggedIn = (username != null && password != null && institution != null);

        // Make sure we're in a consistent state, so remove all personal info in case only 1 or 2 is present
        if(!returnIsUserLoggedIn) {clearPersonalData();}
        return returnIsUserLoggedIn;
    }

    private void clearPersonalData() {
        userPreferencesEditor
                .remove(USER_USERNAME)
                .remove(USER_PASSWORD)
                .remove(USER_INSTITUTION)
                .commit();
    }
}
