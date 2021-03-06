package com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction.flows;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.BookingInteractionModel;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionEvent;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionEventType;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionEventUserRequest;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionEventUserRequestType;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.requestoptions.JoinOrLeaveRequest;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarDay;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCell;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCellType;
import com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction.common.InteractionFragment;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.HashMap;

import static com.objectivetruth.uoitlibrarybooking.common.constants.LIBRARY.BOOK_WEBPAGE;

public class JoinOrLeave extends InteractionFragment{
    private TimeCell timeCell;
    private String monthWord = "";
    private String dayOfMonthNumber = "";
    private BookingInteractionEvent bookingInteractionEvent;
    private TextView errorTextView;
    private Spinner joinSpinner;
    private Spinner leaveSpinner;
    private ProgressBar joinProgressBar;
    private ProgressBar leaveProgressBar;
    private ProgressBar createProgressBar;
    private Button joinButton;
    private Button leaveButton;
    private Button createButton;
    private ImageView pictureOfRoom;
    private String currentJoinSpinnerValue;
    private String currentLeaveSpinnerValue;
    private CalendarDay calendarDay;
    private CompositeSubscription subscriptions = new CompositeSubscription();
    @Inject BookingInteractionModel bookingInteractionModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bookinginteraction_joinorleave, container, false);

        TextView roomNumberTextView = (TextView) view.findViewById(R.id.joinorleave_room_number);
        createButton = (Button) view.findViewById(R.id.joinorleave_create_group_button);
        joinSpinner = (Spinner) view.findViewById(R.id.joinorleave_join_spinner);
        leaveSpinner = (Spinner) view.findViewById(R.id.joinorleave_leave_spinner);
        leaveButton = (Button) view.findViewById(R.id.joinorleave_leave_grou_button);
        joinButton = (Button) view.findViewById(R.id.joinorleave_join_button);
        joinProgressBar = (ProgressBar) view.findViewById(R.id.joinorleave_join_loadingbar);
        leaveProgressBar = (ProgressBar) view.findViewById(R.id.joinorleave_leave_loadingbar);
        createProgressBar = (ProgressBar) view.findViewById(R.id.joinorleave_create_loadingbar);
        errorTextView = (TextView) view.findViewById(R.id.joinorleave_error_text);

        pictureOfRoom = (ImageView) view.findViewById(R.id.room_landing_room_picture);

        if(roomNumberTextView != null) {roomNumberTextView.setText(timeCell.param_room);}
        if(pictureOfRoom != null) {pictureOfRoom.setImageResource(getResourceIDForRoomOrDefault(timeCell));}

        createButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Timber.i("Clicked JOINORLEAVE-CREATE button");
                // Done to recycle the booking flow, we simply mutate this timeCell into a booking one
                TimeCell bookTimeCell = _convertJoinOrLeaveTimeCellToBookTimeCell(timeCell);
                bookingInteractionModel.getBookingInteractionEventReplaySubject()
                        .onNext(new BookingInteractionEvent(bookTimeCell,
                                BookingInteractionEventType.BOOK,
                                dayOfMonthNumber,
                                monthWord));
            }
        });

        _doInitialRequestToGetSpinnerValues(bookingInteractionModel);


        joinButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Timber.i("Clicked JOINORLEAVE-JOIN button");
                _hideErrorMessage();
                BookingInteractionEventUserRequest request = new BookingInteractionEventUserRequest(
                        timeCell,
                        BookingInteractionEventUserRequestType.JOINORLEAVE_JOIN_REQUEST,
                        dayOfMonthNumber,
                        monthWord,
                        new JoinOrLeaveRequest(calendarDay, joinSpinner.getSelectedItem().toString(),
                                currentJoinSpinnerValue, timeCell));

                bookingInteractionModel.getBookingInteractionEventUserRequestSubject().onNext(request);
            }
        });

        leaveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Timber.i("Clicked JOINORLEAVE-LEAVE button");
                _hideErrorMessage();
                BookingInteractionEventUserRequest request = new BookingInteractionEventUserRequest(
                        timeCell,
                        BookingInteractionEventUserRequestType.JOINORLEAVE_LEAVE_REQUEST,
                        dayOfMonthNumber,
                        monthWord,
                        new JoinOrLeaveRequest(calendarDay, leaveSpinner.getSelectedItem().toString(),
                                currentLeaveSpinnerValue, timeCell));

                bookingInteractionModel.getBookingInteractionEventUserRequestSubject().onNext(request);
            }
        });
        return view;
    }

    private TimeCell _convertJoinOrLeaveTimeCellToBookTimeCell(TimeCell joinOrLeaveTimeCell) {
        Timber.i(joinOrLeaveTimeCell.toString());
        TimeCell bookTimeCell = new TimeCell();
        bookTimeCell.timeCellType = TimeCellType.BOOKING_OPEN;
        bookTimeCell.param_get_link = joinOrLeaveTimeCell.param_get_link;
        bookTimeCell.param_next = BOOK_WEBPAGE;
        bookTimeCell.param_starttime = joinOrLeaveTimeCell.param_starttime;
        bookTimeCell.param_room = joinOrLeaveTimeCell.param_room;
        bookTimeCell.param_eventargument = joinOrLeaveTimeCell.param_eventargument;
        bookTimeCell.param_eventtarget = joinOrLeaveTimeCell.param_eventtarget;
        return bookTimeCell;
    }

    public static JoinOrLeave newInstance(BookingInteractionEvent bookingInteractionEvent) {
        JoinOrLeave fragment =  new JoinOrLeave();
        fragment.timeCell = bookingInteractionEvent.timeCell;
        fragment.monthWord = bookingInteractionEvent.monthWord;
        fragment.dayOfMonthNumber = bookingInteractionEvent.dayOfMonthNumber;
        fragment.bookingInteractionEvent = bookingInteractionEvent;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((UOITLibraryBookingApp) getActivity().getApplication()).getComponent().inject(this);
    }

    @Override
    protected void setupViewBindings() {
        subscriptions.add(bookingInteractionModel.getBookingInteractionEventObservable()
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Action1<BookingInteractionEvent>() {
                    @Override
                    public void call(BookingInteractionEvent bookingInteractionEvent) {
                        switch(bookingInteractionEvent.type) {
                            case JOIN_OR_LEAVE_JOIN_RUNNING:
                            case JOIN_OR_LEAVE_LEAVE_RUNNING:
                                _hideJoinAndLeaveButtonsAndShowLoadingBars();
                                _hideCreateButtonAndShowCreateLoadingBar();
                                break;
                            case JOIN_OR_LEAVE_GETTING_SPINNER_VALUES_RUNNING:
                                HashMap<String, String> loadingSpinnerValues = new HashMap<>();
                                loadingSpinnerValues.put("Loading...", "loading");
                                _setupSpinnerWithValues(loadingSpinnerValues, loadingSpinnerValues);
                                _hideJoinAndLeaveButtonsAndShowLoadingBars();
                                _showCreateButtonAndHideCreateLoadingBar();
                                break;

                            case JOIN_OR_LEAVE_GETTING_SPINNER_VALUES_ERROR_NO_VALUES:
                                Toast.makeText(getActivity(), R.string.ERROR_INVALID_EVENT, Toast.LENGTH_LONG).show();
                                popFragmentBackstack();
                                break;

                            case JOIN_OR_LEAVE_GETTING_SPINNER_VALUES_ERROR:
                                HashMap<String, String> errorSpinnerValues = new HashMap<>();
                                errorSpinnerValues.put("ERROR", "error");
                                _setupSpinnerWithValues(errorSpinnerValues, errorSpinnerValues);
                                _showErrorMessage(bookingInteractionEvent.message);
                                _showJoinAndLeaveButtonsAndHideLoadingBars();
                                _showCreateButtonAndHideCreateLoadingBar();
                                break;

                            case JOIN_OR_LEAVE_GETTING_SPINNER_VALUES_SUCCESS:
                                _setupSpinnerWithValues(
                                        bookingInteractionEvent.joinOrLeaveGetSpinnerResult.getLeft(),
                                        bookingInteractionEvent.joinOrLeaveGetSpinnerResult.getMiddle());
                                calendarDay = bookingInteractionEvent.joinOrLeaveGetSpinnerResult.getRight();
                                _showJoinAndLeaveButtonsAndHideLoadingBars();
                                _showCreateButtonAndHideCreateLoadingBar();
                                break;

                            case JOIN_OR_LEAVE_JOIN_ERROR:
                            case JOIN_OR_LEAVE_LEAVE_ERROR:
                                _showErrorMessage(bookingInteractionEvent.message);
                                _showJoinAndLeaveButtonsAndHideLoadingBars();
                                _showCreateButtonAndHideCreateLoadingBar();
                                break;

                            // No default, should fall through
                        }
                    }
                }));
    }

    @SuppressWarnings("Duplicates")
    private void _setupSpinnerWithValues(final HashMap<String, String> joinSpinnerValues,
                                         final HashMap<String, String> leaveSpinnerValues) {
        String[] joinSpinnerValueArr = joinSpinnerValues.keySet().toArray(new String[joinSpinnerValues.keySet().size()]);
        String[] leaveSpinnerValueArr = leaveSpinnerValues.keySet().toArray(new String[joinSpinnerValues.keySet().size()]);

        ArrayAdapter<String> joinAdapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, joinSpinnerValueArr);
        joinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        joinSpinner.setAdapter(joinAdapter);

        ArrayAdapter<String> leaveAdapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, leaveSpinnerValueArr);
        leaveAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        leaveSpinner.setAdapter(leaveAdapter);

        joinSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapter, View view,
                                       int position, long id) {
                String labelOfSelection = ((String) adapter.getItemAtPosition(position));
                currentJoinSpinnerValue = joinSpinnerValues.get(labelOfSelection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
                String labelOfSelection = ((String) adapter.getItemAtPosition(0));
                currentJoinSpinnerValue = joinSpinnerValues.get(labelOfSelection);
            }
        });

        leaveSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapter, View view,
                                       int position, long id) {
                String labelOfSelection = ((String) adapter.getItemAtPosition(position));
                currentLeaveSpinnerValue = leaveSpinnerValues.get(labelOfSelection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
                String labelOfSelection = ((String) adapter.getItemAtPosition(0));
                currentLeaveSpinnerValue = leaveSpinnerValues.get(labelOfSelection);
            }
        });
    }

    @Override
    protected void teardownViewBindings() {
        subscriptions.unsubscribe();
    }

    /**
     * When the view is initially called, this will fire, to get the spinner values asynchronously
     * @param bookingInteractionModel
     */
    private void _doInitialRequestToGetSpinnerValues(BookingInteractionModel bookingInteractionModel) {
        bookingInteractionModel
                .getBookingInteractionEventUserRequestSubject()
                .onNext(new BookingInteractionEventUserRequest(
                        timeCell,
                        BookingInteractionEventUserRequestType.JOINORLEAVE_GETTING_SPINNER_VALUES_REQUEST,
                        dayOfMonthNumber,
                        monthWord,
                        null));
    }

    private void _showErrorMessage(String message) {
        Float SEE_THROUGH = 0.5f;
        errorTextView.setVisibility(View.VISIBLE);
        errorTextView.setText(message);
        pictureOfRoom.setAlpha(SEE_THROUGH);
    }

    private void _hideErrorMessage() {
        Float FULLY_VISIBLE = 1.0f;
        errorTextView.setVisibility(View.INVISIBLE);
        errorTextView.setText("");
        pictureOfRoom.setAlpha(FULLY_VISIBLE);
    }

    private void _hideJoinAndLeaveButtonsAndShowLoadingBars() {
        joinButton.setVisibility(View.INVISIBLE);
        leaveButton.setVisibility(View.INVISIBLE);
        joinProgressBar.setVisibility(View.VISIBLE);
        leaveProgressBar.setVisibility(View.VISIBLE);
    }

    private void _showJoinAndLeaveButtonsAndHideLoadingBars() {
        joinButton.setVisibility(View.VISIBLE);
        leaveButton.setVisibility(View.VISIBLE);
        joinProgressBar.setVisibility(View.INVISIBLE);
        leaveProgressBar.setVisibility(View.INVISIBLE);
    }

    private void _showCreateButtonAndHideCreateLoadingBar() {
        createButton.setVisibility(View.VISIBLE);
        createProgressBar.setVisibility(View.INVISIBLE);
    }

    private void _hideCreateButtonAndShowCreateLoadingBar() {
        createButton.setVisibility(View.INVISIBLE);
        createProgressBar.setVisibility(View.VISIBLE);
    }
}
