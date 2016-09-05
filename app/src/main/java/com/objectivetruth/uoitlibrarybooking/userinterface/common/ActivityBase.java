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
import java.util.Set;

import static com.objectivetruth.uoitlibrarybooking.common.constants.FragmentTags.*;

public abstract class ActivityBase extends AppCompatActivity {
    private ActionBarDrawerToggle _mDrawerToggle;
    private DrawerLayout _mDrawerLayout;
    private HashMap<String, Fragment> stringFragmentHashMap = new HashMap<String, Fragment>();
    private int _lastMenuItemIDRequested;
    private boolean _isFirstTimeSelectDrawerItem = true;
    private NavigationView navigationView;
    private String BUNDLE_KEY_LAST_MENU_ITEM_ID_REQUESTED = "LAST_MENU_ITEM_ID_REQUESTED";
    private String BUNDLE_KEY_MAIN_FRAGMENT_TAGS = "BUNDLE_KEY_MAIN_FRAGMENT_TAGS";
    private String BUNDLE_KEY_IS_A_NON_DRAWER_SCREEN_SHOWING = "BUNDLE_KEY_IS_A_NON_DRAWER_SCREEN_SHOWING";
    private boolean _isANonDrawerScreenShowing = false;
    private boolean isFirstLoadThisSession = false;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            isFirstLoadThisSession = true;
        }else{
            _restorePreviousInstanceInformation(savedInstanceState);
        }

        if (BuildConfig.DEBUG) {
            // Programmatically unlock the screen for testing
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        }
    }

    protected void setupToolbar(int toolbarLayoutIdToLoad) {
        Toolbar toolbar = (Toolbar) findViewById(toolbarLayoutIdToLoad);
        // Tells the system to use the toolbar as the actionbar at the top (its not really an action bar, its better!)
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            // enable ActionBar app icon to behave as any action menu item to toggle nav drawer
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void _restorePreviousInstanceInformation(Bundle savedInstanceState) {
        _lastMenuItemIDRequested = savedInstanceState.getInt(BUNDLE_KEY_LAST_MENU_ITEM_ID_REQUESTED);
        _isANonDrawerScreenShowing = savedInstanceState.getBoolean(BUNDLE_KEY_IS_A_NON_DRAWER_SCREEN_SHOWING);

        String[] previousFragmentTags = savedInstanceState.getStringArray(BUNDLE_KEY_MAIN_FRAGMENT_TAGS);
        stringFragmentHashMap = new HashMap<String, Fragment>(); // Android destroyed the previous HashMap. Must rebuild
        for(String fragTag: previousFragmentTags) {
            stringFragmentHashMap.put(fragTag, null);
        }
    }

    protected NavigationView setupDrawer(int drawerLayoutIdToLoad) {
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
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

    protected void initializeAllMainFragmentsAndPreloadToView() {
        if(isFirstLoadThisSession) {
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
                    .hide(guidelinesAndPoliciesFrag)
                    .hide(aboutFrag)
                    .hide(myAccountFrag)
                    .add(R.id.mainactivity_content_frame, calendarFrag, CALENDAR_FRAGMENT_TAG)
                    .commit();
        }else{
            Set<String> fragmentTagSet = stringFragmentHashMap.keySet();
            FragmentManager fragmentManager = getSupportFragmentManager();
            for(String fragTag: fragmentTagSet) {
                stringFragmentHashMap.put(fragTag, fragmentManager.findFragmentByTag(fragTag));
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String[] mainFragmentTags = _stringSetToStringArray(stringFragmentHashMap.keySet());

        outState.putInt (BUNDLE_KEY_LAST_MENU_ITEM_ID_REQUESTED, _lastMenuItemIDRequested);
        outState.putBoolean(BUNDLE_KEY_IS_A_NON_DRAWER_SCREEN_SHOWING, _isANonDrawerScreenShowing);
        outState.putStringArray(BUNDLE_KEY_MAIN_FRAGMENT_TAGS, mainFragmentTags);
    }

    /**
     * Tells the Activity that a non-drawer related screen is currently showing, this will give it a hint that
     * it should NOT track the last item being shown. This is important for when app comma happens (everything is fully
     * destroyed and needs to be created). This is because the other screen will handle the backstack itself.
     * @see com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction.BookingInteraction
     * @param isANonDrawerScreenShowing
     */
    public void setIsNonDrawerScreenShowing(boolean isANonDrawerScreenShowing) {
        _isANonDrawerScreenShowing = isANonDrawerScreenShowing;
    }

    protected boolean areOnlyDrawerRelatedScreensShowing() {
        return !_isANonDrawerScreenShowing;
    }

    private String[] _stringSetToStringArray(Set<String> strings) {
        String[] returnArr = new String[strings.size()];
        int i = 0;
        for(String s: strings) {
            returnArr[i] = s;
            i++;
        }
        return returnArr;
    }

    /**
     * Boolean tells you if the fragment was found {@code isFound}
     * @param fragmentTag
     * @param fragmentClass
     * @return
     */
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
     * Returns the Navigation view which represents the menu. This can be used to search for menu items or do
     * work on the menu items
     * @return
     */
    protected NavigationView getDrawerView() {
        if(navigationView == null) {
            navigationView = (NavigationView) findViewById(R.id.navigation_view);
        }
        return navigationView;
    }

    protected int getLastMenuItemIDRequested() {
        return _lastMenuItemIDRequested;
    }

    /**
     * Create a new fragment and specify the fragment to show based on nav item clicked
     * @param menuItem
     * @return
     */
    protected boolean selectDrawerItem(MenuItem menuItem) {
        Triple<Fragment, String, Boolean> fragmentTagIsFoundTriple = null;

        _lastMenuItemIDRequested = menuItem.getItemId();
        switch(_lastMenuItemIDRequested) {
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

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        // If the fragment was already created(isFound), just show it. If not,
        // Insert the fragment by replacing any existing fragment.
        // Don't forget to tag it so it can be retrieved later without loading it again

        if(_isFragmentFoundAndVisible(fragmentTagIsFoundTriple)) {
            Timber.d("Drawer Item: " + menuItem.getTitle() + " already being shown. Not changing screen");
        }else if(!_isFragmentFoundAndVisible(fragmentTagIsFoundTriple)) {
            fragmentTransaction =
                    _addHideAllVisibleFragmentsToFragmentTransaction(stringFragmentHashMap, fragmentTransaction)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .show(fragmentTagIsFoundTriple.getLeft());
            // On the first time opening the drawer there is a special case where the backstack will be empty, so adding
            // will cause the user to see a blank screen with nothing if they press back
            if(_isFirstTimeSelectDrawerItem) {
                _isFirstTimeSelectDrawerItem = false;
            }else{
                fragmentTransaction.addToBackStack(null);
            }

            fragmentTransaction
                    .commit();
        }else {
            // To make sure the backstack works correctly, we split this into 2 separate transactions
            // This one JUST adds it to the correct frame, and the 2nd one hides/unhides
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.mainactivity_content_frame,
                            fragmentTagIsFoundTriple.getLeft(),
                            fragmentTagIsFoundTriple.getMiddle())
                    .commit();

            _addHideAllVisibleFragmentsToFragmentTransaction(stringFragmentHashMap, fragmentTransaction)
                    .addToBackStack(null)
                    .commit();
        }

        // Highlight the selected item
        menuItem.setChecked(true);
        // Set action bar title
        _mDrawerLayout.closeDrawers();
        return true;
    }

    private boolean _isFragmentFoundAndVisible(Triple<Fragment, String, Boolean> fragmentTagIsFoundTriple) {
        return fragmentTagIsFoundTriple.getRight() && fragmentTagIsFoundTriple.getLeft().isVisible();
    }

    private FragmentTransaction _addHideAllVisibleFragmentsToFragmentTransaction(HashMap<String, Fragment>
                                                                                         stringFragmentHashMap,
                                          FragmentTransaction fragmentTransaction) {
        Collection<Fragment> fragmentCollection = stringFragmentHashMap.values();
        for(Fragment fragment: fragmentCollection) {
            if(fragment != null && fragment.isVisible()) {
                fragmentTransaction = fragmentTransaction.hide(fragment);
            }
        }
        return fragmentTransaction;
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

    protected FragmentTransaction addHidingOfAllCurrentFragmentsToTransaction(FragmentTransaction fragmentTransaction) {
        return _addHideAllVisibleFragmentsToFragmentTransaction(stringFragmentHashMap, fragmentTransaction);
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

    public void setDrawerState(boolean isEnabled) {
        if ( isEnabled ) {
            _mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
            if(getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            getActionBarDrawerToggle().syncState();
        }
        else {
            _mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
            if(getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
            getActionBarDrawerToggle().syncState();
        }
    }
}
