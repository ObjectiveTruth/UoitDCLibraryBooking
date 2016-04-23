package com.objectivetruth.uoitlibrarybooking.userinterface.calendar.common;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.grid.Grid;

import java.util.Random;

public class CalendarPagerAdapter extends FragmentStatePagerAdapter{

    public CalendarPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }
    @Override
    public Fragment getItem(int position) {
        return new Grid();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Tab " + new Random().nextInt(5);
    }

    @Override
    public int getCount() {
        return 2;
    }
}
