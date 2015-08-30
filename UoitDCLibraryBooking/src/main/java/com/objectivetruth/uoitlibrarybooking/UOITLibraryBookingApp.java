package com.objectivetruth.uoitlibrarybooking;

import android.app.Application;
import android.preference.PreferenceManager;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

import java.util.UUID;

public class UOITLibraryBookingApp extends Application {
	//initialized the tracker to null so I can check when the app is made
	Tracker t = null;
	//Logging TAG
	private static final String TAG = "UOITLibraryBookingApp";

	String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyWiJKYNadTZh746EgXiI4GWYpWWbGu+F+UAnGz6kuCkH2gIxknj8gXNw/3pESTIsC/LWg06VT/Z/P8dAFR5qV5VcytyqKvAwqPYvKn8an7A+dV6CFIL2QrFI0771oOhvLC/Jj2XiX/M9iAX3kNcYMXwvwHY7T9SP6wX6gCnrFA9Pgc0vIM8JvvSJR6iyb9diJpEhf2a8ZRul6A97n06+926n1hexPZPOKlm2UvvlwMmMwDLsZ68OJoX45pZWdtUp86AW0pkx6c1sAYvCZrtW3chVI8YpxlsB6jLV65ov4Jj+3SN1AMyyqBlF0srtyFdKZgJya72ps8nrZhS+O6v7cwIDAQAB";
	private boolean isPremium = false;

	public UOITLibraryBookingApp() {
			super();
	}
	 
	public Tracker getTracker() {
		if(t == null){
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			if(BuildConfig.DEBUG){
				analytics.setDryRun(true);
			}
			t = analytics.newTracker(R.xml.app_tracker);
            String mUUID = PreferenceManager.getDefaultSharedPreferences(this).getString(MainActivity.SHARED_PREF_UUID, null);

            if(mUUID == null){
                mUUID = UUID.randomUUID().toString();
            }
            t.set("&cid", mUUID);
			
			//analytics.setAppOptOut(true);
			
		}
	
	return t;
	}

    @Override
    public void onCreate() {
        super.onCreate();
        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
            String mUUID = PreferenceManager.getDefaultSharedPreferences(this).getString(MainActivity.SHARED_PREF_UUID, null);

            if(mUUID == null){
                mUUID = UUID.randomUUID().toString();
            }
            Crashlytics.setUserIdentifier(mUUID);
        }
    }




	


    /** A tree which logs important information for crash reporting. */
    private static class CrashReportingTree extends Timber.DebugTree {
        @Override
        public void v(String message, Object... args) {
            Crashlytics.log(message);
        }


        @Override
        public void i(String message, Object... args) {
            Crashlytics.log(String.format(message, args));
        }

        @Override
        public void i(Throwable t, String message, Object... args) {
            i(message, args); // Just add to the log.
        }

        @Override
        public void e(String message, Object... args) {
            i("ERROR: " + message, args); // Just add to the log.
        }

        @Override
        public void e(Throwable t, String message, Object... args) {
            e(message, args);
            Crashlytics.log(message);

            Crashlytics.logException(t);
        }
    }


}


