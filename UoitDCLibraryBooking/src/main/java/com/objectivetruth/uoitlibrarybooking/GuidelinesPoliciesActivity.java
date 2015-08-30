package com.objectivetruth.uoitlibrarybooking;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.google.android.gms.analytics.GoogleAnalytics;

public class GuidelinesPoliciesActivity extends ActivityBase {
	//private final String TAG = "GuideLinesPoliciesActivity";
	//public static DbHelper mdbHelper;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] menuItems;
	static SharedPreferences defaultPreferences;
	static SharedPreferences.Editor defaultPrefsEditor;
	//Tracker t;
	//Worker mWorker;
	//KenBurnsView mKenBurnsView;
	//Activity mActivity;
	final int ACTIVITYPAGENUMBER = 2;
	
	@Override
	protected void onCreate(Bundle arg0) {
    //		mdbHelper = MainActivity.mdbHelper;
        super.onCreate(arg0);
		

        setContentView(R.layout.guidelines_policies);
		//mKenBurnsView = (KenBurnsView) findViewById(R.id.students_background);
/*
		defaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		defaultPrefsEditor = defaultPreferences.edit();*/
	//	ArrayList<String> guidelinesPolicies = new ArrayList<String>();
		
		//mActivity = this;
/*		guidelinesPolicies.add("Booking Guidelines");
		guidelinesPolicies.add(
		    "1. Your student ID can be used only once per day to a maximum of 20 bookings per term for group study room bookings.\n\n" +
		    "2. Bookings are available for a maximum of 2 hours per day.\n\n" +
		    "3. Bookings can be made one day in advance.\n\n" +
		    "4. Bookings for tomorrow will open at 10:00 A.M. today.\n\n" +
		    "5. Booking cannot be made or changed five minutes before the start time.\n\n" +
		    "6. When a group completes a booking, all incomplete reservations associated with the same time/room are voided. If your reservation has been voided, you may re-use your student ID to reserve or complete a booking for the same day.\n\n" +
		    "7. Your Library Account must be in good standing in order to reserve a room.\n\n" +
		    "8. The booking system will not accept passwords longer than 10 characters and all characters must be alphanumeric.\n\n"); 
		guidelinesPolicies.add("Room Use Guidelines");
		guidelinesPolicies.add(
			"1. It is the intent of the system to set the maximum for a single group's bookings to 4 hours/day. Attempts to monopolize group study rooms will be considered a breach of the Building Use Guideline. The library reserves the right to cancel bookings and /or suspend booking privileges in violation of this, or any other library guideline.\n\n" +
		    "2. Bookings may be forfeited for rooms occupied by fewer than the required number of persons to book the room.\n\n" +  
		    "3. If a room is vacant after 15 minutes of booking time, the booking is void and the room is free for use until the next reservation.\n\n" +
		    "4. Students using the group study rooms are responsible for any damage that may occur.\n\n" +
		    "5. Eating in group study rooms may result in suspension of group study room booking privileges.\n\n" +
		    "6. Photo ID is required for the loan of whiteboard markers and Ethernet cables from the Circulation desk.\n\n" +
		    "7. The rooms are intended for organized groups'; individuals should not join a group unless they have a prior affiliation with that group - the original booker is the owner of the booking and can ask any joiners to leave the booking or the room.\n\n\n");
        com.objectivetruth.uoitlibrarybooking.RobotoTextView titleWord1 = (com.objectivetruth.uoitlibrarybooking.RobotoTextView) findViewById(R.id.guidelines_policies_landing_title_word1);
		com.objectivetruth.uoitlibrarybooking.RobotoTextView paragraph1 = (com.objectivetruth.uoitlibrarybooking.RobotoTextView) findViewById(R.id.guidelines_policies_landing_para1);
		com.objectivetruth.uoitlibrarybooking.RobotoTextView titleWord2 = (com.objectivetruth.uoitlibrarybooking.RobotoTextView) findViewById(R.id.guidelines_policies_landing_title_word2);
		com.objectivetruth.uoitlibrarybooking.RobotoTextView paragraph2 = (com.objectivetruth.uoitlibrarybooking.RobotoTextView) findViewById(R.id.guidelines_policies_landing_para2);
		
		//Log.i(TAG, guidelinespolicies.toString());
		titleWord1.setText(guidelinesPolicies.get(0));
		paragraph1.setText(guidelinesPolicies.get(1));
		titleWord2.setText(guidelinesPolicies.get(2));
		paragraph2.setText(guidelinesPolicies.get(3));*/
		
		
		
		
		mTitle = "Policies Guidelines"; 
		mDrawerTitle = getTitle();
        menuItems = getResources().getStringArray(R.array.menuItems);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_guidelines_policies);
        mDrawerList = (ListView) findViewById(R.id.left_drawer_guidelinespolicies);

        // set a custom shadow that overlays the main content when the drawer
        // opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new DrawerListAdapter(this,
                R.layout.drawer_list_item, menuItems, ACTIVITYPAGENUMBER, this));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(mTitle);
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
                        getSupportActionBar().setTitle(mTitle);
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
    
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            
        	selectItem(position);
            
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
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
    
    /*public class Worker implements Target{

		@Override
		public void onBitmapFailed(Drawable arg0) {
			//Do nothing
		}

		@Override
		public void onBitmapLoaded(Bitmap bitmap, LoadedFrom loadedFrom) {
			final ViewFlipper mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
			mKenBurnsView.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
			mViewFlipper.getInAnimation().setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {}
                public void onAnimationRepeat(Animation animation) {}
                public void onAnimationEnd(Animation animation) {
                	if(mViewFlipper!=null){
                		mViewFlipper.setBackgroundResource(0);	
                	}
                	        
                }
             });
			mViewFlipper.setDisplayedChild(1);
			mKenBurnsView.resume();
			*//*if(loadedFrom == loadedFrom.DISK){

				Drawable[] layers = new Drawable[2];
				layers[0] = placeholderView.getDrawable();
				layers[1] = new BitmapDrawable(getResources(), bitmap);

				TransitionDrawable transitionDrawable = new TransitionDrawable(layers);
				placeholderView.setImageDrawable(transitionDrawable);
				transitionDrawable.startTransition(1000);
	
			}
			else{
				placeholderView.setImageBitmap(bitmap);
			}
						
			KenBurnsView mKenBurnsView = new KenBurnsView(mActivity);
	        
	        ViewGroup parent = (ViewGroup) placeholderView.getParent();
	        int index = parent.indexOfChild(placeholderView);
	        parent.removeView(placeholderView);
	        mKenBurnsView.setImageResource(R.drawable.background_about2);
	        parent.addView(mKenBurnsView, index);*//*
			
		}*/
		
		/*@Override
		public void onPrepareLoad(Drawable arg0) {
			//Do Nothing
		}
			
	}*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
        	Intent intent = new Intent(this, ActivitySettings.class);
        	startActivity(intent);
        }
        else if(id == R.id.refresh_calendar){
        	
        	//Log.i(TAG, "Refresh Started!");
        	//(new CalendarRefresher(this)).execute();
        	View refresh_button = this.findViewById(R.id.refresh_calendar);
        	refresh_button.setEnabled(false);
            return true;
            
            /*View v = mViewPager.getChildAt(mViewPager.getCurrentItem());
        	v.
        	FragmentManager fragmentManager = getSupportFragmentManager();
        	Calendar2_GridView_Fragment frag2 = (Calendar2_GridView_Fragment) fragmentManager.findFragmentByTag("calendar2gridview");
        	if (frag2 ==null) {Log.i(TAG, "GOOFED");}
        	else{
        		frag2.refreshGridView();
        	}
        	*/
        }
        else if(mDrawerToggle.onOptionsItemSelected(item)) {
	           return true;
        }

        return super.onOptionsItemSelected(item);
    }
	@Override
	protected void onStart() {
		super.onStart();
		//mKenBurnsView.resume();
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}
	
    @Override
	protected void onStop() {
		super.onStop();
		//mKenBurnsView.pause();
    	GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}
}
