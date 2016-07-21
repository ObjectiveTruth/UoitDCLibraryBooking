package com.objectivetruth.uoitlibrarybooking;

import com.squareup.otto.Bus;

/**
 * Created by ObjectiveTruth on 8/12/2014.
 */
public final class OttoBusSingleton {
    private static Bus busSingleton = null;

    public static Bus getInstance(){
        if(busSingleton == null){
            busSingleton = new Bus();
        }

        return busSingleton;
    }

}
