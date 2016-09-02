package com.objectivetruth.uoitlibrarybooking.userinterface.myaccount;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.objectivetruth.uoitlibrarybooking.MainActivity;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.UserModel;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.MyAccountDataLoginState;
import com.objectivetruth.uoitlibrarybooking.userinterface.loading.Loading;
import com.objectivetruth.uoitlibrarybooking.userinterface.myaccount.login.LoginFragment;
import com.objectivetruth.uoitlibrarybooking.userinterface.myaccount.myaccountloaded.MyAccountLoaded;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

import javax.inject.Inject;

public class MyAccount extends Fragment {
    private Subscription myAccountDataLoginStateObservableSubscription;
    private static final String MY_ACCOUNT_TITLE = "My Account";
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
    public void onStart() {
        Timber.d(getClass().getSimpleName() + " onStart");
        _setupViewBindings(userModel.getLoginStateObservable());
        super.onStart();
    }

    @Override
    public void onHiddenChanged(boolean isNowHidden) {
        if(isNowHidden) {
            Timber.d(getClass().getSimpleName() + " isNowHidden");
            _teardownViewBindings();
        }else {
            Timber.d(getClass().getSimpleName() + " isNowVisible");
            _setTitle(MY_ACCOUNT_TITLE);
            _setupViewBindings(userModel.getLoginStateObservable());
        }
        super.onHiddenChanged(isNowHidden);
    }

    @Override
    public void onStop() {
        Timber.d(getClass().getSimpleName() + " Stopped");
        _teardownViewBindings();
        super.onStop();
    }

    private void _teardownViewBindings() {
        if(myAccountDataLoginStateObservableSubscription != null &&
                !myAccountDataLoginStateObservableSubscription.isUnsubscribed()) {
            myAccountDataLoginStateObservableSubscription.unsubscribe();
        }
    }

    private void _setupViewBindings(Observable<MyAccountDataLoginState> myAccountDataLoginStateObservable) {
        // if the subscription is still active, don't setup new bindings
        if(myAccountDataLoginStateObservableSubscription != null &&
                !myAccountDataLoginStateObservableSubscription.isUnsubscribed()) {
            return;
        }
        myAccountDataLoginStateObservableSubscription =
            myAccountDataLoginStateObservable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<MyAccountDataLoginState>() {
                        @Override
                        public void onCompleted() {
                            // Do nothing
                        }

                        @Override
                        public void onError(Throwable e) {
                            // Do nothing
                        }

                        @Override
                        public void onNext(MyAccountDataLoginState myAccountDataLoginState) {
                            Timber.i("On next called: " + myAccountDataLoginState.type);
                            Timber.v(myAccountDataLoginState.toString());
                            // We check if the AccountLoaded is showing because if it is, we do nothing since it takes
                            // care of handling the events
                            switch(myAccountDataLoginState.type) {
                                case RUNNING:
                                    if(_isMyAccountLoadedNOTShowing()) { _showFullscreenLoading();}
                                    break;
                                case SIGNED_IN:
                                    if(_isMyAccountLoadedNOTShowing()) {
                                        _showMyAccountLoadedFragment(myAccountDataLoginState); }
                                    break;
                                case ERROR:
                                    if(_isMyAccountLoadedNOTShowing()) {_showLoginFragment(myAccountDataLoginState);}
                                case SIGNED_OUT:
                                default:
                                    _showLoginFragment(myAccountDataLoginState);
                            }
                        }
                    });
    }

    private void _showFullscreenLoading() {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.my_account_content_frame, Loading.newInstance())
                .commit();
    }

    private boolean _isMyAccountLoadedShowing() {
        Fragment currentFragmentInContentFrame = getChildFragmentManager()
                .findFragmentById(R.id.my_account_content_frame);
        return currentFragmentInContentFrame instanceof MyAccountLoaded && currentFragmentInContentFrame.isVisible();
    }

    private void _setTitle(String title) {
        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    private boolean _isMyAccountLoadedNOTShowing() {
        return !_isMyAccountLoadedShowing();
    }

    private void _showMyAccountLoadedFragment(MyAccountDataLoginState myAccountDataLoginState) {
        String MY_ACCOUNT_LOADED_FRAGMENT_TAG = "SINGLETON_MY_ACCOUNT_LOADED_FRAGMENT_TAG";
        Fragment myAccountLoadedFragment = getChildFragmentManager().findFragmentByTag(MY_ACCOUNT_LOADED_FRAGMENT_TAG);

        if(myAccountLoadedFragment == null) {
            myAccountLoadedFragment = MyAccountLoaded.newInstance(myAccountDataLoginState);
            Timber.d("NOT FOUND fragment with tag: " + MY_ACCOUNT_LOADED_FRAGMENT_TAG + ", creating a new one");
        }else{
            Timber.d("FOUND fragment with tag: " + MY_ACCOUNT_LOADED_FRAGMENT_TAG +
                    ", retrieving it without creating a new one");
        }
        //getActivity().invalidateOptionsMenu();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.my_account_content_frame, myAccountLoadedFragment, MY_ACCOUNT_LOADED_FRAGMENT_TAG)
                .commit();
    }

    private void _showLoginFragment(MyAccountDataLoginState myAccountDataLoginState) {
        String MY_ACCOUNT_LOGIN_FRAGMENT_TAG = "SINGLETON_MY_ACCOUNT_LOGIN_FRAGMENT_TAG";
        LoginFragment myAccountLoginFragment = (LoginFragment) getChildFragmentManager()
                .findFragmentByTag(MY_ACCOUNT_LOGIN_FRAGMENT_TAG);

        if(myAccountLoginFragment == null) {
            myAccountLoginFragment = LoginFragment.newInstance(myAccountDataLoginState);
            Timber.d("NOT FOUND fragment with tag: " + MY_ACCOUNT_LOGIN_FRAGMENT_TAG + ", creating a new one");
        }else{
            Timber.d("FOUND fragment with tag: " + MY_ACCOUNT_LOGIN_FRAGMENT_TAG +
                    ", retrieving it without creating a new one");
        }

        getChildFragmentManager().beginTransaction()
                .replace(R.id.my_account_content_frame, myAccountLoginFragment, MY_ACCOUNT_LOGIN_FRAGMENT_TAG)
                .commit();
    }

    public static MyAccount newInstance() {
        return new MyAccount();
    }
}
