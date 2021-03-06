package com.objectivetruth.uoitlibrarybooking.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.objectivetruth.uoitlibrarybooking.BuildConfig;
import com.objectivetruth.uoitlibrarybooking.data.DataModule;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCES_KEYS.*;

public class UOITLibraryBookingApp extends Application {
    private AppComponent mComponent;
    public static boolean IS_FIRST_TIME_LAUNCH_SINCE_UPGRADE_OR_INSTALL = false;

	public UOITLibraryBookingApp() {
			super();
	}

    @Override
    public void onCreate() {
        super.onCreate();
        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());

        mComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .dataModule(new DataModule(this))
                .build();

        _initializeLogger();
        _checkIfFirstTimeAppLaunchedSinceInstall();
        _checkIfFirstTimeAppLaunchedSinceVersionUpgradeOrInstall();
    }

    public void setComponent(AppComponent mComponent) {
        this.mComponent = mComponent;

    }

    public AppComponent getComponent() {
        return mComponent;
    }

    @SuppressLint("CommitPrefEdits") // We want this to be blocking because its relied upon in rest of app
    private void _checkIfFirstTimeAppLaunchedSinceVersionUpgradeOrInstall() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int oldAppVersion = sharedPreferences.getInt(APPVERSION, -1);
        if(oldAppVersion < 0){
            Crashlytics.setBool("Upgradeing" , false);
            IS_FIRST_TIME_LAUNCH_SINCE_UPGRADE_OR_INSTALL = true;
            Timber.i("App Version NOT found, saving current app version as " + BuildConfig.VERSION_CODE);
            sharedPreferences.edit()
                    .putBoolean(HAS_DISMISSED_WHATSNEW_DIALOG_THIS_VERSION, false)
                    .putInt(APPVERSION, BuildConfig.VERSION_CODE)
                    .commit();
        }
        else if(oldAppVersion != BuildConfig.VERSION_CODE){
            Crashlytics.setBool("Upgradeing" , true);
            IS_FIRST_TIME_LAUNCH_SINCE_UPGRADE_OR_INSTALL = true;
            Timber.i("App Version has CHANGED: " + BuildConfig.VERSION_CODE + " (previously: " +  oldAppVersion + ")");
            sharedPreferences.edit()
                    .putBoolean(HAS_DISMISSED_WHATSNEW_DIALOG_THIS_VERSION, false)
                    .putInt(APPVERSION, BuildConfig.VERSION_CODE)
                    .commit();
        }
        else{
            IS_FIRST_TIME_LAUNCH_SINCE_UPGRADE_OR_INSTALL = false;
            Crashlytics.setBool("Upgradeing" , false);
            Timber.i("App Version has NOT changed: " + oldAppVersion);
        }
    }

    public static boolean isFirstTimeLaunchSinceUpgradeOrInstall() {
        return IS_FIRST_TIME_LAUNCH_SINCE_UPGRADE_OR_INSTALL;
    }

    private void _initializeLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
        String mUUID = PreferenceManager.getDefaultSharedPreferences(this).getString(UUID, null);
        if(mUUID == null){
            mUUID = java.util.UUID.randomUUID().toString();
            Timber.i("UUID NOT found, creating a new one and storing: " + mUUID);
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString(UUID, mUUID).commit();
        }else{
            Timber.d("UUID found: " + mUUID);
        }
        Crashlytics.setUserIdentifier(mUUID);
    }

    private void _checkIfFirstTimeAppLaunchedSinceInstall() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstTimeAppOpening = sharedPreferences.getBoolean(IS_FIRST_TIME_LAUNCH, true);
        if(isFirstTimeAppOpening){
            Timber.i("First time app is being launched since initial install");
            Crashlytics.setBool(IS_FIRST_TIME_LAUNCH, true);
            sharedPreferences.edit().putBoolean(IS_FIRST_TIME_LAUNCH, false).apply();
        }
        else{
            Crashlytics.setBool(IS_FIRST_TIME_LAUNCH, false);
        }
    }

    /** A tree which logs important information for crash reporting. */
    private static class CrashReportingTree extends Timber.DebugTree {
        @Override
        public void v(String message, Object... args) {
            // Don't log Verbose messages
        }

        @Override
        public void d(String message, Object... args) {
            Crashlytics.log(String.format(message, args));
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


