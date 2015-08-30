package com.objectivetruth.uoitlibrarybooking;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.google.android.gms.analytics.GoogleAnalytics;

public class ActivitySettings extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
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
}
