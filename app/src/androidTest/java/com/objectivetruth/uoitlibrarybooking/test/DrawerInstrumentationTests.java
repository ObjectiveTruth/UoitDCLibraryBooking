package com.objectivetruth.uoitlibrarybooking.test;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.objectivetruth.uoitlibrarybooking.MainActivity;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.app.AppModule;
import com.objectivetruth.uoitlibrarybooking.app.DaggerMockAppComponent;
import com.objectivetruth.uoitlibrarybooking.app.MockAppComponent;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.app.networking.MockHttpStack;
import com.objectivetruth.uoitlibrarybooking.data.DataModule;
import com.squareup.spoon.Spoon;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DrawerInstrumentationTests {
    private Activity mActivity;

    @ClassRule
    //public static DisableAnimationsRule disableAnimationsRule = new DisableAnimationsRule();

    @Rule
    public static ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class,
            true,       // Initial touch mode
            false);     // Auto-launch activity

    @Before
    public void setUp() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        final UOITLibraryBookingApp mApplication = (UOITLibraryBookingApp) instrumentation.getTargetContext()
                .getApplicationContext();

        MockAppComponent testComponent = DaggerMockAppComponent.builder()
                .appModule(new AppModule(mApplication))
                .dataModule(new DataModule(mApplication) {
                    @Override
                    protected RequestQueue providesRequestQueue() {
                        return Volley.newRequestQueue(mApplication, new MockHttpStack(mApplication));
                    }

                })
                .build();

        mApplication.setComponent(testComponent);
        mActivityRule.launchActivity(new Intent());
        mActivity = mActivityRule.getActivity();
    }

    @Test
    public void testDrawerGoesToCorrectScreens() {
        Spoon.screenshot(mActivity, "initial_screen");

        onView(withId(android.R.id.button1)).perform(click());
        Spoon.screenshot(mActivity, "after_dismiss_dialog");
        onView(withId(R.id.calendar_content_frame)).check(matches((isDisplayed())));

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        Spoon.screenshot(mActivity, "after_open_drawer");

        onView(withText("Library Booking Policies")).perform(click());
        Spoon.screenshot(mActivity, "after_open_guidelines_policies");
        onView(withText("Booking Guidelines")).check(matches(isDisplayed()));

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        Spoon.screenshot(mActivity, "after_open_drawer_2");

        onView(withText("About")).perform(click());
        Spoon.screenshot(mActivity, "after_open_about");
        onView(withText(R.string.about_description)).check(matches(isDisplayed()));

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        Spoon.screenshot(mActivity, "after_open_drawer_3");

        onView(withText("My Account")).perform(click());
        Spoon.screenshot(mActivity, "after_open_my_account");
        onView(withText("Login")).check(matches(isDisplayed()));

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        Spoon.screenshot(mActivity, "after_open_drawer_4");

        onView(withText("Calendar")).perform(click());
        Spoon.screenshot(mActivity, "after_open_calendar");
        onView(withId(R.id.calendar_content_frame)).check(matches((isDisplayed())));
    }
}
