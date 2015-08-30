package com.objectivetruth.uoitlibrarybooking;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ActivityRoomInfo extends ActivityBase {
	private final String TAG = "MyBookingsActivity";
	public static DbHelper mdbHelper;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private static String[] roomArr;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] menuItems;
	
	static SharedPreferences defaultPreferences;
	static SharedPreferences.Editor defaultPrefsEditor;
	
	final int ACTIVITYPAGENUMBER = 4;
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		mdbHelper = MainActivity.mdbHelper;
		setContentView(R.layout.room_info);
		defaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		defaultPrefsEditor = defaultPreferences.edit();
		
/*		if(defaultPreferences.getBoolean("firstLaunchRooms", true)){
			roomArr = mdbHelper.ROOMSMASTERLIST;
			new RoomInfoRefresher(this).execute(roomArr);

		}
		else{			
			
			roomArr = mdbHelper.ROOMSMASTERLIST;
		}*/
		roomArr = mdbHelper.ROOMSMASTERLIST;
		
		RoomPickerFragment throwawayFrag = new RoomPickerFragment();
		RoomPickerFragment mRoomPickerFragment = throwawayFrag.newInstance();
		FragmentManager fragman = getSupportFragmentManager();
		fragman.beginTransaction().replace(R.id.roominfo_frame, mRoomPickerFragment, "roompickerfragment")
			.commit();
		
		mTitle = "Room Info";
		mDrawerTitle = getTitle();
        menuItems = getResources().getStringArray(R.array.menuItems);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_roominfo);
        mDrawerList = (ListView) findViewById(R.id.left_drawer_roominfo);

        // set a custom shadow that overlays the main content when the drawer
        // opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new DrawerListAdapter(this,
                R.layout.drawer_list_item, menuItems, ACTIVITYPAGENUMBER, this));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(mTitle);

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
                        getActionBar().setTitle(mTitle);
                        invalidateOptionsMenu(); // creates call to
                                                 // onPrepareOptionsMenu()
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        getActionBar().setTitle(mDrawerTitle);
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
        	
        	Log.i(TAG, "Refresh Started!");
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
    
    
    
    
    
    
    
    @SuppressLint("ValidFragment") public class RoomPickerFragment extends Fragment{
    	private final String TAG = "RoomPickerFragment";
    	ListView listView;
    	
    	public RoomPickerFragment newInstance(){
    		RoomPickerFragment fragment = new RoomPickerFragment();
			
    		return fragment;
    		
    	}
    	
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

				final ListView listView = (ListView)inflater.inflate(R.layout.room_picker_listview, container, false);
		        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, roomArr);
		        listView.setAdapter(adapter);
		        listView.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> parent,
							View view, int position, long id) {
						String selectedFromList =(String) (listView.getItemAtPosition(position));
						
						RoomFragment throwawayFrag = new RoomFragment();
						RoomFragment realFrag = throwawayFrag.newInstance(selectedFromList);
						FragmentManager fragman = getSupportFragmentManager();
						fragman.beginTransaction().replace(R.id.roominfo_frame, realFrag, "roompickerfragment")
							.addToBackStack(null)
							.commit();
						Tracker t = ((UOITLibraryBookingApp) getApplication()).getTracker();
						t.send(new HitBuilders.EventBuilder()
							.setCategory("Room Info")
							.setAction(selectedFromList)
							.build()
							);
						
					}
		        	
		        	
		        	
		        	
		        });
				return listView;
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
		}

    }
    
    
    
    
    
    
    @SuppressLint("ValidFragment") public class RoomFragment extends Fragment{
    	private final String TAG = "RoomFragment";
    	ListView listView;
    	String roomNameString;
    	
    	public RoomFragment newInstance(String room){
    		RoomFragment frag = new RoomFragment();
			frag.roomNameString = room;
    		return frag;
    		
    	}
    	
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

				View rootView = inflater.inflate(R.layout.room_landing, container, false);
				
				//final BitmapFactory.Options options = new BitmapFactory.Options();
				final TextView roomName = (TextView) rootView.findViewById(R.id.room_landing_top_title);
				final TextView floorNumber = (TextView) rootView.findViewById(R.id.room_landing_floor_number);
				final TextView seatingCap = (TextView) rootView.findViewById(R.id.room_landing_seating_capacity);
				final TextView minBookers = (TextView) rootView.findViewById(R.id.room_landing_min_bookers);
				final TextView MaxTime = (TextView) rootView.findViewById(R.id.room_landing_max_time);
				final TextView comments = (TextView) rootView.findViewById(R.id.room_landing_comments);
				final ImageView libpic = (ImageView) rootView.findViewById(R.id.room_landing_room_picture);

				SQLiteDatabase db = mdbHelper.getReadableDatabase();
				Cursor c = db.query(mdbHelper.ROOMS_TABLE_NAME, null, null, null, null, null, null, null);
				while(c.moveToNext()){
					//Log.i(TAG, c.getString(c.getColumnIndex(mdbHelper.ROOM_NAME)));
					if(c.getString(c.getColumnIndex(mdbHelper.ROOM_NAME)).compareTo(roomNameString) == 0){
						
						roomName.setText(c.getString(c.getColumnIndex(mdbHelper.ROOM_NAME)));
						floorNumber.setText(c.getString(c.getColumnIndex(mdbHelper.FLOOR)));
						seatingCap.setText(c.getString(c.getColumnIndex(mdbHelper.SEATING_CAP)));
						minBookers.setText(c.getString(c.getColumnIndex(mdbHelper.MIN_BOOKERS)));
						MaxTime.setText(c.getString(c.getColumnIndex(mdbHelper.MAX_TIME)));
						//Bitmap bm = LoadImageFromStorage(c.getString(c.getColumnIndex(mdbHelper.IMAGEFILE)));
						
						libpic.setImageResource(getResources().getIdentifier(roomNameString.toLowerCase(),"drawable",  getPackageName()));
						
						
						comments.setText(c.getString(c.getColumnIndex(mdbHelper.COMMENT)));
					}
				}
				c.close();

				
				return rootView;
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
		}

    }
	
    private Bitmap LoadImageFromStorage(String path){
    	String TAG = "LoadImageFromStorage";
    	Bitmap b = null;
        try {
            File f=new File(path);
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        } 
        catch (FileNotFoundException e)
        {
        	Log.i(TAG, e.toString());
            e.printStackTrace();
        }
        return b;
        

    }
	@Override
	protected void onStart() {
		super.onStart();

		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}
	
    @Override
	protected void onStop() {
		super.onStop();
    	GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}
	
	
	
	
}
