package com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction.common;

import timber.log.Timber;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Utils {

    /**
     * Returns the day of the week based on the 2 inputs. Assumes the year is "THIS" year
     * @param dayOfMonth
     * @param monthName
     * @return
     */
    public static String getDayOfWeekBasedOnDayNumberMonthNumber(String dayOfMonth, String monthName) {
        try{
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.CANADA);

            int currentYear = calendar.get(Calendar.YEAR);
            int monthInt = _getMonthIntFromMonthString(monthName);
            int dayInt = Integer.parseInt(dayOfMonth);

            calendar.set(currentYear, monthInt, dayInt);
            return dayFormat.format(calendar.getTime());
        }catch(ParseException e) {
            Timber.e(e, "Could not parse date information when trying to find day of week. dayOfMonth: " + dayOfMonth
                    + ", monthName: " + monthName);
        }catch(Exception e) {
            Timber.e(e, "Unexpected Error when parsing date information when trying to find day of week. dayOfMonth: "
                    + dayOfMonth + ", monthName: " + monthName);
        }
        return "Booking";
    }

    private static int _getMonthIntFromMonthString(String monthName) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new SimpleDateFormat("MMMM", Locale.CANADA).parse(monthName));
        return calendar.get(Calendar.MONTH);
    }
}
