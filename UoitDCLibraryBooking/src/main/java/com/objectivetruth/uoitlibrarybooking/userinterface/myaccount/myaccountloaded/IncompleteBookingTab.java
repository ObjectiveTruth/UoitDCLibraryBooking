package com.objectivetruth.uoitlibrarybooking.userinterface.myaccount.myaccountloaded;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.MyAccountBooking;

import java.util.ArrayList;

public class IncompleteBookingTab extends Fragment {
    private ArrayList<MyAccountBooking> myAccountBookings;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.my_account_loaded_booking_tab, container, false);
    }

    public static IncompleteBookingTab newInstance(ArrayList<MyAccountBooking> myAccountBookings) {
        IncompleteBookingTab returnFragment = new IncompleteBookingTab();
        returnFragment.myAccountBookings = myAccountBookings;
        return returnFragment;
    }
}
