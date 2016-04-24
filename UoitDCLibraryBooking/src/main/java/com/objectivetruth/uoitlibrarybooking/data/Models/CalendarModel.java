package com.objectivetruth.uoitlibrarybooking.data.models;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarData;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarParser;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarWebService;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class CalendarModel {
    private SharedPreferences calendarSharedPreferences;
    private SharedPreferences.Editor calendarSharedPreferencesEditor;
    final static private String CALENDAR_SHARED_PREFERENCES_NAME = "CALENDAR";
    private CalendarWebService calendarWebService;

    @SuppressLint("CommitPrefEdits")
    public CalendarModel(Application mApplication) {
        calendarSharedPreferences = mApplication.getSharedPreferences(CALENDAR_SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        calendarSharedPreferencesEditor = calendarSharedPreferences.edit();

        calendarWebService = new CalendarWebService(mApplication);
    }

    public Observable<CalendarData> getCalendarDataObs() {
        return calendarWebService.getRawWebPageObs()
                .observeOn(Schedulers.computation())
                .flatMap(new Func1<String, Observable<CalendarData>>() {
                    @Override
                    public Observable<CalendarData> call(String rawWebPage) {
                        Timber.i("Raw webpage received, passing to the Parser...");
                        return CalendarParser.parseData(rawWebPage);
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation());
    }
}
