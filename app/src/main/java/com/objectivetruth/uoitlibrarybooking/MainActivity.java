package com.objectivetruth.uoitlibrarybooking;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.BookingInteractionModel;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionScreenLoadEvent;
import com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction.BookingInteraction;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.whatsnew.WhatsNewDialog;
import com.objectivetruth.uoitlibrarybooking.userinterface.common.ActivityBase;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import timber.log.Timber;

import javax.inject.Inject;
import java.net.CookieManager;

import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCES_KEYS.HAS_DISMISSED_WHATSNEW_DIALOG_THIS_VERSION;


public class MainActivity extends ActivityBase {
    public static final String GROUP_CODE_DIALOGFRAGMENT_TAG = "groupCodeInfoDiaFrag";
	AppCompatActivity mActivity = this;
	public static CookieManager cookieManager;
    private boolean isFirstLoadThisSession = false;
    private Subscription bookingInteractionScreenLoadEventSubscription;
	@Inject SharedPreferences mDefaultSharedPreferences;
	@Inject SharedPreferences.Editor mDefaultSharedPreferencesEditor;
    @Inject BookingInteractionModel bookingInteractionModel;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        if(savedInstanceState == null) {isFirstLoadThisSession = true; Timber.i("First time opening app this session");}

        ((UOITLibraryBookingApp) getApplication()).getComponent().inject(this);

        setContentView(R.layout.app_root);
        initializeAllMainFragmentsAndPreloadToView();
        setupToolbar(R.id.toolbar);
        setupDrawer(R.id.drawer_layout);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if(isFirstLoadThisSession) {
            _goToScreenByMenuID(R.id.drawer_menu_item_calendar);
        }else if(areOnlyDrawerRelatedScreensShowing()){ // A hack for the time being, use git blame to find out more
            _goToScreenByMenuID(getLastMenuItemIDRequested());
        }

        if(_hasNOTDismissedWhatsNewDialogThisVersion()) {
        	WhatsNewDialog
					.show(this)
					.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                                    .putBoolean(HAS_DISMISSED_WHATSNEW_DIALOG_THIS_VERSION, true)
                                    .apply();
                        }
		});}

    }

    private boolean _hasNOTDismissedWhatsNewDialogThisVersion() {
        return !PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getBoolean(HAS_DISMISSED_WHATSNEW_DIALOG_THIS_VERSION, false);
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

	private void _goToScreenByMenuID(int menuItemResourceID) {
		MenuItem initialMenuItem = getDrawerView().getMenu().findItem(menuItemResourceID);
		selectDrawerItem(initialMenuItem);
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
        _bindBookingInteractionEventToLoadingBookingInteractionScreen(
                bookingInteractionModel.getBookingInteractionScreenLoadEventObservable());
    }

    @Override
    protected void onStop() {
        if(bookingInteractionScreenLoadEventSubscription != null) {
            bookingInteractionScreenLoadEventSubscription.unsubscribe();
        }
        super.onStop();
    }

    private void _bindBookingInteractionEventToLoadingBookingInteractionScreen(
			Observable<BookingInteractionScreenLoadEvent> bookingInteractionEventObservable) {
	    bookingInteractionScreenLoadEventSubscription = bookingInteractionEventObservable
                .subscribe(new Action1<BookingInteractionScreenLoadEvent>() {
            @Override
            public void call(BookingInteractionScreenLoadEvent bookingInteractionScreenLoadEvent) {
                addHidingOfAllCurrentFragmentsToTransaction(getSupportFragmentManager().beginTransaction())
                        .add(R.id.mainactivity_content_frame, BookingInteraction.newInstance())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}
