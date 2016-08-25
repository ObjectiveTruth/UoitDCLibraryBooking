package com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import rx.Observable;
import rx.functions.Func0;
import rx.functions.FuncN;
import timber.log.Timber;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.objectivetruth.uoitlibrarybooking.common.constants.LIBRARY.MAIN_CALENDAR_URL;

public class CalendarWebService {
    @Inject RequestQueue requestQueue;

    public CalendarWebService(UOITLibraryBookingApp mApplication) {
        mApplication.getComponent().inject(this);
    }

    public Observable<String> getRawInitialWebPageObs() {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                try {
                    return Observable.just(_getRawInitialWebpage());
                } catch (InterruptedException | ExecutionException e) {
                    Timber.w(e, "Error while trying to load main uoitlibrary webpage");
                    return Observable.error(e);
                }
            }
        });
    }

    /**
     * Returns an {@code Observable<String[]>} which is guaranteed to be in the order of the {@code calendarData.days}
     * array. All webcalls are done in parallel
     * @param calendarData
     * @return
     */
    public Observable<String[]> getRawClickableDatesWebPagesObs(final CalendarData calendarData) {
        return Observable.defer(new Func0<Observable<String[]>>() {
            @Override
            public Observable<String[]> call() {
                // Create an ArrayList of Observables that will go and get the info for each date.
                // This is done for parallelism
                ArrayList<Observable<String>> observablesArrayForEachDay = new ArrayList<Observable<String>>();
                for (CalendarDay calendarDay : calendarData.days) {
                    observablesArrayForEachDay.add(_getRawClickableDatesWebPageObs(calendarDay, calendarData));
                }

                return Observable.zip(observablesArrayForEachDay, new FuncN<String[]>() {
                    @Override
                    public String[] call(Object... args) {
                        return _convertArrayOfObjectsToArrayOfStrings(args);
                    }
                });
            }
        });
    }

    public Observable<String> getRawClickableCalendarDayPageUsingCalendarDay(final CalendarDay calendarDay) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                try {
                    return Observable.just(_getRawCalendarDayPage(calendarDay));
                } catch (InterruptedException | ExecutionException e) {
                    Timber.w(e, "Error while getting raw clickable date" + calendarDay.toString());
                    return Observable.error(e);
                }
            }
        });
    }

    private String _getRawCalendarDayPage(final CalendarDay calendarDay)
            throws InterruptedException, ExecutionException{
        Timber.d("Starting the POST request to the clickable date " + calendarDay.extDayOfMonthNumber + "...");
        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest stringRequest =
                new StringRequest(Request.Method.POST, MAIN_CALENDAR_URL, future, future) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String>  headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/x-www-form-urlencoded");
                        headers.put("Referer", MAIN_CALENDAR_URL);
                        return headers;
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return _getBodyInBytesForCalendarDayData(calendarDay);
                    }
                };
        requestQueue.add(stringRequest);
        String rawWebPage = future.get();
        Timber.i("POST request finished when searching for cookie on day: " + calendarDay.extDayOfMonthNumber);
        return rawWebPage;
    }

    /**
     * Returns an observable that gets the raw Webpage for 1 clickable calendarday
     * @param calendarDay
     * @return
     */
    private Observable<String> _getRawClickableDatesWebPageObs(final CalendarDay calendarDay,
                                                               final CalendarData calendarData) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                try {
                    return Observable.just(_getRawClickableDateWebPage(calendarDay, calendarData));
                } catch (InterruptedException | ExecutionException e) {
                    Timber.w(e, "Error while trying to load a clickable date here was the contents: " +
                            calendarDay.toString());
                    return Observable.error(e);
                }
            }
        });
    }

    /**
     * Blocking HTTP call from the Asynchronous Volley Request Queue, intended to be used with RxJava
     * @return String
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private String _getRawClickableDateWebPage(final CalendarDay calendarDay, final CalendarData calendarData)
            throws ExecutionException, InterruptedException{
        Timber.i("Starting the POST request to the clickable date " + calendarDay.extDayOfMonthNumber + "...");
        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest stringRequest =
                new StringRequest(Request.Method.POST, MAIN_CALENDAR_URL, future, future) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String>  headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/x-www-form-urlencoded");
                        return headers;
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return _getBodyInBytesForCalendarDayData(calendarDay);
                    }
                };
        requestQueue.add(stringRequest);
        String rawWebpage = future.get();
        Timber.i("POST request finished for clickable date " + calendarDay.extDayOfMonthNumber);
        return rawWebpage;
    }

    /**
     * Blocking HTTP call from the Asynchronous Volley Request Queue, intended to be used with RxJava
     * @return String
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private String _getRawInitialWebpage() throws ExecutionException, InterruptedException{
        Timber.i("Starting the GET request to the initial uoitlibrary webpage...");
        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest stringRequest =
                new StringRequest(Request.Method.GET, MAIN_CALENDAR_URL, future, future) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };

        requestQueue.add(stringRequest);
        String rawWebpage = future.get();
        Timber.i("GET request to the initial uoitlibrary webpage finished");
        return rawWebpage;
    }

    /**
     * Returns an Array of Strings from an Array of Objects. This is because {@code Observables.zip()} requires a lambda
     * that takes an array of Objects. This is because of invariance if you want to learn more about this concept.
     * In general Java sucks for it. Thanks Obama...
     * @param args
     * @return
     */
    public static String[] _convertArrayOfObjectsToArrayOfStrings(Object[] args) {
        String[] returnStringArr = new String[args.length];
        int i = 0;
        for(Object object: args) {
            returnStringArr[i] = (String) object;
            i++;
        }
        return returnStringArr;
    }

    private byte[] _getBodyInBytesForCalendarDayData(CalendarDay calendarDay) {
        try{
            String content =
                    "__EVENTTARGET=" + URLEncoder.encode(calendarDay.extEventTarget, "UTF-8")
                    + "&__EVENTARGUMENT=" + URLEncoder.encode(calendarDay.extEventArgument, "UTF-8")
                    + "&__VIEWSTATE=" + URLEncoder.encode(calendarDay.extViewStateMain, "UTF-8")
                    + "&__EVENTVALIDATION=" + URLEncoder.encode(calendarDay.extEventValidation, "UTF-8")
                    + "&__VIEWSTATEGENERATOR=" + URLEncoder.encode(calendarDay.extViewStateGenerator, "UTF-8");
            Timber.v(content);
            return content.getBytes();
        }catch (UnsupportedEncodingException e) {
            Timber.e(e, "UTF-8 Not supported by URLEncoder");
            return null;
        }

    }

}
