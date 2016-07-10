package com.objectivetruth.uoitlibrarybooking.data.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.util.Pair;
import com.google.gson.Gson;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.*;
import rx.Observable;
import rx.Observer;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.FuncN;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import java.util.ArrayList;

import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCES_KEYS.CALENDAR_DATA_JSON;
import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCE_NAMES.CALENDAR_SHARED_PREFERENCES_NAME;

public class CalendarModel {
    private static final String EMPTY_JSON = "{}";

    private SharedPreferences calendarSharedPreferences;
    private SharedPreferences.Editor calendarSharedPreferencesEditor;
    // Keep a reference to both so we send back the same when a client asks
    private BehaviorSubject<CalendarDataRefreshState> calendarDataRefreshStateBehaviorSubject;
    private Observable<CalendarDataRefreshState> calendarDataRefreshStateBehaviorSubjectAsObservable;
    private PublishSubject<RefreshActivateEvent> refreshActivateEventPublishSubject;
    private CalendarWebService calendarWebService;

    @SuppressLint("CommitPrefEdits")
    public CalendarModel(UOITLibraryBookingApp mApplication, CalendarWebService calendarWebService) {
        calendarSharedPreferences = mApplication.getSharedPreferences(CALENDAR_SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        calendarSharedPreferencesEditor = calendarSharedPreferences.edit();
        this.calendarWebService = calendarWebService;
    }

    /**
     * An Observable that is always updated with the latest state of the Refresh system.
     * @see CalendarDataRefreshState
     * @return
     */
    public Observable<CalendarDataRefreshState> getCalendarDataRefreshObservable() {
        _getCalendarDataRefreshStateBehaviorSubject(); // Sets up all the references before we return
        return calendarDataRefreshStateBehaviorSubjectAsObservable;
    }

    private BehaviorSubject<CalendarDataRefreshState> _getCalendarDataRefreshStateBehaviorSubject() {
        if(calendarDataRefreshStateBehaviorSubject == null || calendarDataRefreshStateBehaviorSubject.hasCompleted()) {
            CalendarDataRefreshState initialState = new CalendarDataRefreshState(CalendarDataRefreshStateType.INITIAL,
                    _getCalendarDataFromStorage(), null);
            calendarDataRefreshStateBehaviorSubject = BehaviorSubject.create(initialState);
            calendarDataRefreshStateBehaviorSubjectAsObservable = calendarDataRefreshStateBehaviorSubject
                    .subscribeOn(Schedulers.computation())
                    .asObservable();
            return calendarDataRefreshStateBehaviorSubject;
        }else {
            return calendarDataRefreshStateBehaviorSubject;
        }
    }

    public PublishSubject<RefreshActivateEvent> getRefreshActivatePublishSubject() {
        if(refreshActivateEventPublishSubject == null || refreshActivateEventPublishSubject.hasCompleted()) {
            refreshActivateEventPublishSubject = PublishSubject.create();
            _bindRefreshActivateEventPublishSubjectToGettingCalendarData(refreshActivateEventPublishSubject);
            return refreshActivateEventPublishSubject;
        }else {
            return refreshActivateEventPublishSubject;
        }
    }

    private void _bindRefreshActivateEventPublishSubjectToGettingCalendarData(PublishSubject<RefreshActivateEvent>
                                                                                 refreshActivateEventPublishSubject) {
        refreshActivateEventPublishSubject
                .observeOn(Schedulers.computation())
                .subscribe(new Action1<RefreshActivateEvent>() {
            @Override
            public void call(RefreshActivateEvent refreshActivateEvent) {
                if(isARefreshRequestNOTRunning()) {
                    Timber.d("Running a new request for Refresh Data since none are running");
                    CalendarDataRefreshState runningState =
                            new CalendarDataRefreshState(CalendarDataRefreshStateType.RUNNING,
                                    _getCalendarDataFromStorage(), null);
                    _getCalendarDataRefreshStateBehaviorSubject().onNext(runningState);

                    _startRefreshAndGetObservable()
                            .subscribe(new Observer<CalendarData>() {
                        @Override
                        public void onCompleted() {
                            // Do nothing
                        }

                        @Override
                        public void onError(Throwable e) {
                            Timber.v("Error when completing the Refresh request, passing into to the view");
                            CalendarDataRefreshState errorState =
                                    new CalendarDataRefreshState(CalendarDataRefreshStateType.ERROR,
                                            _getCalendarDataFromStorage(), e);
                            _getCalendarDataRefreshStateBehaviorSubject().onNext(errorState);
                        }

                        @Override
                        public void onNext(CalendarData calendarData) {
                            CalendarDataRefreshState successState =
                                    new CalendarDataRefreshState(CalendarDataRefreshStateType.SUCCESS,
                                            calendarData, null);
                            _getCalendarDataRefreshStateBehaviorSubject().onNext(successState);
                        }
                    });
                }else {
                    Timber.d("Refresh is already running, ignorning request");
                }
            }
        });
    }

    public boolean isARefreshRequestRunning() {
        CalendarDataRefreshState currentState = _getCalendarDataRefreshStateBehaviorSubject().getValue();
        return currentState.type == CalendarDataRefreshStateType.RUNNING;
    }

    public boolean isARefreshRequestNOTRunning() {
        return !isARefreshRequestRunning();
    }

    private Observable<CalendarData> _startRefreshAndGetObservable() {
        return calendarWebService.getRawInitialWebPageObs() // Get the initial raw Webpage of the site
                .subscribeOn(Schedulers.computation())

                // Transform it into CalendarData by Parsing the raw Webpage
                .flatMap(new Func1<String, Observable<CalendarData>>() {
                    @Override
                    public Observable<CalendarData> call(String rawWebPage) {
                        Timber.i("Raw webpage received, passing to the Parser...");
                        Timber.v(rawWebPage);
                        return CalendarParser.parseDataToFindNumberOfDaysInfoObs(rawWebPage);
                    }
                })

                // Because we need a unique ViewStateMain value and ViewStateValidation, we need to do the above process
                // again if there is more than 1 day
                .flatMap(new Func1<CalendarData, Observable<Pair<CalendarData, String[]>>>() {
                    @Override
                    public Observable<Pair<CalendarData, String[]>> call(final CalendarData calendarData) {
                        // If No data was found in the previous step or
                        // there's less than 2 days, just pass through No need to make more calls
                        if(calendarData == null || calendarData.days.size() <= 1) {
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

                // Store the results of those webcalls into the CalendarData before returning it
                .flatMap(new Func1<Pair<CalendarData, String[]>, Observable<CalendarData>>() {
                    @Override
                    public Observable<CalendarData> call(Pair<CalendarData, String[]> calendarDataPair){
                        // Pass it through if there's no days
                        if(calendarDataPair == null) {return Observable.just(null);}

                        return CalendarParser.parseDataToGetClickableDateDetailsObs(calendarDataPair);
                    }
                })

                // Store the final result in Storage
                .flatMap(new Func1<CalendarData, Observable<CalendarData>>() {
                    @Override
                    public Observable<CalendarData> call(CalendarData calendarData) {
                        _storeCalendarDataResultsInStorage(calendarData);
                        return Observable.just(calendarData);
                    }
                })
                .observeOn(Schedulers.computation());
    }

    private void _storeCalendarDataResultsInStorage(CalendarData calendarData) {
        Timber.d("Storing calendar Data in storage");
        Gson gson = new Gson();
        String calendarDataJson = gson.toJson(calendarData);
        Timber.v(calendarDataJson);
        calendarSharedPreferencesEditor
                .putString(CALENDAR_DATA_JSON, calendarDataJson)
                .apply();
    }

    private CalendarData _getCalendarDataFromStorage() {
        Timber.d("Getting Calendar Data from Storage");
        Gson gson = new Gson();
        String userDataJSON = calendarSharedPreferences.getString(CALENDAR_DATA_JSON, EMPTY_JSON);
        CalendarData returnCalendarData = gson.fromJson(userDataJSON, CalendarData.class);
        if(returnCalendarData == null) {
            Timber.v("Stored CalendarData JSON is null");
        }else{
            Timber.v(returnCalendarData.toString());
        }
        return returnCalendarData;
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
