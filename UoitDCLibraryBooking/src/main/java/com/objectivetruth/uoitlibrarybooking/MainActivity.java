package com.objectivetruth.uoitlibrarybooking;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
    private static final long AUTO_REFRESH_DELAY = 6000;
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

        configureAndSetupLayoutAndDrawer(
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
        if(UOITLibraryBookingApp.IS_FIRST_TIME_LAUNCH_SINCE_UPGRADE_OR_INSTALL) {
            WhatsNewDialog.show(this);
        }
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        return getActionBarDrawerToggle().onOptionsItemSelected(item);
	}

/*    @Override
    public void ClearResetIcon(){
    	if(refreshItem !=null){
            //refreshItem.setEnabled(true);
            refreshItem.setActionView(null);
        }
        Timber.i("RefreshIcon Cleared");

    }*/

/*	@Override
	public void ChangeScrollPosition(int firstVisibleItem, int scrollTarget, float ycoord) {
			//Log.i(TAG, "i'm here");
*//*    		Calendar_Generic_Page_Fragment fragmentPage = (Calendar_Generic_Page_Fragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + String.valueOf(scrollTarget));
    		Calendar_Generic_ListView_Fragment currentListView = (Calendar_Generic_ListView_Fragment) fragmentPage.getChildFragmentManager().findFragmentByTag("calendar" + String.valueOf(scrollTarget) + "listview");
    		currentListView.changeTheScroll(firstVisibleItem, ycoord);*//*


	}*/

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

/*   /**
     * Starts a refresh event but does not cancel if it already running. If a refresh is already
     * running, then this will just return and nothing will happen. This is useful if you want to
     * ensure the current refresh is current. If you want to emulate the user clicking the button
     * and respect the current refresh state(ie. cancel if already running) then see function below
     * @see this#handleRefreshClick()
     *//*
	public void DoRefresh(){
        if(isNetworkAvailable(this)){
            if(mCalendarRefresher == null){

                Timber.i("Refresh Started!");
                mCalendarRefresher = new CalendarRefresher(this);
                mCalendarRefresher.execute();
                refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
                refreshItem.getActionView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity.this.handleRefreshClick();
                    }
                });
                //Commented out to enable the refresh icon to be repressed to cancel
                //refreshItem.setEnabled(false);
            }

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
	@Override
	protected void onStart() {
		super.onStart();
/*        if(mDefaultSharedPreferences.getBoolean(HAS_LEARNED_MYACCOUNT, false)){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!isDialogShowing && isFront && !hasManuallyRefreshedSinceOpeningActivity){
                        //DoRefresh();
                    }

                }
            }, AUTO_REFRESH_DELAY);
        }*/
	}
	
    /**
     * Takes care of creating the MyAccount Fragment, disabling the actionview if required, and
     * setting the correct sharedPref values
     */
/*    private void handleMyAccountClick(){
        FragmentManager fragMan = getSupportFragmentManager();
        DiaFragMyAccount currentFrag = (DiaFragMyAccount)fragMan.findFragmentByTag(MY_ACCOUNT_DIALOGFRAGMENT_TAG);
        if(currentFrag == null){
            currentFrag = new DiaFragMyAccount();
            Timber.i("Dialog Fragment with tag " + MY_ACCOUNT_DIALOGFRAGMENT_TAG + " doesn't exist, creating it before showing");
        }
        else{
            Timber.i("Dialog Fragment with tag " + MY_ACCOUNT_DIALOGFRAGMENT_TAG + " exists, reusing and showing now");
        }
        if(myAccountItem != null){
            myAccountItem.setActionView(null);
        }
        if(!mDefaultSharedPreferences.getBoolean(HAS_LEARNED_MYACCOUNT, false)){
            Timber.i("User has learned My Account");
            long firstMyAccountDelay = activityStartTime - System.currentTimeMillis();
            googleAnalyticsTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Calendar Home")
                    .setAction("First Time My Account Pressed")
                    .setValue(firstMyAccountDelay)
                    .build());
            mDefaultSharedPreferencesEditor.putBoolean(HAS_LEARNED_MYACCOUNT, true)
                    .commit();
        }


        currentFrag.show(fragMan, MY_ACCOUNT_DIALOGFRAGMENT_TAG);
    }*/

/*    public void displayMyAccountHint(){
        Timber.i("Displaying My Account Bounce Hint because user didn't log in when trying to book");
        googleAnalyticsTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Calendar Home")
                .setAction("Display My Account Hint, user didn't log in")
                .build());
        LayoutInflater inflater = this.getLayoutInflater();
        ImageView iv = (ImageView) inflater.inflate(R.layout.action_bar_myaccount_imageview,
                null);
        startActionBarBounceAnimation(iv);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleMyAccountClick();
            }
        });
        myAccountItem.setActionView(iv);
    }*/
}
