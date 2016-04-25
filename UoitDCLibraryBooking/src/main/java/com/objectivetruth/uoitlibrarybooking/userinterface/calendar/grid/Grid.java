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

public class Grid extends Fragment {
    private CalendarDay calendarDay;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View gridView = inflater.inflate(R.layout.calendar_page_grid, container, false);

        TableFixHeaders _mTableFixheaders = (TableFixHeaders) gridView.findViewById(R.id.calendar_page_grid);
        _mTableFixheaders.setAdapter(new GridAdapter(getActivity(), calendarDay));

        return gridView;
    }

    static public Grid newInstance(CalendarDay calendarDay) {
        Grid grid = new Grid();
        grid.calendarDay = calendarDay;

        return grid;
    }
}
