package com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.objectivetruth.uoitlibrarybooking.R;
import timber.log.Timber;

public class Success extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bookinginteraction_success, container, false);
        TextView body = (TextView) view.findViewById(R.id.bookingInteraction_success_body);

        TextView title = (TextView) view.findViewById(R.id.bookingInteraction_success_title);

        Button addToCalendarButton = (Button) view.findViewById(R.id.bookingInteraction_success_add_to_calendar_button);

        Button okButton = (Button) view.findViewById(R.id.bookingInteraction_success_ok_button);
        _setupOkButton(okButton);


        return view;
    }

    public static Success newInstance() {
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
}
