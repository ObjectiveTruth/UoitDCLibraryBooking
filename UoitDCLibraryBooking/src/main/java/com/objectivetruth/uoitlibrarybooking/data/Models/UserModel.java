package com.objectivetruth.uoitlibrarybooking.data.models;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.util.Pair;
import com.android.volley.AuthFailureError;
import com.google.gson.Gson;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.MyAccountParser;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserCredentials;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserData;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserWebService;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCES_KEYS.*;

public class UserModel {
    private SharedPreferences userSharedPreferences;
    final static private String USER_SHARED_PREFERENCES_NAME = "USER_INFO";
    private UserWebService userWebService;
    private PublishSubject<LogoutEvent> logoutSubject;
    private static final String EMPTY_JSON = "{}";

    @SuppressLint("CommitPrefEdits")
    public UserModel(Application mApplication) {
        userSharedPreferences = mApplication.getSharedPreferences(USER_SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        userWebService = new UserWebService(mApplication);
    }

    public boolean isUserSignedIn() {
        String username = userSharedPreferences.getString(USER_USERNAME, null);
        String password = userSharedPreferences.getString(USER_PASSWORD, null);
        String institution = userSharedPreferences.getString(USER_INSTITUTION, null);
        boolean returnIsUserLoggedIn = (username != null && password != null && institution != null);

        if(!returnIsUserLoggedIn) {
            Timber.d("User credentials not found, clearing all credentials to make sure data stays consistent");
            _clearPersonalData();
        }
        return returnIsUserLoggedIn;
    }

    private void _clearPersonalData() {
        Timber.d("Clearing all personal data");
        userSharedPreferences.edit()
                .remove(USER_USERNAME)
                .remove(USER_PASSWORD)
                .remove(USER_INSTITUTION)
                .apply();
    }

    private boolean isUserNotSignedIn() {
        return !isUserSignedIn();
    }

    private UserCredentials _getUserCredentialsFromStorage() {
        Timber.d("Getting user's credentials from storage");
        UserCredentials userCredentials = new UserCredentials(
                userSharedPreferences.getString(USER_USERNAME, ""),
                userSharedPreferences.getString(USER_PASSWORD, ""),
                userSharedPreferences.getString(USER_INSTITUTION, ""));
        return userCredentials;
    }

    private void _savePersonalData(UserData userData, UserCredentials userCredentials) {
        Timber.d("Saving user's personal data");
        Gson gson = new Gson();
        String userDataInJSON = gson.toJson(userData);
        Timber.v(userDataInJSON);
        userSharedPreferences.edit()
                .putString(USER_USERNAME, userCredentials.username)
                .putString(USER_PASSWORD, userCredentials.password)
                .putString(USER_INSTITUTION, userCredentials.institutionId)
                .putString(USER_DATA_JSON, userDataInJSON)
                .apply();
    }

    public UserData getUserDataFromStorage() {
        Timber.d("Gettign Users Data from Storage");
        Gson gson = new Gson();
        String userDataJSON = userSharedPreferences.getString(USER_DATA_JSON, EMPTY_JSON);
        UserData returnUserData = gson.fromJson(userDataJSON, UserData.class);
        return returnUserData;
    }

    public Observable<Pair<UserData, UserCredentials>> signInObs() {
        if(isUserNotSignedIn()) {
            return Observable.error(new AuthFailureError("User is not signed in, no credentials found in storage"));}

        UserCredentials userCredentials = _getUserCredentialsFromStorage();
        return signInObs(userCredentials);
    }

    public Observable<Pair<UserData, UserCredentials>> signInObs(final UserCredentials userCredentials) {
        Observable<Pair<UserData, UserCredentials>> returnObservable =
                userWebService.getRawInitialSignInWebPageObs()
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

                .flatMap(new Func1<UserCredentials, Observable<Pair<String, UserCredentials>>>() {
                    @Override
                    public Observable<Pair<String, UserCredentials>> call(UserCredentials userCredentials) {
                        return userWebService.getRawSignedInMyReservationsPageObs(userCredentials);
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())

                .flatMap(new Func1<Pair<String, UserCredentials>, Observable<Pair<UserData, UserCredentials>>>() {
                    @Override
                    public Observable<Pair<UserData, UserCredentials>>
                    call(Pair<String, UserCredentials>  rawWebpageUserCredentialsPair) {
                        Timber.d("Received raw signed In Result from My Reservation Login Page, passing to Parser");
                        Timber.v(rawWebpageUserCredentialsPair.first);
                        return MyAccountParser
                                .parseRawSignedInMyReservationsWebPageForUserData(rawWebpageUserCredentialsPair);
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())

                .flatMap(new Func1<Pair<UserData, UserCredentials>, Observable<Pair<UserData, UserCredentials>>>() {
                    @Override
                    public Observable<Pair<UserData, UserCredentials>>
                    call(Pair<UserData, UserCredentials> userDataUserCredentialsPair) {
                        if(userDataUserCredentialsPair.first.errorMessage != null) {
                            Timber.d("Got Error from Sign in, clearing personal user data");
                            _clearPersonalData();
                        }
                        return Observable.just(userDataUserCredentialsPair);
                    }
                });
        _subscribeToSignInObservable(returnObservable);
        return returnObservable;
    }

    private void _subscribeToLogoutSubject(PublishSubject<LogoutEvent> logoutClickSubject) {
        logoutClickSubject.subscribe(new Observer<LogoutEvent>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(LogoutEvent logoutEvent) {
                _clearPersonalData();
            }
        });
    }

    private void _subscribeToSignInObservable(Observable<Pair<UserData, UserCredentials>>
                                                      userDataUserCredPairObservable) {
        userDataUserCredPairObservable.subscribe(new Observer<Pair<UserData, UserCredentials>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Pair<UserData, UserCredentials> userDataUserCredentialsPair) {
                Timber.i("Observed a request to login, changing the Stored UserState to match the result");
                if(userDataUserCredentialsPair.first.errorMessage != null) {
                    _clearPersonalData();
                }else {
                    _savePersonalData(userDataUserCredentialsPair.first,
                            userDataUserCredentialsPair.second);
                }
            }
        });
    }

    public PublishSubject<LogoutEvent> getLogoutSubject() {
        if(logoutSubject == null) {
            Timber.d("Current logoutSubject is NULL, making new one");
            logoutSubject = PublishSubject.create();
            _subscribeToLogoutSubject(logoutSubject);
            return logoutSubject;
        }else if (logoutSubject.hasCompleted()) {
            Timber.d("Current logoutSubject hasCompleted, making new one");
            logoutSubject = PublishSubject.create();
            _subscribeToLogoutSubject(logoutSubject);
            return logoutSubject;
        }else {
            Timber.d("Current logoutSubject is still valid, passing it back");
            return logoutSubject;
        }
    }

    public static class LogoutEvent {}
}
