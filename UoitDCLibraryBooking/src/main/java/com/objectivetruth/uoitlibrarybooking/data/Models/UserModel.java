package com.objectivetruth.uoitlibrarybooking.data.models;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.util.Pair;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.MyAccountParser;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserCredentials;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserData;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserWebService;
import rx.Observable;
import rx.Observer;
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
        if(!returnIsUserLoggedIn) {_clearPersonalData();}
        return returnIsUserLoggedIn;
    }

    private void _clearPersonalData() {
        Timber.d("Clearing all personal data");
        userPreferencesEditor
                .remove(USER_USERNAME)
                .remove(USER_PASSWORD)
                .remove(USER_INSTITUTION)
                .commit();
    }

    private void _savePersonalData(UserCredentials userCredentials) {
        Timber.d("Saving user's personal data");
        userPreferencesEditor
                .putString(USER_USERNAME, userCredentials.username)
                .putString(USER_PASSWORD, userCredentials.password)
                .putString(USER_INSTITUTION, userCredentials.institutionId)
                .commit();
    }

    public Observable<Pair<UserData, UserCredentials>> signIn(final UserCredentials userCredentials) {
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
                });
        _bindObservableAndLoginState(returnObservable);
        return returnObservable;
    }

    private void _bindObservableAndLoginState(Observable<Pair<UserData, UserCredentials>>
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
                    _savePersonalData(userDataUserCredentialsPair.second);
                }
            }
        });
    }
}
