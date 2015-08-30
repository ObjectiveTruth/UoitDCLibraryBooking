package com.objectivetruth.uoitlibrarybooking;

import java.util.ArrayList;

/**
 * Created by ObjectiveTruth on 8/12/2014.
 */
public class MyAccountLoginResultEvent{
    String errorMessage;
    ArrayList<String[]> result;

    public MyAccountLoginResultEvent(String errorMessage, ArrayList<String[]> result){
        this.result = result;
        this.errorMessage = errorMessage;
    }
}