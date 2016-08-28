package com.objectivetruth.uoitlibrarybooking.userinterface.calendar.common;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarData;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarDay;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.grid.Grid;
import timber.log.Timber;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class CalendarPagerAdapter extends FragmentStatePagerAdapter{
    private CalendarData calendarData;
    private boolean[] arrayCopyToTellWhichViewsToRefresh;
    private Grid[] listOfGridsUnderThisAdapter;

    public CalendarPagerAdapter(FragmentManager fragmentManager, CalendarData calendarData) {
        super(fragmentManager);
        this.calendarData = calendarData;
        arrayCopyToTellWhichViewsToRefresh = new boolean[calendarData.days.size()];
        listOfGridsUnderThisAdapter = new Grid[calendarData.days.size()];
        Arrays.fill(arrayCopyToTellWhichViewsToRefresh, false);
    }

    @Override
    public Fragment getItem(int position) {
        CalendarDay calendarDayAtThisPosition = calendarData.days.get(position);
        Grid newlyCreatedGrid = Grid.newInstance(calendarDayAtThisPosition);
        listOfGridsUnderThisAdapter[position] = newlyCreatedGrid;
        return newlyCreatedGrid;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String tabTitleString = "";
        CalendarDay dayAtThisPosition = calendarData.days.get(position);

        String dayOfWeek = _getDayOfWeekFromCalendarDataAtPosition(calendarData, position);
        if (isNotEmpty(dayOfWeek)) {
            tabTitleString += dayOfWeek + ", ";
        }

        tabTitleString += _removeLeadingZeros(dayAtThisPosition.extDayOfMonthNumber) +
                ", " + dayAtThisPosition.extMonthWord;

        return tabTitleString;
    }

    @Override
    public int getItemPosition(Object object) {
        if(_shouldRefreshDataForObject(object, arrayCopyToTellWhichViewsToRefresh, calendarData)) {
            if(_isObjectAtValidPositionInArray(object, arrayCopyToTellWhichViewsToRefresh, calendarData)) {
                Timber.d("Fragment at position " + calendarData.days.indexOf(object) +
                        " in Calendar Loaded will be refreshed");
                arrayCopyToTellWhichViewsToRefresh[calendarData.days.indexOf(object)] = false;
            }
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }

    private boolean _isObjectAtValidPositionInArray(Object objectRequested,
                                                    boolean[] arrayCopyToTellWhichViewsToRefresh,
                                                    CalendarData calendarData) {
        int indexOfObject = calendarData.days.indexOf(objectRequested);
        return indexOfObject > -1;
    }

    private boolean _shouldRefreshDataForObject(Object objectRequested, boolean[] arrayCopyToTellWhichViewsToRefresh,
                                                CalendarData calendarData) {
        int indexOfObject = calendarData.days.indexOf(objectRequested);
        if(indexOfObject < 0) {
            return true;
        }else{
            return arrayCopyToTellWhichViewsToRefresh[indexOfObject];
        }
    }

    public void saveInformationAndUpdatePagerFragmentUI(CalendarData calendarData) {
        this.calendarData = calendarData;
        arrayCopyToTellWhichViewsToRefresh = new boolean[calendarData.days.size()];
        listOfGridsUnderThisAdapter = new Grid[calendarData.days.size()];
        Arrays.fill(arrayCopyToTellWhichViewsToRefresh, true);
        notifyDataSetChanged(); // Will set off a call to getItemPosition to refresh the views
    }

    public void saveInformationAndDONTUpdatePagerFragmentUI(CalendarData calendarData) {
        this.calendarData = calendarData;
        int i = 0;
        for(Grid grid: listOfGridsUnderThisAdapter) {
            grid.saveNewCalendarDayWontUpdateUI(calendarData.days.get(i));
            i++;
        }
        if(calendarData.days.size() != this.calendarData.days.size()) {
            throw new IllegalStateException("Called saveInformationAndDONTUpdatePagerFragmentUI but the information " +
                    "has changed. Are you sure you didn't mean to call saveInformationAndUpdatePagerFragmentUI?");
        }
    }

    @Override
    public int getCount() {
        return calendarData.days.size();
    }

    private String _getDayOfWeekFromCalendarDataAtPosition(CalendarData calendarData, int positionZeroBased) {
        // Will be used to convert the day we get to a human readable string
        final String[] NAMES_OF_DAYS =  {"Sat", "Sun", "Mon", "Tue", "Wed","Thur", "Fri"};

        java.util.Calendar c = java.util.Calendar.getInstance();
        CalendarDay dayToParse = calendarData.days.get(positionZeroBased);

        String parseMe = dayToParse.extMonthWord + "-" +
                dayToParse.extDayOfMonthNumber + "-" +
                // Current year
                c.get(java.util.Calendar.YEAR);

        try {
            Date date;
            date = new SimpleDateFormat("MMMM-dd-yyyy", Locale.CANADA).parse(parseMe);

            c.setTime(date);
            int IntDayOfTheWeek = c.get(java.util.Calendar.DAY_OF_WEEK);
            return NAMES_OF_DAYS[IntDayOfTheWeek];
        } catch (ParseException e) {
            Timber.e(e, "Couldn't parse the string for the date. Got: " + parseMe);
            return "";
        }
    }

    /**
     * Removes leading zeros, but leaves a 0 if its the only one
     * "01234",         // "[1234]"
     * "0001234a",      // "[1234a]"
     * "101234",        // "[101234]"
     * "000002829839",  // "[2829839]"
     * "0",             // "[0]"
     * "0000000",       // "[0]"
     * "0000009",       // "[9]"
     * "000000z",       // "[z]"
     * "000000.z",      // "[.z]"
     * @param subject
     * @return
     */
    private String _removeLeadingZeros(String subject) {
        return subject.replaceFirst("^0+(?!$)", "");
    }

    /**
     * Quick Util for readability that checks, suprise suprise, if a String isn't empty
     * @param subject
     * @return
     */
    private boolean isNotEmpty(String subject) {
        return !subject.isEmpty();
    }
}
