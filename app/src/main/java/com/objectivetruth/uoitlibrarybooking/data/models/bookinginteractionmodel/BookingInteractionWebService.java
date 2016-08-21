package com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.CalendarModel;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCell;
import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;
import timber.log.Timber;

import javax.inject.Inject;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.objectivetruth.uoitlibrarybooking.common.constants.LIBRARY.MAIN_CALENDAR_RELATIVE_PATH;
import static com.objectivetruth.uoitlibrarybooking.common.constants.LIBRARY.MAIN_CALENDAR_URL;

public class BookingInteractionWebService {
    @Inject RequestQueue requestQueue;
    @Inject CalendarModel calendarModel;

    public BookingInteractionWebService(UOITLibraryBookingApp mApplication) {
        mApplication.getComponent().inject(this);
    }

    public Observable<String> getRawWebpageWithForm(final HttpCookie httpCookie, final TimeCell timeCell) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                try {
                    return Observable.just(_getRedirectByBlocking(httpCookie, timeCell))
                            .flatMap(new Func1<String, Observable<String>>() {
                                @Override
                                public Observable<String> call(String s) {
                                    try {
                                        return Observable.just(_getFormFromRedirect(httpCookie, timeCell));
                                    } catch (InterruptedException | ExecutionException e) {
                                        Timber.e(e, "Error while getting raw form with cookie: " + httpCookie.toString());
                                        return Observable.error(e);
                                    }
                                }
                            });
                } catch (InterruptedException | ExecutionException e) {
                    Timber.e(e, "Error while getting raw form with cookie: " + httpCookie.toString());
                    return Observable.error(e);
                }
            }
        });
    }
    private String _getFormFromRedirect(final HttpCookie httpCookie, final TimeCell timeCell)
            throws InterruptedException, ExecutionException{
        Timber.d("Starting the GET request to the form");
        RequestFuture<String> future = RequestFuture.newFuture();
        String urlPath = MAIN_CALENDAR_RELATIVE_PATH + timeCell.param_next;
        StringRequest stringRequest =
                new StringRequest(Request.Method.GET, urlPath, future, future) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String>  headers = new HashMap<String, String>();
                        headers.put("Cookie", httpCookie.toString());
                        headers.put("Referer", MAIN_CALENDAR_URL);
                        return headers;
                    }
                };
        requestQueue.add(stringRequest);
        Timber.d("GET request finished for getting the raw webpage with form");
        return future.get();
    }

    private String _getRedirectByBlocking(final HttpCookie httpCookie, final TimeCell timeCell)
            throws InterruptedException, ExecutionException{
        Timber.d("Starting the GET request to the 1st redirect");
        RequestFuture<String> future = RequestFuture.newFuture();
        String urlPath = MAIN_CALENDAR_RELATIVE_PATH + timeCell.param_get_link;
        StringRequest stringRequest =
                new StringRequest(Request.Method.GET, urlPath, future, future) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String>  headers = new HashMap<String, String>();
                        headers.put("Cookie", httpCookie.toString());
                        return headers;
                    }

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String>  queryParams = new HashMap<String, String>();
                        queryParams.put("starttime", timeCell.param_starttime);
                        queryParams.put("room", timeCell.param_room);
                        queryParams.put("next", timeCell.param_next);
                        return queryParams;
                    }
                };
        requestQueue.add(stringRequest);
        Timber.d("GET request finished for getting the 1st redirect");
        return future.get();
    }
}
