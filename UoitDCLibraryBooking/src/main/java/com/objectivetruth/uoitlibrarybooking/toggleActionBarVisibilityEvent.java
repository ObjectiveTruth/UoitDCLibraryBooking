package com.objectivetruth.uoitlibrarybooking;

/**
 * Created by ObjectiveTruth on 8/17/2014.
 */
class ToggleActionBarVisibilityEvent {
    boolean hideTheActionBar;

    /**
     * Sends a message to MainActivity to either show or hide the actionBar tabs.
     * The tabs will reappears as they were before and will fire off an event listener for
     * tabs changing
     *
     * @param hideTheActionBar true = hide the tabs, false = show the tabs
     */
    public ToggleActionBarVisibilityEvent(boolean hideTheActionBar){
        this.hideTheActionBar = hideTheActionBar;
    }
}
