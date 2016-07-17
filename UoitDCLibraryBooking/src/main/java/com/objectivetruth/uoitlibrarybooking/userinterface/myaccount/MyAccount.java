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
        Timber.d("MyAccount onStart");
        _setupViewBindings(userModel.getLoginStateObservable());
        super.onStart();
    }

    @Override
    public void onHiddenChanged(boolean isNowHidden) {
        if(isNowHidden) {
            Timber.d("MyAccount isNowHidden");
            _teardownViewBindings();
        }else {
            Timber.d("MyAccount isNowVisible");
            _setupViewBindings(userModel.getLoginStateObservable());
        }
        super.onHiddenChanged(isNowHidden);
    }
    @Override
    public void onStop() {
        Timber.d("MyAccount Stopped");
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
                            Timber.i("On next called: " + myAccountDataLoginState.type.name());
                            switch(myAccountDataLoginState.type) {
                                case RUNNING:
                                    _showFullscreenLoading();
                                    break;
                                case SIGNED_IN:
                                    _showMyAccountLoadedFragment(myAccountDataLoginState);
                                    break;
                                case SIGNED_OUT:
                                case ERROR:
                                default:
                                    _showLoginFragment(myAccountDataLoginState);
                            }
                        }
                    });
    }

    private void _showFullscreenLoading() {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.my_account_content_frame, Loading.newInstance())
                .addToBackStack(null)
                .commit();
    }

    private void _showMyAccountLoadedFragment(MyAccountDataLoginState myAccountDataLoginState) {
        String MY_ACCOUNT_LOADED_FRAGMENT_TAG = "SINGLETON_MY_ACCOUNT_LOADED_FRAGMENT_TAG";
        getActivity().invalidateOptionsMenu();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.my_account_content_frame,
                        MyAccountLoaded.newInstance(myAccountDataLoginState),
                        MY_ACCOUNT_LOADED_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    private void _showLoginFragment(MyAccountDataLoginState myAccountDataLoginState) {
        String MY_ACCOUNT_LOGIN_FRAGMENT_TAG = "SINGLETON_MY_ACCOUNT_LOGIN_FRAGMENT_TAG";
        getChildFragmentManager().beginTransaction()
                .replace(R.id.my_account_content_frame,
                        LoginFragment.newInstance(myAccountDataLoginState),
                        MY_ACCOUNT_LOGIN_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    public static MyAccount newInstance() {
        return new MyAccount();
    }
}
