package com.objectivetruth.uoitlibrarybooking.userinterface.myaccount.myaccountloaded;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.*;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.data.models.UserModel;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.MyAccountDataLoginState;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.MyAccountSignoutEvent;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserData;
import timber.log.Timber;

public class MyAccountLoaded extends Fragment {
    private UserData userData;
    private UserModel userModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View myBookingsLoadedView = inflater.inflate(R.layout.my_account_loaded, container, false);

        ViewPager _mViewPager = (ViewPager) myBookingsLoadedView.findViewById(R.id.my_account_view_pager);
        TabLayout _mTabLayout = (TabLayout) myBookingsLoadedView.findViewById(R.id.my_account_tab_layout);
        SwipeRefreshLayout _mSwipeLayout =
                (SwipeRefreshLayout) myBookingsLoadedView.findViewById(R.id.my_account_loaded_refresh_swipe_layout);


        // Disables the viewpager's horizontal swipes from triggering the vertical refresh
        _disableHorizontalSwipesFromTriggeringVerticalRefresh(_mViewPager, _mSwipeLayout);

        // Will supply the ViewPager with what should be displayed
        MyAccountPagerAdapter _mPagerAdapter = new MyAccountPagerAdapter(getFragmentManager(), userData, getContext());
        _mViewPager.setAdapter(_mPagerAdapter);

        // Bind the TabLayout and ViewPager together
        _mTabLayout.setupWithViewPager(_mViewPager);

        // Bind the swipelayout to the refreshing of the tabs
        _bindSwipeLayoutToMyAccountRefreshEvent(_mSwipeLayout, _mPagerAdapter);

        return myBookingsLoadedView;
    }

    public static MyAccountLoaded newInstance(UserModel userModel,
                                              MyAccountDataLoginState myAccountDataLoginState) {
        MyAccountLoaded returnFragment = new MyAccountLoaded();
        returnFragment.userData = myAccountDataLoginState.userData;
        returnFragment.userModel = userModel;
        return returnFragment;
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
                userModel.getSignoutActivatePublishSubject().onNext(new MyAccountSignoutEvent());
                return true;
            default:
                getActivity().onOptionsItemSelected(item);
                return true;
        }
    }


    private void _disableHorizontalSwipesFromTriggeringVerticalRefresh(ViewPager viewPager,
                                                                       final SwipeRefreshLayout swipeRefreshLayout) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                swipeRefreshLayout.setEnabled(state == ViewPager.SCROLL_STATE_IDLE);
            }
        });
    }

    private void _bindSwipeLayoutToMyAccountRefreshEvent(final SwipeRefreshLayout swipeRefreshLayout,
                                                         final MyAccountPagerAdapter myAccountPagerAdapter) {
/*        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                parentMyAccountFragment.getSignInObs()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Pair<UserData, UserCredentials>>() {
                            @Override
                            public void onCompleted() {
                                swipeRefreshLayout.setRefreshing(false);
                            }

                            @Override
                            public void onError(Throwable e) {
                                swipeRefreshLayout.setRefreshing(false);
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNext(Pair<UserData, UserCredentials> userDataUserCredentialsPair) {
                                Timber.v("Successfully refreshed!");
                                myAccountPagerAdapter.refreshPagerFragmentsAndViews();
                            }
                        });
            }
        });*/
    }
}
