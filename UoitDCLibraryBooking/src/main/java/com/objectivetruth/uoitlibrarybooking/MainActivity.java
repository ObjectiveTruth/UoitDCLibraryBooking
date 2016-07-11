package com.objectivetruth.uoitlibrarybooking;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import com.google.android.gms.analytics.Tracker;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.whatsnew.WhatsNewDialog;
import com.objectivetruth.uoitlibrarybooking.userinterface.common.ActivityBase;

import javax.inject.Inject;
import java.net.CookieManager;


public class MainActivity extends ActivityBase {
    final static private String ACTIVITY_TITLE = "Calendar";

    public static final String MY_ACCOUNT_DIALOGFRAGMENT_TAG = "myAccountDiaFrag";
    public static final String GROUP_CODE_DIALOGFRAGMENT_TAG = "groupCodeInfoDiaFrag";
    public static final int MAX_BOOKINGS_ALLOWED = 20; //This can safely be changed
    public static boolean isDialogShowing = false;
	AppCompatActivity mActivity = this;
	public static DbHelper mdbHelper;
	public static CookieManager cookieManager;
	@Inject SharedPreferences mDefaultSharedPreferences;
	@Inject SharedPreferences.Editor mDefaultSharedPreferencesEditor;
    @Inject Tracker googleAnalyticsTracker;
    static LoginAsynkTask mLoginAsyncTask = null;
    //TODO put this in savedinstancestate
    public static String errorMessageFromLogin = "";

	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

        ((UOITLibraryBookingApp) getApplication()).getComponent().inject(this);

        NavigationView drawerView = configureAndSetupLayoutAndDrawer(
                R.layout.activity_main,
                R.id.drawer_layout,
                R.id.toolbar);
    }

    @Override
    protected String getActivityTitle() {
        return ACTIVITY_TITLE;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(UOITLibraryBookingApp.isFirstTimeLaunchSinceUpgradeOrInstall()) {
            WhatsNewDialog.show(this);
        }
    }


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        return getActionBarDrawerToggle().onOptionsItemSelected(item);
	}

/*	@Override
	public void LaunchRoomInteraction(CookieManager cookieManager, String roomNumber, String date, String viewState, String eventValidation,
			int shareRow, int shareColumn, int pageNumberInt, String viewStateGenerator){
			
		Intent intent = new Intent(this, ActivityRoomInteraction.class);
		intent.putExtra("type", "createbooking");
		intent.putExtra("date", date);
		intent.putExtra("room", roomNumber);
		intent.putExtra("shareRow", shareRow);
		intent.putExtra("shareColumn", shareColumn);
		intent.putExtra("pageNumberInt", pageNumberInt);
		intent.putExtra("viewState", viewState);
		intent.putExtra("eventValidation", eventValidation);
        intent.putExtra("viewStateGenerator", viewStateGenerator);
		MainActivity.cookieManager = cookieManager;
		
		startActivity(intent);
	}
	@Override
	public void LaunchViewLeaveOrJoin(CookieManager cookieManager,
			String roomNumber, String date, String groupName, String groupCode, String timeRange,
			String institution, String notes, String currentViewState,
			String currentEventValidation,
			int shareRow, int shareColumn, int pageNumberInt,
            String viewStateGenerator){
		Intent intent = new Intent(this, ActivityRoomInteraction.class);
		intent.putExtra("type", "viewleaveorjoin");
		intent.putExtra("groupName", groupName);
		intent.putExtra("date", date);
		intent.putExtra("room", roomNumber);
		intent.putExtra("shareRow", shareRow);
		intent.putExtra("shareColumn", shareColumn);
		intent.putExtra("pageNumberInt", pageNumberInt);
		intent.putExtra("groupCode", groupCode);
		intent.putExtra("viewState", currentViewState);
		intent.putExtra("eventValidation", currentEventValidation);
		intent.putExtra("timeRange", timeRange);
		intent.putExtra("institution", institution);
		intent.putExtra("notes", notes);
        intent.putExtra("viewStateGenerator", viewStateGenerator);
		MainActivity.cookieManager = cookieManager;
		
		
		
		startActivity(intent);
		
	}
	@Override
	public void LaunchJoinOrLeave(CookieManager cookieManager,
			String roomNumber, String weekDayName, String calendarDate,
			String calendarMonth, String currentViewState,
			String currentEventValidation,String[] joinSpinnerArr, String[] leaveSpinnerArr,
			int shareRow, int shareColumn, int pageNumberInt, String viewStateGenerator){
		Intent intent = new Intent(this, ActivityRoomInteraction.class
                );
		intent.putExtra("type", "joinorleave");
		intent.putExtra("date", weekDayName + ", " + calendarDate + ", " + calendarMonth);
		intent.putExtra("room", roomNumber);
		intent.putExtra("shareRow", shareRow);
		intent.putExtra("shareColumn", shareColumn);
		intent.putExtra("pageNumberInt", pageNumberInt);
		//Log.i(TAG, "room number"+ roomNumber);
		intent.putExtra("viewState", currentViewState);
		intent.putExtra("eventValidation", currentEventValidation);
		intent.putExtra("joinSpinnerArr", joinSpinnerArr);
		intent.putExtra("leaveSpinnerArr", leaveSpinnerArr);
        intent.putExtra("viewStateGenerator", viewStateGenerator);
		MainActivity.cookieManager = cookieManager;
		
		
		
		startActivity(intent);
		

		
	}*/

	@Override
	protected void onRestart() {
		Bundle intentExtras = getIntent().getExtras();
        if(intentExtras == null){
        	
        }
        else if(intentExtras.getString("bookURL") != null){
        	
        	String linkString = intentExtras.getString("bookURL");
        	getIntent().removeExtra("bookURL");
        	//Log.i(TAG, "linkString is " + linkString);
			if(isNetworkAvailable(this)){
				new AsyncModifiedBookOnly(this, cookieManager).execute(linkString);
        	}
        	else{
        		new AlertDialog.Builder(this)
        	    .setTitle("Connectivity Issue")
        	    .setMessage(R.string.networkerrordialogue)
        	    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
        	        public void onClick(DialogInterface dialog, int which) {
        	            // Do nothing
        	        }
        	     })
        	    .setIcon(R.drawable.ic_dialog_alert)
        	     .show();
        	}
        }
		super.onRestart();
	}

	@Override
	protected void onNewIntent(Intent intent) {
	    super.onNewIntent(intent);
	    setIntent(intent);
	}

    public boolean isNetworkAvailable(Context ctx){
	    ConnectivityManager cm = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()&& cm.getActiveNetworkInfo().isAvailable()&& cm.getActiveNetworkInfo().isConnected()) 
	    {
	        return true;
	    }
	    else
	    {
	        return false;
	    }
    }
}
