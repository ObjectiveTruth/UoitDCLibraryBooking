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
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.BookingInteractionModel;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionEvent;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

import javax.inject.Inject;

public class BookingInteraction extends Fragment {
    private static final String BOOKING_INTERACTION_TITLE = "Booking";
    private Subscription bookingInteractionEventSubscription;
    @Inject BookingInteractionModel bookingInteractionModel;

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
        _setupViewBindings(bookingInteractionModel.getBookingInteractionEventObservable());
    }

    @Override
    public void onStop() {
        ((MainActivity) getActivity()).setDrawerState(true);
        _tearDownViewBindings(bookingInteractionEventSubscription);
        super.onStop();
    }

    private void _setupViewBindings(Observable<BookingInteractionEvent> bookingInteractionEventObservable) {
        bookingInteractionEventSubscription = bookingInteractionEventObservable
                .subscribe(new Action1<BookingInteractionEvent>() {
            @Override
            public void call(BookingInteractionEvent bookingInteractionEvent) {
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.bookinginteraction_content_frame,
                                Book.newInstance(bookingInteractionEvent.timeCell))
                        .commit();
            }
        });
    }

    private void _tearDownViewBindings(Subscription subscription) {
        if(subscription != null) {subscription.unsubscribe();}
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

    public static BookingInteraction newInstance() {
        return new BookingInteraction();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((UOITLibraryBookingApp) getActivity().getApplication()).getComponent().inject(this);
        setHasOptionsMenu(true);
    }

    private void _setTitle(String title) {
        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(title);
        }
    }
}
