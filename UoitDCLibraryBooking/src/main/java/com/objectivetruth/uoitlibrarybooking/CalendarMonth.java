package com.objectivetruth.uoitlibrarybooking;

import android.util.Log;
import timber.log.Timber;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarMonth {
	private final String TAG = "CalendarMonth DataStruc";
	public String monthName = null;
	public String dayNumber = null;
	public String dayOfTheWeek = null;
	public String[] data = null;
	public String[] source = null;
	public String calendarNumber = null;
	public String startTime = null;
	public String eventTarget;
	public String eventArgument;
	public String viewState;
	public String eventValidation;
	public int dataLength;
	public int columnCount;
	
	public CalendarMonth(String monthName, String dayNumber, String[] data){

			this.data = data;
			this.monthName = monthName;
			this.dayNumber = dayNumber;
			Calendar c = Calendar.getInstance();
			String[] namesOfDays =  {"Sat", "Sun", "Mon", "Tue", "Wed","Thur", "Fri"};
			Date date;
			try {
				String parseMe = monthName + "-" + dayNumber + "-" + c.get(Calendar.YEAR);
				date = new SimpleDateFormat("MMMM-dd-yyyy", Locale.CANADA).parse(parseMe);


                c.setTime(date);
                int IntDayOfTheWeek = c.get(Calendar.DAY_OF_WEEK);
                dayOfTheWeek = namesOfDays[IntDayOfTheWeek];
                Timber.i("Date object is set to: " + c.getTime().toString());
				Timber.i("Day of the week calculated to be " + IntDayOfTheWeek + " which means its a " + dayOfTheWeek);
			} catch (ParseException e) {
				Log.i("CalendarMonth", "REALY? They put the wrong name for the month???");
				dayOfTheWeek = "";
				e.printStackTrace();
				
			}
	}
	
	public String toString(){
		return monthName + dayNumber + dayOfTheWeek;
	}
}
