package com.objectivetruth.uoitlibrarybooking.data;

import android.app.Application;
import com.objectivetruth.uoitlibrarybooking.data.models.CalendarModel;
import com.objectivetruth.uoitlibrarybooking.data.models.UserModel;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class DataModule {
    private Application mApplication;

    public DataModule(Application application) {
        this.mApplication = application;
    }

    @Provides
    @Singleton
    CalendarModel providesCalendarModel() {
        return new CalendarModel(mApplication);
    }

    @Provides
    @Singleton
    UserModel providesUserModel() {
        return new UserModel(mApplication);
    }
}
