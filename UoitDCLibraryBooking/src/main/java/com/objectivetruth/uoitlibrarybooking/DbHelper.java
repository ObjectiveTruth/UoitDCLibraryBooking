package com.objectivetruth.uoitlibrarybooking;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import timber.log.Timber;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static com.objectivetruth.uoitlibrarybooking.constants.SHARED_PREFERENCES_KEYS.SHARED_PREF_APPVERSION;

public class DbHelper extends SQLiteOpenHelper {
	
	public final String DATABASE_NAME; //Declared in constructor below
	public final String ROOMS_TABLE_NAME = "rooms_cache";
	public final String C_ID = "_id";
	public final String ROOM_NAME = "roomName";
	public final String FLOOR = "floor";
	public final String SEATING_CAP = "seatingCap";
	public final String MIN_BOOKERS = "minBookers";
	public final String MAX_TIME = "maxTime";
	public final String IMAGEFILE = "imageDir";
	public final String COMMENT = "comment"; //Don't forget this was added last
	public final static String DATABASE_PATH = "/data/data/com.objectivetruth.uoitlibrarybooking/databases/";
	public final String CALENDAR_TABLE_NAME = "calendar_cache";
	public final String ARRAY_ID = "_id";
	public final String LAST_UPDATE = "last_updated";
	public final String DAY1 = "day1";
	public final String DAY2 = "day2";
	public final String DAY3 = "day3";
	
	public final String MY_BOOKINGS_TABLE_NAME = "my_bookings";
	public final String POSITION_ID = "_id";
	public final String PAST_BOOKINGS = "pastBookings";
	public final String PENDING_BOOKINGS = "pendingBookings";
	public final String COMPLETED_BOOKINGS = "completedBookings";

	//This is the master list. You can change it but be careful
	public final static String[] ROOMSMASTERLIST =
		{"LIB202A", "LIB202B", "LIB202C", "LIB303", "LIB304", "LIB305", "LIB306", "LIB307", "LIB309", "LIB310"};
	
	private SQLiteDatabase dataBase;
	private final Context dbContext;
	
	private final String TAG = "DbHelper";


	
	public final String createCalendarTable = "create table if not exists "
			+ CALENDAR_TABLE_NAME + " ( "
			+ ARRAY_ID + " integer primary key autoincrement, "
			+ LAST_UPDATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
			+ DAY1 + " text, "
			+ DAY2 + " text, "
			+ DAY3 + " text );";
	//THIS IS DYNAMICALLY handled below, the database is destroyed everytime the refresh button returns its result
	//THe views are updated once the database has been written
	//FIRST 4 ROWS of EACH DAY 
	//  |last_update | day1    	      | day2 			| day1source|
	//  |TimeStamp   |Dayofweek		  | Dayofweek		| 			|
	//  |============|serverDayNumber | serverDayNumber |			|
	//  |============|MonthName		  | MonthName		|  			|
	//  |============|StartTime       | StartTime       |			|
	//	|============|first entry	  | 1st Entry		|hrefcode	|
	

	public DbHelper(Context context, String name, CursorFactory factory,
			int version) {
		//this constructor MUST return the database name as the return string
		super(context, "infocache", factory, version);
		this.dbContext = context;
		DATABASE_NAME = "infocache";
	    if (shouldDatabaseBeCopied()) {
			try {
				getReadableDatabase();
				copyDataBase();
				close();
				openDataBase();

			} catch (IOException e) {
				throw new Error("Error copying database");
			}
			Toast.makeText(context, "Initial Setup Complete. Please Refresh", Toast.LENGTH_LONG).show();
	    } else {
			openDataBase();
	    }
	    
		
	}
	private void copyDataBase() throws IOException {
	    InputStream myInput = dbContext.getAssets().open(DATABASE_NAME);
	    String outFileName = DATABASE_PATH + DATABASE_NAME;
	    OutputStream myOutput = new FileOutputStream(outFileName);
	
	    byte[] buffer = new byte[1024];
	    int length;
	    while ((length = myInput.read(buffer))>0){
	        myOutput.write(buffer, 0, length);
	    }
	
	    myOutput.flush();
	    myOutput.close();
	    myInput.close();
	}

    /**
     * This returns based on if the database is present or the app version has changed
     * @return true if all good, false if there's an issue and will copy the default database
     */
	private boolean shouldDatabaseBeCopied() {
	    boolean shouldCopyDatabase = false;
		// Attempt to open then close the database to make sure it works
	    try {
	        String dbPath = DATABASE_PATH + DATABASE_NAME;
			SQLiteDatabase openAndImmediatelyCloseMe = SQLiteDatabase.openDatabase(dbPath, null,
					SQLiteDatabase.OPEN_READONLY);
            openAndImmediatelyCloseMe.close();
	    } catch (SQLiteException e) {
			shouldCopyDatabase = true;
	    }
	
		if (UOITLibraryBookingApp.IS_FIRST_TIME_LAUNCH_SINCE_UPGRADE_OR_INSTALL) {
			shouldCopyDatabase = true;
		}
	    return shouldCopyDatabase;
	}
	
	public void openDataBase() throws SQLException {
	    String dbPath = DATABASE_PATH + DATABASE_NAME;
	    dataBase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
	}
	
	
	@Override
	public void onCreate(SQLiteDatabase dB) {
		//dB.enableWriteAheadLogging();
		//Log.i(TAG, "Sending Databse SQL Command::  " + createRoomsTable);
		//dB.execSQL(createRoomsTable);
		//Log.i(TAG, "Sending Databse SQL Command:: " + createCalendarTable);
		//dB.execSQL(createCalendarTable);

	}

	@Override
	public void onUpgrade(SQLiteDatabase dB, int oldversion, int newversion) {
		Log.i(TAG, "Dropping table with command:: " + "drop table if exists" + ROOMS_TABLE_NAME);
		dB.execSQL("drop table if exists " + ROOMS_TABLE_NAME);


	}
	
	public void updateCalendarDatabase(ArrayList<CalendarMonth> result){
		final String TAG = "DbHelper - UpdatedCalendarDatabase";
		Timber.i("Starting Database Update");
		if(result.size() == 0){
			Log.i(TAG, "Wahhh?");
		}
			
		String createTableBasedOnDays = "create table if not exists "
				+ CALENDAR_TABLE_NAME + " ( "
				+ ARRAY_ID + " integer primary key autoincrement, "
				+ LAST_UPDATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP";
				for(int i = 0; i < result.size(); i ++){
					createTableBasedOnDays = createTableBasedOnDays 
							+ ", day" 
							+ String.valueOf(i + 1)
							+ " text"
							+ ", day" 
							+ String.valueOf(i + 1)
							+ "source text";
				}
				createTableBasedOnDays = createTableBasedOnDays + " );";
				//Log.i(TAG, "create string is : " + createTableBasedOnDays);
		
		String day = "";
		String daySource = "";
		ContentValues cv = new ContentValues();
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("drop table if exists " + CALENDAR_TABLE_NAME);
		db.execSQL(createTableBasedOnDays);
		
		int numberOfDays = result.size();
		int maxDBListLength = result.get(0).data.length;
		if(numberOfDays > 1){
			
			for(int i = 0; i < numberOfDays; i ++){
				maxDBListLength = (int) Math.max(result.get(i).data.length, maxDBListLength);
			}	
		}
		
		Timber.v("Longest DB List will be: " + maxDBListLength);
		//Log.i(TAG, "Datalength 0 is : " + result.get(0).dataLength);
		//Log.i(TAG, "Datalength 1 is : " + result.get(1).dataLength);
		for(int i = 0; i < maxDBListLength + 4; i ++){
			
			cv.clear();			
			for(int j = 0; j < numberOfDays; j ++){
				
				day = "day" + String.valueOf(j + 1);
				daySource = day + "source";
				
				if(i == 0){
					cv.put(day, result.get(j).dayOfTheWeek);
					cv.put(daySource, result.get(j).eventTarget);
				}
				else if(i==1){
					cv.put(day, result.get(j).dayNumber);
					cv.put(daySource, result.get(j).eventArgument);
				}
				else if(i==2){
					cv.put(day, result.get(j).monthName);	
					cv.put(daySource, result.get(j).viewState);
				}
				else if(i==3){
					cv.put(day, result.get(j).dataLength);
					cv.put(daySource, result.get(j).eventValidation);
				}
				else if(i==4){
					cv.put(day, result.get(j).columnCount);
					cv.put(daySource, result.get(j).eventValidation);
				}
				else{
					if(i < result.get(j).dataLength + 4){
						if(i == 4){
							
						}
						cv.put(day, result.get(j).data[i-4]);
						cv.put(daySource, result.get(j).source[i-4]);
					}
				}

			}
			db.insert(CALENDAR_TABLE_NAME, null, cv);

			
		}
        //Took this out
		//db.close();
		Timber.i("Update Complete");
	}
	public void UpdateMyBookingsDatabase(ArrayList<String[]> result) {
		final String TAG = "DbHelper - UpdatedMyBookingsDatabase";
		Timber.i("Updating Database");
		if(result.size() == 0){
			Log.i(TAG, "Wahhh? crash");
		}
			
		String createMyBookingsString = "create table if not exists "
				+ MY_BOOKINGS_TABLE_NAME+ " ( "
				+ POSITION_ID + " integer primary key autoincrement, "
				+ LAST_UPDATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ PENDING_BOOKINGS + " text, "
				+ COMPLETED_BOOKINGS + " text, "
				+ PAST_BOOKINGS + " text);";

		ContentValues cv = new ContentValues();
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("drop table if exists " + MY_BOOKINGS_TABLE_NAME);
		db.execSQL(createMyBookingsString);
		int longestArr = Math.max(result.get(0).length, Math.max(result.get(2).length, result.get(1).length));
		
		for(int i = 0; i < longestArr; i ++){
			cv.clear();
			if(i < result.get(0).length){
				cv.put(PENDING_BOOKINGS, result.get(0)[i]);
			}
			if(i < result.get(1).length){
				cv.put(COMPLETED_BOOKINGS, result.get(1)[i]);
			}
			if(i< result.get(2).length){
				cv.put(PAST_BOOKINGS, result.get(2)[i]);
			}
			db.insert(MY_BOOKINGS_TABLE_NAME, null, cv);
			
		}
		Timber.i("Update Complete");
		
	}


	
	public void UpdateRoomInfoDatabase(ArrayList<String[]> result) {
		final String TAG = "UpdateRoomInfo";
		final String createRoomsTable = "create table if not exists " + ROOMS_TABLE_NAME + " ( "
				+ C_ID + " integer primary key autoincrement, "
				+ ROOM_NAME + " text, "
				+ FLOOR + " text, "
				+ SEATING_CAP + " text, "
				+ MIN_BOOKERS + " text, "
				+ MAX_TIME + " text, "
				+ IMAGEFILE + " text, "
				+ COMMENT + "); ";
		
		Log.i(TAG, "Updating Database");
		ContentValues cv = new ContentValues();
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("drop table if exists " + ROOMS_TABLE_NAME);
		db.execSQL(createRoomsTable);

			try{
				for(int i = 0; i < result.size(); i++){
					cv.clear();
									
					
					cv.put(IMAGEFILE, result.get(i)[5]);

					cv.put(ROOM_NAME, result.get(i)[0]);
					cv.put(COMMENT, result.get(i)[6]);
					cv.put(FLOOR, result.get(i)[1]);
					cv.put(SEATING_CAP,result.get(i)[2]);
					
					
					cv.put(MIN_BOOKERS, result.get(i)[3]);
					
					cv.put(MAX_TIME, result.get(i)[4]);
					
					Log.i(TAG, cv.toString());
					db.insert(ROOMS_TABLE_NAME, null, cv);
					Log.i(TAG, "Database Update Complete");
				}
				
			}catch(Exception e){
				Log.i("RefreshTableAndImages", e.toString());
			}
		
	}
}
