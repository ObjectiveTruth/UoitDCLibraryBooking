package com.objectivetruth.uoitlibrarybooking.data.models.usermodel;

import android.support.v4.util.Pair;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class UserWebService {
    @Inject RequestQueue requestQueue;
    final private static String UOIT_LIBRARY_SIGNIN_URL =
            "https://rooms.library.dc-uoit.ca/uoit_studyrooms/myreservations.aspx";

    public UserWebService(UOITLibraryBookingApp mApplication) {
        mApplication.getComponent().inject(this);
    }

    public Observable<String> getRawInitialSignInWebPageObs() {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                try {
                    return Observable.just(_getRawInitialSignInWebpage());
                } catch (InterruptedException | ExecutionException e) {
                    Timber.e(e, "Error while trying to load initial sign-in uoitlibrary webpage");
                    return Observable.error(e);
                }
            }
        });
    }

    public Observable<Pair<String, UserCredentials>> getRawSignedInMyReservationsPageObs(final UserCredentials userCredentials) {
        return Observable.defer(new Func0<Observable<Pair<String, UserCredentials>>>() {
            @Override
            public Observable<Pair<String, UserCredentials>> call() {
                try {
                    return Observable.just(new Pair<>(_getRawSignedInMyReservationsWebpage(userCredentials),
                            userCredentials));
                } catch (InterruptedException | ExecutionException e) {
                    Timber.e(e, "Error while trying to load signed-in myreservations uoitlibrary webpage");
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
    private String _getRawSignedInMyReservationsWebpage(final UserCredentials userCredentials)
            throws ExecutionException, InterruptedException {
        Timber.i("Starting the POST request to the signed-in my reservations uoitlibrary webpage...");
        RequestFuture<String> future = RequestFuture.newFuture();

        StringRequest stringRequest =
                new StringRequest(Request.Method.POST, UOIT_LIBRARY_SIGNIN_URL, future, future) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/x-www-form-urlencoded");
                        return headers;
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return _getBodyInBytesForUserCredentials(userCredentials);
                    }
                };
        requestQueue.add(stringRequest);
        Timber.i("POST request to the initial sign-in uoitlibrary webpage finished");
        return future.get();

    }

    /**
     * Blocking HTTP call from the Asynchronous Volley Request Queue, intended to be used with RxJava
     * @return String
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private String _getRawInitialSignInWebpage() throws ExecutionException, InterruptedException{
        Timber.i("Starting the GET request to the initial sign-in uoitlibrary webpage...");
        RequestFuture<String> future = RequestFuture.newFuture();

        StringRequest stringRequest =
                new StringRequest(Request.Method.GET, UOIT_LIBRARY_SIGNIN_URL, future, future);
        requestQueue.add(stringRequest);
        Timber.i("GET request to the initial sign-in uoitlibrary webpage finished");
        return future.get();
    }

    private byte[] _getBodyInBytesForUserCredentials(UserCredentials userCredentials) {
        try{
            String content =  "__VIEWSTATE=" + URLEncoder.encode(userCredentials.viewState, "UTF-8")
                    + "&__EVENTVALIDATION=" + URLEncoder.encode(userCredentials.eventValidation, "UTF-8")
                    + "&__VIEWSTATEGENERATOR=" + URLEncoder.encode(userCredentials.viewStateGenerator, "UTF-8")
                    + "&ctl00$ContentPlaceHolder1$TextBoxPassword=" + URLEncoder.encode(userCredentials.password, "UTF-8")
                    + "&ctl00$ContentPlaceHolder1$TextBoxID=" + URLEncoder.encode(userCredentials.username, "UTF-8")
                    + "&ctl00$ContentPlaceHolder1$ButtonListBookings=" + URLEncoder.encode("My Bookings", "UTF-8");
            Timber.v("Body content will be:");
            Timber.v(content);
            return content.getBytes();
        }catch (UnsupportedEncodingException e) {
            Timber.e(e, "UTF-8 Not supported by URLEncoder");
            return null;
        }

    }
}
