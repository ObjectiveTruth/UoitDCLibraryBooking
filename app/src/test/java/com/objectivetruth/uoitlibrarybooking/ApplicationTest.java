package com.objectivetruth.uoitlibrarybooking;

import com.objectivetruth.uoitlibrarybooking.statelessutilities.Triple;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class ApplicationTest {
    private Triple<String, String, String> triple;

    @Before
    public void setUp() throws Exception {
        triple = new Triple<>("foo", "", "");
    }


    @Test
    public void tripleInitializingIsNotEmpty() {
        assertFalse(triple.getLeft().isEmpty());
    }
}

