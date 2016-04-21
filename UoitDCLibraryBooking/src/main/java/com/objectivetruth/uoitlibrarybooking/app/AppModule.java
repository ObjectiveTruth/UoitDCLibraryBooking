package com.objectivetruth.uoitlibrarybooking.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.objectivetruth.uoitlibrarybooking.BuildConfig;
import com.objectivetruth.uoitlibrarybooking.R;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.util.UUID;

import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCES_KEYS.SHARED_PREF_UUID;

@Module
class AppModule {
    private Application mApplication;

    AppModule(Application application) {
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
        if(BuildConfig.DEBUG){
            // Will just log the information without actually sending it
            analytics.setDryRun(true);
        }
        Tracker googleAnalyticsTracker = analytics.newTracker(R.xml.app_tracker);
        String usersUUID = defaultSharedPreferences.getString(SHARED_PREF_UUID, null);

        if(usersUUID == null){
            // Generate new Unique User ID if there isn't one already made. Ensures anonymity
            usersUUID = UUID.randomUUID().toString();
        }
        googleAnalyticsTracker.set("&cid", usersUUID);
        return googleAnalyticsTracker;
    }

}
