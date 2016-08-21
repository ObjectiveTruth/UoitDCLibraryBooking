package com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.objectivetruth.uoitlibrarybooking.MainActivity;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.BookingInteractionModel;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookinginteractionEventWithDateInfo;
import com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction.common.Utils;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import timber.log.Timber;

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
        _setTitleAndBackButton(BOOKING_INTERACTION_TITLE, true);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                _popFragmentBackstack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void _setupViewBindings(Observable<BookinginteractionEventWithDateInfo> bookingInteractionEventObservable) {
        bookingInteractionEventSubscription = bookingInteractionEventObservable
                .subscribe(new Action1<BookinginteractionEventWithDateInfo>() {
            @Override
            public void call(BookinginteractionEventWithDateInfo bookingInteractionEvent) {
                _replaceContentFrameWithFragmentBasedOnEvent(bookingInteractionEvent);
            }
        });
    }

    private void _replaceContentFrameWithFragmentBasedOnEvent(
            BookinginteractionEventWithDateInfo bookingInteractionEvent) {
        Fragment currentFragmentInContentFrame = getChildFragmentManager()
                .findFragmentById(R.id.bookinginteraction_content_frame);

        if(_doesCurrentContentFrameNeedToBeChanged(bookingInteractionEvent, currentFragmentInContentFrame)) {
            Timber.d("Received event request to change fragment, changing: " + bookingInteractionEvent.type);
            _setTitleAndBackButton(_getFormattedTitle(bookingInteractionEvent), true);
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.bookinginteraction_content_frame,
                            _getFragmentForEvent(bookingInteractionEvent))
                    .commit();
        }else{
            Timber.d("Received event request to change fragment, but fragment is already in the correct state: " +
                    bookingInteractionEvent.type);
        }
    }

    private String _getFormattedTitle(BookinginteractionEventWithDateInfo bookingInteractionEvent) {
        String dayOfWeekWord = Utils.getDayOfWeekBasedOnDayNumberMonthNumber(bookingInteractionEvent.dayOfMonthNumber,
                bookingInteractionEvent.monthWord);
        return dayOfWeekWord + ", " + bookingInteractionEvent.dayOfMonthNumber + " @ " +
                bookingInteractionEvent.timeCell.param_starttime;
    }

    private Fragment _getFragmentForEvent(BookinginteractionEventWithDateInfo bookinginteractionEventWithDateInfo) {
       switch(bookinginteractionEventWithDateInfo.type) {
           case BOOK:
               Timber.i("Showing: Book");
               return Book.newInstance(bookinginteractionEventWithDateInfo);
           case SUCCESS:
               Timber.i("Showing: Success");
               return Success.newInstance();
           case JOIN_OR_LEAVE:
               Timber.i("Showing: JoinOrLeave");
               return JoinOrLeave.newInstance(bookinginteractionEventWithDateInfo);
           default:
               Timber.w("No valid Fragment requested, showing Success");
               return Success.newInstance();
       }
    }

    /**
     * Checks whether the content frame shoudld be updated. For example, if its empty, or doesn't contain the correct
     * fragment for the currently requested BookingEvent
     * @param bookingInteractionEvent
     * @param currentFragmentInContentFrame
     * @return
     */
    private boolean _doesCurrentContentFrameNeedToBeChanged(BookinginteractionEventWithDateInfo bookingInteractionEvent,
                                                                        Fragment currentFragmentInContentFrame) {
        if(currentFragmentInContentFrame == null) {return true;}

        switch(bookingInteractionEvent.type) {
            case BOOK:
                return !(currentFragmentInContentFrame instanceof Book);
            case SUCCESS:
                return !(currentFragmentInContentFrame instanceof Success);
            case JOIN_OR_LEAVE:
                return !(currentFragmentInContentFrame instanceof JoinOrLeave);
            default:
                Toast.makeText(getActivity(), R.string.ERROR_GENERAL, Toast.LENGTH_LONG).show();
                Timber.w("Tried to load a bookinginteractionevent, but it didn't contain any expected " +
                        "values in the param_next field: " + bookingInteractionEvent.timeCell.toString());
                _popFragmentBackstack();
                return false;
        }
    }

    /**
     * Remove this Fragment. Effectively undoes the booking interaction fragment loading event
     */
    private void _popFragmentBackstack() {
        Timber.d("Popping backstack");
        getActivity().getSupportFragmentManager().popBackStack();
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

    private void _setTitleAndBackButton(String title, boolean shouldSetBackbutton) {
        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(shouldSetBackbutton);
            actionBar.setTitle(title);
        }
    }

}
