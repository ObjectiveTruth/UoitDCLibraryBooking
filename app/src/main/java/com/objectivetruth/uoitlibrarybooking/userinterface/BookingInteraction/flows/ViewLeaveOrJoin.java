package com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction.flows;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.BookingInteractionModel;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionEvent;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarDay;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCell;
import com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction.common.InteractionFragment;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import javax.inject.Inject;

public class ViewLeaveOrJoin extends InteractionFragment {
    private TimeCell timeCell;
    private String monthWord = "";
    private String dayOfMonthNumber = "";
    private BookingInteractionEvent bookingInteractionEvent;
    private TextView errorTextView;
    private ImageView pictureOfRoom;
    private CalendarDay calendarDay;
    private CompositeSubscription subscriptions = new CompositeSubscription();
    @Inject BookingInteractionModel bookingInteractionModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bookinginteraction_viewleaveorjoin, container, false);

        TextView roomNumberTextView = (TextView) view.findViewById(R.id.viewleaveorjoin_room_number);
        Button leaveButton = (Button) view.findViewById(R.id.viewleaveorjoin_leave_grou_button);
        Button joinButton = (Button) view.findViewById(R.id.viewleaveorjoin_join_button);
        errorTextView = (TextView) view.findViewById(R.id.viewleaveorjoin_error_text);

        pictureOfRoom = (ImageView) view.findViewById(R.id.room_landing_room_picture);

        if(roomNumberTextView != null) {roomNumberTextView.setText(timeCell.param_room);}
        if(pictureOfRoom != null) {pictureOfRoom.setImageResource(getResourceIDForRoomOrDefault(timeCell));}

        joinButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Timber.i("Clicked VIEWLEAVEORJOIN-JOIN button");
                _hideErrorMessage();
                Toast.makeText(getActivity(), "Not implemented", Toast.LENGTH_LONG).show();
/*                BookingInteractionEventUserRequest request = new BookingInteractionEventUserRequest(
                        timeCell,
                        BookingInteractionEventUserRequestType.VIEWLEAVEORJOIN_JOIN,
                        dayOfMonthNumber,
                        monthWord,
                        new ViewLeaveOrJoinRequest(calendarDay, joinSpinner.getSelectedItem().toString(),
                                currentJoinSpinnerValue, timeCell));

                bookingInteractionModel.getBookingInteractionEventUserRequestSubject().onNext(request);*/
            }
        });

        leaveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Timber.i("Clicked VIEWLEAVEORJOIN-LEAVE button");
                _hideErrorMessage();
                Toast.makeText(getActivity(), "Not implemented", Toast.LENGTH_LONG).show();
/*                BookingInteractionEventUserRequest request = new BookingInteractionEventUserRequest(
                        timeCell,
                        BookingInteractionEventUserRequestType.VIEWLEAVEORJOIN_LEAVE,
                        dayOfMonthNumber,
                        monthWord,
                        new ViewLeaveOrJoinRequest(calendarDay, leaveSpinner.getSelectedItem().toString(),
                                currentLeaveSpinnerValue, timeCell));

                bookingInteractionModel.getBookingInteractionEventUserRequestSubject().onNext(request);*/
            }
        });
        return view;
    }

    public static ViewLeaveOrJoin newInstance(BookingInteractionEvent bookingInteractionEvent) {
        ViewLeaveOrJoin fragment =  new ViewLeaveOrJoin();
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
/*        subscriptions.add(bookingInteractionModel.getBookingInteractionEventObservable()
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Action1<BookingInteractionEvent>() {
                    @Override
                    public void call(BookingInteractionEvent bookingInteractionEvent) {
                        switch(bookingInteractionEvent.type) {
                            case VIEWJOINORLEAVE_RUNNING:
                                break;
                            case VIEWJOINORLEAVE_ERROR:
                                _showErrorMessage(bookingInteractionEvent.message);
                                break;
                            // No default, should fall through
                        }
                    }
                }));*/
    }

    @Override
    protected void teardownViewBindings() {
        subscriptions.unsubscribe();
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
