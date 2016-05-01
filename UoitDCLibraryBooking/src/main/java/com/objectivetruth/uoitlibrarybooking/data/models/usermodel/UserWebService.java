package com.objectivetruth.uoitlibrarybooking.data.models.usermodel;

import android.app.Application;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import rx.Observable;
import rx.functions.Func0;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.concurrent.ExecutionException;

public class UserWebService {
    @Inject RequestQueue requestQueue;
    final private static String UOIT_LIBRARY_INITIAL_SIGNIN_URL =
            "https://rooms.library.dc-uoit.ca/uoit_studyrooms/myreservations.aspx";

    public UserWebService(Application mApplication) {
        ((UOITLibraryBookingApp) mApplication).getComponent().inject(this);
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
                new StringRequest(Request.Method.GET, UOIT_LIBRARY_INITIAL_SIGNIN_URL, future, future);
        requestQueue.add(stringRequest);
        Timber.i("GET request to the initial sign-in uoitlibrary webpage finished");
        return future.get();
    }
}
