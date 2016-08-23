package com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionEvent;
import com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction.common.InteractionFragment;
import timber.log.Timber;

public class Success extends InteractionFragment{
    private BookingInteractionEvent bookingInteractionEvent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bookinginteraction_success, container, false);
        TextView body = (TextView) view.findViewById(R.id.bookingInteraction_success_body);
        body.setText(_setMessageOrDefault(bookingInteractionEvent.message));

        TextView title = (TextView) view.findViewById(R.id.bookingInteraction_success_title);

        Button addToCalendarButton = (Button) view.findViewById(R.id.bookingInteraction_success_add_to_calendar_button);

        Button okButton = (Button) view.findViewById(R.id.bookingInteraction_success_ok_button);
        _setupOkButton(okButton);


        return view;
    }

    private String _setMessageOrDefault(String message) {
        return message == null ? "No Information Available" : message;
    }

    public static Success newInstance(BookingInteractionEvent bookingInteractionEvent) {
        Success fragment = new Success();
        fragment.bookingInteractionEvent = bookingInteractionEvent;
        return new Success();
    }

    private void _setupOkButton(Button button) {
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                _popFragmentBackstack();
            }

        });
    }

    /**
     * Remove this Fragment. Effectively undoes the booking interaction fragment loading event
     */
    private void _popFragmentBackstack() {
        Timber.d("Popping backstack");
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    protected void setupViewBindings() {

    }

    @Override
    protected void teardownViewBindings() {

    }
}
