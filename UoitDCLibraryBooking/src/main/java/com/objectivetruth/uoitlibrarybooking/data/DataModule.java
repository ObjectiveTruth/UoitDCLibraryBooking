package com.objectivetruth.uoitlibrarybooking.data;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.gson.Gson;
import com.objectivetruth.uoitlibrarybooking.data.Models.CalendarModel;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class DataModule {
    private Application mApplication;

    DataModule(Application application) {
        this.mApplication = application;
    }

    @Provides
    @Singleton
    CalendarModel providesCalendarModel() {
        return new CalendarModel();
    }

    @Provides
    @Named("Calendar")
    @Singleton
    SharedPreferences providesSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mApplication);
    }

    @Provides
    @Named("Calendar")
    @Singleton
    SharedPreferences.Editor providesSharedPreferencesEditor(SharedPreferences sharedPreferences) {
        return sharedPreferences.edit();
    }

    @Provides
    @Singleton
    Gson providesGson() {
        return new Gson();
    }
}
