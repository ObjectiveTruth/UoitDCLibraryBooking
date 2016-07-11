package com.objectivetruth.uoitlibrarybooking;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class ApplicationTest {
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

