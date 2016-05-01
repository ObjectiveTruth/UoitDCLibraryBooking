package com.objectivetruth.uoitlibrarybooking.userinterface.myaccount;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.UserModel;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserCredentials;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserData;
import com.objectivetruth.uoitlibrarybooking.userinterface.myaccount.login.LoginFragment;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import javax.inject.Inject;

public class MyAccount extends Fragment {
    private PublishSubject<UserCredentials> signInClickSubject;
    private PublishSubject<String> loginErrorSubject;
    @Inject UserModel userModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((UOITLibraryBookingApp) getActivity().getApplication()).getComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_account, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //ImageButton logoutButton = (ImageButton) view.findViewById(R.id.my_account_logout);

        if(userModel.isUserSignedIn()) {
            //_showAccountInfoFragment();
            //logoutButton.setVisibility(View.VISIBLE);
            //logoutButton.setVisibility(View.GONE);
        }else {
            Timber.i("User isn't signed in, showing login screen");
            _showLoginFragment();
            _subscribeToSignInClickSubject(_getSignInClickSubject());
        }
    }

    private void _subscribeToSignInClickSubject(PublishSubject<UserCredentials> signInClickSubject) {
        signInClickSubject
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())

                .flatMap(new Func1<UserCredentials, Observable<UserData>>() {
                    @Override
                    public Observable<UserData> call(UserCredentials userCredentials) {
                        return userModel.signIn(userCredentials);
                        }
                    })
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.computation())

                .subscribe(new Observer<UserData>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(UserData userData) {
                if(userData.errorMessage != null) {
                    Timber.d("Received Error Message from Parser, publishing to Subject");
                    _sendErrorMessageToLoginErrorSubject(userData.errorMessage, _getLoginErrorSubject());
                }else {
                    Timber.i("Received UserData");
                    Timber.v(userData.toString());
                }
                Timber.i("Got it");
            }
        });
    }

    private static void _sendErrorMessageToLoginErrorSubject(String errorMessage,
                                                            PublishSubject<String> loginErrorSubject) {
        loginErrorSubject.onNext(errorMessage);
    }

    public static MyAccount newInstance() {
        return new MyAccount();
    }

    private void _showLoginFragment() {
        String MY_ACCOUNT_LOGIN_FRAGMENT_TAG = "SINGLETON_MY_ACCOUNT_LOGIN_FRAGMENT_TAG";
/*        Fragment mLoginFragment = getFragmentManager()
                .findFragmentByTag(MY_ACCOUNT_LOGIN_FRAGMENT_TAG);
        if(mLoginFragment == null){
            Timber.d("Fragment with tag: " + MY_ACCOUNT_LOGIN_FRAGMENT_TAG + " not found, instantiating a new one");
            mLoginFragment = LoginFragment.newInstance();
        }else {
            Timber.d("Fragment with tag: " + MY_ACCOUNT_LOGIN_FRAGMENT_TAG +
                    " found. retrieving it without creating a new one");
        }*/
        PublishSubject<UserCredentials> signInClickSubject = _getSignInClickSubject();
        getFragmentManager().beginTransaction()
                .replace(R.id.my_account_content_frame,
                        LoginFragment.newInstance(signInClickSubject, _getLoginErrorSubject()),
                        MY_ACCOUNT_LOGIN_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Replaces the my_account_content_frame with the AccountInfoFragment which loads its info from the database
     */
/*    private void _showAccountInfoFragment(){
        String MY_ACCOUNT_PAGER_ADAPTER_FRAGMENT_TAG = "SINGLETON_MY_ACCOUNT_PAGER_ADAPTER_FRAGMENT_TAG";
        ViewPagerFragment mViewPagerFragment = (ViewPagerFragment) getFragmentManager()
                .findFragmentByTag(MY_ACCOUNT_PAGER_ADAPTER_FRAGMENT_TAG);
        if(mViewPagerFragment ==null ){
            mViewPagerFragment = ViewPagerFragment.newInstance(new CustomViewPagerAdapter(getFragmentManager()));
            getFragmentManager().beginTransaction().
                    replace(R.id.my_account_movement_frame, mViewPagerFragment, MY_ACCOUNT_PAGER_ADAPTER_FRAGMENT_TAG)
                    .commit();
        }
        else{
            //Do nothing Each Tab is registered to Auto to receive an update success callback

        }
    }*/

    private PublishSubject<UserCredentials> _getSignInClickSubject() {
        if(signInClickSubject == null) {
            Timber.d("Current singInClickSubject is NULL, making new one");
            return signInClickSubject = PublishSubject.create();
        }else if (signInClickSubject.hasCompleted()) {
            Timber.d("Current singInClickSubject has completed already, making new one");
            return signInClickSubject = PublishSubject.create();
        }else {
            Timber.d("Current singInClickSubject is still valid, passing it back");
            return signInClickSubject;
        }
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

    @Override
    public void onPause() {
        _getSignInClickSubject().onCompleted();
        _getLoginErrorSubject().onCompleted();
        super.onPause();
    }
}
