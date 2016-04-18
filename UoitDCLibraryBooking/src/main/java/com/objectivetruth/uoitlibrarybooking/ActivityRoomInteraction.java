package com.objectivetruth.uoitlibrarybooking;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.squareup.otto.Subscribe;
import timber.log.Timber;

import javax.inject.Inject;
import java.net.CookieManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.objectivetruth.uoitlibrarybooking.MainActivity.SHARED_PREF_KEY_PASSWORD;
import static com.objectivetruth.uoitlibrarybooking.MainActivity.SHARED_PREF_KEY_USERNAME;
import static com.objectivetruth.uoitlibrarybooking.constants.SHARED_PREFERENCES_KEYS.SHARED_PREF_INSTITUTION;

public class ActivityRoomInteraction extends FragmentActivity implements CommunicatorRoomInteractions {
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
	AsyncResponse comm;
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
        OttoBusSingleton.getInstance().register(this);
        //TODO added the eventViewstateGenerrator to the main head part, now use it in the book instance
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
		
		this.cookieManager = MainActivity.cookieManager;

		
		

		if(bundleExtras.getString("type").equalsIgnoreCase("test")){
            Timber.i("CreateBooking SubRoutine Executing...");
			InteractionSuccess("THIS IS A TEST LOREM IPSUM AND ALL THAT", true);
		}


		//CREATE BOOKING
		//CREATE BOOKING
		//CREATE BOOKING
		//CREATE BOOKING
		//CREATE BOOKING
		//CREATE BOOKING
		//CREATE BOOKING
		//CREATE BOOKING
		
		else if(bundleExtras.getString("type").equalsIgnoreCase("createbooking")){
            Timber.i("CreateBooking SubRoutine Executing...");
			setContentView(R.layout.interaction_book);


            TextView roomNumberTextView = (TextView) findViewById(R.id.interaction_book_room_number);
			errorTextView = (TextView) findViewById(R.id.book_error_message_actual);
			TextView dateField = (TextView) findViewById(R.id.book_date_actual);
            durationSpinner = (Spinner) findViewById(R.id.book_spinner_duration);
			groupNameEditText = (EditText) findViewById(R.id.book_group_name_actual);
            ImageButton commentImageButton = (ImageButton) findViewById(R.id.comment_button);
			groupCodeEditText = (EditText) findViewById(R.id.book_group_code_actual);
			titleButton = (Button) findViewById(R.id.book_room_number_actual);
            ImageButton groupCodeInfoImageButton = (ImageButton) findViewById(R.id.info_group_code);
			//titleButton.setTextColor(getResources().getColor(R.color.disabled_button_text));
            ImageView roomPicture = (ImageView) findViewById(R.id.room_landing_room_picture);
            roomPicture.setImageResource(getResources().getIdentifier(roomNumber.toLowerCase(), "drawable", getPackageName()));
			durationSpinnerValue = null;
            roomNumberTextView.setText(roomNumber);

            groupCodeInfoImageButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fragMan = getSupportFragmentManager();
                    DiaFragGeneric frag = new DiaFragGeneric();
                    frag.setArguments("Group Code Information", getString(R.string.tooltip_groupcode));
                    frag.show(fragMan, MainActivity.GROUP_CODE_DIALOGFRAGMENT_TAG);
                }
            });


            commentImageButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    googleAnalyticsTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Calendar Interaction")
                            .setAction("Show Comments Input Dialog")
                            .build());
                    FragmentManager fragMan = getSupportFragmentManager();
                    new commentDiaFrag().show(fragMan, null);
                }
            });
            /*ToolTipRelativeLayout toolTipRelativeLayout = (ToolTipRelativeLayout) findViewById(R.id.activity_main_tooltipRelativeLayout);
            ToolTip toolTip = new ToolTip()
                    .withText("A beautiful View")
                    .withColor(R.color.blue_font)
                    .withShadow();


            toolTipRelativeLayout.showToolTipForView(toolTip, commentImageButton);*/
            /*myToolTipView.setOnToolTipViewClickedListener(new ToolTipView.OnToolTipViewClickedListener() {
                @Override
                public void onToolTipViewClicked(ToolTipView toolTipView) {

                }
            });*/

			//titleButton.setText("Create Group - " + roomNumber);
            titleButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isValidBook()) {
                        String inputUsername = mDefaultSharedPreferences.getString(SHARED_PREF_KEY_USERNAME, null);
                        String inputPassword = mDefaultSharedPreferences.getString(SHARED_PREF_KEY_PASSWORD, null);
                        String institutionSpinnerValue = mDefaultSharedPreferences.getString(SHARED_PREF_INSTITUTION, null);
                        //Checks if the sharedPrefs values are valid
                        if(inputPassword != null && inputUsername != null && institutionSpinnerValue != null){
                            String[] fieldData = new String[]{
                                    viewState,
                                    eventValidation,
                                    groupNameEditText.getText().toString(),
                                    commentsStringActual,
                                    inputPassword,
                                    inputUsername,
                                    "Create group",
                                    groupCodeEditText.getText().toString(),
                                    durationSpinnerValue,
                                    institutionSpinnerValue,
                                    viewStateGenerator


                            };
                            if(isNetworkAvailable()){
                                new AsyncRoomConfirmation(cookieManager, mActivity).execute(fieldData);
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
                }
            });

            dateField.setText(date);
			ArrayAdapter<CharSequence> durationAdapter = ArrayAdapter.createFromResource(this,
                    R.array.duration, android.R.layout.simple_spinner_item);
			durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			durationSpinner.setAdapter(durationAdapter);
			durationSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> adapter, View view,
						int position, long id) {
					String[] timeToDecimal = new String[]{null, "0.5", "1.0", "1.5", "2"};
					durationSpinnerValue = timeToDecimal[position];
				}

				@Override
				public void onNothingSelected(AdapterView<?> adapter) {

					
				}
				
			});

            googleAnalyticsTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Calendar Interaction")
                            .setAction("CreateBooking")
                            .setLabel(roomNumber)
                            .build()
            );


			//JOIN OR LEAVE
			//JOIN OR LEAVE
			//JOIN OR LEAVE
			//JOIN OR LEAVE
			//JOIN OR LEAVE
			//JOIN OR LEAVE
			//JOIN OR LEAVE
			
		}
		else if(bundleExtras.getString("type").equalsIgnoreCase("joinorleave")){
            Timber.i("JoinOrLeave SubRoutine Executing...");
			setContentView(R.layout.interaction_joinorleave);

			TextView roomNumberTextView = (TextView) findViewById(R.id.joinorleave_room_number);
			TextView dateTextView = (TextView) findViewById(R.id.joinorleave_date);
			Button createButton = (Button) findViewById(R.id.joinorleave_create_group_button);
			Spinner joinSpinner = (Spinner) findViewById(R.id.joinorleave_join_spinner);
			Spinner leaveSpinner = (Spinner) findViewById(R.id.joinorleave_leave_spinner);
			Button leaveButton = (Button) findViewById(R.id.joinorleave_leave_grou_button);
			Button joinButton = (Button) findViewById(R.id.joinorleave_join_button);
			String[] joinSpinnerArr = (String[]) bundleExtras.get("joinSpinnerArr");
			String[] leaveSpinnerArr = (String[]) bundleExtras.get("leaveSpinnerArr");
			errorTextView = (TextView) findViewById(R.id.joinorleave_error_text);
            ImageView roomPicture = (ImageView) findViewById(R.id.room_landing_room_picture);
			//Timber.i("room number"+ roomNumber);
			roomNumberTextView.setText(roomNumber);
			dateTextView.setText(date);

            roomPicture.setImageResource(getResources().getIdentifier(roomNumber.toLowerCase(), "drawable", getPackageName()));

			createButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View view) {
					if(isNetworkAvailable()){
						new AsyncBridgeJoinOrLeaveToCreate(cookieManager, mActivity, viewState, eventValidation, viewStateGenerator).execute();
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
				
			});
			
			
			ArrayAdapter<String> joinAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
					joinSpinnerArr);
			joinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			joinSpinner.setAdapter(joinAdapter);

			ArrayAdapter<String> leaveAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
					leaveSpinnerArr);
			leaveAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			leaveSpinner.setAdapter(leaveAdapter);

			leaveSpinnerValue = leaveSpinnerArr[0].split(":")[3].substring(1, 5);
			//Timber.i(leaveSpinnerValue);
			joinSpinnerValue = joinSpinnerArr[0].split(":")[3].substring(1, 5);
			spinnerJoinString = joinSpinnerArr[0];
			int foundAt = joinSpinnerArr[0].indexOf("(");
			calendarGroupName = joinSpinnerArr[0].substring(0, foundAt - 1);


			joinSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> adapter, View view,
						int position, long id) {
					spinnerJoinString = ((String) adapter.getItemAtPosition(position));
					joinSpinnerValue = spinnerJoinString.split(":")[3].substring(1, 5);
					int foundAt = spinnerJoinString.indexOf("(");
					calendarGroupName = spinnerJoinString.substring(0, foundAt - 1);
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> adapter) {

					
				}
				
			});
			
			leaveSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> adapter, View view,
						int position, long id) {
					leaveSpinnerValue = ((String) adapter.getItemAtPosition(position)).split(":")[3].substring(1, 5);
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> adapter) {

					
				}
				
			});
			
			joinButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View view) {

                    String inputUsername = mDefaultSharedPreferences.getString(SHARED_PREF_KEY_USERNAME, null);
                    String inputPassword = mDefaultSharedPreferences.getString(SHARED_PREF_KEY_PASSWORD, null);
                    //Checks if the sharedPrefs values are valid
                    if(inputPassword != null && inputUsername != null){
                        String[] joinGroupInput = new String[]{
                                viewState,
                                eventValidation,
                                joinSpinnerValue, //group code
                                "Create or Join a Group",
                                inputUsername,
                                inputPassword,
                                viewStateGenerator

                        };

                        if(isNetworkAvailable()){
                            new AsyncRoomJoin(cookieManager, mActivity).execute(joinGroupInput);
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
		
			
			leaveButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View view) {
                    String inputUsername = mDefaultSharedPreferences.getString(SHARED_PREF_KEY_USERNAME, null);
                    String inputPassword = mDefaultSharedPreferences.getString(SHARED_PREF_KEY_PASSWORD, null);
                    //Checks if the sharedPrefs values are valid
                    if(inputPassword != null && inputUsername != null){
                        String[] leaveGroupInput = new String[]{
                                viewState,
                                eventValidation,
                                "invalid_code", //has to be sent to show that you didn't want to create a room
                                leaveSpinnerValue, //group code
                                "Leave the Group",
                                inputUsername,
                                inputPassword,
                                viewStateGenerator
                        };

                        if(isNetworkAvailable()){
                            new AsyncRoomLeave(cookieManager, mActivity).execute(leaveGroupInput);
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
            googleAnalyticsTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Calendar Interaction")
                            .setAction("JoinOrLeave")
                            .setLabel(roomNumber)
                            .build()
            );

			//VIEWLEAVEORJOIN
			//VIEWLEAVEORJOIN
			//VIEWLEAVEORJOIN
			//VIEWLEAVEORJOIN
			//VIEWLEAVEORJOIN
			//VIEWLEAVEORJOIN
			
		}
		else if(bundleExtras.getString("type").equalsIgnoreCase("viewleaveorjoin")){
            Timber.i("ViewLeaveOfJoin SubRoutine Executing...");
			setContentView(R.layout.interaction_viewleaveorjoin);
			String groupCode = bundleExtras.getString("groupCode");
			timeRange = bundleExtras.getString("timeRange");
			String institution = bundleExtras.getString("institution");
			String notes = bundleExtras.getString("notes");
			String groupName = bundleExtras.getString("groupName");
            TextView groupNameTV = (TextView) findViewById(R.id.viewleaveorjoin_groupname);
			TextView roomTV = (TextView) findViewById(R.id.viewleaveorjoin_room);
			TextView dateTV = (TextView) findViewById(R.id.viewleaveorjoin_date);
			TextView groupCodeTV = (TextView) findViewById(R.id.viewleaveorjoin_groupcode);
			TextView timeRangeTV = (TextView) findViewById(R.id.viewleaveorjoin_timerange);
			TextView institutionTV = (TextView) findViewById(R.id.viewleaveorjoin_institution);
			TextView notesTV = (TextView) findViewById(R.id.viewleaveorjoin_notes);
            ImageView roomPicture = (ImageView) findViewById(R.id.room_landing_room_picture);
            roomPicture.setImageResource(getResources().getIdentifier(roomNumber.toLowerCase(), "drawable", getPackageName()));
			
			calendarGroupName = groupName;
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
			}
			
			

					
			Button joinButton = (Button) findViewById(R.id.viewleaveorjoin_join_button);
			Button leaveButton = (Button) findViewById(R.id.viewleaveorjoin_leave_group_button);
			
			joinButton.setOnClickListener(new OnClickListener(){


				@Override
				public void onClick(View view) {
                    String inputUsername = mDefaultSharedPreferences.getString(SHARED_PREF_KEY_USERNAME, null);
                    String inputPassword = mDefaultSharedPreferences.getString(SHARED_PREF_KEY_PASSWORD, null);
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
			
			leaveButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View view) {
                    String inputUsername = mDefaultSharedPreferences.getString(SHARED_PREF_KEY_USERNAME, null);
                    String inputPassword = mDefaultSharedPreferences.getString(SHARED_PREF_KEY_PASSWORD, null);
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
			});
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

    
	@Override
	public void InteractionSuccess(final String returnMessage, final boolean isCalendarable) {
		Button addToCalendarButton;
		Button okButton;
		firstHiddenFunny = false;
		setContentView(R.layout.interaction_book_success);
		final ImageView qrCodeActual = (ImageView) findViewById(R.id.qractual);
		final TextView successMessage = (TextView) findViewById(R.id.book_success_actual);



        addToCalendarButton = (Button) findViewById(R.id.addtocalendarbutton);
        //A Cancel success Event
        if(!isCalendarable){
        	addToCalendarButton.setVisibility(View.GONE);
            qrCodeActual.setVisibility(View.INVISIBLE);
            googleAnalyticsTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Calendar Interaction")
                            .setAction("Successfully Left a Booking")
                            .build()
            );

        }
        //Join/Create booking Success Event
        else{
            googleAnalyticsTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Calendar Interaction")
                            .setAction("Successfully Created/Joined a Booking")
                            .build()
            );
            if(shareRow > -1){
                String qrData = "uoitdclibrarybooking" + "-" + String.valueOf(shareRow) + "-" + String.valueOf(shareColumn) + "-" + String.valueOf(pageNumberInt);
            }
            else{
                qrCodeActual.setImageResource(R.drawable.ic_stamp_no_info);
            }
            addToCalendarButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    googleAnalyticsTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Calendar Interaction")
                            .setAction("Add to Calendar")
                            .build());
                    String calendarTitle = null;

                    Calendar startTime = Calendar.getInstance();
                    double MILLIESPERHOUR = 3600000.0;
                    long eventDurationLong = (long) MILLIESPERHOUR;

                    //Getting the groupname for the calendarEntry
                    if (calendarGroupName == null) {
                        calendarTitle = "Group Study Session";
                    } else {
                        calendarTitle = calendarGroupName + " - Group Study Session";
                    }

                    //Switch statments for each type of success
                    if (bundleExtras.getString("type").compareTo("createbooking") == 0) {

                        if (durationSpinnerValue != null) {
                            eventDurationLong = (long) (Float.valueOf(durationSpinnerValue) * MILLIESPERHOUR);
                        }
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, y h:mm a");
                            startTime.setTime(sdf.parse(date));
                            //startTime.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else if (bundleExtras.getString("type").compareTo("joinorleave") == 0) {

                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d, MMMM, y h:mm a");
                            if (spinnerJoinString != null) {
                                int stateStart = spinnerJoinString.indexOf("(") + 1;

                                int stateEnd = spinnerJoinString.indexOf("-", stateStart);
                                String startTimeString = spinnerJoinString.substring(stateStart, stateEnd);

                                stateStart = stateEnd + 1;
                                stateEnd = spinnerJoinString.indexOf("C", stateStart) - 1;
                                String endTimeString = spinnerJoinString.substring(stateStart, stateEnd);

                                String startDate = date + ", " + startTime.get(Calendar.YEAR) + " " + startTimeString;
                                String endDate = date + ", " + startTime.get(Calendar.YEAR) + " " + endTimeString;

                                startTime.setTime(sdf.parse(startDate));
                                Calendar endCalendar = Calendar.getInstance();
                                endCalendar.setTime(sdf.parse(endDate));

                                eventDurationLong = endCalendar.getTimeInMillis() - startTime.getTimeInMillis();
                            }
                        } catch (Exception p) {
                            p.printStackTrace();
                        }
                    } else if (bundleExtras.getString("type").compareTo("viewleaveorjoin") == 0) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, y, h:mm a");
                            if (timeRange != null) {
                                Timber.i(timeRange);
                                int stateStart = 0;
                                int stateEnd = timeRange.indexOf("-");
                                String startTimeString = timeRange.substring(0, stateEnd - 1);

                                stateStart = stateEnd + 1;
                                //stateEnd = timeRange.indexOf(".", stateStart);

                                String endTimeString = timeRange.substring(stateStart);

                                String startDate = date + ", " + startTimeString;
                                String endDate = date + ", " + endTimeString;
                                Timber.i(startDate);
                                Timber.i(endDate);
                                startTime.setTime(sdf.parse(startDate));
                                Calendar endCalendar = Calendar.getInstance();
                                endCalendar.setTime(sdf.parse(endDate));

                                eventDurationLong = endCalendar.getTimeInMillis() - startTime.getTimeInMillis();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }


                    Timber.i(startTime.toString());

                    CalendarOrganizer.createEvent(
                            startTime.getTimeInMillis(),
                            startTime.getTimeInMillis() + eventDurationLong,
                            calendarTitle,
                            getResources().getString(R.string.calendardescription),
                            roomNumber + " @ UOIT/DC Library",
                            false,
                            mActivity);

                            /*startTime,
                            endTime,
                            title,
                            description,
                            location,
                            isAllDay,
                            context)*/


                }
            });
        }
		okButton = (Button) findViewById(R.id.okbutton);
		
		
		okButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {

				finish();
				
			}
			
		});
		
		successMessage.setText(Html.fromHtml(returnMessage));
	}

	@Override
	public void CreateRoomFail(String errorMessage) {
		errorTextView.setText(Html.fromHtml(errorMessage));
        errorTextView.setVisibility(View.VISIBLE);
        decreaseAlphaOnRoomPicture();
		googleAnalyticsTracker.send(new HitBuilders.EventBuilder()
		.setCategory("Calendar Interaction")
		.setAction("Error Create Room")
		.setLabel(errorMessage)
		.build()
		);
	}

	@Override
	public void RoomLeaveFail(String returnMessage) {
		TextView errorText = (TextView) findViewById(R.id.joinorleave_error_text);
        errorText.setVisibility(View.VISIBLE);
        decreaseAlphaOnRoomPicture();

        errorText.setText(Html.fromHtml(returnMessage));
		googleAnalyticsTracker.send(new HitBuilders.EventBuilder()
		.setCategory("Calendar Interaction")
		.setAction("Error Leave Room")
		.setLabel(returnMessage)
		.build()
		);
		
	}

	@Override
	public void createFromJoinOrLeave(CookieManager cookieManager, String result) {

		if(result == null){
			Toast.makeText(mActivity.getApplicationContext(), "Couldn't find that time slot, try refreshing calendar",
                    Toast.LENGTH_LONG).show();
		}
		else{
			MainActivity.cookieManager = cookieManager;
    		Intent intent = new Intent(this, MainActivity.class);
    		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    		intent.putExtra("bookURL", result);
			startActivity(intent);
			finish();
		}
		
		
	}

	@Override
	public void ViewLeaveOrJoinFail(String returnMessage) {
		TextView errorText = (TextView) findViewById(R.id.viewleaveorjoin_error_text);
        errorText.setVisibility(View.VISIBLE);
        decreaseAlphaOnRoomPicture();
		errorText.setText(Html.fromHtml(returnMessage));
		googleAnalyticsTracker.send(new HitBuilders.EventBuilder()
		.setCategory("Calendar Interaction")
		.setAction("Error View Leave or Join")
		.setLabel(returnMessage)
		.build()
		);
		
	}
    private void decreaseAlphaOnRoomPicture(){
        ImageView roomPicture = (ImageView)findViewById(R.id.room_landing_room_picture);
        AlphaAnimation alpha = new AlphaAnimation(0.1F, 0.1F);
        alpha.setDuration(0);
        alpha.setFillAfter(true);
        roomPicture.startAnimation(alpha);
    }

    @Override
    protected void onDestroy() {
        OttoBusSingleton.getInstance().unregister(this);
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

    public static class commentDiaFrag extends DialogFragment{
        EditText commentsEditText;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.diafrag_comments, container, false);
            Button okButton = (Button) rootView.findViewById(R.id.comments_ok_button);
            commentsEditText = (EditText) rootView.findViewById(R.id.diafrag_comments_actual);
            commentsEditText.setText(commentsStringActual);
            okButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    getDialog().dismiss();

                }
            });
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
            getDialog().setTitle(R.string.my_account_frag_title);
            getDialog().getWindow()
                    .getAttributes().windowAnimations = R.style.DialogAnimation;
            return rootView;
        }

        @Override
        public void onStop() {
            if(commentsEditText != null){
                commentsEditText.onEditorAction(EditorInfo.IME_ACTION_DONE);
                OttoBusSingleton.getInstance().post(new CommentsUpdateEvent(commentsEditText.getText().toString()));
            }
            super.onStop();
        }
    }

    //Received a callback whenever the comments close
    @Subscribe
    public void CommentsUpdated(CommentsUpdateEvent event){
        commentsStringActual = event.commentsData;
    }


    /*public static class PremiumPromoFragment extends DialogFragment{
    	final static String TAG = "PremiumPromoFragment";
    	String returnMessage;
    	boolean isCalendarable;
    	
    	static PremiumPromoFragment newInstance(String returnMessage, boolean isCalendarable){
    		
    		PremiumPromoFragment frag = new PremiumPromoFragment();
    		frag.returnMessage = returnMessage;
    		frag.isCalendarable = isCalendarable;
    		return frag;
    	}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.premium_promo, container, false);
			Button negativeButton = (Button) rootView.findViewById(R.id.cancel);
			negativeButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					getDialog().dismiss();
					
				}
				
			});
			Button upgradeOK = (Button) rootView.findViewById(R.id.upgradeok);
			upgradeOK.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
		        	((UOITLibraryBookingApp)getActivity().getApplication()).getIABHelper().launchPurchaseFlow(getActivity(), MainActivity.SKU_PREMIUM, 10001,   
		        			new IabHelper.OnIabPurchaseFinishedListener() {
		        		 public void onIabPurchaseFinished(IabResult result, Purchase purchase) 
		        		 {
		        		    if (result.isFailure()) {
		        		       	*//*Toast.makeText(getActivity(), "Error purchasing",
		        		 			   Toast.LENGTH_SHORT).show();*//*
		        		       	
		        		       return;
		        		    }      
		        		    else if (purchase.getSku().equals(MainActivity.SKU_PREMIUM)) {
		        		    	Toast.makeText(getActivity(), "Purchased! Thank you!", 
			        		 			   Toast.LENGTH_SHORT).show();
		        		    	((UOITLibraryBookingApp)getActivity().getApplication()).setIsPremium(true);
		        		    	try{
		        		    		((ActivityRoomInteraction)getActivity()).InteractionSuccess(returnMessage, isCalendarable);
		        		    		((ActivityRoomInteraction)getActivity()).justPurchased = true;
		        		    		PremiumPromoFragment promoFrag = ((ActivityRoomInteraction)getActivity()).promoFrag;
		        		    		if(promoFrag.isVisible()){
		        		    			promoFrag.dismiss();
		        		    		}
		        		    	}catch(Exception e){
		        		    		e.printStackTrace();
		        		    	}
		        		    	
		        		    	
		        		    		
		        		    
		        		       //Timber.i("purchased!");
		        		       
		        		       
		        		       
		        		    }

		        		 }
		        		}, "");
					
					
				}
				
			});

			getDialog().setTitle("Upgrade to Premium");
			return rootView;
			
		}
    	
    	
    }*/
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
