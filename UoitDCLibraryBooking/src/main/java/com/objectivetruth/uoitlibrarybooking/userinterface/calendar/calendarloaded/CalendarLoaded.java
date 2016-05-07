package com.objectivetruth.uoitlibrarybooking.userinterface.calendar.calendarloaded;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarData;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.common.CalendarPagerAdapter;

public class CalendarLoaded extends Fragment {
    private CalendarData calendarData;
    private CalendarPagerAdapter _mPagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View calendarLoadedView = inflater.inflate(R.layout.calendar_loaded, container, false);

        ViewPager _mViewPager = (ViewPager) calendarLoadedView.findViewById(R.id.calendar_view_pager);
        TabLayout _mTabLayout = (TabLayout) calendarLoadedView.findViewById(R.id.calendar_tab_layout);

        // Will supply the ViewPager with what should be displayed
        _mPagerAdapter = new CalendarPagerAdapter(getFragmentManager(), calendarData);
        _mViewPager.setAdapter(_mPagerAdapter);

        // Bind the TabLayout and ViewPager together
        _mTabLayout.setupWithViewPager(_mViewPager);

        return calendarLoadedView;
    }

    public static CalendarLoaded newInstance(CalendarData calendarData) {
        CalendarLoaded calendarLoadedToReturn = new CalendarLoaded();
        calendarLoadedToReturn.calendarData = calendarData;

        return calendarLoadedToReturn;
    }

    public void refreshPagerFragmentsAndViews(CalendarData calendarData) {
        _mPagerAdapter.refreshPagerFragmentsAndViews(calendarData);

    }
}
