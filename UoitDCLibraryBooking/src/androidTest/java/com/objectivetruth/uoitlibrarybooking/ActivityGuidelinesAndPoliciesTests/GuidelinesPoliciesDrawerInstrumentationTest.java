package com.objectivetruth.uoitlibrarybooking.ActivityGuidelinesAndPoliciesTests;

import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import com.objectivetruth.uoitlibrarybooking.GuidelinesPoliciesActivity;
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
public class GuidelinesPoliciesDrawerInstrumentationTest {
    private GuidelinesPoliciesActivity mActivity;

    @Rule
    public ActivityTestRule<GuidelinesPoliciesActivity> mActivityRule = new ActivityTestRule<GuidelinesPoliciesActivity>(
            GuidelinesPoliciesActivity.class);

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
        onView(withId(R.id.left_drawer_guidelinespolicies)).check(matches(not((isDisplayed()))));
    }

    @Test
    public void isDrawerDisplayedCorrectlyAfterClicking() {
        Spoon.screenshot(mActivity, "before_opening_drawer");
        onView(withId(R.id.drawer_layout_guidelines_policies)).perform(DrawerActions.open());
        onView(withId(R.id.left_drawer_guidelinespolicies)).check(matches((isDisplayed())));
        Spoon.screenshot(mActivity, "after_opening_drawer");
    }
}
