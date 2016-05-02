package com.objectivetruth.uoitlibrarybooking.userinterface.myaccount.myaccountloaded;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserData;
import timber.log.Timber;

class MyAccountPagerAdapter extends FragmentStatePagerAdapter {
    private UserData userData;

    MyAccountPagerAdapter(FragmentManager fragmentManager, UserData userData) {
        super(fragmentManager);
        this.userData = userData;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return IncompleteBookingTab.newInstance(userData.incompleteBookings);
            case 1:
                return CompleteBookingTab.newInstance(userData.completeBookings);
            case 2:
                return PastBookingTab.newInstance(userData.pastBookings);
            default:
                // Should never happen
                Timber.w("WTF? My Account requested a tab that's outside of what's expected(0-2). Requested: " +
                        position);
                return CompleteBookingTab.newInstance(userData.completeBookings);
        }
    }

    @Override
    public int getCount() {
        int NUMBER_OF_PAGES_COMPLETE_INCOMPLETE_PAST = 3;
        return NUMBER_OF_PAGES_COMPLETE_INCOMPLETE_PAST;
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
