package com.objectivetruth.uoitlibrarybooking;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;
import timber.log.Timber;

/**
 * Created by ObjectiveTruth on 8/19/2014.
 */
public abstract class ActivityBase extends ActionBarActivity {

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

    protected abstract int getActivityPageNumber();

    protected abstract String[] getMenuItems();

    protected abstract DrawerLayout getmDrawerLayout();

    protected abstract ListView getmDrawerList();

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
        //mDrawerList.setItemChecked(position, true);
        setTitle(getMenuItems()[position]);
        getmDrawerLayout().closeDrawer(getmDrawerList());
    }
}
