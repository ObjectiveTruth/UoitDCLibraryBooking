package com.objectivetruth.uoitlibrarybooking.data;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.app.networking.OkHttp3Stack;
import com.objectivetruth.uoitlibrarybooking.data.models.CalendarModel;
import com.objectivetruth.uoitlibrarybooking.data.models.UserModel;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarWebService;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserWebService;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class DataModule {
    private UOITLibraryBookingApp mApplication;

    public DataModule(UOITLibraryBookingApp application) {
        this.mApplication = application;
    }

    @Provides
    @Singleton
    CalendarModel providesCalendarModel(CalendarWebService calendarWebService) {
        return new CalendarModel(mApplication, calendarWebService);
    }

    @Provides
    @Singleton
    UserModel providesUserModel(UserWebService userWebService) {
        return new UserModel(mApplication, userWebService);
    }

    @Provides
    @Singleton
    CalendarWebService providesCalendarWebService(RequestQueue requestQueue) {
        return new CalendarWebService(mApplication, requestQueue);
    }

    @Provides
    @Singleton
    UserWebService providesUserWebService() {
        return new UserWebService(mApplication);
    }

    @Provides
    @Singleton
    protected RequestQueue providesRequestQueue() {
        //return Volley.newRequestQueue(mApplication, new MockHttpStack(mApplication));
        return Volley.newRequestQueue(mApplication, new OkHttp3Stack());
    }
}
