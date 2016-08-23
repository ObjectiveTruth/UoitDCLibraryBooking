package com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionEvent;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCell;
import com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction.common.InteractionFragment;
import timber.log.Timber;

public class JoinOrLeave extends InteractionFragment{
    private TimeCell timeCell;
    private String monthWord = "";
    private String dayOfMonthNumber = "";
    private BookingInteractionEvent bookingInteractionEvent;
    private TextView errorTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bookinginteraction_joinorleave, container, false);

        TextView roomNumberTextView = (TextView) view.findViewById(R.id.joinorleave_room_number);
        Button createButton = (Button) view.findViewById(R.id.joinorleave_create_group_button);
        Spinner joinSpinner = (Spinner) view.findViewById(R.id.joinorleave_join_spinner);
        Spinner leaveSpinner = (Spinner) view.findViewById(R.id.joinorleave_leave_spinner);
        Button leaveButton = (Button) view.findViewById(R.id.joinorleave_leave_grou_button);
        Button joinButton = (Button) view.findViewById(R.id.joinorleave_join_button);
/*        String[] joinSpinnerArr = (String[]) bundleExtras.get("joinSpinnerArr");
        String[] leaveSpinnerArr = (String[]) bundleExtras.get("leaveSpinnerArr");*/
        errorTextView = (TextView) view.findViewById(R.id.joinorleave_error_text);

        ImageView roomPicture = (ImageView) view.findViewById(R.id.room_landing_room_picture);

        roomNumberTextView.setText(timeCell.param_room);

        //roomPicture.setImageResource(getResources().getIdentifier(roomNumber.toLowerCase(), "drawable", getPackageName()));

        createButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Timber.w("Create Button Clicked, Not Implemented");
            }
        });


/*        ArrayAdapter<String> joinAdapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, joinSpinnerArr);
        joinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        joinSpinner.setAdapter(joinAdapter);

        ArrayAdapter<String> leaveAdapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, leaveSpinnerArr);
        leaveAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        leaveSpinner.setAdapter(leaveAdapter);

        leaveSpinnerValue = leaveSpinnerArr[0].split(":")[3].substring(1, 5);
        //Timber.i(leaveSpinnerValue);
        joinSpinnerValue = joinSpinnerArr[0].split(":")[3].substring(1, 5);
        spinnerJoinString = joinSpinnerArr[0];
        int foundAt = joinSpinnerArr[0].indexOf("(");
        calendarGroupName = joinSpinnerArr[0].substring(0, foundAt - 1);*/


        joinSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapter, View view,
                                       int position, long id) {
/*                spinnerJoinString = ((String) adapter.getItemAtPosition(position));
                joinSpinnerValue = spinnerJoinString.split(":")[3].substring(1, 5);
                int foundAt = spinnerJoinString.indexOf("(");
                calendarGroupName = spinnerJoinString.substring(0, foundAt - 1);*/

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });

        leaveSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapter, View view,
                                       int position, long id) {
                //leaveSpinnerValue = ((String) adapter.getItemAtPosition(position)).split(":")[3].substring(1, 5);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });

        joinButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
            }
        });

        leaveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
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
    protected void setupViewBindings() {
    }

    @Override
    protected void teardownViewBindings() {
    }
}
