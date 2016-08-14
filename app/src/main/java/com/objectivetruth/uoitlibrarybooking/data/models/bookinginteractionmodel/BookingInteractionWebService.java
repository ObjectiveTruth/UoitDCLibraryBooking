package com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel;

import com.android.volley.RequestQueue;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;

import javax.inject.Inject;

public class BookingInteractionWebService {
    @Inject RequestQueue requestQueue;

    public BookingInteractionWebService(UOITLibraryBookingApp mApplication) {
        mApplication.getComponent().inject(this);
    }
}
