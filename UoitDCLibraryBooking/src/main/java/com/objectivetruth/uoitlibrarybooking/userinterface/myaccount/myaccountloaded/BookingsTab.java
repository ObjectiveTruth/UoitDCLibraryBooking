package com.objectivetruth.uoitlibrarybooking.userinterface.myaccount.myaccountloaded;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.MyAccountBooking;

import java.util.ArrayList;

public class BookingsTab extends Fragment{
    private ArrayList<MyAccountBooking> myAccountBookings;
    private Context context;
    public int positionInParentPagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View bookingTabView = inflater.inflate(R.layout.my_account_loaded_booking_tab, container, false);

        GridView gridView = (GridView) bookingTabView.findViewById(R.id.my_account_loaded_gridview);
        BookingsAdapter adapter = new BookingsAdapter(context, myAccountBookings);
        gridView.setAdapter(adapter);

        return bookingTabView;
    }

    public static BookingsTab newInstance(ArrayList<MyAccountBooking> myAccountBookings, Context context,
                                          int positionInParentPagerAdapter) {
        BookingsTab returnFragment = new BookingsTab();
        returnFragment.myAccountBookings = myAccountBookings;
        returnFragment.context = context;
        returnFragment.positionInParentPagerAdapter = positionInParentPagerAdapter;
        return returnFragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
