package com.objectivetruth.uoitlibrarybooking;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.*;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.objectivetruth.uoitlibrarybooking.Calendar_Generic_Page_Fragment.RoomFragmentDialog;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.userinterface.ActivityBase;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.whatsnew.WhatsNewDialog;
import com.squareup.otto.Subscribe;
import timber.log.Timber;

import javax.inject.Inject;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;

import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCES_KEYS.SHARED_PREF_HASLEARNED_MYACCOUNT;
import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCES_KEYS.SHARED_PREF_HAS_LEARNED_HELP;


public class MainActivity extends ActivityBase implements ActionBar.TabListener, AsyncResponse{
    final static private String ACTIVITY_TITLE = "Calendar";
    final static private int ACTIVITYPAGENUMBER = 0;

    public static final String MY_ACCOUNT_DIALOGFRAGMENT_TAG = "myAccountDiaFrag";
    public static final String PASSWORD_INFO_DIALOGFRAGMENT_TAG = "passwordInfoDiaFrag";
    public static final String GROUP_CODE_DIALOGFRAGMENT_TAG = "groupCodeInfoDiaFrag";
    public static final int MAX_BOOKINGS_ALLOWED = 20; //This can safely be changed
    private static final long AUTO_REFRESH_DELAY = 6000;
    private static final String HELP_DIALOGFRAGMENT_TAG = "helpDiaFrag";
    public static boolean isDialogShowing = false;
    private final String TAG = "MainActivity";
    public long activityStartTime = System.currentTimeMillis();
	AppCompatActivity mActivity = this;
	public static DbHelper mdbHelper;
    private boolean isRefreshWaiting = false;
    public static String hasLEARNED_REFRESH = "has_learned_refresh";
	public static CookieManager cookieManager;
	MenuItem refreshItem;
    MenuItem myAccountItem;
	@Inject SharedPreferences mDefaultSharedPreferences;
	@Inject SharedPreferences.Editor mDefaultSharedPreferencesEditor;
    @Inject Tracker googleAnalyticsTracker;
	View refresh_button;
	static CalendarRefresher mCalendarRefresher = null;
    static LoginAsynkTask mLoginAsyncTask = null;
	boolean isFront = false;
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
	int shareRow = 0;
	int shareColumn = 0;
	int pageNumberInt = 0;
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
	public ArrayList<CalendarMonth> calendarCache = null;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

        ((UOITLibraryBookingApp) getApplication()).getComponent().inject(this);

        configureAndSetupLayoutAndDrawer(
                R.layout.activity_main,
                R.id.drawer_layout,
                R.id.left_drawer,
                R.id.toolbar);

/*
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
                googleAnalyticsTracker.send(new HitBuilders.EventBuilder()
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

        OttoBusSingleton.getInstance().register(this);

*/

    }

    @Override
    protected int getActivityPageNumber() {
        return ACTIVITYPAGENUMBER;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
    	if(BuildConfig.DEBUG){
        	getMenuInflater().inflate(R.menu.calendar_action_icons_menu_debug, menu);
        }
        else{
        	getMenuInflater().inflate(R.menu.calendar_action_icons_menu, menu);
        }
    	//getMenuInflater().inflate(R.menu.calendar_action_icons_menu_debug, menu);
        refreshItem = menu.findItem(R.id.refresh_calendar);
        myAccountItem = menu.findItem(R.id.user_account);

        if(!mDefaultSharedPreferences.getBoolean(hasLEARNED_REFRESH, false)){

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
        if(mDefaultSharedPreferences.getBoolean(hasLEARNED_REFRESH, false) && !mDefaultSharedPreferences.getBoolean(SHARED_PREF_HASLEARNED_MYACCOUNT, false)){
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
            googleAnalyticsTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Calendar Home")
                            .setAction("HelpDialog")
                            .setLabel("Pressed by User")
                            .build()
            );
            handleHelpClick();
            return true;
        }
        else if(id == R.id.user_account){
            googleAnalyticsTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Calendar Home")
                            .setAction("MyAccount")
                            .setLabel("Pressed by User")
                            .build()
            );
            handleMyAccountClick();
            return true;
        }
        else if(id == R.id.refresh_calendar){
            googleAnalyticsTracker.send(new HitBuilders.EventBuilder()
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
        // If the item that was clicked is in the drawer then do its workflow
        else {
            getActionBarDrawerToggle().onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
    	//Log.i(TAG, "tab selected: " + tab.getPosition());
        //mViewPager.setCurrentItem(tab.getPosition());
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
        hasManuallyRefreshedSinceOpeningActivity = false;
        Timber.i("Main Activity: onStart()");
        if(mDefaultSharedPreferences.getBoolean(SHARED_PREF_HASLEARNED_MYACCOUNT, false)){
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
        isRefreshWaiting = false;
		super.onStop();
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

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Timber.i("Help Dialog Created");
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
        if(!mDefaultSharedPreferences.getBoolean(SHARED_PREF_HASLEARNED_MYACCOUNT, false)){
            Timber.i("User has learned My Account");
            long firstMyAccountDelay = activityStartTime - System.currentTimeMillis();
            googleAnalyticsTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Calendar Home")
                    .setAction("First Time My Account Pressed")
                    .setValue(firstMyAccountDelay)
                    .build());
            mDefaultSharedPreferencesEditor.putBoolean(SHARED_PREF_HASLEARNED_MYACCOUNT, true)
                    .commit();
        }


        currentFrag.show(fragMan, MY_ACCOUNT_DIALOGFRAGMENT_TAG);
    }

    /**
     * Displays the help dialog
     */
    private void handleHelpClick(){
        Timber.i("User Clicked Help Dialog");
        if(!mDefaultSharedPreferences.getBoolean(SHARED_PREF_HAS_LEARNED_HELP, false)){
            long firstHelpClickDelay = activityStartTime - System.currentTimeMillis();
            googleAnalyticsTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Calendar Home")
                    .setAction("HelpDialog")
                    .setLabel("First Time Help Pressed")
                    .setValue(firstHelpClickDelay)
                    .build());
            mDefaultSharedPreferencesEditor.putBoolean(SHARED_PREF_HAS_LEARNED_HELP, true).commit();
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

        if(!mDefaultSharedPreferences.getBoolean(SHARED_PREF_HASLEARNED_MYACCOUNT, false) && myAccountItem != null){
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
        if(!mDefaultSharedPreferences.getBoolean(hasLEARNED_REFRESH, false)){
            Timber.i("User has learned Refresh");
            long firstRefreshDelay = activityStartTime - System.currentTimeMillis();
            googleAnalyticsTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Calendar Home")
                    .setAction("First Time Refresh Pressed")
                    .setValue(firstRefreshDelay)
                    .build());
             mDefaultSharedPreferencesEditor.putBoolean(hasLEARNED_REFRESH, true)
                    .commit();
        }

    }
    public void displayMyAccountHint(){
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
    }
}
