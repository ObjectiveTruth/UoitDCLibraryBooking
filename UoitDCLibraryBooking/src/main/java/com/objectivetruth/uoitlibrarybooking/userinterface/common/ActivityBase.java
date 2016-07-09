package com.objectivetruth.uoitlibrarybooking.userinterface.common;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.objectivetruth.uoitlibrarybooking.BuildConfig;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.userinterface.about.About;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.Calendar;
import com.objectivetruth.uoitlibrarybooking.userinterface.guidelinespolicies.GuidelinesAndPolicies;
import com.objectivetruth.uoitlibrarybooking.userinterface.myaccount.MyAccount;
import timber.log.Timber;

public abstract class ActivityBase extends AppCompatActivity {
    protected abstract String                           getActivityTitle();
    private ActionBarDrawerToggle _mDrawerToggle        = null;
    private DrawerLayout _mDrawerLayout                 = null;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Pass any configuration change to the drawer toggles so it stays in the same configuration
        getActionBarDrawerToggle().onConfigurationChanged(newConfig);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            Timber.d("Configuration Changed to LANDSCAPE");
        }
        else{
            Timber.d("Configuration Changed to PORTRAIT");
        }
    }

    @Override
    protected void onCreate(Bundle bundleExtras) {
        super.onCreate(bundleExtras);
        if (BuildConfig.DEBUG) {
            // Programmatically unlock the screen for testing
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        }
    }

    protected final void configureAndSetupLayoutAndDrawer(int layoutIdToLoad,
                                                int drawerLayoutIdToLoad,
                                                int toolbarLayoutIdToLoad) {
        setContentView(layoutIdToLoad);
        _configureAndSetupToolbar(toolbarLayoutIdToLoad);
        _configureAndSetupDrawer(drawerLayoutIdToLoad);
    }

    private void _configureAndSetupToolbar(int toolbarLayoutIdToLoad) {
        Toolbar toolbar = (Toolbar) findViewById(toolbarLayoutIdToLoad);
        // Tells the system to use the toolbar as the actionbar at the top (its not really an action bar, its better!)
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            // enable ActionBar app icon to behave as any action menu item to toggle nav drawer
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getActivityTitle());
        }
    }

    private void _configureAndSetupDrawer(int drawerLayoutIdToLoad) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        _mDrawerLayout = (DrawerLayout) findViewById(drawerLayoutIdToLoad);

        if (navigationView != null) {
            // In Android support 23.2.1, there is a bug where you can't inflate the menu in XML, must be done manually
            navigationView.inflateMenu(R.menu.drawer_menu_items);
            navigationView.inflateHeaderView(R.layout.drawer_header);
            navigationView.
                    setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                // This method will trigger on item Click of navigation menu
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    return _selectDrawerItem(menuItem, _mDrawerLayout);
                }
            });
        }
        _mDrawerToggle = new ActionBarDrawerToggle(
                this,                               // Host Activity
                _mDrawerLayout,                     // DrawerLayout object
                R.string.navigation_drawer_open,    // Description for accessibility
                R.string.navigation_drawer_close    // Description for accessibility
        );
        _mDrawerLayout.addDrawerListener(_mDrawerToggle);
    }

    private Pair<Fragment, String> _findFragmentByTagOrReturnNewInstance(String fragmentTag, Class fragmentClass) {
        Fragment returnFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if (returnFragment == null) {
            try {
                Timber.d("Fragment with tag: " + fragmentTag + " not found, instantiating a new one");
                returnFragment = (Fragment) fragmentClass.newInstance();
                return new Pair<Fragment, String>(returnFragment, fragmentTag);
            } catch (Exception e) {
                Timber.e("Could not instantiate the Fragment for the menu", e);
                return null;
            }
        }else {
            Timber.d("Fragment with tag: " + fragmentTag + " found. retrieving it without creating a new one");
            return new Pair<Fragment, String>(returnFragment, fragmentTag);
        }
    }

    /**
     * Create a new fragment and specify the fragment to show based on nav item clicked
     * @param menuItem
     * @param mDrawerLayout
     * @return
     */
    private boolean _selectDrawerItem(MenuItem menuItem, DrawerLayout mDrawerLayout) {
        String CALENDAR_FRAGMENT_TAG = "SINGLETON_CALENDAR_FRAGMENT_TAG";
        String GUIDELINES_POLICIES_FRAGMENT_TAG = "SINGLETON_GUIDELINES_POLICIES_FRAGMENT_TAG";
        String ABOUT_FRAGMENT_TAG = "SINGLETON_ABOUT_FRAGMENT_TAG";
        String MY_ACCOUNT_FRAGMENT_TAG = "SINGLETON_MY_ACCOUNT_FRAGMENT_TAG";

        Pair<Fragment, String> fragmentTagPair = null;

        switch(menuItem.getItemId()) {
            case R.id.drawer_menu_item_calendar:
                Timber.i("Calendar selected from Drawer");
                fragmentTagPair = _findFragmentByTagOrReturnNewInstance(CALENDAR_FRAGMENT_TAG, Calendar.class);
                break;
            case R.id.drawer_menu_item_guidelines_and_policies:
                Timber.i("Guidelines And Policies selected from Drawer");
                fragmentTagPair = _findFragmentByTagOrReturnNewInstance(GUIDELINES_POLICIES_FRAGMENT_TAG,
                        GuidelinesAndPolicies.class);
                break;
            case R.id.drawer_menu_item_about:
                Timber.i("About selected from Drawer");
                fragmentTagPair = _findFragmentByTagOrReturnNewInstance(ABOUT_FRAGMENT_TAG, About.class);
                break;
            case R.id.drawer_menu_item_my_account:
                Timber.i("My Account selected from Drawer");
                fragmentTagPair = _findFragmentByTagOrReturnNewInstance(MY_ACCOUNT_FRAGMENT_TAG, MyAccount.class);
                break;
            default:
                Timber.w("No layout mapped to the menu item requested, moving to the default, Calendar");
                fragmentTagPair = _findFragmentByTagOrReturnNewInstance(CALENDAR_FRAGMENT_TAG, Calendar.class);
        }
        if(fragmentTagPair == null) {return false;}

        // Insert the fragment by replacing any existing fragment.
        // Don't forget to tag it so it can be retrieved later without loading it again
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mainactivity_content_frame, fragmentTagPair.first, fragmentTagPair.second)
                .addToBackStack(null)
                .commit();

        // Highlight the selected item
        menuItem.setChecked(true);
        // Set action bar title
        if(getSupportActionBar() != null) {getSupportActionBar().setTitle(menuItem.getTitle());};
        mDrawerLayout.closeDrawers();
        return true;
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
        // Insert the default fragment once the view has been created
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainactivity_content_frame, new Calendar()).commit();
        _mDrawerToggle.syncState();
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

    @Override
    public void onBackPressed() {
        Timber.d("Back button has been pressed, checking if the drawer is open");
        if (_isNavDrawerOpen()) {
            Timber.d("Drawer is Open, closing.");
            _closeNavDrawer();
        } else {
            Timber.d("Drawer is closed, not doing anything.");
            super.onBackPressed();
        }
    }

    private boolean _isNavDrawerOpen() {
        return _mDrawerLayout != null && _mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    private void _closeNavDrawer() {
        if (_mDrawerLayout != null) {
            _mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }
}
