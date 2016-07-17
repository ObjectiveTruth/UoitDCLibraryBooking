package com.objectivetruth.uoitlibrarybooking.userinterface.myaccount.myaccountloaded;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.*;
import android.widget.Toast;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.UserModel;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.MyAccountDataLoginState;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.MyAccountSignoutEvent;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserData;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

import javax.inject.Inject;

public class MyAccountLoaded extends Fragment {
    private UserData userData;
    private ViewPager _mViewPager;
    private TabLayout _mTabLayout;
    private SwipeRefreshLayout _mSwipeLayout;
    private Subscription calendarDataRefreshStateTypeSubscription;
    @Inject UserModel userModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((UOITLibraryBookingApp) getActivity().getApplication()).getComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View myBookingsLoadedView = inflater.inflate(R.layout.my_account_loaded, container, false);

        _mViewPager = (ViewPager) myBookingsLoadedView.findViewById(R.id.my_account_view_pager);
        _mTabLayout = (TabLayout) myBookingsLoadedView.findViewById(R.id.my_account_tab_layout);
        _mSwipeLayout =
                (SwipeRefreshLayout) myBookingsLoadedView.findViewById(R.id.my_account_loaded_refresh_swipe_layout);

        return myBookingsLoadedView;
    }

    public static MyAccountLoaded newInstance(MyAccountDataLoginState myAccountDataLoginState) {
        MyAccountLoaded returnFragment = new MyAccountLoaded();
        returnFragment.userData = myAccountDataLoginState.userData;
        return returnFragment;
    }

    @Override
    public void onStart() {
        Timber.d(this.getClass().getSimpleName() + " onStart");
        _setupViewBindings(_mViewPager, _mSwipeLayout, _mTabLayout, userData);
        super.onStart();
    }

    @Override
    public void onHiddenChanged(boolean isNowHidden) {
        if(isNowHidden) {
            Timber.d(this.getClass().getSimpleName() + " isNowHidden");
            _teardownViewBindings(_mViewPager, _mSwipeLayout);
        }else {
            Timber.d(this.getClass().getSimpleName() + " isNowVisible");
            _setupViewBindings(_mViewPager, _mSwipeLayout, _mTabLayout, userData);
        }
        super.onHiddenChanged(isNowHidden);
    }

    @Override
    public void onStop() {
        Timber.d(this.getClass().getSimpleName() + " onStop");
        _teardownViewBindings(_mViewPager, _mSwipeLayout);
        super.onStop();
    }

    private void _teardownViewBindings(ViewPager _mViewPager, SwipeRefreshLayout _mSwipeLayout) {
        _mViewPager.clearOnPageChangeListeners();
        _mSwipeLayout.setOnRefreshListener(null);
        if(calendarDataRefreshStateTypeSubscription != null) {
            calendarDataRefreshStateTypeSubscription.unsubscribe(); // Idempotent
        }
    }

    private void _setupViewBindings(ViewPager _mViewPager, final SwipeRefreshLayout _mSwipeLayout,
                                    TabLayout _mTabLayout, UserData userData) {
        if(_mViewPager == null || _mSwipeLayout == null) {return;} //quit early if state is inconsistent

        // Disables the viewpager's horizontal swipes from triggering the vertical refresh
        _disableHorizontalSwipesFromTriggeringVerticalRefresh(_mViewPager, _mSwipeLayout);

        // Will supply the ViewPager with what should be displayed
        final MyAccountPagerAdapter _mPagerAdapter = new MyAccountPagerAdapter(getChildFragmentManager(),
                userData, getContext());
        _mViewPager.setAdapter(_mPagerAdapter);

        // Bind the TabLayout and ViewPager together
        _mTabLayout.setupWithViewPager(_mViewPager);

        // Bind the swipelayout to the refreshing of the tabs
        _bindSwipeLayoutToMyAccountSigninEvent(_mSwipeLayout, _mPagerAdapter);

        if(calendarDataRefreshStateTypeSubscription == null ||
                calendarDataRefreshStateTypeSubscription.isUnsubscribed()) {

            calendarDataRefreshStateTypeSubscription =
                    userModel.getLoginStateObservable()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<MyAccountDataLoginState>() {
                                @Override
                                public void onCompleted() {
                                    // Do nothing
                                }

                                @Override
                                public void onError(Throwable e) {
                                    _mSwipeLayout.setRefreshing(false);
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onNext(MyAccountDataLoginState myAccountDataLoginState) {
                                    switch(myAccountDataLoginState.type) {
                                        case RUNNING:
                                            if(!_mSwipeLayout.isRefreshing()) {_mSwipeLayout.setRefreshing(true);}
                                            break;

                                        case ERROR:
                                            Toast.makeText(getContext(), myAccountDataLoginState.exception.getMessage(),
                                                    Toast.LENGTH_LONG).show();
                                            _mSwipeLayout.setRefreshing(false);
                                            break;

                                        case SIGNED_IN:
                                            if(myAccountDataLoginState.userData != null) {
                                                Timber.d("Successfully Refresh MyAccount info");
                                                _mPagerAdapter.refreshPagerFragmentsAndViews(myAccountDataLoginState
                                                        .userData);
                                            }
                                            _mSwipeLayout.setRefreshing(false);
                                            break;

                                        case SIGNED_OUT:
                                        default:
                                            _mSwipeLayout.setRefreshing(false);

                                    }
                                }
                            });
        }
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
        if(viewPager == null || swipeRefreshLayout == null) {return;} //quit early if incorrect state

        viewPager.clearOnPageChangeListeners();
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

    private void _bindSwipeLayoutToMyAccountSigninEvent(final SwipeRefreshLayout swipeRefreshLayout,
                                                         final MyAccountPagerAdapter myAccountPagerAdapter) {
        swipeRefreshLayout.setOnRefreshListener(null);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                userModel.getSigninActivatePublishSubject().onNext(null);
            }
        });
    }
}
