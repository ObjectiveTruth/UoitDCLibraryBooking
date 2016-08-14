package com.objectivetruth.uoitlibrarybooking.data.models;

import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionWebService;

public class BookingInteractionModel {
    private BookingInteractionWebService bookingInteractionWebService;

    public BookingInteractionModel(UOITLibraryBookingApp mApplication,
                                   BookingInteractionWebService bookingInteractionWebService) {
        this.bookingInteractionWebService = bookingInteractionWebService;
    }
}
