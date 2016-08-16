package com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
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

        View view = inflater.inflate(R.layout.bookinginteraction_book, container, false);
        _setTitle(BOOKING_INTERACTION_TITLE);

        TextView roomNumberTextView = (TextView) view.findViewById(R.id.interaction_book_room_number);
        if(roomNumberTextView != null) {roomNumberTextView.setText(timeCell.param_room.toUpperCase());}

        TextView dateField = (TextView) view.findViewById(R.id.book_date_actual);
        if(dateField != null) {dateField.setText(timeCell.param_starttime);}

        TextView errorTextView = (TextView) view.findViewById(R.id.book_error_message_actual);


        EditText groupNameEditText = (EditText) view.findViewById(R.id.book_group_name_actual);

        ImageButton commentImageButton = (ImageButton) view.findViewById(R.id.comment_button);

        EditText groupCodeEditText = (EditText) view.findViewById(R.id.book_group_code_actual);

        Button titleButton = (Button) view.findViewById(R.id.book_room_number_actual);

        ImageButton groupCodeInfoImageButton = (ImageButton) view.findViewById(R.id.info_group_code);


        Spinner durationSpinner = (Spinner) view.findViewById(R.id.book_spinner_duration);
        _setupDurationSpinner(durationSpinner);

        return view;
    }

    private void _setupDurationSpinner(Spinner durationSpinner) {
        ArrayAdapter<CharSequence> durationAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.duration, android.R.layout.simple_spinner_item);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        durationSpinner.setAdapter(durationAdapter);
        durationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> adapter, View view,
                                       int position, long id) {
                String[] timeToDecimal = new String[]{"0.5", "1.0", "1.5", "2"};
                String durationSpinnerValue = timeToDecimal[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) { }
        });
        durationSpinner.setSelection(1);
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).setDrawerState(false);
    }

    @Override
    public void onStop() {
        ((MainActivity) getActivity()).setDrawerState(true);
        super.onStop();
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
