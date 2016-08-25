package com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction;

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
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.requestoptions.JoinOrLeaveLeaveRequest;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarDay;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCell;
import com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction.common.InteractionFragment;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.HashMap;

public class JoinOrLeave extends InteractionFragment{
    private TimeCell timeCell;
    private String monthWord = "";
    private String dayOfMonthNumber = "";
    private BookingInteractionEvent bookingInteractionEvent;
    private TextView errorTextView;
    private Spinner joinSpinner;
    private Spinner leaveSpinner;
    private ImageView pictureOfRoom;
    private String currentJoinSpinnerValue;
    private String currentLeaveSpinnerValue;
    private CalendarDay calendarDay;
    CompositeSubscription subscriptions = new CompositeSubscription();
    @Inject BookingInteractionModel bookingInteractionModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bookinginteraction_joinorleave, container, false);

        TextView roomNumberTextView = (TextView) view.findViewById(R.id.joinorleave_room_number);
        Button createButton = (Button) view.findViewById(R.id.joinorleave_create_group_button);
        joinSpinner = (Spinner) view.findViewById(R.id.joinorleave_join_spinner);
        leaveSpinner = (Spinner) view.findViewById(R.id.joinorleave_leave_spinner);
        Button leaveButton = (Button) view.findViewById(R.id.joinorleave_leave_grou_button);
        Button joinButton = (Button) view.findViewById(R.id.joinorleave_join_button);
        errorTextView = (TextView) view.findViewById(R.id.joinorleave_error_text);

        pictureOfRoom = (ImageView) view.findViewById(R.id.room_landing_room_picture);

        roomNumberTextView.setText(timeCell.param_room);

        createButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Timber.w("Create Button Clicked, Not Implemented");
            }
        });

        _doInitialRequestToGetSpinnerValues(bookingInteractionModel);


        joinButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
            }
        });

        leaveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Timber.i("Clicked Leave button");
                BookingInteractionEventUserRequest request = new BookingInteractionEventUserRequest(
                        timeCell,
                        BookingInteractionEventUserRequestType.JOINORLEAVE_LEAVE_REQUEST,
                        dayOfMonthNumber,
                        monthWord,
                        new JoinOrLeaveLeaveRequest(calendarDay, leaveSpinner.getSelectedItem().toString(),
                                currentLeaveSpinnerValue, timeCell));

                bookingInteractionModel.getBookingInteractionEventUserRequestSubject().onNext(request);
            }
        });
        return view;
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

    private void _setupSpinnerSuccessCase() {
        subscriptions.add(bookingInteractionModel.getBookingInteractionEventObservable()
                .filter(new Func1<BookingInteractionEvent, Boolean>() {
                    @Override
                    public Boolean call(BookingInteractionEvent bookingInteractionEvent) {
                        return bookingInteractionEvent.type ==
                                BookingInteractionEventType.JOIN_OR_LEAVE_GETTING_SPINNER_VALUES_SUCCESS;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Action1<BookingInteractionEvent>() {
                    @Override
                    public void call(BookingInteractionEvent bookingInteractionEvent) {
                        _setupSpinnerWithValues(
                                bookingInteractionEvent.joinOrLeaveGetSpinnerResult.getLeft(),
                                bookingInteractionEvent.joinOrLeaveGetSpinnerResult.getMiddle());
                        calendarDay = bookingInteractionEvent.joinOrLeaveGetSpinnerResult.getRight();
                    }
                }));
    }

    private void _setupSpinnerErrorCase() {
        subscriptions.add(bookingInteractionModel.getBookingInteractionEventObservable()
                .filter(new Func1<BookingInteractionEvent, Boolean>() {
                    @Override
                    public Boolean call(BookingInteractionEvent bookingInteractionEvent) {
                        return bookingInteractionEvent.type ==
                                BookingInteractionEventType.JOIN_OR_LEAVE_GETTING_SPINNER_VALUES_ERROR;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Action1<BookingInteractionEvent>() {
                    @Override
                    public void call(BookingInteractionEvent bookingInteractionEvent) {
                        HashMap<String, String> errorSpinnerValues = new HashMap<>();
                        errorSpinnerValues.put("ERROR", "error");
                        _setupSpinnerWithValues(errorSpinnerValues, errorSpinnerValues);
                        _showErrorMessage(bookingInteractionEvent.message);
                    }
                }));
    }

    private void _setupSpinnerRunningCase() {
        subscriptions.add(bookingInteractionModel.getBookingInteractionEventObservable()
                .filter(new Func1<BookingInteractionEvent, Boolean>() {
                    @Override
                    public Boolean call(BookingInteractionEvent bookingInteractionEvent) {
                        return bookingInteractionEvent.type ==
                                BookingInteractionEventType.JOIN_OR_LEAVE_GETTING_SPINNER_VALUES_RUNNING;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Action1<BookingInteractionEvent>() {
                    @Override
                    public void call(BookingInteractionEvent bookingInteractionEvent) {
                        HashMap<String, String> loadingSpinnerValues = new HashMap<>();
                        loadingSpinnerValues.put("Loading...", "loading");
                        _setupSpinnerWithValues(loadingSpinnerValues, loadingSpinnerValues);
                    }
                }));
    }

    @Override
    protected void setupViewBindings() {
        _setupSpinnerSuccessCase();
        _setupSpinnerRunningCase();
        _setupSpinnerErrorCase();
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
                .onNext(
                        new BookingInteractionEventUserRequest(
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
}
