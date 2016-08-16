package com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.objectivetruth.uoitlibrarybooking.MainActivity;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCell;

public class BookingInteraction extends Fragment {
    private static final String BOOKING_INTERACTION_TITLE = "Booking";
    private static final String TIME_CELL_BUNDLE_KEY = "TIME_CELL_BUNDLE_KEY";
    private TimeCell timeCell;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bookinginteraction_root, container, false);
        _setTitle(BOOKING_INTERACTION_TITLE);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).setDrawerState(false);
        _setupViewBindings(timeCell);
    }

    @Override
    public void onStop() {
        ((MainActivity) getActivity()).setDrawerState(true);
        super.onStop();
    }

    private void _setupViewBindings(TimeCell timeCell) {
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.bookinginteraction_content_frame, Book.newInstance(timeCell))
                .commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity) getActivity()).setIsNonDrawerScreenShowing(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((MainActivity) getActivity()).setIsNonDrawerScreenShowing(false);
    }

    public static BookingInteraction newInstance(TimeCell timeCell) {
        BookingInteraction fragment = new BookingInteraction();
        fragment.timeCell = timeCell;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(savedInstanceState != null) {
            _loadPreviousStateIfAvailable(savedInstanceState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TIME_CELL_BUNDLE_KEY, timeCell);
    }

    private void _loadPreviousStateIfAvailable(Bundle inState) {
        timeCell = inState.getParcelable(TIME_CELL_BUNDLE_KEY);
    }

    private void _setTitle(String title) {
        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(title);
        }
    }
}
