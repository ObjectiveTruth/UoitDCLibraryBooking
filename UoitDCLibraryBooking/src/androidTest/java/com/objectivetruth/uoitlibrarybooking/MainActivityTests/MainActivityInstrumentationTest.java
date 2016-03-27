package com.objectivetruth.uoitlibrarybooking.MainActivityTests;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import com.objectivetruth.uoitlibrarybooking.DisableAnimationsRule;
import com.objectivetruth.uoitlibrarybooking.MainActivity;
import com.objectivetruth.uoitlibrarybooking.R;
import com.squareup.spoon.Spoon;
import org.junit.*;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityInstrumentationTest {
    private MainActivity mActivity;

    @ClassRule
    public static DisableAnimationsRule disableAnimationsRule = new DisableAnimationsRule();


    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Before
    public void setUp() {
        mActivity = mActivityRule.getActivity();
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());
        // Use commit instead of apply. Espresso handles background thread timings for you!
        // Stop all the animations, otherwise will go on forever waiting for all animations to stop
        preferences.edit()
                .putBoolean(MainActivity.SHARED_PREF_HASLEARNED_MYACCOUNT, true)
                .putBoolean(MainActivity.SHARED_PREF_HAS_LEARNED_HELP, true)
                .putBoolean(MainActivity.hasLEARNED_REFRESH, true)
                .commit();
    }

    @After
    public void tearDown() {
        mActivity = null;
        PreferenceManager.getDefaultSharedPreferences((InstrumentationRegistry.getTargetContext()))
                .edit().clear().commit();
    }

    @Test
    public void isGridViewDisplayed(){
        Spoon.screenshot(mActivity, "first_view");
        onView(ViewMatchers.withId(R.id.drawer_frame_layout)).check(matches(isDisplayed()));
    }
}
