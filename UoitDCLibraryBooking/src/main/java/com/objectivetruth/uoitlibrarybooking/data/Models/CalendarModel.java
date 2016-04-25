package com.objectivetruth.uoitlibrarybooking.data.models;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.support.v4.util.Pair;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarData;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarParser;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarWebService;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
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
        return calendarWebService.getRawInitialWebPageObs() // Get the initial raw Webpage of the site
                .observeOn(Schedulers.computation())

                // Transform it into CalendarData by Parsing the raw Webpage
                .flatMap(new Func1<String, Observable<CalendarData>>() {
                    @Override
                    public Observable<CalendarData> call(String rawWebPage) {
                        Timber.i("Raw webpage received, passing to the Parser...");
                        Timber.v(rawWebPage);
                        return CalendarParser.parseDataToFindNumberOfDaysInfoObs(rawWebPage);
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())

                // Make more webcalls based on the Parsing information from previous step. Return those raw webpages
                .flatMap(new Func1<CalendarData, Observable<Pair<CalendarData, String[]>>>() {
                    @Override
                    public Observable<Pair<CalendarData, String[]>> call(CalendarData calendarData) {
                        return Observable.combineLatest(Observable.just(calendarData),
                                calendarWebService.getRawClickableDatesWebPagesObs(calendarData),
                                new Func2<CalendarData, String[], Pair<CalendarData, String[]>>() {
                                    @Override
                                    public Pair<CalendarData, String[]> call(CalendarData calendarData,
                                                                             String[] strings) {
                                        return new Pair<CalendarData, String[]>(calendarData, strings);
                                    }
                                });
                    }
                })
                .observeOn(Schedulers.computation())

                // Store the results of those webcalls into the CalendarData before returning it
                .flatMap(new Func1<Pair<CalendarData, String[]>, Observable<CalendarData>>() {
                    @Override
                    public Observable<CalendarData> call(Pair<CalendarData, String[]> calendarDataPair){
                        return CalendarParser.parseDataToGetClickableDateDetailsObs(calendarDataPair);
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation());
    }
}
