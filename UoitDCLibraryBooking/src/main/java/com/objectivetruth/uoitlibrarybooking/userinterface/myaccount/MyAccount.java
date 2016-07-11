package com.objectivetruth.uoitlibrarybooking.userinterface.myaccount;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.*;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.UserModel;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserCredentials;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserData;
import com.objectivetruth.uoitlibrarybooking.userinterface.myaccount.login.LoginFragment;
import com.objectivetruth.uoitlibrarybooking.userinterface.myaccount.myaccountloaded.MyAccountLoaded;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import javax.inject.Inject;

public class MyAccount extends Fragment {
    private PublishSubject<String> loginErrorSubject;
    private Subscription currentLogoutSubscription;
    private Subscription currentSignInSubscription;
    private PublishSubject<UserCredentials> signInClickedSubject;
    public @Inject UserModel userModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((UOITLibraryBookingApp) getActivity().getApplication()).getComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true); // Notifies activity that this fragment will interact with the action/options menu
        return inflater.inflate(R.layout.my_account, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(userModel.isUserSignedIn()) {
            Timber.i("User is already signed in, showing his details");
            _showMyAccountLoadedFragment();
        }else {
            Timber.i("User isn't signed in, showing login screen");
            _showLoginFragment(_getSignInClickedSubject());
        }
    }

    private static void _sendErrorMessageToLoginErrorSubject(String errorMessage,
                                                             PublishSubject<String> loginErrorSubject) {
        loginErrorSubject.onNext(errorMessage);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.my_account_loaded_action_icons_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.my_account_menu_item_logout:
                Timber.i("Logout clicked");
                userModel.getLogoutSubject().onNext(new UserModel.LogoutEvent());
                _showLoginFragment(_getSignInClickedSubject());
                return true;
            default:
                getActivity().onOptionsItemSelected(item);
                return true;
        }
    }


    public static MyAccount newInstance() {
        return new MyAccount();
    }

    private void _showMyAccountLoadedFragment() {
        String MY_ACCOUNT_LOADED_FRAGMENT_TAG = "SINGLETON_MY_ACCOUNT_LOADED_FRAGMENT_TAG";
        getActivity().invalidateOptionsMenu();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.my_account_content_frame,
                        MyAccountLoaded.newInstance(userModel.getUserDataFromStorage(), this),
                        MY_ACCOUNT_LOADED_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    private void _showLoginFragment(PublishSubject<UserCredentials> signInClickedSubject) {
        String MY_ACCOUNT_LOGIN_FRAGMENT_TAG = "SINGLETON_MY_ACCOUNT_LOGIN_FRAGMENT_TAG";
/*        Fragment mLoginFragment = getChildFragmentManager()
                .findFragmentByTag(MY_ACCOUNT_LOGIN_FRAGMENT_TAG);
        if(mLoginFragment == null){
            Timber.d("Fragment with tag: " + MY_ACCOUNT_LOGIN_FRAGMENT_TAG + " not found, instantiating a new one");
            mLoginFragment = LoginFragment.newInstance();
        }else {
            Timber.d("Fragment with tag: " + MY_ACCOUNT_LOGIN_FRAGMENT_TAG +
                    " found. retrieving it without creating a new one");
        }*/
        _bindSignInClickedSubjectToSignInFlow(signInClickedSubject);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.my_account_content_frame,
                        LoginFragment.newInstance(signInClickedSubject, _getLoginErrorSubject()),
                        MY_ACCOUNT_LOGIN_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    private PublishSubject<String> _getLoginErrorSubject() {
        if(loginErrorSubject == null) {
            Timber.d("Current loginErrorSubject is NULL, making new one");
            return loginErrorSubject = PublishSubject.create();
        }else if (loginErrorSubject.hasCompleted()) {
            Timber.d("Current loginErrorSubject hasCompleted, making new one");
            return loginErrorSubject = PublishSubject.create();
        }else {
            Timber.d("Current loginErrorSubject is still valid, passing it back");
            return loginErrorSubject;
        }
    }

    public Observable<Pair<UserData, UserCredentials>> getSignInObs() {
        Observable<Pair<UserData, UserCredentials>> returnObservable = userModel.signInObs();
        returnObservable
                .subscribe(new Observer<Pair<UserData, UserCredentials>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Pair<UserData, UserCredentials> userDataUserCredentialsPair) {
                        if(userDataUserCredentialsPair.first.errorMessage != null) {
                            Timber.w("There was an authentication error in MyAccount, showing Login fragment");
                            _showLoginFragment(_getSignInClickedSubject());
                        }
                    }
                });
        return returnObservable;
    }

    private void _bindSignInClickedSubjectToSignInFlow(PublishSubject<UserCredentials>
                                                                              signInClickSubject) {
        currentSignInSubscription = signInClickSubject
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())

                .flatMap(new Func1<UserCredentials, Observable<Pair<UserData, UserCredentials>>>() {
                    @Override
                    public Observable<Pair<UserData, UserCredentials>> call(UserCredentials userCredentials) {
                        return userModel.signInObs(userCredentials);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.computation())

                .subscribe(new Observer<Pair<UserData, UserCredentials>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Pair<UserData, UserCredentials> userDataUserCredentialsPair) {
                        if(userDataUserCredentialsPair.first.errorMessage != null) {
                            Timber.d("Received Error Message from Parser, publishing to Subject");
                            _sendErrorMessageToLoginErrorSubject(userDataUserCredentialsPair.first.errorMessage,
                                    _getLoginErrorSubject());
                        }else {
                            Timber.i("Received UserData");
                            Timber.v(userDataUserCredentialsPair.first.toString());
                            _showMyAccountLoadedFragment();
                        }
                    }
                });
    }

    private PublishSubject<UserCredentials> _getSignInClickedSubject() {
        if(signInClickedSubject == null) {
            Timber.d("Current sigInClickSubject is NULL, making new one");
            return signInClickedSubject = PublishSubject.create();
        }else if (signInClickedSubject.hasCompleted()) {
            Timber.d("Current signInClickSubject has completed already, making new one");
            return signInClickedSubject = PublishSubject.create();
        }else {
            Timber.d("Current signInClickSubject is still valid, passing it back");
            return signInClickedSubject;
        }
    }

    @Override
    public void onDestroyView() {
        _unsubscribeFromAllObservablesAndCompleteAllSubjectsCreated();
        super.onDestroyView();
    }

    /**
     * Prevents memory leaks by unsubscribing to any active subscriptions and completing any
     * subjects created by MyAccount
     */
    private void _unsubscribeFromAllObservablesAndCompleteAllSubjectsCreated() {
        if(currentLogoutSubscription != null) {currentLogoutSubscription.unsubscribe();}
        if(currentSignInSubscription != null) {currentSignInSubscription.unsubscribe();}
        if(signInClickedSubject != null) {signInClickedSubject.onCompleted();}
        if(loginErrorSubject != null) {loginErrorSubject.onCompleted();}
    }
}
