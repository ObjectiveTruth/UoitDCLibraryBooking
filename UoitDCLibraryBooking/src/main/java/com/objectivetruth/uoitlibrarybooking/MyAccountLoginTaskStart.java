package com.objectivetruth.uoitlibrarybooking;

/**
 * Created by ObjectiveTruth on 8/12/2014.
 */
public class MyAccountLoginTaskStart {
    String[] loginInput;
    int options;
    /**
     * Event that notifies a request to start an account login
     * @param loginInput String Array where [0] is username, [1] is password
     * @param options   based on the constants in DiaFragMyAccount, AUTOREFRESH or USERINITIATED, to show progress bar
     */
    public MyAccountLoginTaskStart(String[] loginInput, int options){
        this.loginInput = loginInput;
        this.options = options;
    }

}
