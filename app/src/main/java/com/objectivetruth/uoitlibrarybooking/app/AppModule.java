package com.objectivetruth.uoitlibrarybooking.app;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.objectivetruth.uoitlibrarybooking.BuildConfig;
import com.objectivetruth.uoitlibrarybooking.R;
import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

import javax.inject.Singleton;

import static com.objectivetruth.uoitlibrarybooking.common.constants.Analytics.SERIALS_TO_IGNORE_FOR_ANALYTICS;
import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCES_KEYS.UUID;

@Module
public class AppModule {
    private UOITLibraryBookingApp mApplication;

    public AppModule(UOITLibraryBookingApp application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mApplication);
    }

    @Provides
    @Singleton
    SharedPreferences.Editor providesSharedPreferencesEditor(SharedPreferences sharedPreferences) {
        return sharedPreferences.edit();
    }

    @Provides
    @Singleton
    Tracker providesGoogleAnalyticsTracker(SharedPreferences defaultSharedPreferences) {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(mApplication);
        if(BuildConfig.DEBUG || _isSerialInIgnoreList(Build.SERIAL)){
            // Will just log the information without actually sending it
            analytics.setDryRun(true);
        }
        Tracker googleAnalyticsTracker = analytics.newTracker(R.xml.app_tracker);
        String usersUUID = defaultSharedPreferences.getString(UUID, null);

        if(usersUUID == null){
            // Generate new Unique User ID if there isn't one already made. Ensures anonymity
            usersUUID = java.util.UUID.randomUUID().toString();
        }
        googleAnalyticsTracker.set("&cid", usersUUID);
        return googleAnalyticsTracker;
    }

    private boolean _isSerialInIgnoreList(String thisDeviceSerial) {
        for (String s: SERIALS_TO_IGNORE_FOR_ANALYTICS) {
            if(s.contentEquals(thisDeviceSerial)) {
                Timber.i("Serial: " + thisDeviceSerial + " is in the analytics ignore list, disabling Google Analytics");
                return true;
            }
        }
        return false;
    }
}
