package com.objectivetruth.uoitlibrarybooking;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import timber.log.Timber;

public abstract class ActivityBase extends ActionBarActivity {
    protected abstract int              getActivityPageNumber();
    protected abstract String[]         getMenuItems();
    protected abstract DrawerLayout     getmDrawerLayout();
    protected abstract ListView         getmDrawerList();


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

    void selectItem(int positionSelected) {
        Timber.i("Drawer Position " + String.valueOf(positionSelected) + " selected");
        final int POSITION_OF_THIS_ACTIVITY_IN_DRAWER = getActivityPageNumber();
        _startIntentForActivityAtPositionInDrawerIfAppropriate(POSITION_OF_THIS_ACTIVITY_IN_DRAWER, positionSelected);

        setTitle(getMenuItems()[positionSelected]);
        getmDrawerLayout().closeDrawer(getmDrawerList());
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
