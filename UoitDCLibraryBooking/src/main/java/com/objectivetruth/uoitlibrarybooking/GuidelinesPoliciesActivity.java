package com.objectivetruth.uoitlibrarybooking;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class GuidelinesPoliciesActivity extends ActivityBase {
    final static private int ACTIVITY_PAGE_NUMBER = 1;
    final static private String ACTIVITY_TITLE = "Policies Guidelines";

	@Override
	protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        configureAndSetupLayoutAndDrawer(
                R.layout.guidelines_policies,
                R.id.drawer_layout_guidelines_policies,
                R.id.left_drawer_guidelinespolicies);
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
