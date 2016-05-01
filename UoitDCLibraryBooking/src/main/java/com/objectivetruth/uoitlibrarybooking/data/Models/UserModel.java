package com.objectivetruth.uoitlibrarybooking.data.models;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.MyAccountParser;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserCredentials;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserData;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserWebService;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCES_KEYS.*;

public class UserModel {
    private SharedPreferences userSharedPreferences;
    private SharedPreferences.Editor userPreferencesEditor;
    final static private String USER_SHARED_PREFERENCES_NAME = "USER_INFO";
    private UserWebService userWebService;

    @SuppressLint("CommitPrefEdits")
    public UserModel(Application mApplication) {
        userSharedPreferences = mApplication.getSharedPreferences(USER_SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        userPreferencesEditor = userSharedPreferences.edit();
        userWebService = new UserWebService(mApplication);
    }

    public boolean isUserSignedIn() {
        String username = userSharedPreferences.getString(USER_USERNAME, null);
        String password = userSharedPreferences.getString(USER_PASSWORD, null);
        String institution = userSharedPreferences.getString(USER_INSTITUTION, null);
        boolean returnIsUserLoggedIn = (username != null && password != null && institution != null);

        // Make sure we're in a consistent state, so remove all personal info in case only 1 or 2 is present
        if(!returnIsUserLoggedIn) {clearPersonalData();}
        return returnIsUserLoggedIn;
    }

    private void clearPersonalData() {
        userPreferencesEditor
                .remove(USER_USERNAME)
                .remove(USER_PASSWORD)
                .remove(USER_INSTITUTION)
                .commit();
    }

    public Observable<UserData> signIn(final UserCredentials userCredentials) {
        return userWebService.getRawInitialSignInWebPageObs()
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())

                .flatMap(new Func1<String, Observable<UserCredentials>>() {
                    @Override
                    public Observable<UserCredentials> call(String rawMyReservationsLoginPage) {
                        Timber.d("Received raw main My Reservation Login Page, passing to Parser");
                        Timber.v(rawMyReservationsLoginPage);
                        return MyAccountParser.parseRawInitialWebPageToGetStateInfo(rawMyReservationsLoginPage,
                                userCredentials);
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())

                .flatMap(new Func1<UserCredentials, Observable<String>>() {
                    @Override
                    public Observable<String> call(UserCredentials userCredentials) {
                        return userWebService.getRawSignedInMyReservationsPageObs(userCredentials);
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())

                .flatMap(new Func1<String, Observable<UserData>>() {
                    @Override
                    public Observable<UserData> call(String rawSignedInMyReservationsWebPage) {
                        Timber.d("Received raw signed In Result from My Reservation Login Page, passing to Parser");
                        Timber.v(rawSignedInMyReservationsWebPage);
                        return MyAccountParser
                                .parseRawSignedInMyReservationsWebPageForUserData(rawSignedInMyReservationsWebPage);
                    }
                });
    }
}
