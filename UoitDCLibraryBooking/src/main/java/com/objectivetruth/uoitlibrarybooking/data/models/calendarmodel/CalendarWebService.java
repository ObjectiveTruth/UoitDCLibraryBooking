package com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel;

import android.app.Application;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import rx.Observable;
import rx.functions.Func0;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CalendarWebService {
    @Inject RequestQueue requestQueue;
    final private static String UOIT_LIBRARY_MAIN_CALENDAR_URL =
            "https://rooms.library.dc-uoit.ca/dc_studyrooms/calendar.aspx";

    public CalendarWebService(Application mApplication) {
        ((UOITLibraryBookingApp) mApplication).getComponent().inject(this);
    }

    public Observable<String> getRawWebPageObs() {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                try {
                    return Observable.just(_getRawWebpage());
                } catch (InterruptedException | ExecutionException e) {
                    Timber.e(e, "Error while trying to load main uoitlibrary webpage");
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
    private String _getRawWebpage() throws ExecutionException, InterruptedException{
        Timber.i("Starting the GET request to the uoitlibrary webpage...");
        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest stringRequest =
                new StringRequest(Request.Method.GET, UOIT_LIBRARY_MAIN_CALENDAR_URL, future, future) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };

        requestQueue.add(stringRequest);
        Timber.i("GET request finished");
        return future.get();
    }
}
