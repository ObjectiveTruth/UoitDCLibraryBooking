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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

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
    }

    @After
    public void tearDown() {
        mActivity = null;
    }

    @Test
    public void initialWhatsNewDialogWhenInstalledForFirstTime(){
        Spoon.screenshot(mActivity, "initial_view");

        onView(withId(android.R.id.button1)).check(matches(isClickable()));
        Spoon.screenshot(mActivity, "whatsnew_dialog");

        onView(withId(android.R.id.button1)).perform(click());
        Spoon.screenshot(mActivity, "after_dismiss_whatsnew");
    }

    @Test
    public void showMyAccountWhenMyIconClicked() {
        Spoon.screenshot(mActivity, "initial_view");
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.user_account)).check(matches(isClickable()));
        onView(withId(R.id.user_account)).perform(click());

        onView(ViewMatchers.withId(R.id.titleMyAccount)).check(matches(isDisplayed()));
        Spoon.screenshot(mActivity, "after_my_account_clicked");
    }
}
