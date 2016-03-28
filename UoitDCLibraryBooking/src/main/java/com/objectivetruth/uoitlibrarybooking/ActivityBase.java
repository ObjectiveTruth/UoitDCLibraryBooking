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


    protected void selectItem(int position) {
        Timber.i("Position " + String.valueOf(position) + " selected");
        int ACTIVITYPAGENUMBER = getActivityPageNumber();

        if(position == 0 && position != ACTIVITYPAGENUMBER){
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
        else if(position == 1 && position != ACTIVITYPAGENUMBER){
            Intent intent = new Intent(this, GuidelinesPoliciesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);

        }
        else if(position == 2 && position != ACTIVITYPAGENUMBER){
            Intent intent = new Intent(this, ActivityAboutMe.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
        setTitle(getMenuItems()[position]);
        getmDrawerLayout().closeDrawer(getmDrawerList());
    }


}
