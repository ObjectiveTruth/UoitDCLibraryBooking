package com.objectivetruth.uoitlibrarybooking.ActivityAboutMeTests;

import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import com.objectivetruth.uoitlibrarybooking.ActivityAboutMe;
import com.objectivetruth.uoitlibrarybooking.R;
import com.squareup.spoon.Spoon;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DrawerInstrumentationTest {
    private ActivityAboutMe mActivity;

    @Rule
    public ActivityTestRule<ActivityAboutMe> mActivityRule = new ActivityTestRule<ActivityAboutMe>(
            ActivityAboutMe.class);

    @Before
    public void setUp() {
        mActivity = mActivityRule.getActivity();
    }

    @After
    public void tearDown() {
        mActivity = null;
    }


    @Test
    public void isDrawerNotDisplayedAtStart() {
        Spoon.screenshot(mActivity, "before_opening_drawer");
        onView(ViewMatchers.withId(R.id.left_drawer_aboutme)).check(matches(not((isDisplayed()))));
    }

    @Test
    public void isDrawerDisplayedCorrectlyAfterClicking() {
        Spoon.screenshot(mActivity, "before_opening_drawer");
        onView(withId(R.id.drawer_layout_aboutme)).perform(DrawerActions.open());
        onView(withId(R.id.left_drawer_aboutme)).check(matches((isDisplayed())));
        Spoon.screenshot(mActivity, "after_opening_drawer");
    }
}
