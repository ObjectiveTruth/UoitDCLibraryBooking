package com.objectivetruth.uoitlibrarybooking.app;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.objectivetruth.uoitlibrarybooking.DrawerListAdapter;
import com.objectivetruth.uoitlibrarybooking.guidelinespolicies.GuidelinesPoliciesActivity;
import com.objectivetruth.uoitlibrarybooking.MainActivity;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.about.ActivityAboutMe;
import timber.log.Timber;

public abstract class ActivityBase extends AppCompatActivity {
    protected abstract int                              getActivityPageNumber();
    protected abstract String                           getActivityTitle();
    private boolean _isDrawerRequestedInThisActivity    = false;
    private ActionBarDrawerToggle _mDrawerToggle        = null;
    private DrawerLayout _mDrawerLayout                 = null;
    private String[] _menuItemsArray                    = null;
    private ListView _mDrawerList                       = null;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            Timber.i("Configuration Changed to LANDSCAPE");
        }
        else{
            Timber.i("Configuration Changed to PORTRAIT");
        }
    }

    @Override
    protected void onCreate(Bundle bundleExtras) {
        super.onCreate(bundleExtras);
        if (UOITLibraryBookingApp.IS_DEBUG_MODE) {
            // Programmatically unlock the screen for testing
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        }
    }

    protected final void configureAndSetupLayoutAndDrawer(int layoutIdToLoad,
                                                int drawerLayoutIdToLoad,
                                                int drawerListIdToLoad) {
        setContentView(layoutIdToLoad);
        _isDrawerRequestedInThisActivity = true;
        _configureAndSetupDrawer(drawerLayoutIdToLoad, drawerListIdToLoad);
    }

    private void _configureAndSetupDrawer(int drawerLayoutIdToLoad, int drawerListIdToLoad) {
        _menuItemsArray = getResources().getStringArray(R.array.menuItems);
        _mDrawerLayout = (DrawerLayout) findViewById(drawerLayoutIdToLoad);
        _mDrawerList = (ListView) findViewById(drawerListIdToLoad);

        if(_mDrawerLayout != null) {
            // set a custom shadow that overlays the main content when the drawer opens
            _mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

            // ActionBarDrawerToggle ties together the the proper interactions
            // between the sliding drawer and the action bar app icon
            _mDrawerToggle = new ActionBarDrawerToggle(
                    this,               /* host Activity */
                    _mDrawerLayout, /* DrawerLayout object */
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
                    getSupportActionBar().setTitle(getActivityTitle());
                    invalidateOptionsMenu(); // creates call to
                    // onPrepareOptionsMenu()
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    getSupportActionBar().setTitle(_getAppTitle());
                    invalidateOptionsMenu(); // creates call to
                    // onPrepareOptionsMenu()
                }

            };
            _mDrawerLayout.addDrawerListener(_mDrawerToggle);
        }

        // set up the drawer's list view with items and click listener
        if(_mDrawerList != null) {
            _mDrawerList.setAdapter(new DrawerListAdapter(this,
                    R.layout.drawer_list_item, _menuItemsArray, getActivityPageNumber(), this));
            _mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        }

        if(getSupportActionBar() != null) {
            // enable ActionBar app icon to behave as action to toggle nav drawer
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //getActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(getActivityTitle());
        }
    }

    protected ActionBarDrawerToggle getActionBarDrawerToggle() {
        if(_mDrawerToggle != null) {
            return _mDrawerToggle;
        }else {
            throw new IllegalStateException(
                    "This Activity did not initialize the ActionBarDrawerToggle, did you forget to call " +
                            "configureAndSetupLayoutAndDrawer()?");
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(_isDrawerRequestedInThisActivity) {
            // Sync the toggle state after onRestoreInstanceState has occurred.
            _mDrawerToggle.syncState();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        if(_isDrawerRequestedInThisActivity) {
            boolean drawerOpen = _mDrawerLayout.isDrawerOpen(_mDrawerList);
            if(drawerOpen) { return false; }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Simple wrapper for Activity.getTitle() which was ambiguous and bothersome
     * @return Title of the Application
     */
    private CharSequence _getAppTitle() {
        return getTitle();
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

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            selectItem(position);

        }
    }

    void selectItem(int positionSelected) {
        Timber.i("Drawer Position " + String.valueOf(positionSelected) + " selected");
        final int POSITION_OF_THIS_ACTIVITY_IN_DRAWER = getActivityPageNumber();
        _startIntentForActivityAtPositionInDrawerIfAppropriate(POSITION_OF_THIS_ACTIVITY_IN_DRAWER, positionSelected);

        setTitle(_menuItemsArray[positionSelected]);
        _mDrawerLayout.closeDrawer(_mDrawerList);
    }

    private void _startIntentForActivityAtPositionInDrawerIfAppropriate(int position_of_this_activity_in_drawer,
                                                                        int positionSelected) {
        if(positionSelected == position_of_this_activity_in_drawer) {return;}

        Intent intentToOpenActivity = null;
        switch (positionSelected) {
            case 0:
                intentToOpenActivity = new Intent(this, MainActivity.class);
                break;
            case 1:
                intentToOpenActivity = new Intent(this, GuidelinesPoliciesActivity.class);
                break;
            case 2:
                intentToOpenActivity = new Intent(this, ActivityAboutMe.class);
                break;
            default:
                break;
        }

        if(intentToOpenActivity != null) {
            intentToOpenActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intentToOpenActivity);
        }
    }
}
