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
import com.objectivetruth.uoitlibrarybooking.userinterface.myaccount.login.LoginFragment;
import timber.log.Timber;

import javax.inject.Inject;

public class MyAccount extends Fragment {
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

        if(userModel.isUserLoggedIn()) {
            //_showAccountInfoFragment();
            //logoutButton.setVisibility(View.VISIBLE);
            //logoutButton.setVisibility(View.GONE);
        }else {
            _showLoginFragment();
        }
    }

    public static MyAccount newInstance() {
        return new MyAccount();
    }

    private void _showLoginFragment() {
        String MY_ACCOUNT_LOGIN_FRAGMENT_TAG = "SINGLETON_MY_ACCOUNT_LOGIN_FRAGMENT_TAG";
        Timber.i("User isn't logged in, showing login screen");
/*        Fragment mLoginFragment = getFragmentManager()
                .findFragmentByTag(MY_ACCOUNT_LOGIN_FRAGMENT_TAG);
        if(mLoginFragment == null){
            Timber.d("Fragment with tag: " + MY_ACCOUNT_LOGIN_FRAGMENT_TAG + " not found, instantiating a new one");
            mLoginFragment = LoginFragment.newInstance();
        }else {
            Timber.d("Fragment with tag: " + MY_ACCOUNT_LOGIN_FRAGMENT_TAG +
                    " found. retrieving it without creating a new one");
        }*/
        getFragmentManager().beginTransaction()
                .replace(R.id.my_account_content_frame, LoginFragment.newInstance(), MY_ACCOUNT_LOGIN_FRAGMENT_TAG)
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
}
