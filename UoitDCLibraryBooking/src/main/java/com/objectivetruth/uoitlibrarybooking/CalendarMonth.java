package com.objectivetruth.uoitlibrarybooking;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
				date = new SimpleDateFormat("MMM").parse(monthName);
	
			c.setTime(date);
			int month = c.get(Calendar.MONTH);
			c.set(c.get(c.YEAR), month, Integer.parseInt(dayNumber));
			int IntDayOfTheWeek = c.get(Calendar.DAY_OF_WEEK);
			dayOfTheWeek = namesOfDays[IntDayOfTheWeek-1];
			Log.i(TAG, String.valueOf(IntDayOfTheWeek));
			
			

			
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
