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

    MyAccountPagerAdapter(FragmentManager fragmentManager, UserData userData, Context context) {
        super(fragmentManager);
        this.userData = userData;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return BookingsTab.newInstance(userData.incompleteBookings, context);
            case 1:
                return BookingsTab.newInstance(userData.completeBookings, context);
            case 2:
                return BookingsTab.newInstance(userData.pastBookings, context);
            default:
                // Should never happen
                Timber.w("WTF? My Account requested a tab that's outside of what's expected(0-2). Requested: " +
                        position);
                return BookingsTab.newInstance(userData.completeBookings, context);
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
