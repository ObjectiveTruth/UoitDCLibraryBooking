package com.objectivetruth.uoitlibrarybooking.data.models;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.util.Pair;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarData;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarParser;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarWebService;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.FuncN;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import java.util.ArrayList;

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

                // Because we need a unique ViewStateMain value and ViewStateValidation, we need to do the above process
                // again if there is more than 1 day
                .flatMap(new Func1<CalendarData, Observable<Pair<CalendarData, String[]>>>() {
                    @Override
                    public Observable<Pair<CalendarData, String[]>> call(final CalendarData calendarData) {
                        // If there's less than 2 days, just pass through No need to make more calls
                        if(calendarData.days.size() <= 1) {
                            return Observable.just(new Pair<CalendarData, String[]>(calendarData, null));}


                        // If its greater than 1 it means we have to get fresh ViewStateMain and ViewStateValidation
                        // value or else the page won't respond correctly
                        ArrayList<Observable<String>> arrayOfRawPageGetsRequestObs =
                            _getArrayListOfObservablesToGetRawMainPageAgainForEveryPageAboveTheFirst(calendarData);

                        return Observable.zip(arrayOfRawPageGetsRequestObs,
                                new FuncN<Pair<CalendarData, String[]>>() {
                            @Override
                            public Pair<CalendarData, String[]> call(Object... args) {
                                String[] stringArrToReturn =
                                        _convertArrayOfObjectsToArrayOfStrings(args);
                                return new Pair<>(calendarData, stringArrToReturn);
                            }
                        });
                    }
                })
                .observeOn(Schedulers.computation())

                // We now have the raw web pages with fresh ViewStateMain and ViewStateValidation values we can use
                // If there was more than 1 page (from previous step)
                .flatMap(new Func1<Pair<CalendarData, String[]>, Observable<CalendarData>>() {
                    @Override
                    public Observable<CalendarData> call(Pair<CalendarData, String[]> calendarDataPair) {
                        // If the String[] is null then we know we passed it throgh from above, so we will pass it again
                        if(calendarDataPair.second == null) {return Observable.just(calendarDataPair.first);}

                        return CalendarParser
                                .parseDataToFindAdditionalNumberOfDaysInfoObs(calendarDataPair.first,
                                        calendarDataPair.second);
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())

                // Make more webcalls based on the Parsing information from previous step. Return those raw webpages
                .flatMap(new Func1<CalendarData, Observable<Pair<CalendarData, String[]>>>() {
                    @Override
                    public Observable<Pair<CalendarData, String[]>> call(CalendarData calendarData) {
                        // Pass it through if there's no days
                        if(calendarData == null) {return Observable.just(null);}

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
                        // Pass it through if there's no days
                        if(calendarDataPair == null) {return Observable.just(null);}

                        return CalendarParser.parseDataToGetClickableDateDetailsObs(calendarDataPair);
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation());
    }

    private ArrayList<Observable<String>>
            _getArrayListOfObservablesToGetRawMainPageAgainForEveryPageAboveTheFirst(CalendarData calendarData) {
        int numberOfDaysAboveTheFirst = calendarData.days.size() -1;
        ArrayList<Observable<String>> arrayListToReturn = new ArrayList<>(numberOfDaysAboveTheFirst);
        for(int i = 0; i < numberOfDaysAboveTheFirst; i++) {
            arrayListToReturn.add(calendarWebService.getRawInitialWebPageObs());
        }
        return arrayListToReturn;
    }

    /**
     * Returns an Array of Strings from an Array of Objects. This is because {@code Observables.zip()} requires a lambda
     * that takes an array of Objects. This is because of invariance if you want to learn more about this concept.
     * In general Java sucks for it. Thanks Obama...
     * @param args
     * @return
     */
    private static String[] _convertArrayOfObjectsToArrayOfStrings(Object[] args) {
        String[] returnStringArr = new String[args.length];
        int i = 0;
        for(Object object: args) {
            returnStringArr[i] = (String) object;
            i++;
        }
        return returnStringArr;
    }
}
