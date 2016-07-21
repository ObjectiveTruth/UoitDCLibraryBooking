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
import timber.log.Timber;

public class CalendarLoaded extends Fragment {
    private CalendarData calendarData;
    private CalendarPagerAdapter _mPagerAdapter;
    private static final String CALENDAR_DATA_BUNDLE_KEY = "CALENDAR_DATA_BUNDLE_KEY";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View calendarLoadedView = inflater.inflate(R.layout.calendar_loaded, container, false);

        ViewPager _mViewPager = (ViewPager) calendarLoadedView.findViewById(R.id.calendar_view_pager);
        TabLayout _mTabLayout = (TabLayout) calendarLoadedView.findViewById(R.id.calendar_tab_layout);

        if(savedInstanceState != null) {
            _restorePreviousState(savedInstanceState);
        }

        // Will supply the ViewPager with what should be displayed
        _mPagerAdapter = new CalendarPagerAdapter(getChildFragmentManager(), calendarData);
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

    /**
     * Compares the data that's being passed in with the data currently stored in the fragment, if its different, then
     * a redraw/refresh takes place, otherwise, does nothing
     * @param calendarData
     */
    public void refreshPagerFragmentsAndViewsIfDataDiffers(CalendarData calendarData) {
        // Either this calendarData has to be Not null or the incoming calendarData has to be Not null to proceed
        // Only other case is if both are null, in which case, do nothing
        if(this.calendarData != null &&
                this.calendarData.isNOTEqualTo(calendarData)) {

            _mPagerAdapter.saveInformationAndUpdatePagerFragmentUI(calendarData);

        }else if(calendarData != null &&
                calendarData.isNOTEqualTo(this.calendarData)) {

            _mPagerAdapter.saveInformationAndUpdatePagerFragmentUI(calendarData);
        }else{
            Timber.d("CalendarData for THIS instance has not changed, will NOT update UI");
            _mPagerAdapter.saveInformationAndDONTUpdatePagerFragmentUI(calendarData);
        }
        this.calendarData = calendarData;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(CALENDAR_DATA_BUNDLE_KEY, calendarData);
    }

    private void _restorePreviousState(Bundle inState) {
        calendarData = inState.getParcelable(CALENDAR_DATA_BUNDLE_KEY);
    }
}
