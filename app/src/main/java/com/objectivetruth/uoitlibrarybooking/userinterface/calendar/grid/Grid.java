package com.objectivetruth.uoitlibrarybooking.userinterface.calendar.grid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.CalendarModel;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarDay;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.grid.common.GridAdapter;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.grid.tablefixheaders.TableFixHeaders;
import timber.log.Timber;

import javax.inject.Inject;

public class Grid extends Fragment {
    @Inject CalendarModel calendarModel;
    private CalendarDay calendarDay;
    private GridAdapter gridAdapter;
    private static final String CALENDAR_DAY_BUNDLE_KEY = "CALENDAR_DAY_BUNDLE_KEY";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ((UOITLibraryBookingApp) getActivity().getApplication()).getComponent().inject(this);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View gridView = inflater.inflate(R.layout.calendar_page_grid, container, false);

        if(savedInstanceState != null) {
            _restorePreviousState(savedInstanceState);
        }
        Timber.i("Starting creation of the grid and all required information for day: " +
                calendarDay.extDayOfMonthNumber);
        Timber.v(calendarDay.toString());

        TableFixHeaders _mTableFixheaders = (TableFixHeaders) gridView.findViewById(R.id.calendar_page_grid);
        gridAdapter = new GridAdapter(getActivity(), calendarDay);
        _mTableFixheaders.setAdapter(gridAdapter);
        _mTableFixheaders.setScrollAtTopGridBehaviourSubject(calendarModel.getScrollAtTopOfGridBehaviourSubject());

        return gridView;
    }

    static public Grid newInstance(CalendarDay calendarDay) {
        Grid grid = new Grid();
        grid.calendarDay = calendarDay;

        return grid;
    }

    /**
     * Saves new CalendarDay and propagates it down to the adapters
     * @param calendarDay
     */
    public void saveNewCalendarDayWontUpdateUI(CalendarDay calendarDay) {
        this.calendarDay = calendarDay;
        gridAdapter.saveNewCalendarDayWontUpdateUI(calendarDay);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(CALENDAR_DAY_BUNDLE_KEY, calendarDay);
    }

    private void _restorePreviousState(Bundle inState) {
        if(calendarDay == null) {
            calendarDay = inState.getParcelable(CALENDAR_DAY_BUNDLE_KEY);
        }
    }
}
