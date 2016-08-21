package com.objectivetruth.uoitlibrarybooking.userinterface.guidelinespolicies.DebugPreferences;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCE_NAMES;
import timber.log.Timber;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;
import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCES_KEYS.DEBUG_DUMP_SHARED_PREFERENCES_BUTTON;

public class DebugPreferences extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_debug, rootKey);

        Preference button = findPreference(DEBUG_DUMP_SHARED_PREFERENCES_BUTTON);
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Timber.d("Dumping shared preferences information to LogCat. ");
                Timber.d("Remember, you can also use: adb pull /data/data/com.objectivetruth.uoitlibrarybooking/shared_prefs/prefs.xml");
                _dumpSharedPreference(
                        _getDefaultSharedPreferencesName(getActivity()),
                        SHARED_PREFERENCE_NAMES.USER_SHARED_PREFERENCES_NAME,
                        SHARED_PREFERENCE_NAMES.CALENDAR_SHARED_PREFERENCES_NAME);

                return true;
            }
        });
    }

    private void _dumpSharedPreference(String ... prefs) {

        // Define default return values. These should not display, but are needed
        final String STRING_ERROR = "error!";
        final Integer INT_ERROR = -1;
        final boolean BOOLEAN_ERROR = false;
        final Float FLOAT_ERROR = 0.0f;
        final Long LONG_ERROR = 0L;
        final Set<String> SET_ERROR = new HashSet<>(1);

        // Add an item to the set
        SET_ERROR.add("Set Error!");

        // Loop through the Shared Prefs
        Timber.i("Loading Shared Prefs", "-----------------------------------");
        Timber.i("------------------", "-------------------------------------");

        for (String pref_name: prefs) {

            SharedPreferences preference = getActivity().getSharedPreferences(pref_name, MODE_PRIVATE);
            Map<String, ?> prefMap = preference.getAll();

            Object prefObj;
            Object prefValue = null;

            for (String key : prefMap.keySet()) {

                prefObj = prefMap.get(key);

                if (prefObj instanceof String) prefValue = preference.getString(key, STRING_ERROR);
                if (prefObj instanceof Integer) prefValue = preference.getInt(key, INT_ERROR);
                if (prefObj instanceof Boolean) prefValue = preference.getBoolean(key, BOOLEAN_ERROR);
                if (prefObj instanceof Float) prefValue = preference.getFloat(key, FLOAT_ERROR);
                if (prefObj instanceof Long) prefValue = preference.getLong(key, LONG_ERROR);
                if (prefObj instanceof Set) prefValue = preference.getStringSet(key, SET_ERROR);

                Timber.d(String.format("Shared Preference Name: %s, key: %s", pref_name, key));
                Timber.d(String.valueOf(prefValue));
            }

            Timber.i("------------------", "-------------------------------------");

        }

        Timber.i("Loaded Shared Prefs", "------------------------------------");

    }
    private static String _getDefaultSharedPreferencesName(Context context) {
        return context.getPackageName() + "_preferences";
    }
}
