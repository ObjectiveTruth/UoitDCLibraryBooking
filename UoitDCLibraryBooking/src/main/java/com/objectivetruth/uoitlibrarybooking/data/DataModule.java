package com.objectivetruth.uoitlibrarybooking.data;

import android.app.Application;
import com.google.gson.Gson;
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
    CalendarModel providesCalendarModel(Gson gson) {
        return new CalendarModel(mApplication);
    }

    @Provides
    @Singleton
    UserModel providesUserModel(Gson gson) {
        return new UserModel(mApplication);
    }

    @Provides
    @Singleton
    Gson providesGson() {
        return new Gson();
    }
}
