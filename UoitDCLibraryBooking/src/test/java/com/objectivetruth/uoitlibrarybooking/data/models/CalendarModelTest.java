package com.objectivetruth.uoitlibrarybooking.data.models;

import com.objectivetruth.uoitlibrarybooking.SillyNameValuePairClass;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class CalendarModelTest {
    private SillyNameValuePairClass mSillyNameValuePairClass;

    @Before
    public void setUp() throws Exception {
        mSillyNameValuePairClass = new SillyNameValuePairClass("", "", "", "");
    }


    @Test
    public void sillyNameValuePair_emptyArguments_isNotEmpty() {
        assertFalse(mSillyNameValuePairClass.toString().isEmpty());
    }
}
