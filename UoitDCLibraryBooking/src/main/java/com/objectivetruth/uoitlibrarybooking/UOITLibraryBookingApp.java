package com.objectivetruth.uoitlibrarybooking;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.KeyguardManager;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

import java.util.UUID;

import static com.objectivetruth.uoitlibrarybooking.constants.SHARED_PREFERENCES_KEYS.SHARED_PREF_APPVERSION;
import static com.objectivetruth.uoitlibrarybooking.constants.SHARED_PREFERENCES_KEYS.SHARED_PREF_IS_FIRST_TIME_LAUNCH;
import static com.objectivetruth.uoitlibrarybooking.constants.SHARED_PREFERENCES_KEYS.SHARED_PREF_UUID;

public class UOITLibraryBookingApp extends Application {
	//initialized the tracker to null so I can check when the app is made
	Tracker t = null;
    public static boolean IS_FIRST_TIME_LAUNCH_SINCE_UPGRADE_OR_INSTALL = false;
    public static boolean IS_DEBUG_MODE = false;

	public UOITLibraryBookingApp() {
			super();
	}

    @Override
    public void onCreate() {
        super.onCreate();
        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());

        _checkIfFirstTimeAppLaunchedSinceInstall();
        _checkIfFirstTimeAppLaunchedSinceVersionUpgradeOrInstall();
        _checkIfIsDebugMode();

    }

    private void _checkIfFirstTimeAppLaunchedSinceVersionUpgradeOrInstall() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int oldAppVersion = sharedPreferences.getInt(SHARED_PREF_APPVERSION, -1);
        if(oldAppVersion < 0){
            Crashlytics.setBool("Upgradeing" , false);
            IS_FIRST_TIME_LAUNCH_SINCE_UPGRADE_OR_INSTALL = true;
            Timber.i("Previous version number not found, saving current app version as " + BuildConfig.VERSION_CODE);
            sharedPreferences.edit().putInt(SHARED_PREF_APPVERSION, BuildConfig.VERSION_CODE).apply();
        }
        else if(oldAppVersion != BuildConfig.VERSION_CODE){
            Crashlytics.setBool("Upgradeing" , true);
            IS_FIRST_TIME_LAUNCH_SINCE_UPGRADE_OR_INSTALL = true;
            Timber.i("Previous version (" + oldAppVersion +
                    ") is different than this version (" + BuildConfig.VERSION_CODE +
                    "), updating the saved code");
            sharedPreferences.edit().putInt(SHARED_PREF_APPVERSION, BuildConfig.VERSION_CODE).apply();
        }
        else{
            IS_FIRST_TIME_LAUNCH_SINCE_UPGRADE_OR_INSTALL = false;
            Crashlytics.setBool("Upgradeing" , false);
            Timber.i("Version number (" + oldAppVersion + ") is the same as the current version, " +
                    "will keep saved values the same");
        }
    }

    private void _checkIfIsDebugMode() {
        if (BuildConfig.DEBUG) {
            IS_DEBUG_MODE = true;
            Timber.plant(new Timber.DebugTree());
        } else {
            IS_DEBUG_MODE = false;
            Timber.plant(new CrashReportingTree());
            String mUUID = PreferenceManager.getDefaultSharedPreferences(this).getString(SHARED_PREF_UUID, null);
            if(mUUID == null){
                mUUID = UUID.randomUUID().toString();
            }
            Crashlytics.setUserIdentifier(mUUID);
        }
    }

    private void _checkIfFirstTimeAppLaunchedSinceInstall() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstTimeAppOpening = sharedPreferences.getBoolean(SHARED_PREF_IS_FIRST_TIME_LAUNCH, true);
        if(isFirstTimeAppOpening){
            Timber.i("First time app is being launched since initial install");
            Crashlytics.setBool(SHARED_PREF_IS_FIRST_TIME_LAUNCH, true);
            sharedPreferences.edit().putBoolean(SHARED_PREF_IS_FIRST_TIME_LAUNCH, false).apply();
        }
        else{
            Crashlytics.setBool(SHARED_PREF_IS_FIRST_TIME_LAUNCH, false);
        }
    }

    public Tracker getTracker() {
        if(t == null){
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            if(BuildConfig.DEBUG){
                analytics.setDryRun(true);
            }
            t = analytics.newTracker(R.xml.app_tracker);
            String mUUID = PreferenceManager.getDefaultSharedPreferences(this).getString(SHARED_PREF_UUID, null);

            if(mUUID == null){
                mUUID = UUID.randomUUID().toString();
            }
            t.set("&cid", mUUID);
        }
        return t;
    }

    /** A tree which logs important information for crash reporting. */
    private static class CrashReportingTree extends Timber.DebugTree {
        @Override
        public void v(String message, Object... args) {
            Crashlytics.log(message);
        }


        @Override
        public void i(String message, Object... args) {
            Crashlytics.log(String.format(message, args));
        }

        @Override
        public void i(Throwable t, String message, Object... args) {
            i(message, args); // Just add to the log.
        }

        @Override
        public void e(String message, Object... args) {
            i("ERROR: " + message, args); // Just add to the log.
        }

        @Override
        public void e(Throwable t, String message, Object... args) {
            e(message, args);
            Crashlytics.log(message);

            Crashlytics.logException(t);
        }
    }


}


