package com.objectivetruth.uoitlibrarybooking;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.*;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import timber.log.Timber;

import javax.inject.Inject;
import java.net.CookieManager;

import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCES_KEYS.USER_PASSWORD;
import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCES_KEYS.USER_USERNAME;

public class ActivityRoomInteraction extends FragmentActivity {
	final public String TAG = "ActivityRoomInteraction";
	CookieManager cookieManager;
	String date;
	String roomNumber;
	String eventValidation;
	String viewState;
    String viewStateGenerator;
	TextView errorTextView;
	Activity mActivity;
	String joinSpinnerValue;
	String leaveSpinnerValue;
	String durationSpinnerValue = null;
	boolean isCorrectlyFilled = false;
	Button titleButton;
	EditText groupNameEditText;
	EditText groupNotesEditText;
	EditText groupCodeEditText;
    Spinner durationSpinner;
	//RequestQueue mRequestQueue = null;
	ImageView realImageView;
	int secretTaps = 0;
	@Inject Tracker googleAnalyticsTracker;
    @Inject SharedPreferences mDefaultSharedPreferences;
    @Inject SharedPreferences.Editor mDefaultSharedPreferencesEditor;
	boolean firstHiddenFunny = false;
	String calendarGroupName = null;
	String spinnerJoinString = null;
	Bundle bundleExtras;
	String timeRange = null;
	int shareRow;
	int shareColumn;
	int pageNumberInt;
    static String commentsStringActual = "";
	//Changable Variables
	boolean justPurchased = false;
    int SECRET_TAPS = 5; //Minimum is 3 (just trust me it is the minimum)

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        ((UOITLibraryBookingApp) getApplication()).getComponent().inject(this);

		Intent intent = getIntent();
		bundleExtras = intent.getExtras();
		mActivity = this;
        //TODO added the eventViewstateGenerrator to the calendar_action_icons_menu head part, now use it in the book instance
        if (bundleExtras != null){
            eventValidation = bundleExtras.getString("eventValidation");
            viewState = bundleExtras.getString("viewState");
            viewStateGenerator = bundleExtras.getString("viewStateGenerator");
            date = bundleExtras.getString("date");
            bundleExtras.getInt("shareRow");
            bundleExtras.getInt("shareColumn");
            bundleExtras.getInt("pageNumberInt");
            roomNumber = bundleExtras.getString("room");

            Timber.i("Activity Room Interaction Called, bundle has info:");
            Timber.v("room = " + roomNumber);
            Timber.v("eventValidation: " + eventValidation);
            Timber.v("viewState: " + viewState);
            Timber.v("viewStateGenerator: " + viewStateGenerator);
            Timber.v("date: " + date);
        }
/*		if(mRequestQueue == null){
			mRequestQueue = Volley.newRequestQueue(this);
		}*/
        //getRandomGif();
		
		//this.cookieManager = MainActivity.cookieManager;

		
		

		if(bundleExtras.getString("type").equalsIgnoreCase("test")){
            Timber.i("CreateBooking SubRoutine Executing...");
		}

		else if(bundleExtras.getString("type").equalsIgnoreCase("viewleaveorjoin")){
            Timber.i("ViewLeaveOfJoin SubRoutine Executing...");
			//setContentView(R.layout.interaction_viewleaveorjoin);
			String groupCode = bundleExtras.getString("groupCode");
			timeRange = bundleExtras.getString("timeRange");
			String institution = bundleExtras.getString("institution");
			String notes = bundleExtras.getString("notes");
			String groupName = bundleExtras.getString("groupName");
/*            TextView groupNameTV = (TextView) findViewById(R.id.viewleaveorjoin_groupname);
			TextView roomTV = (TextView) findViewById(R.id.viewleaveorjoin_room);
			TextView dateTV = (TextView) findViewById(R.id.viewleaveorjoin_date);
			TextView groupCodeTV = (TextView) findViewById(R.id.viewleaveorjoin_groupcode);
			TextView timeRangeTV = (TextView) findViewById(R.id.viewleaveorjoin_timerange);
			TextView institutionTV = (TextView) findViewById(R.id.viewleaveorjoin_institution);
			TextView notesTV = (TextView) findViewById(R.id.viewleaveorjoin_notes);*/
            ImageView roomPicture = (ImageView) findViewById(R.id.room_landing_room_picture);
            roomPicture.setImageResource(getResources().getIdentifier(roomNumber.toLowerCase(), "drawable", getPackageName()));
			
/*			calendarGroupName = groupName;
			groupNameTV.setText(groupName);
			roomTV.setText(roomNumber);
			dateTV.setText(date);
			groupCodeTV.setText(groupCode);
			timeRangeTV.setText(timeRange);
			institutionTV.setText(institution);
			if(notes.trim().isEmpty()){
				notesTV.setText("(none)");
			}
			else{
				notesTV.setText(notes);	
			}*/
			
			

					
			Button joinButton = (Button) findViewById(R.id.viewleaveorjoin_join_button);
			//Button leaveButton = (Button) findViewById(R.id.viewleaveorjoin_leave_group_button);
			
			joinButton.setOnClickListener(new OnClickListener(){


				@Override
				public void onClick(View view) {
                    String inputUsername = mDefaultSharedPreferences.getString(USER_USERNAME, null);
                    String inputPassword = mDefaultSharedPreferences.getString(USER_PASSWORD, null);
                    //Checks if the sharedPrefs values are valid
                    if(inputPassword != null && inputUsername != null){
                        String[] joinGroupInput = new String[]{
                                viewState,
                                eventValidation,
                                inputPassword,
                                inputUsername,
                                "Join",
                                viewStateGenerator

                        };

                        if(isNetworkAvailable()){
                            new AsyncRoomViewJoin(cookieManager, mActivity).execute(joinGroupInput);
                        }
                        else{
                            new AlertDialog.Builder(mActivity)
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
                    else{
                        String errorDescript = "Was about to book, but the values in Shared Pref were null, was handled";
                        Timber.e(new IllegalStateException(errorDescript), errorDescript);
                        Toast.makeText(mActivity, R.string.error_shared_pref_values_incorrect, Toast.LENGTH_LONG).show();
                    }
                }
			});
			
/*			leaveButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View view) {
                    String inputUsername = mDefaultSharedPreferences.getString(USER_USERNAME, null);
                    String inputPassword = mDefaultSharedPreferences.getString(USER_PASSWORD, null);
                    //Checks if the sharedPrefs values are valid
                    if(inputPassword != null && inputUsername != null){
                        String[] leaveGroupInput = new String[]{
                                viewState,
                                eventValidation,
                                inputPassword,
                                inputUsername,
                                "Leave",
                                viewStateGenerator
                        };

                        if(isNetworkAvailable()){
                            new AsyncRoomViewLeave(cookieManager, mActivity).execute(leaveGroupInput);
                        }
                        else{
                            new AlertDialog.Builder(mActivity)
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
                    else{
                        String errorDescript = "Was about to book, but the values in Shared Pref were null, was handled";
                        Timber.e(new IllegalStateException(errorDescript), errorDescript);
                        Toast.makeText(mActivity, R.string.error_shared_pref_values_incorrect, Toast.LENGTH_LONG).show();
                    }
				}
			});*/
		}

        googleAnalyticsTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Calendar Interaction")
                        .setAction("LeaveViewOrJoin")
                        .setLabel(roomNumber)
                        .build()
        );

	}	
	//CHECKING VALID BOOK
	//CHECKING VALID BOOK
	//CHECKING VALID BOOK
	//CHECKING VALID BOOK
	//CHECKING VALID BOOK
	//CHECKING VALID BOOK
	//CHECKING VALID BOOK
	//CHECKING VALID BOOK

    /**
     * Checks each edittext and spinner for correctness and animates the
     * error animation to the user. DOES NOT CHECK SHAREDPREFS, that's done on the button itself
     * @return true if all is good, false if something is wrong
     */
	public boolean isValidBook() {
        boolean isValid = true;
        if (groupNameEditText != null) {
            String groupNameToCheck = groupNameEditText.getText().toString();
            if (groupNameToCheck.isEmpty()) {
                YoYo.with(Techniques.Shake).duration(700).playOn(groupNameEditText);
                isValid = false;
            }
        }
        if (groupCodeEditText != null) {
            String groupCodeToCheck = groupCodeEditText.getText().toString();
            if (groupCodeToCheck.isEmpty()) {
                YoYo.with(Techniques.Shake).duration(600).delay(100).playOn(groupCodeEditText);
                isValid = false;
            }
        }
        if (durationSpinner != null) {
            if (durationSpinnerValue == null) {
                YoYo.with(Techniques.Shake).duration(500).delay(200).playOn(durationSpinner);
                isValid = false;
            }
        }
        return isValid;
    }

    public boolean isNetworkAvailable(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
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
		/*if(groupNameEditText.getError() == null && groupCodeEditText.getError() == null ){
			titleButton.setEnabled(true);
			
			calendarGroupName = groupNameEditText.getText().toString();
			titleButton.setOnClickListener(new OnClickListener(){

				
				
			}});
		}
		else{
			titleButton.setOnClickListener(null);
			titleButton.setEnabled(false);
			calendarGroupName = null;
			
			//titleButton.setTextColor(getResources().getColor(R.color.disabled_button_text));
		}*/

    
    private void decreaseAlphaOnRoomPicture(){
        ImageView roomPicture = (ImageView)findViewById(R.id.room_landing_room_picture);
        AlphaAnimation alpha = new AlphaAnimation(0.1F, 0.1F);
        alpha.setDuration(0);
        alpha.setFillAfter(true);
        roomPicture.startAnimation(alpha);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static class CalendarOrganizer {
        private final static int ICE_CREAM_BUILD_ID = 14;
        /**
         * Creates a calendar intent going from startTime to endTime
         * @param startTime
         * @param endTime
         * @param context
         * @return true if the intent can be handled and was started, 
         * false if the intent can't be handled
         */
        public static boolean createEvent(long startTime, long endTime, String title, String description,
                String location, boolean isAllDay, Context context) {
            int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < ICE_CREAM_BUILD_ID) {
                // all SDK below ice cream sandwich
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("beginTime", startTime);
                intent.putExtra("endTime", endTime);
                intent.putExtra("title", title);
                intent.putExtra("description", description);
                intent.putExtra("eventLocation", location);
                intent.putExtra("allDay", isAllDay);
//              intent.putExtra("rrule", "FREQ=YEARLY");

                try {
                    context.startActivity(intent);
                    return true;
                } catch(Exception e) {
                    return false;
                }
            } else {
                // ice cream sandwich and above
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime);
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime);
                intent.putExtra(Events.TITLE, title);
                intent.putExtra(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
                intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY , isAllDay);
                intent.putExtra(Events.DESCRIPTION, description);
                intent.putExtra(Events.EVENT_LOCATION, location);

//              intent.putExtra(Events.RRULE, "FREQ=DAILY;COUNT=10") 
                try {
                    context.startActivity(intent);
                    return true;
                } catch(Exception e) {
                    return false;
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

/*    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        int itemId = item.getItemId();
        switch (itemId) {
        case android.R.id.home:
        	if(justPurchased){
        		Intent intent = new Intent(this, MainActivity.class);
         		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);	
        		intent.putExtra("purchased", true);
        		startActivity(intent);
        		finish();	
        		return true;
    		}
            break;

        }
        super.onMenuItemSelected(featureId, item);
        return true;
    }*/
/*    @Override
    public void onBackPressed() {
    	if(justPurchased){
    		Intent intent = new Intent(this, MainActivity.class);
     		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);	
    		intent.putExtra("purchased", true);
    		startActivity(intent);
    		finish();	
		}
        else {
            super.onBackPressed();
        }
    }*/
}
