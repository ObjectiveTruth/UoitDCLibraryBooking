package com.objectivetruth.uoitlibrarybooking.userinterface.calendar.grid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarDay;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.grid.common.GridAdapter;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.grid.tablefixheaders.TableFixHeaders;
import timber.log.Timber;

public class Grid extends Fragment {
    private CalendarDay calendarDay;
    private GridAdapter gridAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Timber.i("Starting creation of the grid and all required information");
        Timber.v(calendarDay.toString());
        View gridView = inflater.inflate(R.layout.calendar_page_grid, container, false);

        TableFixHeaders _mTableFixheaders = (TableFixHeaders) gridView.findViewById(R.id.calendar_page_grid);
        gridAdapter = new GridAdapter(getActivity(), calendarDay);
        _mTableFixheaders.setAdapter(gridAdapter);

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
}
