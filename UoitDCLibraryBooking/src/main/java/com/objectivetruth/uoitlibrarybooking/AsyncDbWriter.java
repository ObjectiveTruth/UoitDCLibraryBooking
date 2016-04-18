package com.objectivetruth.uoitlibrarybooking;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import timber.log.Timber;

import java.util.ArrayList;

public class AsyncDbWriter extends AsyncTask<ArrayList<CalendarMonth>, int[], String> {
	final String TAG = "AsyncDbWriter";
	Activity mActivity;
	boolean isDbWriteSuccess = false;
	public AsyncDbWriter(Activity mActivity){
		this.mActivity = mActivity;
	}
	
	@Override
	protected String doInBackground(ArrayList<CalendarMonth>... params) {
		long dbStartTime = System.currentTimeMillis();

		try{
			MainActivity.mdbHelper.updateCalendarDatabase(params[0]);
			isDbWriteSuccess = true;
			Log.i(TAG, "DB Writing Successful!");
		}
		catch(Exception e){
	        e.printStackTrace();
	        Timber.i(e, "DB Writing Failed...");
	        
	        
		}
		long durationWriteDataBase = System.currentTimeMillis() - dbStartTime;
		Timber.i("DB writing took: " + durationWriteDataBase);
		return null;
	}

}
