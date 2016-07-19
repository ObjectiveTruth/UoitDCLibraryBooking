package com.objectivetruth.uoitlibrarybooking.userinterface.common;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.objectivetruth.uoitlibrarybooking.statelessutilities.Triple;
import com.objectivetruth.uoitlibrarybooking.userinterface.about.About;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.Calendar;
import com.objectivetruth.uoitlibrarybooking.userinterface.guidelinespolicies.GuidelinesAndPolicies;
import com.objectivetruth.uoitlibrarybooking.userinterface.myaccount.MyAccount;
import timber.log.Timber;

import java.util.Collection;
import java.util.HashMap;

import static com.objectivetruth.uoitlibrarybooking.common.constants.FragmentTags.*;

public abstract class ActivityBase extends AppCompatActivity {
    protected abstract String                           getActivityTitle();
    private ActionBarDrawerToggle _mDrawerToggle;
    private DrawerLayout _mDrawerLayout;
    private HashMap<String, Fragment> stringFragmentHashMap = new HashMap<>();

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

    protected NavigationView configureAndSetupLayoutAndDrawer(int layoutIdToLoad,
                                                int drawerLayoutIdToLoad,
                                                int toolbarLayoutIdToLoad) {
        setContentView(layoutIdToLoad);
        _configureAndSetupToolbar(toolbarLayoutIdToLoad);
        _initializeAllMainFragmentsAndPreloadToView();
        return _configureAndSetupDrawer(drawerLayoutIdToLoad);
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

    private NavigationView _configureAndSetupDrawer(int drawerLayoutIdToLoad) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        _mDrawerLayout = (DrawerLayout) findViewById(drawerLayoutIdToLoad);

        // In Android support 23.2.1, there is a bug where you can't inflate the menu in XML, must be done manually
        navigationView.inflateMenu(R.menu.drawer_menu_items);
        navigationView.inflateHeaderView(R.layout.drawer_header);
        navigationView.
                setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                return selectDrawerItem(menuItem);
            }
        });
        _mDrawerToggle = new ActionBarDrawerToggle(
                this,                               // Host Activity
                _mDrawerLayout,                     // DrawerLayout object
                R.string.navigation_drawer_open,    // Description for accessibility
                R.string.navigation_drawer_close    // Description for accessibility
        );
        _mDrawerLayout.addDrawerListener(_mDrawerToggle);

        return navigationView;
    }

    private void _initializeAllMainFragmentsAndPreloadToView() {
        Calendar calendarFrag = Calendar.newInstance();
        GuidelinesAndPolicies guidelinesAndPoliciesFrag = GuidelinesAndPolicies.newInstance();
        About aboutFrag = About.newInstance();
        MyAccount myAccountFrag = MyAccount.newInstance();

        stringFragmentHashMap.put(CALENDAR_FRAGMENT_TAG, calendarFrag);
        stringFragmentHashMap.put(GUIDELINES_POLICIES_FRAGMENT_TAG, guidelinesAndPoliciesFrag);
        stringFragmentHashMap.put(ABOUT_FRAGMENT_TAG, aboutFrag);
        stringFragmentHashMap.put(MY_ACCOUNT_FRAGMENT_TAG, myAccountFrag);

        // The only one NOT being hidden is the Calendar
        getSupportFragmentManager().beginTransaction()
                .add(R.id.mainactivity_content_frame, guidelinesAndPoliciesFrag, GUIDELINES_POLICIES_FRAGMENT_TAG)
                .add(R.id.mainactivity_content_frame, aboutFrag, ABOUT_FRAGMENT_TAG)
                .add(R.id.mainactivity_content_frame, myAccountFrag, MY_ACCOUNT_FRAGMENT_TAG)
                .add(R.id.mainactivity_content_frame, calendarFrag, CALENDAR_FRAGMENT_TAG)
                .hide(guidelinesAndPoliciesFrag)
                .hide(aboutFrag)
                .hide(myAccountFrag)
                .hide(calendarFrag)
                .commit();
    }

    private Triple<Fragment, String, Boolean> _findFragmentByTagOrReturnNewInstance(String fragmentTag,
                                                                                    Class fragmentClass) {
        Fragment returnFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if (returnFragment == null) {
            try {
                Timber.d("NOT found Fragment with tag: " + fragmentTag + ", instantiating a new one");
                returnFragment = (Fragment) fragmentClass.newInstance();
                stringFragmentHashMap.put(fragmentTag, returnFragment);
                return new Triple<>(returnFragment, fragmentTag, false);
            } catch (Exception e) {
                Timber.e("Could not instantiate the Fragment for the menu", e);
                return null;
            }
        }else {
            Timber.d("FOUND fragment with tag: " + fragmentTag + ", retrieving it without creating a new one");
            return new Triple<>(returnFragment, fragmentTag, true);
        }
    }

    /**
     * Create a new fragment and specify the fragment to show based on nav item clicked
     * @param menuItem
     * @return
     */
    protected boolean selectDrawerItem(MenuItem menuItem) {
        Triple<Fragment, String, Boolean> fragmentTagIsFoundTriple = null;

        switch(menuItem.getItemId()) {
            case R.id.drawer_menu_item_calendar:
                Timber.i("Calendar selected from Drawer");
                fragmentTagIsFoundTriple = _findFragmentByTagOrReturnNewInstance(CALENDAR_FRAGMENT_TAG, Calendar.class);
                break;
            case R.id.drawer_menu_item_guidelines_and_policies:
                Timber.i("Guidelines And Policies selected from Drawer");
                fragmentTagIsFoundTriple = _findFragmentByTagOrReturnNewInstance(GUIDELINES_POLICIES_FRAGMENT_TAG,
                        GuidelinesAndPolicies.class);
                break;
            case R.id.drawer_menu_item_about:
                Timber.i("About selected from Drawer");
                fragmentTagIsFoundTriple = _findFragmentByTagOrReturnNewInstance(ABOUT_FRAGMENT_TAG, About.class);
                break;
            case R.id.drawer_menu_item_my_account:
                Timber.i("My Account selected from Drawer");
                fragmentTagIsFoundTriple = _findFragmentByTagOrReturnNewInstance(MY_ACCOUNT_FRAGMENT_TAG, MyAccount.class);
                break;
            default:
                Timber.w("No layout mapped to the menu item requested, moving to the default, Calendar");
                fragmentTagIsFoundTriple = _findFragmentByTagOrReturnNewInstance(CALENDAR_FRAGMENT_TAG, Calendar.class);
        }
        if(fragmentTagIsFoundTriple == null) {return false;}

        FragmentManager fragmentManager = getSupportFragmentManager();
        // If the fragment was already created(isFound), just show it. If not,
        // Insert the fragment by replacing any existing fragment.
        // Don't forget to tag it so it can be retrieved later without loading it again

        if(_isFragmentFoundAndVisible(fragmentTagIsFoundTriple)) {
            Timber.d("Drawer Item: " + menuItem.getTitle() + " already being shown. Not changing screen");
        }else if(!_isFragmentFoundAndVisible(fragmentTagIsFoundTriple)) {
            _hideAllVisibleFragments(stringFragmentHashMap);
            fragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .show(fragmentTagIsFoundTriple.getLeft())
                    .commit();
        }else {
            _hideAllVisibleFragments(stringFragmentHashMap);
            fragmentManager.beginTransaction()
                    .add(R.id.mainactivity_content_frame,
                            fragmentTagIsFoundTriple.getLeft(),
                            fragmentTagIsFoundTriple.getMiddle())
                    .commit();
        }

        // Highlight the selected item
        menuItem.setChecked(true);
        // Set action bar title
        if(getSupportActionBar() != null) {getSupportActionBar().setTitle(menuItem.getTitle());};
        _mDrawerLayout.closeDrawers();
        return true;
    }

    private boolean _isFragmentFoundAndVisible(Triple<Fragment, String, Boolean> fragmentTagIsFoundTriple) {
        return fragmentTagIsFoundTriple.getRight() && fragmentTagIsFoundTriple.getLeft().isVisible();
    }

    private void _hideAllVisibleFragments(HashMap<String, Fragment> stringFragmentHashMap) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        Collection<Fragment> fragmentCollection = stringFragmentHashMap.values();
        for(Fragment fragment: fragmentCollection) {
            if(fragment != null && fragment.isVisible()) {
                fragmentTransaction = fragmentTransaction.hide(fragment);
            }
        }
        fragmentTransaction.commit();
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
