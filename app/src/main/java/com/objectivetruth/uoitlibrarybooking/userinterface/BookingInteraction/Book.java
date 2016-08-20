package com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.BookingInteractionModel;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionEventType;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookinginteractionEventWithDateInfo;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCell;

import javax.inject.Inject;

public class Book extends Fragment{
    private TimeCell timeCell;
    private String monthWord = "";
    private String dayOfMonthNumber = "";
    private static final String TIME_CELL_BUNDLE_KEY = "TIME_CELL_BUNDLE_KEY";
    private static final String MONTH_WORD_BUNDLE_KEY = "MONTH_WORD_BUNDLE_KEY";
    private static final String DAY_OF_MONTH_NUMBER_BUNDLE_KEY = "DAY_OF_MONTH_NUMBER_BUNDLE_KEY";
    @Inject BookingInteractionModel bookingInteractionModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bookinginteraction_book, container, false);

        TextView roomNumberTextView = (TextView) view.findViewById(R.id.interaction_book_room_number);
        if(roomNumberTextView != null) {roomNumberTextView.setText(timeCell.param_room.toUpperCase());}

        TextView dateField = (TextView) view.findViewById(R.id.book_date_actual);
        if(dateField != null) {dateField.setText(_getFormattedDateString());}

        TextView errorTextView = (TextView) view.findViewById(R.id.book_error_message_actual);

        EditText groupNameEditText = (EditText) view.findViewById(R.id.book_group_name_actual);

        ImageButton commentImageButton = (ImageButton) view.findViewById(R.id.comment_button);

        EditText groupCodeEditText = (EditText) view.findViewById(R.id.book_group_code_actual);

        Button createButton = (Button) view.findViewById(R.id.bookingInteraction_book_create_button);
        _setupCreateButton(createButton);

        ImageButton groupCodeInfoImageButton = (ImageButton) view.findViewById(R.id.info_group_code);

        Spinner durationSpinner = (Spinner) view.findViewById(R.id.book_spinner_duration);
        _setupDurationSpinner(durationSpinner);
        return view;
    }

    static public Book newInstance(BookinginteractionEventWithDateInfo bookinginteractionEventWithDateInfo) {
        Book fragment = new Book();
        fragment.timeCell = bookinginteractionEventWithDateInfo.timeCell;
        fragment.monthWord = bookinginteractionEventWithDateInfo.monthWord;
        fragment.dayOfMonthNumber = bookinginteractionEventWithDateInfo.dayOfMonthNumber;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((UOITLibraryBookingApp) getActivity().getApplication()).getComponent().inject(this);
        if(savedInstanceState != null) {
            _loadPreviousStateIfAvailable(savedInstanceState);
        }
    }

    private void _setupCreateButton(Button createButton) {
        if(createButton != null) {
            createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bookingInteractionModel.getBookingInteractionEventReplaySubject()
                            .onNext(new BookinginteractionEventWithDateInfo(
                                    timeCell, BookingInteractionEventType.SUCCESS,
                                    dayOfMonthNumber, monthWord
                            ));
                }
            });
        }
    }

    private String _getFormattedDateString() {
        return dayOfMonthNumber + ", " + monthWord + " @ " + timeCell.param_starttime;
    }

    private void _loadPreviousStateIfAvailable(Bundle inState) {
        timeCell = inState.getParcelable(TIME_CELL_BUNDLE_KEY);
        monthWord = inState.getString(MONTH_WORD_BUNDLE_KEY);
        dayOfMonthNumber = inState.getString(DAY_OF_MONTH_NUMBER_BUNDLE_KEY);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TIME_CELL_BUNDLE_KEY, timeCell);
        outState.putString(DAY_OF_MONTH_NUMBER_BUNDLE_KEY, dayOfMonthNumber);
        outState.putString(MONTH_WORD_BUNDLE_KEY, monthWord);
    }
}
