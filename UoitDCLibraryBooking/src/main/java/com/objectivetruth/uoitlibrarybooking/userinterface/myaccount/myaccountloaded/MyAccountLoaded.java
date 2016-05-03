package com.objectivetruth.uoitlibrarybooking.userinterface.myaccount.myaccountloaded;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.*;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.data.models.UserModel;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserData;
import com.objectivetruth.uoitlibrarybooking.userinterface.myaccount.MyAccount;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import javax.inject.Inject;

public class MyAccountLoaded extends Fragment {
    private UserData userData;
    private PublishSubject<MyAccount.LogOutClicked> logoutClickedSubject;
    @Inject UserModel userModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true); // Notifies activity that this fragment will interact with the action/options menu
        View myBookingsLoadedView = inflater.inflate(R.layout.my_account_loaded, container, false);

        ViewPager _mViewPager = (ViewPager) myBookingsLoadedView.findViewById(R.id.my_account_view_pager);
        TabLayout _mTabLayout = (TabLayout) myBookingsLoadedView.findViewById(R.id.my_account_tab_layout);

        // Will supply the ViewPager with what should be displayed
        PagerAdapter _mPagerAdapter = new MyAccountPagerAdapter(getFragmentManager(), userData, getContext());
        _mViewPager.setAdapter(_mPagerAdapter);

        // Bind the TabLayout and ViewPager together
        _mTabLayout.setupWithViewPager(_mViewPager);

        return myBookingsLoadedView;
    }

    public static MyAccountLoaded newInstance(UserData userData, PublishSubject<MyAccount.LogOutClicked>
            logoutClickedSubject) {
        MyAccountLoaded returnFragment = new MyAccountLoaded();
        returnFragment.userData = userData;
        returnFragment.logoutClickedSubject = logoutClickedSubject;
        return returnFragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.my_account_loaded_action_icons_menu, menu);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.my_account_menu_item_logout:
                Timber.i("Logout clicked");
                logoutClickedSubject.onNext(new MyAccount.LogOutClicked());
                return true;
            default:
                getActivity().onOptionsItemSelected(item);
                return true;
        }
    }
}
