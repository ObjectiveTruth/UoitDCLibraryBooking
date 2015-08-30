package com.objectivetruth.uoitlibrarybooking;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import com.google.android.gms.analytics.GoogleAnalytics;


public class ActivityUpgrade extends ActivityBase {

	private final String TAG = "Upgrade Activity";
	public static DbHelper mdbHelper;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] menuItems;
	static SharedPreferences defaultPreferences;
	static SharedPreferences.Editor defaultPrefsEditor;
	final int ACTIVITYPAGENUMBER = 6;
	Activity mActivity;
	private static boolean isPremium = false;
	ImageView backgroundImage;
	
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		mdbHelper = MainActivity.mdbHelper;
		
		super.onCreate(arg0);
		setContentView(R.layout.activity_upgrade);
		
		defaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		defaultPrefsEditor = defaultPreferences.edit();

		
		backgroundImage = (ImageView) findViewById(R.id.students_background);
		mActivity = this;
		mTitle = "Upgrade"; 
		mDrawerTitle = getTitle();
        menuItems = getResources().getStringArray(R.array.menuItems);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer_aboutme);

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

		
        FragmentManager fragman = getSupportFragmentManager();
        fragman.
        	beginTransaction()
        	.replace(R.id.fragframe, PremiumPromoFragment.newInstance())
        	.commit();
        	
		
	}

	
/*
	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		mHelper = ((UOITLibraryBookingApp)getApplication()).getIABHelper();
		if(!mHelper.mSetupDone){
			mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
	            public void onIabSetupFinished(IabResult result) {
	                

	                if (!result.isSuccess()) {
	                	Toast.makeText(getApplicationContext(), R.string.iab_setup_fail, 
	                			   Toast.LENGTH_SHORT).show();
	                    return;
	                }
	                // Have we been disposed of in the meantime? If so, quit.
	                if (mHelper == null) return;
	                mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
	            	    public void onQueryInventoryFinished(IabResult result,
	            	 	       Inventory inventory) {
	            	 	    	if (mHelper == null) {

	            	 	    		return;
	            	 	    	}
	            	 	    	
	            	 	    	if (result.isFailure()) {
	            	 	    		Toast.makeText(getApplicationContext(), R.string.iab_query_inventory_fail, 
	            	 	 			   Toast.LENGTH_SHORT).show();
	            	 	    		Log.i(TAG, result.getMessage());

	            	 	    		return;
	            	 	    	}

	            	 	    	
            	 	    		Purchase premiumPurchase = inventory.getPurchase(MainActivity.SKU_PREMIUM);
            	 	    		((UOITLibraryBookingApp) getApplication()).setIsPremium((premiumPurchase != null ));
	            	             //isPremium = inventory.hasPurchase(SKU_PREMIUM);        
	            	             Log.i(TAG, "isPremium is :" + ((UOITLibraryBookingApp) getApplication()).getIsPremium());

	            	 	    }
	            	 	 });
	            }
	        });
			
		}
		return super.onCreateView(name, context, attrs);
		
	}*/


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
	protected void onStart() {
		super.onStart();

		
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}
	
    @Override
	protected void onStop() {
		super.onStop();
    	GoogleAnalytics.getInstance(this).reportActivityStop(this);

	}
    
    public static class PremiumPromoFragment extends Fragment{
    	final static String TAG = "PremiumPromoFragment";
    	static PremiumPromoFragment newInstance(){
    		
    		PremiumPromoFragment frag = new PremiumPromoFragment();
    		return frag;
    	}

		/*@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.premium_promo, container, false);
			Button negativeButton = (Button) rootView.findViewById(R.id.cancel);
			negativeButton.setEnabled(false);
			negativeButton.setVisibility(View.GONE);
			Button upgradeOK = (Button) rootView.findViewById(R.id.upgradeok);
			upgradeOK.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
		        	((UOITLibraryBookingApp)getActivity().getApplication()).getIABHelper().launchPurchaseFlow(getActivity(), MainActivity.SKU_PREMIUM, 10001,   
		        			new IabHelper.OnIabPurchaseFinishedListener() {
		        		
		        		@Override
		        		 public void onIabPurchaseFinished(IabResult result, Purchase purchase) 
		        		 {
		        		    if (result.isFailure()) {
		        		       	*//*Toast.makeText(getActivity(), "Error purchasing: " + result,
		        		 			   Toast.LENGTH_SHORT).show();
		        		       	*//*
		        		       return;
		        		    }      
		        		    else if (purchase.getSku().equals(MainActivity.SKU_PREMIUM)) {
		        		       Log.i(TAG, "purchased!");
		        		       ((UOITLibraryBookingApp)getActivity().getApplication()).setIsPremium(true);
		        		       isPremium = true;
		        		       FrameLayout fragFrame = (FrameLayout) getActivity().findViewById(R.id.fragframe);
		        		       fragFrame.setVisibility(View.GONE);
		        		       TextView thankYouMessage = (TextView) getActivity().findViewById(R.id.thankyou);
		        		       thankYouMessage.setVisibility(View.VISIBLE);
		        		    	ListView mDrawerList = (ListView) getActivity().findViewById(R.id.left_drawer_aboutme);
		        		    	if(mDrawerList != null){
		        		    		mDrawerList.invalidateViews();
		        		    	}
		        		       
		        		    }

		        		 }

		        	});
				}});

			
		    return rootView;
		}*/
    
    }
    /*@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
    }*/
    @Override
    public void onBackPressed() {
    	if(isPremium){
    		Intent intent = new Intent(this, MainActivity.class);
     		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    		intent.putExtra("purchased", true);
    		startActivity(intent);
    		finish();	
		}
        else {
            super.onBackPressed();
        }
    }
}