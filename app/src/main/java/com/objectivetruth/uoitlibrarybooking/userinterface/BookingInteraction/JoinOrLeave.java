package com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookinginteractionEventWithDateInfo;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCell;
import timber.log.Timber;

public class JoinOrLeave extends Fragment{
    private TimeCell timeCell;
    private String monthWord = "";
    private String dayOfMonthNumber = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bookinginteraction_joinorleave, container, false);
        return view;
    }

    public static JoinOrLeave newInstance(BookinginteractionEventWithDateInfo bookingInteractionEvent) {
        JoinOrLeave fragment =  new JoinOrLeave();
        fragment.timeCell = bookingInteractionEvent.timeCell;
        fragment.monthWord = bookingInteractionEvent.monthWord;
        fragment.dayOfMonthNumber = bookingInteractionEvent.dayOfMonthNumber;
        return fragment;
    }

    /**
     * Remove this Fragment. Effectively undoes the booking interaction fragment loading event
     */
    private void _popFragmentBackstack() {
        Timber.d("Popping backstack");
        getActivity().getSupportFragmentManager().popBackStack();
    }
}
