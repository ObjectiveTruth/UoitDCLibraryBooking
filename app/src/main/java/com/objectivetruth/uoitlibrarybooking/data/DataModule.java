package com.objectivetruth.uoitlibrarybooking.data;

import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.objectivetruth.uoitlibrarybooking.BuildConfig;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.app.networking.MockHttpStack;
import com.objectivetruth.uoitlibrarybooking.app.networking.OkHttp3Stack;
import com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCES_KEYS;
import com.objectivetruth.uoitlibrarybooking.data.models.BookingInteractionModel;
import com.objectivetruth.uoitlibrarybooking.data.models.CalendarModel;
import com.objectivetruth.uoitlibrarybooking.data.models.UserModel;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionWebService;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarWebService;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserWebService;
import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

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
    BookingInteractionModel providesBookingInteractionModel(BookingInteractionWebService bookingInteractionWebService) {
        return new BookingInteractionModel(mApplication, bookingInteractionWebService);
    }

    @Provides
    @Singleton
    BookingInteractionWebService providesBookingInteractionWebService() {
        return new BookingInteractionWebService(mApplication);
    }

    @Provides
    @Singleton
    CalendarWebService providesCalendarWebService() {
        return new CalendarWebService(mApplication);
    }

    @Provides
    @Singleton
    UserWebService providesUserWebService() {
        return new UserWebService(mApplication);
    }

    @Provides
    @Singleton
    protected RequestQueue providesRequestQueue() {
        if(BuildConfig.DEBUG && _hasUserRequestedMockingOfHTTP()) {
            Timber.i("Using Mock Http Stack");
            return Volley.newRequestQueue(mApplication, new MockHttpStack(mApplication));
        }else {
            return Volley.newRequestQueue(mApplication, new OkHttp3Stack());
        }
    }

    private boolean _hasUserRequestedMockingOfHTTP() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mApplication);
        return sharedPreferences.getBoolean(SHARED_PREFERENCES_KEYS.DEBUG_SHOULD_MOCK_HTTP_CALLS, false);
    }
}
