package com.objectivetruth.uoitlibrarybooking.userinterface.guidelinespolicies;

import android.os.Bundle;
import android.view.MenuItem;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.userinterface.common.ActivityBase;

public class GuidelinesPoliciesActivity extends ActivityBase {
    final static private int ACTIVITY_PAGE_NUMBER = 1;
    final static private String ACTIVITY_TITLE = "Policies Guidelines";

	@Override
	protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        configureAndSetupLayoutAndDrawer(
                R.layout.guidelines_policies,
                R.id.left_drawer_guidelinespolicies, 0);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        getActionBarDrawerToggle().onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }
}
