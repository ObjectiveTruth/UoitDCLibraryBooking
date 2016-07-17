package com.objectivetruth.uoitlibrarybooking.data.models;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import com.android.volley.AuthFailureError;
import com.google.gson.Gson;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.*;
import rx.Observable;
import rx.Observer;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCES_KEYS.*;
import static com.objectivetruth.uoitlibrarybooking.data.models.usermodel.MyAccountDataLoginStateType.*;

public class UserModel {
    private SharedPreferences userSharedPreferences;
    final static private String USER_SHARED_PREFERENCES_NAME = "USER_INFO";
    private UserWebService userWebService;
    private static final String EMPTY_JSON = "{}";
    private BehaviorSubject<MyAccountDataLoginState> myAccountDataLoginStateBehaviorSubject;
    private Observable<MyAccountDataLoginState> myAccountDataLoginStateBehaviourSubjectAsObservable;
    private PublishSubject<MyAccountSignoutEvent> signoutEventPublishSubject;
    private PublishSubject<UserCredentials> signinEventPublishSubject;

    @SuppressLint("CommitPrefEdits")
    public UserModel(Application mApplication) {
        userSharedPreferences = mApplication.getSharedPreferences(USER_SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        userWebService = new UserWebService(mApplication);
    }

    /**
     * An Observable that is always updated with the latest login state of the user
     * @see MyAccountDataLoginState
     * @return
     */
    public Observable<MyAccountDataLoginState> getLoginStateObservable() {
        _getMyAccountDataLoginStateBehaviourSubject(); // Sets up all the references before we return
        return myAccountDataLoginStateBehaviourSubjectAsObservable;
    }

    /**
     * Can push events to this publish subject to tell the model to sign the user out
     * @see MyAccountSignoutEvent
     * @return
     */
    public PublishSubject<MyAccountSignoutEvent> getSignoutActivatePublishSubject() {
        if(signoutEventPublishSubject == null || signoutEventPublishSubject.hasCompleted()) {
            signoutEventPublishSubject = PublishSubject.create();
            _bindSignoutEventPublishSubjectToSignout(signoutEventPublishSubject);
            return signoutEventPublishSubject;
        }else {
            return signoutEventPublishSubject;
        }
    }

    /**
     * Can push events to this publish subject to tell the model to attempt to sign the user in.
     * Passing null to the .onNext of the publish subject will attempt to get credentials from storage
     * @see UserCredentials
     * @return
     */
    public PublishSubject<UserCredentials> getSigninActivatePublishSubject() {
        if(signinEventPublishSubject == null || signinEventPublishSubject.hasCompleted()) {
            signinEventPublishSubject = PublishSubject.create();
            _bindSigninEventPublishSubjectToSignin(signinEventPublishSubject);
            return signinEventPublishSubject;
        }else {
            return signinEventPublishSubject;
        }
    }

    private BehaviorSubject<MyAccountDataLoginState> _getMyAccountDataLoginStateBehaviourSubject() {
        if(myAccountDataLoginStateBehaviorSubject == null || myAccountDataLoginStateBehaviorSubject.hasCompleted()) {
            MyAccountDataLoginStateType loginState = _isUserSignedIn() ? SIGNED_IN : SIGNED_OUT;
            UserData userData = _getUserDataFromStorage();
            MyAccountDataLoginState initialState = new MyAccountDataLoginState(loginState, userData, null);

            myAccountDataLoginStateBehaviorSubject = BehaviorSubject.create(initialState);
            myAccountDataLoginStateBehaviourSubjectAsObservable = myAccountDataLoginStateBehaviorSubject
                    .subscribeOn(Schedulers.computation())
                    .asObservable();
            return myAccountDataLoginStateBehaviorSubject;
        }else {
            return myAccountDataLoginStateBehaviorSubject;
        }
    }

    private void _bindSigninEventPublishSubjectToSignin(PublishSubject<UserCredentials>
                                                            myAccountSigninEventPublishSubject) {
        myAccountSigninEventPublishSubject
                .observeOn(Schedulers.computation())
                .subscribe(new Action1<UserCredentials>() {
            @Override
            public void call(UserCredentials userCredentials) {
                UserCredentials userCredentialsToUse =
                        userCredentials == null ? null : _getUserCredentialsFromStorage();
                if(isASigninRequestRunning()) {Timber.d("Signin request is already running, ignoring request"); return;}

                Timber.d("Running a new request for Signin since none are running");
                MyAccountDataLoginState runningState =
                        new MyAccountDataLoginState(RUNNING, null, null);
                _getMyAccountDataLoginStateBehaviourSubject().onNext(runningState);

                _startSigninAndGetObservable(userCredentialsToUse)
                        .observeOn(Schedulers.computation())
                        .subscribe(new Observer<UserData>() {
                            @Override
                            public void onCompleted() {
                                // Do nothing
                            }

                            @Override
                            public void onError(Throwable t) {
                                Timber.w("Error when completing the Signin request, passing into to the view");
                                MyAccountDataLoginState errorState =
                                        new MyAccountDataLoginState(ERROR, null, t);
                                _getMyAccountDataLoginStateBehaviourSubject().onNext(errorState);
                            }

                            @Override
                            public void onNext(UserData userData) {
                                // null on errorMessage means all is good
                                MyAccountDataLoginState state = userData.errorMessage == null ?
                                        new MyAccountDataLoginState(SIGNED_IN, userData, null) :
                                        new MyAccountDataLoginState(ERROR, null,
                                                new AuthFailureError(userData.errorMessage));

                                _getMyAccountDataLoginStateBehaviourSubject().onNext(state);
                            }
                        });
            }
        });
    }

    private void _bindSignoutEventPublishSubjectToSignout(PublishSubject<MyAccountSignoutEvent>
                                                                    logoutEventPublishSubject) {
        logoutEventPublishSubject.subscribe(new Action1<MyAccountSignoutEvent>() {
            @Override
            public void call(MyAccountSignoutEvent myAccountSignoutEvent) {
                _clearPersonalData();
                MyAccountDataLoginState logoutState = new MyAccountDataLoginState(SIGNED_OUT, null, null);
                _getMyAccountDataLoginStateBehaviourSubject().onNext(logoutState);
            }
        });
    }

    private Observable<UserData> _startSigninAndGetObservable(final UserCredentials userCredentials) {
        return userWebService.getRawInitialSignInWebPageObs() // Get the initial webpage

                .flatMap(new Func1<String, Observable<UserCredentials>>() {
                    @Override
                    public Observable<UserCredentials> call(String rawMyReservationsLoginPage) {
                        Timber.d("Received raw main My Reservation Login Page, passing to Parser");
                        Timber.v(rawMyReservationsLoginPage);
                        return MyAccountParser.parseRawInitialWebPageToGetStateInfo(rawMyReservationsLoginPage,
                                userCredentials);
                    }
                })

                .flatMap(new Func1<UserCredentials, Observable<Pair<String, UserCredentials>>>() {
                    @Override
                    public Observable<Pair<String, UserCredentials>> call(UserCredentials userCredentials) {
                        return userWebService.getRawSignedInMyReservationsPageObs(userCredentials);
                    }
                })

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

                .flatMap(new Func1<Pair<UserData, UserCredentials>, Observable<UserData>>() {
                    @Override
                    public Observable<UserData> call(Pair<UserData, UserCredentials> userDataUserCredentialsPair) {
                        if(userDataUserCredentialsPair.first.errorMessage == null) {
                            Timber.i("Observed a request to login, changing the Stored UserState to match the result");
                            _savePersonalData(userDataUserCredentialsPair.first,
                                    userDataUserCredentialsPair.second);
                        }else {
                            Timber.d("Got Error from Sign in, clearing personal user data");
                            _clearPersonalData();
                        }
                        return Observable.just(userDataUserCredentialsPair.first);
                    }
                })
                .subscribeOn(Schedulers.computation());
    }
    public boolean isASigninRequestRunning() {
        MyAccountDataLoginState currentState = _getMyAccountDataLoginStateBehaviourSubject().getValue();
        return currentState.type == RUNNING;
    }

    public boolean isARefreshRequestNOTRunning() {
        return !isASigninRequestRunning();
    }

    @SuppressLint("CommitPrefEdits")
    private void _clearPersonalData() {
        Timber.d("Clearing all personal data");
        userSharedPreferences.edit()
                .remove(USER_USERNAME)
                .remove(USER_PASSWORD)
                .remove(USER_INSTITUTION)
                .commit();
    }

    private UserCredentials _getUserCredentialsFromStorage() {
        Timber.d("Getting user's credentials from storage");
        UserCredentials userCredentials = new UserCredentials(
                userSharedPreferences.getString(USER_USERNAME, ""),
                userSharedPreferences.getString(USER_PASSWORD, ""),
                userSharedPreferences.getString(USER_INSTITUTION, ""));
        return userCredentials;
    }

    @SuppressLint("CommitPrefEdits")
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
                .commit();
    }

    @Nullable
    public UserData _getUserDataFromStorage() {
        Timber.d("Gettign Users Data from Storage");
        Gson gson = new Gson();
        String userDataJSON = userSharedPreferences.getString(USER_DATA_JSON, EMPTY_JSON);
        UserData returnUserData = gson.fromJson(userDataJSON, UserData.class);
        return returnUserData;
    }

    private boolean _isUserSignedIn() {
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

    private boolean _isUserNotSignedIn() {
        return !_isUserSignedIn();
    }

}
