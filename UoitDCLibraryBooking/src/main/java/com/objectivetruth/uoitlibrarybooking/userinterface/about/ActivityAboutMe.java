package com.objectivetruth.uoitlibrarybooking.userinterface.about;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.objectivetruth.uoitlibrarybooking.userinterface.ActivityBase;
import com.objectivetruth.uoitlibrarybooking.R;

public class ActivityAboutMe extends ActivityBase {
    final static private int ACTIVITY_PAGE_NUMBER = 2;
    final static private String ACTIVITY_TITLE = "About";

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

        configureAndSetupLayoutAndDrawer(
                R.layout.activity_about,
                R.id.drawer_layout_aboutme,
                R.id.left_drawer_aboutme);
	}

    @Override
    protected int getActivityPageNumber() {
        return ACTIVITY_PAGE_NUMBER;
    }

    @Override
    protected String getActivityTitle() {
        return ACTIVITY_TITLE;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        getActionBarDrawerToggle().onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }
}
