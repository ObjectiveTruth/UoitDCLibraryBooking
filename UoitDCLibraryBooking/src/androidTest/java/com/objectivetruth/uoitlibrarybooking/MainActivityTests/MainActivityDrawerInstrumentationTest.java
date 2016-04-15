package com.objectivetruth.uoitlibrarybooking.MainActivityTests;

import android.support.test.espresso.contrib.DrawerActions;
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
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityDrawerInstrumentationTest {
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

    }

    @Test
    public void isDrawerNotDisplayedAtStart() {
        _clearWhatsNewDialog();

        Spoon.screenshot(mActivity, "before_opening_drawer");
        onView(withId(R.id.left_drawer)).check(matches(not((isDisplayed()))));
    }

    @Test
    public void isDrawerDisplayedCorrectlyAfterClicking() {
        _clearWhatsNewDialog();

        Spoon.screenshot(mActivity, "before_opening_drawer");

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.left_drawer)).check(matches((isDisplayed())));
        Spoon.screenshot(mActivity, "after_opening_drawer");
    }

    private void _clearWhatsNewDialog() {
        onView(withId(android.R.id.button1)).perform(click());
    }
}
