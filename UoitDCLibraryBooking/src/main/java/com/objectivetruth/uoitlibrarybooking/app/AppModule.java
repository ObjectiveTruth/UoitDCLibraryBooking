package com.objectivetruth.uoitlibrarybooking.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class AppModule {
    private Application mApplication;

    public AppModule(Application application) {
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
}
