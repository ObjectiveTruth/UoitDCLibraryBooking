package com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction.flows;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.CalendarModel;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionEvent;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.RefreshActivateEvent;
import com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction.common.InteractionFragment;
import timber.log.Timber;

import javax.inject.Inject;

public class Success extends InteractionFragment{
    private BookingInteractionEvent bookingInteractionEvent;
    private static final String SUCCESS_MESSAGE_BUNDLE_KEY = "SUCCESS_MESSAGE_BUNDLE_KEY";
    private String successParagraph;
    @Inject CalendarModel calendarModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        _restoreStateIfAvailable(savedInstanceState);
        View view = inflater.inflate(R.layout.bookinginteraction_success, container, false);
        TextView body = (TextView) view.findViewById(R.id.bookingInteraction_success_body);

        body.setText(_getMessageOrDefault(successParagraph));

        TextView title = (TextView) view.findViewById(R.id.bookingInteraction_success_title);

        Button addToCalendarButton = (Button) view.findViewById(R.id.bookingInteraction_success_add_to_calendar_button);

        Button okButton = (Button) view.findViewById(R.id.bookingInteraction_success_ok_button);
        _setupOkButton(okButton);
        // Do it everytime this screen gets created so the user doesn't have to wait to do another refresh
        _doRefresh();

        return view;
    }

    private void _restoreStateIfAvailable(Bundle inState) {
        if(inState != null) {
            successParagraph = inState.getString(SUCCESS_MESSAGE_BUNDLE_KEY, "No Information Available");
        }
    }

    private Spanned _getMessageOrDefault(String successParagraph) {
        String returnMessage = successParagraph == null ? "No Information Available" : successParagraph;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(returnMessage, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(returnMessage);
        }
    }

    private void _doRefresh() {
        calendarModel.getRefreshActivatePublishSubject().onNext(new RefreshActivateEvent());
    }

    public static Success newInstance(BookingInteractionEvent bookingInteractionEvent) {
        Success fragment = new Success();
        fragment.bookingInteractionEvent = bookingInteractionEvent;
        fragment.successParagraph = bookingInteractionEvent.message;
        return fragment;
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SUCCESS_MESSAGE_BUNDLE_KEY, bookingInteractionEvent.message);
    }

    @Override
    protected void setupViewBindings() {

    }

    @Override
    protected void teardownViewBindings() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((UOITLibraryBookingApp) getActivity().getApplication()).getComponent().inject(this);
    }
}
