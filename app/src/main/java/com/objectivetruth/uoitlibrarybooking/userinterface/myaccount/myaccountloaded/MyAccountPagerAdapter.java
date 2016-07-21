package com.objectivetruth.uoitlibrarybooking.userinterface.myaccount.myaccountloaded;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserData;
import timber.log.Timber;

class MyAccountPagerAdapter extends FragmentStatePagerAdapter {
    private UserData userData;
    private Context context;
    private boolean[] arrayCopyToTellWhichViewsToRefresh;

    MyAccountPagerAdapter(FragmentManager fragmentManager, UserData userData, Context context) {
        super(fragmentManager);
        this.userData = userData;
        this.context = context;
        this.arrayCopyToTellWhichViewsToRefresh = new boolean[]{false, false, false};
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return BookingsTab.newInstance(userData.incompleteBookings, context, 0);
            case 1:
                return BookingsTab.newInstance(userData.completeBookings, context, 1);
            case 2:
                return BookingsTab.newInstance(userData.pastBookings, context, 2);
            default:
                // Should never happen
                Timber.w("WTF? My Account requested a tab that's outside of what's expected(0-2). Requested: " +
                        position);
                return BookingsTab.newInstance(userData.completeBookings, context, 1);
        }
    }

    @Override
    public int getItemPosition(Object object) {
        BookingsTab bookingsTabRequested = (BookingsTab) object;
        if(_shouldRefreshDataForObject(bookingsTabRequested, arrayCopyToTellWhichViewsToRefresh)) {
            Timber.d("Fragment at position " + bookingsTabRequested.positionInParentPagerAdapter +
                    " in My Account Loaded will be refreshed");
            arrayCopyToTellWhichViewsToRefresh[bookingsTabRequested.positionInParentPagerAdapter] = false;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        int NUMBER_OF_PAGES_COMPLETE_INCOMPLETE_PAST = 3;
        return NUMBER_OF_PAGES_COMPLETE_INCOMPLETE_PAST;
    }

    public void refreshPagerFragmentsAndViews(UserData userData) {
        Timber.i("Refreshing My Account pager adapter's Fragments, Data and views");
        this.userData = userData;
        arrayCopyToTellWhichViewsToRefresh = getArrayWhereAllCorrespondingFragmentsShouldbeRefreshed();
        notifyDataSetChanged();
    }

    private boolean _shouldRefreshDataForObject(BookingsTab myBookingTab, boolean[] booleans) {
        return booleans[myBookingTab.positionInParentPagerAdapter];
    }

    private boolean[] getArrayWhereAllCorrespondingFragmentsShouldbeRefreshed() {
        return new boolean[]{true, true, true};
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return "Incomplete";
            case 1:
                return "Complete";
            case 2:
                return "Past";
            default:
                return "Complete";
        }
    }
}
