package com.objectivetruth.uoitlibrarybooking;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.*;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.util.SparseArray;
import android.view.*;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;
import com.crashlytics.android.Crashlytics;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.objectivetruth.uoitlibrarybooking.Calendar_Generic_Page_Fragment.RoomFragmentDialog;
import com.squareup.otto.Subscribe;
import timber.log.Timber;

import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActivityBase implements ActionBar.TabListener, AsyncResponse{
    public static final String MY_ACCOUNT_DIALOGFRAGMENT_TAG = "myAccountDiaFrag";
    public static final String PASSWORD_INFO_DIALOGFRAGMENT_TAG = "passwordInfoDiaFrag";
    public static final String GROUP_CODE_DIALOGFRAGMENT_TAG = "groupCodeInfoDiaFrag";
    public static final String SHARED_PREF_REGISTRATIONID = "shared_pref_gcm_registration_id";
    public static final String SHARED_PREF_REGISTRATION_VERSION = "shared_pref_registration_version";
    public static final String SHARED_PREF_APPVERSION = "shared_pref_appversion_db";
    public static final String SHARED_PREF_HAS_LEARNED_HELP = "shared_pref_has_learned_help";
    public static final String SHARED_PREF_IS_FIRST_TIME_LAUNCH = "shared_pref_is_first_time_launch";
    public static final int MAX_BOOKINGS_ALLOWED = 20; //This can safely be changed
    public static final String SHARED_PREF_INSTITUTION = "shared_pref_institution";
    public static final String SHARED_PREF_KEY_BOOKINGS_LEFT = "shared_pref_bookings_left";
    public static final String SHARED_PREF_HASLEARNED_MYACCOUNT = "shared_pref_haslearned_myaccount";
    private static final long AUTO_REFRESH_DELAY = 6000;
    public static final String SHARED_PREF_UUID = "shared_pref_uuid" ;
    private static final String HELP_DIALOGFRAGMENT_TAG = "helpDiaFrag";
    public static boolean isDialogShowing = false;
    private final String TAG = "MainActivity";
    public long activityStartTime = System.currentTimeMillis();
	ActionBarActivity mActivity = this;
	public static DbHelper mdbHelper;
    private boolean isRefreshWaiting = false;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
    public static String hasLEARNED_REFRESH = "has_learned_refresh";
	private CharSequence mDrawerTitle;
	//private CharSequence mTitle =  "Calendar";
	private String[] menuItems;
	public static CookieManager cookieManager;
	MenuItem refreshItem;
    MenuItem myAccountItem;
	static SharedPreferences defaultPreferences;
	static SharedPreferences.Editor defaultPrefsEditor;
	View refresh_button;
	static CalendarRefresher mCalendarRefresher = null;
    static LoginAsynkTask mLoginAsyncTask = null;
	boolean isFront = false;
	Tracker t = null;
    public final static String SHARED_PREF_KEY_USERNAME = "username";
    public final static String SHARED_PREF_KEY_PASSWORD = "password";
    boolean hasManuallyRefreshedSinceOpeningActivity = false;

    //TODO put this in savedinstancestate
    public static String errorMessageFromLogin = "";

	boolean isNewInstance = true;

	int tabNumber = -1;

	int gridViewLastVisiblePosition;
	boolean isForQRCode = false;
	ProgressDialog progDialogQRCode = null;

	final int ACTIVITYPAGENUMBER = 0;
	int shareRow = 0;
	int shareColumn = 0;
	int pageNumberInt = 0;


	/**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
	public ArrayList<CalendarMonth> calendarCache = null;
	


	@Override
    protected void onCreate(Bundle savedInstanceState) {
		Timber.i("MainActivity: onCreate()");
    	mdbHelper = new DbHelper(this, null, null, 1);
    	super.onCreate(savedInstanceState);
    	
    	
    	
		if(t == null){
    		t = ((UOITLibraryBookingApp) getApplication()).getTracker();
    	}

		setContentView(R.layout.activity_main);
		defaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		defaultPrefsEditor = defaultPreferences.edit();

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                t.send(new HitBuilders.EventBuilder()
                                .setCategory("Calendar Home")
                                .setAction("Switch Page")
                                .setLabel(String.valueOf(position))
                                .build());
                Timber.i("Main Activity View Pager switching page to " + position);
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        int initialViewPagerPageCount = mSectionsPagerAdapter.getCount();
        Timber.i("Adding initial ViewPager Page Count (this is not from a Refresh event). Count = " + initialViewPagerPageCount);
        for (int i = 0; i < initialViewPagerPageCount; i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        mDrawerTitle = getTitle();
        menuItems = getResources().getStringArray(R.array.menuItems);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer
        // opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new DrawerListAdapter(this,
                R.layout.drawer_list_item, menuItems, ACTIVITYPAGENUMBER, this));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(menuItems[ACTIVITYPAGENUMBER]);
        //getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this, /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open, /*
                                       * "open drawer" description for
                                       * accessibility
                                       */
                R.string.navigation_drawer_close /*
                                       * "close drawer" description for
                                       * accessibility
                                       */
                ) {
                    @Override
                    public void onDrawerClosed(View view) {
                        getSupportActionBar().setTitle(menuItems[ACTIVITYPAGENUMBER]);
                        invalidateOptionsMenu(); // creates call to
                                                 // onPrepareOptionsMenu()
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        getSupportActionBar().setTitle(mDrawerTitle);
                        invalidateOptionsMenu(); // creates call to
                                                 // onPrepareOptionsMenu()
                    }

                };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        //TODO enable this for GCM
/*        if (checkPlayServices()) {
            String registrationId = getRegistrationId();
            if (registrationId == null) {
                Timber.i("GCM registrationId not found, getting new registrationId");
                new AsyncGcmRegistration().execute(this);
            }
            else{
                Timber.i("GCM RegistrationId found and valid");
                Crashlytics.setString(SHARED_PREF_REGISTRATIONID, registrationId);
            }
        } else {
            Timber.i("GCM: No valid Google Play Services APK found.");
            Toast.makeText(this, "Google Play Services Not Found, some content may not work", Toast.LENGTH_LONG);
        }*/

        //if its the first time using the app
        boolean isFirstTimeAppOpening = defaultPreferences.getBoolean(SHARED_PREF_IS_FIRST_TIME_LAUNCH, true);
        if(isFirstTimeAppOpening){
            Crashlytics.setBool(SHARED_PREF_IS_FIRST_TIME_LAUNCH, true);
            defaultPrefsEditor.putBoolean(SHARED_PREF_IS_FIRST_TIME_LAUNCH, false);
        }
        else{
            Crashlytics.setBool(SHARED_PREF_IS_FIRST_TIME_LAUNCH, false);
        }
        OttoBusSingleton.getInstance().register(this);


    }

    /**
     * checks if the registration id (if present) is valid for new version, or not
     * @return null if no valid registrationId found (checks for app version too)
     */
    private String getRegistrationId(){
        String registrationId = defaultPreferences.getString(SHARED_PREF_REGISTRATIONID, "");
        int registrationVersion = defaultPreferences.getInt(SHARED_PREF_REGISTRATION_VERSION, -1);

        if (registrationId.isEmpty() || registrationVersion < 0) {
            return null;
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = registrationVersion;
        int currentVersion = BuildConfig.VERSION_CODE;
        if (registeredVersion != currentVersion) {
            Timber.i("GCM: App version changed, invalidating GCM Reg Id, must run new GCM Registraion...");
            return null;
        }
        return registrationId;

    }


    /**
     * Checks for GooglePlayServices Availabity
     * @return true if available, false if not
     */
    private boolean checkPlayServices(){
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(ConnectionResult.SUCCESS == resultCode){
            return true;
        }else{
            return false;
        }
    }
    @Override
    protected int getActivityPageNumber() {
        return ACTIVITYPAGENUMBER;
    }

    @Override
    protected String[] getMenuItems() {
        return menuItems;
    }

    @Override
    protected DrawerLayout getmDrawerLayout() {
        return mDrawerLayout;
    }

    @Override
    protected ListView getmDrawerList() {
        return mDrawerList;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content
        // view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen){
        	return false;
        }
        return super.onPrepareOptionsMenu(menu);
    }

/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        return true;
    }*/

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            
        	selectItem(position);
            
        }
    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Timber.i("MainActivity: onCreateOptionsMenu()");
        // Inflate the menu; this adds items to the action bar if it is present.
    	if(BuildConfig.DEBUG){
        	getMenuInflater().inflate(R.menu.debugmenu, menu);

            // Unlock the screen
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        }
        else{
        	getMenuInflater().inflate(R.menu.main, menu);
        }
    	//getMenuInflater().inflate(R.menu.debugmenu, menu);
        refreshItem = menu.findItem(R.id.refresh_calendar);
        myAccountItem = menu.findItem(R.id.user_account);
        //defaultPreferences.getBoolean("is_first_load_tutorial", true) &&
        if(defaultPreferences.getBoolean("is_first_load_tutorial", true) && isNewInstance == true){
            //showDisclaimer();

        	defaultPrefsEditor.putBoolean("is_first_load_tutorial", false)
    		.commit();
        }

        if(!defaultPreferences.getBoolean(hasLEARNED_REFRESH, false)){

            LayoutInflater inflater = this.getLayoutInflater();
            ImageView iv = (ImageView) inflater.inflate(R.layout.action_bar_refresh_imageview,
                    null);
            Timber.i("User has Not Learned Refresh");
            startActionBarBounceAnimation(iv);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleRefreshClick();

                }
            });
            refreshItem.setActionView(iv);
        }
        //Prepare MyAccount Actionview if has learned refresh and has NOT learned myaccount
        if(defaultPreferences.getBoolean(hasLEARNED_REFRESH, false) && !defaultPreferences.getBoolean(SHARED_PREF_HASLEARNED_MYACCOUNT, false)){
            Timber.i("User has not Learned MyAccount");
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
        }
		isNewInstance = false;

        if(mCalendarRefresher != null && isFront == true){
            if(refreshItem != null){
                refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
            }
        }



        return true;
    }

    private void showDisclaimer(){
        String disclaimerString = getResources().getString(R.string.showcaseIntro);
        SpannableString disclaimerSpan = new SpannableString(disclaimerString);
        Object span = new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER);
        disclaimerSpan.setSpan(span, 0, disclaimerString.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        new AlertDialog.Builder(this)
                .setTitle("Disclaimer")
                .setMessage(disclaimerSpan)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                })
                .setIcon(R.drawable.ic_dialog_alert)
                .show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_settings) {
        	Intent intent = new Intent(this, ActivitySettings.class);
        	startActivity(intent);

        }
        else if(id == R.id.help_calendar){
            t.send(new HitBuilders.EventBuilder()
                            .setCategory("Calendar Home")
                            .setAction("HelpDialog")
                            .setLabel("Pressed by User")
                            .build()
            );
            handleHelpClick();
            return true;
        }
        else if(id == R.id.user_account){
            t.send(new HitBuilders.EventBuilder()
                            .setCategory("Calendar Home")
                            .setAction("MyAccount")
                            .setLabel("Pressed by User")
                            .build()
            );
            handleMyAccountClick();
            return true;
        }
        else if(id == R.id.refresh_calendar){
            t.send(new HitBuilders.EventBuilder()
                            .setCategory("Calendar Home")
                            .setAction("Refresh")
                            .setLabel("Pressed by User")
                            .build()
            );
            handleRefreshClick();
            return true;
        }
        else if(id == R.id.debug_success){
        	Intent intent = new Intent(this, ActivityRoomInteraction.class);
        	intent.putExtra("type", "test");
        	startActivity(intent);
        }
        else if(id == R.id.debug_booknew){
        	Intent intent = new Intent(this, ActivityRoomInteraction.class);
        	intent.putExtra("type", "createbooking");
            intent.putExtra("room", "Lib999");
            intent.putExtra("date", "March 15, 1984, Monday");
        	startActivity(intent);
        }
        else if(mDrawerToggle.onOptionsItemSelected(item)) {

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
    	//Log.i(TAG, "tab selected: " + tab.getPosition());
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
        //0 is x and 1 is y for each int[]
        List<int[]> lastKnownScrollPositions = null;
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

        /**
         *
         * @return a listing of all the current fragments in this SectionPagerAdapter
         */
        public SparseArray<Fragment> getRegisteredFragmentSparseArray(){
            return registeredFragments;
        }

        @Override
        public void notifyDataSetChanged() {
            lastKnownScrollPositions = new ArrayList<int[]>();
            int key = 0;
            for(int i = 0; i < registeredFragments.size(); i++) {
                key = registeredFragments.keyAt(i);
                // get the object by the key.
                Calendar_Generic_Page_Fragment frag = (Calendar_Generic_Page_Fragment) registeredFragments.get(key);
                if(frag != null && frag.tableFixHeaders != null){
                    int[] addToLastKnownScrollPositions = {
                            frag.tableFixHeaders.getActualScrollX(),
                            frag.tableFixHeaders.getActualScrollY()
                    };
                    lastKnownScrollPositions.add(addToLastKnownScrollPositions);
                }

            }
            super.notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
        
		@Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
        	//the throwaway frag is called to instantiate another verison of iteself, it will destoryed by the system

        		Calendar_Generic_Page_Fragment throwawayfrag = new Calendar_Generic_Page_Fragment();
            	Calendar_Generic_Page_Fragment fragment = throwawayfrag.newInstance(position, mActivity, calendarCache);
                if(lastKnownScrollPositions!=null && lastKnownScrollPositions.size() > position){
                    fragment.oldScrollPositionX = lastKnownScrollPositions.get(position)[0];
                    fragment.oldScrollPositionY = lastKnownScrollPositions.get(position)[1];
                }

        		return fragment;	


        	/*switch(position){

        	case 0:
        		Calendar_Generic_Page_Fragment fragment = Calendar_Generic_Page_Fragment.newInstance(position);
        		return fragment;
        				Calendar1_Main_Fragment fragment = new Calendar1_Main_Fragment();
        		return fragment;
        	case 1:
        		Calendar_Generic_Page_Fragment fragment = Calendar_Generic_Page_Fragment.newInstance(position);
        		return fragment;
        	default: 
        		return PlaceholderFragment.newInstance(position + 1);
        		
        	}
*/        	
        	
        }

        @Override
        public int getCount() {
        	if(calendarCache == null){

                try{
    	        	SQLiteDatabase db = mdbHelper.getReadableDatabase();
    	            Cursor c = db.query(mdbHelper.CALENDAR_TABLE_NAME, null, mdbHelper.ARRAY_ID + " = 1", null, null, null, null);
    	            int count = (c.getColumnCount() - 2)/2;
    	            //Log.i(TAG, "get count is " + count);
    	            c.close();
    	            //Log.i(TAG, String.valueOf(count));
    	            return count;
                }catch(Exception e){
                	Log.i("MainActivity - getCount", e.getMessage());
                	e.printStackTrace();
                	return 1;
                }	
        	}
        	else{
        		if(calendarCache.size() <1){
        			Log.i(TAG, "size is" + calendarCache.size());
        			return calendarCache.size();
        		}
        		else{
        			return calendarCache.size();
        		}
        		
        	}
            
            
        }

        @Override
        public CharSequence getPageTitle(int position) {
        	if(calendarCache == null){
            	try{
    	        	String titleToReturn = "";
    	        	//Locale l = Locale.getDefault();
    	        	SQLiteDatabase db = mdbHelper.getReadableDatabase();
    	        	String day = "day" + String.valueOf(position + 1);
    	        	Cursor c = db.query(mdbHelper.CALENDAR_TABLE_NAME, new String[]{day}, null, null, null, null, null);
    	        	c.moveToFirst();
    	        	String weekDayName = c.getString(c.getColumnIndex(day));
    	        	c.moveToNext();
    	        	String dayNumber = c.getString(c.getColumnIndex(day));
    	        	c.moveToNext();
    	        	String monthName = c.getString(c.getColumnIndex(day));
    	        	titleToReturn = weekDayName + ", " + dayNumber + ", " + monthName;
    	            c.close();
    	            /*
    	            titleToReturn = titleToReturn + getString(c.getColumnIndex(day));
    	            */
    	            //String titleToReturn = getString(0) + ", " + c.getString(1) + ", " + c.getString(2); 
    	            if(monthName.isEmpty()){
    	            	return "";
    	            }
    	            else if(monthName.equalsIgnoreCase("refreshme")){
    	            	return "";
    	            }
    	            return titleToReturn;
            	}catch(Exception e){
            		e.printStackTrace();
            		return "null";
            	}
        	}
        	else{
        		String titleToReturn = calendarCache.get(position).dayOfTheWeek +
        				", " + calendarCache.get(position).dayNumber + 
        				", " + calendarCache.get(position).monthName;
	            if(calendarCache.get(position).monthName.isEmpty()){
	            	return "";
	            }
	            else if(calendarCache.get(position).monthName.equalsIgnoreCase("refreshme")){
	            	return "";
	            }
	            else{
	            	return titleToReturn;	
	            }
        		
        	}

            /*
            
            return c.getColumnCount(
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;*/
        }
    }

/*    @Subscribe
    public void PageFragmentViewCreated(PageFragmentViewCreatedEvent event){
        //Checks if the call came from the last page to be created, then do the action
        //we dont want to set the tab before the last view has been created
        Timber.i("tabNUmber "+ tabNumber + " event.pageNumber " +  event.pageNumber);
        if(event.pageNumber == mSectionsPagerAdapter.getCount() - 1){
            if(tabNumber > -1 && tabNumber < mSectionsPagerAdapter.getCount()){
                getSupportActionBar().selectTab(getSupportActionBar().getTabAt(1));
                //mViewPager.setCurrentItem(tabNumber);
                Timber.i("testing");
            }
        }

    }*/

    @Override
    public void SendMessageToAllGridViews(ArrayList<CalendarMonth> originalCalendarCache){
        Timber.i("SendMessageToAllGridViews() Called");
    	if(originalCalendarCache == null){
            Timber.e(new IllegalStateException(),"CalendarCache received from CalendarRefresher was null");
    		mCalendarRefresher = null;
        	if(isFront){
        		ClearResetIcon();	
        	}
    	}
    	else if(originalCalendarCache.size() > 0 && isFront && !isDialogShowing){
            Timber.i("isFront: " + isFront + " and isDialogShowing: " + isDialogShowing + " will Refresh Layout");
    		this.calendarCache = originalCalendarCache;
/*    		for(int i = 0; i < originalCalendarCache.size(); i++){
    			Calendar_Generic_Page_Fragment pageFrag = (Calendar_Generic_Page_Fragment) mSectionsPagerAdapter.getItem(i);
    			pageFrag.calendarCache = originalCalendarCache;
    		}*/
    		ActionBar actionBar = getSupportActionBar();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    		tabNumber = actionBar.getSelectedTab().getPosition();
		    Timber.i("Saving Currently Selected Tab : " + String.valueOf(tabNumber));
    		mSectionsPagerAdapter.notifyDataSetChanged();
        	actionBar.removeAllTabs();
            for (int i = 0; i < calendarCache.size(); i++) {
                // Create a tab with text corresponding to the page title defined by
                // the adapter. Also specify this Activity object, which implements
                // the TabListener interface, as the callback (listener) for when
                // this tab is selected.
                actionBar.addTab(
                        actionBar.newTab()
                                .setText(mSectionsPagerAdapter.getPageTitle(i))
                                .setTabListener(this));
            }
            YoYo.with(Techniques.FadeIn).duration(1000).playOn(mViewPager);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(tabNumber > -1 && tabNumber < calendarCache.size()){
                        Timber.i("Setting the tab to " + tabNumber);
                        //actionBar.setSelectedNavigationItem(tabNumber);
                        mViewPager.setCurrentItem(tabNumber, false);
                        //getSupportActionBar().selectTab(getSupportActionBar().getTabAt(tabNumber));
                        //mViewPager.setCurrentItem(tabNumber);
                    }
                }
            }, 100);
        	mCalendarRefresher = null;
        	if(isFront){
        		ClearResetIcon();	
        	}
            isRefreshWaiting = false;
    	}
        //If a Dialog is showing, don't refresh but set the flag so that when the dialog is closed, the
        //Appropriate Refresh takes place
        else if(originalCalendarCache.size() > 0 && isFront && isDialogShowing){
            this.calendarCache = originalCalendarCache;
            mCalendarRefresher = null;
            if(isFront){
                ClearResetIcon();
            }
            Timber.i("isFront: " + isFront + " and isDialogShowing: " + isDialogShowing + " will NOT refresh Layout, until the open Dialog is Closed");
            isRefreshWaiting = true;
        }
    	else{
            Timber.i("isFront: " + isFront + " and isDialogShowing: " + isDialogShowing + " will NOT refresh Layout");
    		mCalendarRefresher = null;
        	if(isFront){
        		ClearResetIcon();	
        	}
            isRefreshWaiting = false;
    	}
    	
    	//Checks if the QRCode thing has been called
    	if(isForQRCode){
    		isForQRCode = false;
			try {

		    	int numberOfItems = calendarCache.get(pageNumberInt).dataLength;
				int columnCount = calendarCache.get(pageNumberInt).columnCount;
				String[][] correspondingArr = new String[numberOfItems/columnCount][columnCount];
				for(int i = 0; i < calendarCache.get(pageNumberInt).data.length; i ++){
					correspondingArr[(i/columnCount)][(i%columnCount)] = calendarCache.get(pageNumberInt).source[i];
				}
		    	String linkString = correspondingArr[shareRow][shareColumn];
		    	
		    	int stateStart = linkString.indexOf("starttime=");
				int stateEnd = linkString.indexOf("&", stateStart+1);
				
				
				String timeDiag = linkString.substring(stateStart+10, stateEnd).replace("%20", " ");
				
				stateStart = linkString.indexOf("room=");
				stateEnd = linkString.indexOf("&", stateStart+1);
				String roomDiag = linkString.substring(stateStart+5, stateEnd);
				
				
				
				//confirmationTextView.setText("Room: " + roomDiag + " at " + timeDiag);
				
				/*LinearLayout ll=new LinearLayout(mActivity);
			        ll.setOrientation(LinearLayout.VERTICAL);
			        ll.addView(roomPic);*/
				String diagTitle = "Room: " + roomDiag + " at " + timeDiag + "?";
				RoomFragmentDialog roomFragDia = RoomFragmentDialog.newInstance(roomDiag, diagTitle 
						, pageNumberInt
						, linkString
						, shareRow
						, shareColumn
						);
				FragmentManager fragMan = mActivity.getSupportFragmentManager();
				roomFragDia.show(fragMan, null);
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(mActivity, "Invalid QR Code Information", Toast.LENGTH_LONG).show();;
			}
			if(progDialogQRCode != null && progDialogQRCode.isShowing()){
				progDialogQRCode.dismiss();
			}
    	}
    }

    @Subscribe
    public void LinkedCalendarDialogsClosed(LinkedCalendarDialogsClosedEvent event){
        Timber.i("Dialog has been closed");
        if(isRefreshWaiting){
            Timber.i("isRefreshWaiting is true, doing a delayed Refresh");
            ActionBar actionBar = getSupportActionBar();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            tabNumber = actionBar.getSelectedTab().getPosition();
            Timber.i("Saving Currently Selected Tab : " + String.valueOf(tabNumber));
            mSectionsPagerAdapter.notifyDataSetChanged();
            actionBar.removeAllTabs();
            for (int i = 0; i < calendarCache.size(); i++) {
                // Create a tab with text corresponding to the page title defined by
                // the adapter. Also specify this Activity object, which implements
                // the TabListener interface, as the callback (listener) for when
                // this tab is selected.
                actionBar.addTab(
                        actionBar.newTab()
                                .setText(mSectionsPagerAdapter.getPageTitle(i))
                                .setTabListener(this));
            }
            YoYo.with(Techniques.FadeIn).duration(1000).playOn(mViewPager);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(tabNumber > -1 && tabNumber < calendarCache.size()){
                        Timber.i("Setting the tab to " + tabNumber);
                        mViewPager.setCurrentItem(tabNumber, false);
                    }
                }
            }, 100);
            isRefreshWaiting = false;
        }

    }
    @Override
    public void ClearResetIcon(){
    	if(refreshItem !=null){
            //refreshItem.setEnabled(true);
            refreshItem.setActionView(null);
        }
        Timber.i("RefreshIcon Cleared");

    }
    



	@Override
	public void ChangeScrollPosition(int firstVisibleItem, int scrollTarget, float ycoord) {
			//Log.i(TAG, "i'm here");
    		Calendar_Generic_Page_Fragment fragmentPage = (Calendar_Generic_Page_Fragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + String.valueOf(scrollTarget));
    		Calendar_Generic_ListView_Fragment currentListView = (Calendar_Generic_ListView_Fragment) fragmentPage.getChildFragmentManager().findFragmentByTag("calendar" + String.valueOf(scrollTarget) + "listview");
    		currentListView.changeTheScroll(firstVisibleItem, ycoord);

		
	}
	@Override
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
		

		
	}

    /**
     * Starts a refresh event but does not cancel if it already running. If a refresh is already
     * running, then this will just return and nothing will happen. This is useful if you want to
     * ensure the current refresh is current. If you want to emulate the user clicking the button
     * and respect the current refresh state(ie. cancel if already running) then see function below
     * @see this#handleRefreshClick()
     */
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
            //Need this for QRCode event
            isForQRCode = false;
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

	@Override
	protected void onResume() {
		isFront = true;
		super.onResume();
	}
	

	@Override
	protected void onPause() {
		isFront = false;
		super.onPause();
	}

	@Override
	protected void onRestart() {
		Bundle intentExtras = getIntent().getExtras();
        if(intentExtras == null){
        	
        }
        else if(intentExtras.getIntArray("qrCode") != null){
        	int[] qrInfo = intentExtras.getIntArray("qrCode");
        	//qrInfo [null, row, column, page]
        	shareRow = qrInfo[0];
        	shareColumn = qrInfo[1];
        	pageNumberInt = qrInfo[2];
        	getIntent().removeExtra("qrCode");
        	isForQRCode = true;
        	progDialogQRCode = new ProgressDialog(this);
        	progDialogQRCode.setTitle("QR Receive");
        	progDialogQRCode.setMessage("Performing Refresh");
        	progDialogQRCode.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        	progDialogQRCode.setCancelable(true);
        	progDialogQRCode.setOnCancelListener(new OnCancelListener(){

				@Override
				public void onCancel(DialogInterface diag) {
					if(mCalendarRefresher != null){
						mCalendarRefresher.cancel(true);
						if(isFront){
							ClearResetIcon();
						}
						mCalendarRefresher = null;
					
					}
					isForQRCode = false;
				}
        	});
        	progDialogQRCode.show();
            DoRefresh();
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
	protected void onDestroy() {
		if(mCalendarRefresher != null){
			mCalendarRefresher.cancel(true);
		}
        if(mLoginAsyncTask != null){
            mLoginAsyncTask.cancel(true);
        }
        OttoBusSingleton.getInstance().unregister(this);
		super.onDestroy();
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
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
        hasManuallyRefreshedSinceOpeningActivity = false;
        Timber.i("Main Activity: onStart()");
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SHARED_PREF_HASLEARNED_MYACCOUNT, false)){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!isDialogShowing && isFront && !hasManuallyRefreshedSinceOpeningActivity){
                        DoRefresh();
                    }

                }
            }, AUTO_REFRESH_DELAY);
        }
		
		
	}
	
    @Override
	protected void onStop() {
        Timber.i("Main Activity: onStop()");
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        isRefreshWaiting = false;
		super.onStop();
	}
    
    public class SimpleEula {

        private String EULA_PREFIX = "eula_";
        private Activity mActivity;

        public SimpleEula(Activity context) {
            mActivity = context;
        }

        private PackageInfo getPackageInfo() {
            PackageInfo pi = null;
            try {
                pi = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), PackageManager.GET_ACTIVITIES);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return pi;
        }

    }


    @Subscribe
    public void toggleActionBarVisibility(ToggleActionBarVisibilityEvent event){
        if(event.hideTheActionBar){
            getSupportActionBar().setNavigationMode((ActionBar.NAVIGATION_MODE_STANDARD));
        }
        else{
            getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        }
    }

    @Subscribe
    public void LoginResults(MyAccountLoginResultEvent event) {
        this.errorMessageFromLogin = event.errorMessage;
    }


    @Subscribe
    public void executeLogin(MyAccountLoginTaskStart event){
        if(mLoginAsyncTask == null){
            Timber.i("mLoginAsyncTask is null, will create/execute new one");
            mLoginAsyncTask = new LoginAsynkTask(this);
            mLoginAsyncTask.options = event.options;
            mLoginAsyncTask.execute(event.loginInput);
        }
        else{
            Timber.e(new IllegalStateException(), "mLoginTask was not null and executeLogin fired from the fragment");
        }

    }

    /**
     * starts the action bar bouncy animation. you can stop it by calling
     * myMenuItem.setActionView(null). Inflate, animate then set for actionbar icons.
     * @param iv View to be animated.
     */
    private void startActionBarBounceAnimation(View iv){
        final ObjectAnimator animY = ObjectAnimator.ofFloat(iv, "translationY", 0f, -20f);
        animY.setDuration(125);
        animY.setInterpolator(new DecelerateInterpolator());
        animY.setRepeatCount(1);
        animY.setStartDelay(1000);
        animY.addListener(new Animator.AnimatorListener() {
            int repeatCount = 0;
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (repeatCount < 5) {
                    repeatCount++;
                    animY.setStartDelay(750);
                    animY.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animY.setRepeatMode(ObjectAnimator.REVERSE);
        animY.start();
    }
    public static class DiaFragHelp extends DialogFragment {
        Long helpDialogOpenDuration = 0L;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Timber.i("Help Dialog Created");
            helpDialogOpenDuration = System.currentTimeMillis();
            View rootView = inflater.inflate(R.layout.diafrag_help, container, false);
            Button okButton = (Button) rootView.findViewById(R.id.help_ok_button);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getDialog().dismiss();

                }
            });
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
            getDialog().setTitle("Help");
            getDialog().getWindow()
                    .getAttributes().windowAnimations = R.style.ActionBarIconDialogAnimation;

            return rootView;
        }

        @Override
        public void onStop() {
            long helpDuration = helpDialogOpenDuration - System.currentTimeMillis();
            Tracker t = ((UOITLibraryBookingApp)getActivity().getApplication()).getTracker();
            t.send(new HitBuilders.EventBuilder()
                    .setCategory("Calendar Home")
                    .setAction("HelpDialog")
                    .setLabel("Help Dialog Open For")
                    .setValue(helpDuration)
                    .build());
            super.onStop();
        }

        @Override
        public void onDestroy() {
            Timber.i("Help Dialog Destroyed");
            super.onDestroy();
        }
    }
    /**
     * Takes care of creating the MyAccount Fragment, disabling the actionview if required, and
     * setting the correct sharedPref values
     */
    private void handleMyAccountClick(){
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
        if(!defaultPreferences.getBoolean(SHARED_PREF_HASLEARNED_MYACCOUNT, false)){
            Timber.i("User has learned My Account");
            long firstMyAccountDelay = activityStartTime - System.currentTimeMillis();
            t.send(new HitBuilders.EventBuilder()
                    .setCategory("Calendar Home")
                    .setAction("First Time My Account Pressed")
                    .setValue(firstMyAccountDelay)
                    .build());
            defaultPrefsEditor.putBoolean(SHARED_PREF_HASLEARNED_MYACCOUNT, true)
                    .commit();
        }


        currentFrag.show(fragMan, MY_ACCOUNT_DIALOGFRAGMENT_TAG);
    }

    /**
     * Displays the help dialog
     */
    private void handleHelpClick(){
        Timber.i("User Clicked Help Dialog");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(!sharedPreferences.getBoolean(SHARED_PREF_HAS_LEARNED_HELP, false)){
            long firstHelpClickDelay = activityStartTime - System.currentTimeMillis();
            t.send(new HitBuilders.EventBuilder()
                    .setCategory("Calendar Home")
                    .setAction("HelpDialog")
                    .setLabel("First Time Help Pressed")
                    .setValue(firstHelpClickDelay)
                    .build());
            sharedPreferences.edit().putBoolean(SHARED_PREF_HAS_LEARNED_HELP, true).commit();
        }
        FragmentManager fragMan = getSupportFragmentManager();
        DiaFragHelp currentFrag = new DiaFragHelp();
        currentFrag.show(fragMan, HELP_DIALOGFRAGMENT_TAG);
    }

    /**
     * Emulates the user clicking. Will check if refresh is already happening and will cancel it if
     * it is. If you want to do a refresh without this check then out DoRefresh()
     * @see this#DoRefresh()
     */
    private void handleRefreshClick(){
        hasManuallyRefreshedSinceOpeningActivity = true;
        if(mCalendarRefresher != null){
            Timber.i("Refresh Canceled by User");
            //TODO check to make sure this is desired, Should i wait for cancel to return or just assume it succeeded
            if(mCalendarRefresher.cancel(true)){
                mCalendarRefresher = null;
                if(isFront){
                    ClearResetIcon();
                }
            }
        }
        else{
            DoRefresh();
        }

        if(!defaultPreferences.getBoolean(SHARED_PREF_HASLEARNED_MYACCOUNT, false) && myAccountItem != null){
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
        }
        if(!defaultPreferences.getBoolean(hasLEARNED_REFRESH, false)){
            Timber.i("User has learned Refresh");
            long firstRefreshDelay = activityStartTime - System.currentTimeMillis();
            t.send(new HitBuilders.EventBuilder()
                    .setCategory("Calendar Home")
                    .setAction("First Time Refresh Pressed")
                    .setValue(firstRefreshDelay)
                    .build());
             defaultPrefsEditor.putBoolean(hasLEARNED_REFRESH, true)
                    .commit();
        }

    }
    public void displayMyAccountHint(){
        Timber.i("Displaying My Account Bounce Hint because user didn't log in when trying to book");
        t.send(new HitBuilders.EventBuilder()
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
    }
}
