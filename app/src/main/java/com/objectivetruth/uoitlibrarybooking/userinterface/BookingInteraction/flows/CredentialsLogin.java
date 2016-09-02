package com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction.flows;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.BookingInteractionModel;
import com.objectivetruth.uoitlibrarybooking.data.models.UserModel;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionEvent;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.MyAccountDataLoginState;
import com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction.common.InteractionFragment;
import com.objectivetruth.uoitlibrarybooking.userinterface.loading.Loading;
import com.objectivetruth.uoitlibrarybooking.userinterface.myaccount.login.LoginFragment;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import javax.inject.Inject;

import static com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.common.Utils.getBookingInteractionEventTypeBasedOnTimeCell;

public class CredentialsLogin extends InteractionFragment{
    // The event that the user originally requested before they were sent to the credentials login
    private BookingInteractionEvent originatingBookingInteractionEvent;
    private CompositeSubscription subscriptions = new CompositeSubscription();
    @Inject UserModel userModel;
    @Inject BookingInteractionModel bookingInteractionModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bookinginteraction_credentialslogin_root, container, false);
        return view;
    }

    @Override
    protected void setupViewBindings() {
        subscriptions.add(userModel.getLoginStateObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MyAccountDataLoginState>() {
                    @Override
                    public void onCompleted() {
                        // Do nothing
                    }

                    @Override
                    public void onError(Throwable e) {
                        // Do nothing
                    }

                    @Override
                    public void onNext(MyAccountDataLoginState myAccountDataLoginState) {
                        Timber.i("On next called: " + myAccountDataLoginState.type);
                        Timber.v(myAccountDataLoginState.toString());
                        switch(myAccountDataLoginState.type) {
                            case SIGNED_IN:
                                Toast.makeText(getActivity(), R.string.SUCCESS_SIGNED_IN, Toast.LENGTH_LONG).show();
                                BookingInteractionEvent eventToFire = new BookingInteractionEvent(
                                        originatingBookingInteractionEvent.timeCell,
                                        getBookingInteractionEventTypeBasedOnTimeCell(
                                                originatingBookingInteractionEvent.timeCell),
                                        originatingBookingInteractionEvent.dayOfMonthNumber,
                                        originatingBookingInteractionEvent.monthWord);
                                bookingInteractionModel
                                        .getBookingInteractionEventReplaySubject()
                                        .onNext(eventToFire);
                                break;
                            case RUNNING:
                                _showFullscreenLoading();
                                break;
                            case SIGNED_OUT:
                            case ERROR:
                            default:
                                _showLoginFragment(myAccountDataLoginState);
                        }
                    }
                }));
    }

    @Override
    protected void teardownViewBindings() {
        subscriptions.unsubscribe();
    }

    private void _showLoginFragment(MyAccountDataLoginState myAccountDataLoginState) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.bookinginteraction_credentialslogin_content_frame,
                        LoginFragment.newInstance(myAccountDataLoginState))
                .commit();
    }

    public static CredentialsLogin newInstance(BookingInteractionEvent bookingInteractionEvent) {
        CredentialsLogin fragment = new CredentialsLogin();
        fragment.originatingBookingInteractionEvent = bookingInteractionEvent;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((UOITLibraryBookingApp) getActivity().getApplication()).getComponent().inject(this);
    }

    private void _showFullscreenLoading() {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.bookinginteraction_credentialslogin_content_frame, Loading.newInstance())
                .commit();
    }
}
