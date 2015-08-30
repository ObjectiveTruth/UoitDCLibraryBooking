package com.objectivetruth.uoitlibrarybooking;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;


public class MyBookingsActivity extends ActivityBase implements CommunicatorLoginAsync{
	private final String TAG = "MyBookingsActivity";
	public static DbHelper mdbHelper;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] menuItems;
	static SharedPreferences defaultPreferences;
	static SharedPreferences.Editor defaultPrefsEditor;
	ViewFlipper mViewFlipper = null;
	
	ViewPagerFragment mViewPagerFragment = null;
	Activity mActivity;
	static CustomViewPagerAdapter mViewPagerAdapter;
	ImageView backgroundImage;
	final int ACTIVITYPAGENUMBER = 1;
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		mdbHelper = MainActivity.mdbHelper;
	
		super.onCreate(arg0);
		setContentView(R.layout.my_bookings);

		
		backgroundImage = (ImageView) findViewById(R.id.landscape);
		mActivity = this;/*
		Random r = new Random();
		mViewFlipper.setDisplayedChild(r.nextInt(3));
		RandomTransitionGenerator generator = new RandomTransitionGenerator(15000, new LinearInterpolator());
		for(int i = 1; i < mViewFlipper.getChildCount(); i ++){
			kbvTemp = (KenBurnsView)mViewFlipper.getChildAt(i);
			kbvTemp.setTransitionGenerator(generator);
		}*/

		defaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		defaultPrefsEditor = defaultPreferences.edit();
		
		LoginFragment mLoginFragment = LoginFragment.newInstance();
		FragmentManager fragman = getSupportFragmentManager();
		fragman.beginTransaction().replace(R.id.mybookings_frame, mLoginFragment, "loginfragment")
			.commit();
		
		mTitle = "Account"; 
		mDrawerTitle = getTitle();
        menuItems = getResources().getStringArray(R.array.menuItems);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_mybookings);
        mDrawerList = (ListView) findViewById(R.id.left_drawer_mybookings);

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
    
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//mKenBurnsView.pause();
	}
    
    
    
    public static class LoginFragment extends Fragment{
    	
    	private final String TAG = "LoginFragment";
    	TextView errorTextView;
    	CheckBox rememberMe;
    	EditText usernameField;
    	EditText passwordField;
    	
    	
		public static LoginFragment newInstance(){
    		LoginFragment fragment = new LoginFragment();
			
    		return fragment;
    		
    	}
    	public void ErrorMessage(String errorMessage){
    		errorTextView.setText(errorMessage);
    		
    	}
    	
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

				View v = inflater.inflate(R.layout.my_bookings_login, container, false);
		        
				return v;
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);

			
			
			usernameField= (EditText)getActivity().findViewById(R.id.editTextUserNameToLogin);
			passwordField= (EditText)getActivity().findViewById(R.id.editTextPasswordToLogin);
			Button signInButton = (Button) getActivity().findViewById(R.id.buttonSignIn);
			errorTextView = (TextView) view.findViewById(R.id.error_textview);
			errorTextView.setGravity(Gravity.CENTER);
			rememberMe = (CheckBox) view.findViewById(R.id.login_remember_me);
			rememberMe.setChecked(defaultPreferences.getBoolean("saveLogin", false));
	        if (rememberMe.isChecked() == true) {
	            usernameField.setText(defaultPreferences.getString("username", ""));
	            passwordField.setText(defaultPreferences.getString("password", ""));
	            
	        }
			rememberMe.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {

					//Log.i(TAG, "Chcked is" + ((CheckBox) arg0).isChecked());
					String[] loginInput = new String[2];
					loginInput[0] = usernameField.getText().toString();
					loginInput[1] = passwordField.getText().toString();
		            if (rememberMe.isChecked()) {
		                defaultPrefsEditor.putBoolean("saveLogin", true);
		                defaultPrefsEditor.putString("username", loginInput[0]);
		                defaultPrefsEditor.putString("password", loginInput[1]);
		                defaultPrefsEditor.commit();
		            } else {
		                defaultPrefsEditor.clear();
		                defaultPrefsEditor.commit();
		            }


					
				}
				
			});
			signInButton.setOnClickListener(new OnClickListener(){
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
				public void onClick(View arg0) {

						String[] loginInput = new String[2];
						errorTextView.setText("");
						loginInput[0] = usernameField.getText().toString();
						loginInput[1] = passwordField.getText().toString();
			            if (rememberMe.isChecked()) {
			                defaultPrefsEditor.putBoolean("saveLogin", true);
			                defaultPrefsEditor.putString("username", loginInput[0]);
			                defaultPrefsEditor.putString("password", loginInput[1]);
			                defaultPrefsEditor.commit();
			            } else {
			                defaultPrefsEditor.clear();
			                defaultPrefsEditor.commit();
			            }
						
			        	if(isNetworkAvailable(getActivity())){
			        		new LoginAsynkTask(getActivity()).execute(loginInput);
			        	}
			        	else{
			        		new AlertDialog.Builder(getActivity())
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
		}




    	
    	
    }
    public static class ViewPagerFragment extends Fragment{
    	ViewPager mViewPager;
    	
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
				View v = inflater.inflate(R.layout.mybookings_viewpager, null);
				mViewPager = (ViewPager) v.findViewById(R.id.viewpager);


				mViewPager.setAdapter(mViewPagerAdapter);
			//return super.onCreateView(inflater, container, savedInstanceState);
				return v;
		}
  	
    }
    public class CustomViewPagerAdapter extends FragmentPagerAdapter {
        public CustomViewPagerAdapter(FragmentManager fm) {
			super(fm);

		}
        
        @Override
        public CharSequence getPageTitle(int position) {
        	switch (position) {
	            case 0:  return "Confirmed";
				case 1:  return "Pending";
	            default:  return "Past";
        	}
        	}
        
        @Override
        public int getCount() {
            return 3;
        }
        
        @Override
        public int getItemPosition(Object object) {
           return POSITION_NONE;
        }
        
        @Override
        public Fragment getItem(int position) {
        	switch (position) {
            case 0:  return new Tab_Complete();
			case 1:  return new Tab_Incomplete();
            default:  return new Tab_Past();
            
        	}
        }
    }

/*    public static class FragmentTabsFragmentSupport extends Fragment {
        private FragmentTabHost mTabHost;
        static String TAG = "FragmentTAbFragmentSupport";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            mTabHost = new FragmentTabHost(getActivity());
            mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realTabFrame);
            
            Log.i(TAG, "here");

            mTabHost.addTab(mTabHost.newTabSpec("pending").setIndicator("Pending"),
                    Tab_Incomplete.class, null);
            mTabHost.addTab(mTabHost.newTabSpec("complete").setIndicator("Completed"),
                    Tab_Complete.class, null);
            mTabHost.addTab(mTabHost.newTabSpec("past").setIndicator("Past"),
                    Tab_Past.class, null);
            //mTabHost.setBackground(getActivity().getResources().getDrawable(R.id.dialog));
            return mTabHost;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            mTabHost = null;
        }
        public static FragmentTabsFragmentSupport newInstance(){
        	FragmentTabsFragmentSupport fragment = new FragmentTabsFragmentSupport();
        	return fragment;
        }
    }*/
	@Override
	public void LoginSuccess(ArrayList<String[]> result) {
		mdbHelper.UpdateMyBookingsDatabase(result);
		//FragmentTabsFragmentSupport fragmentTab = FragmentTabsFragmentSupport.newInstance();
		if(mViewPagerFragment==null){
			mViewPagerFragment = new ViewPagerFragment();
			mViewPagerAdapter = new CustomViewPagerAdapter(getSupportFragmentManager());

		}
		else{
			mViewPagerAdapter.notifyDataSetChanged();
		}
		
		FragmentManager fragman = getSupportFragmentManager();
		
		fragman.beginTransaction().
		replace(R.id.mybookings_frame, mViewPagerFragment)
			.addToBackStack(null)
			.commit();
		
		
	    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
	    //Find the currently focused view, so we can grab the correct window token from it.
	    View view = getCurrentFocus();
	    //If no view currently has focus, create a new one, just so we can grab a window token from it
	    if(view == null) {
	        view = new View(this);
	    }
	    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	
		Tracker t = ((UOITLibraryBookingApp) getApplication()).getTracker();
		t.send(new HitBuilders.EventBuilder()
			.setCategory("My Bookings")
			.setAction("Login Success")
			.build()
			);
		
				
	}
	public static void buttonEffect(View button, final int color){
		
	    button.setOnTouchListener(new OnTouchListener() {
	    	@Override
	        public boolean onTouch(View v, MotionEvent event) {
	            switch (event.getAction()) {
	                case MotionEvent.ACTION_DOWN: {
	                    v.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
	                    v.invalidate();
	                    break;
	                    //0xe0f47521
	                }
	                case MotionEvent.ACTION_UP: {
	                    v.getBackground().clearColorFilter();
	                    v.invalidate();
	                    break;
	                }
	            }
	            return false;
	        }

	    });
	}
	
	@Override
	public void LoginFail(String errorMessage) {
		
		FragmentManager fragman = getSupportFragmentManager();
		LoginFragment loginFragment = (LoginFragment) fragman.findFragmentByTag("loginfragment");
		//if(loginFragment == null){Log.i(TAG, "GOOFED");}
		loginFragment.ErrorMessage(errorMessage);
		Tracker t = ((UOITLibraryBookingApp) getApplication()).getTracker();
		t.send(new HitBuilders.EventBuilder()
			.setCategory("My Bookings")
			.setAction("Login Fail")
			.build()
			);
		
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
    	GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}
    

}
