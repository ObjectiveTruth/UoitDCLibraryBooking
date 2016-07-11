package com.objectivetruth.uoitlibrarybooking.userinterface.calendar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.*;
import android.widget.Toast;
import com.android.volley.TimeoutError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.objectivetruth.uoitlibrarybooking.ActivityRoomInteraction;
import com.objectivetruth.uoitlibrarybooking.ActivitySettings;
import com.objectivetruth.uoitlibrarybooking.BuildConfig;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.CalendarModel;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarData;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarDataRefreshState;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.RefreshActivateEvent;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.MyAccountBooking;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserCredentials;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserData;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.calendarloaded.CalendarLoaded;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.firsttimeloaded.FirstTimeLoaded;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.helpdialog.HelpDialogFragment;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.sorrycartoon.SorryCartoon;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.ArrayList;

import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCES_KEYS.HAS_LEARNED_HELP;

public class Calendar extends Fragment {
    @Inject CalendarModel calendarModel;
    @Inject SharedPreferences mDefaultSharedPreferences;
    @Inject SharedPreferences.Editor mDefaultSharedPreferencesEditor;
    @Inject Tracker googleAnalyticsTracker;
    private Subscription calendarDataRefreshStateObservableSubscription;
    private SwipeRefreshLayout _mSwipeLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((UOITLibraryBookingApp) getActivity().getApplication()).getComponent().inject(this);

        setHasOptionsMenu(true); // Notifies activity that this fragment will interact with the action/options menu

        View view = inflater.inflate(R.layout.calendar, container, false);
        _mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.calendar_swipe_refresh_layout);
        return view;
    }

    @Override
    public void onStart() {
        Timber.d("Calendar onStart");
        _setupViewBindings(_mSwipeLayout, calendarModel.getCalendarDataRefreshObservable());
        super.onStart();
    }

    @Override
    public void onHiddenChanged(boolean isNowHidden) {
        if(isNowHidden) {
            Timber.d("Calendar isNowHidden");
            _teardownViewBindings(_mSwipeLayout);
        }else {
            Timber.d("Calendar isNowVisible");
            _setupViewBindings(_mSwipeLayout, calendarModel.getCalendarDataRefreshObservable());
        }
        super.onHiddenChanged(isNowHidden);
    }

    @Override
    public void onStop() {
        Timber.d("Calendar Stopped");
        _teardownViewBindings(_mSwipeLayout);
        super.onStop();
    }

    /**
     * Undoes all the bindings to the view, this is important so events don't get fired when the view is no longer
     * visible, or "in-view". This function is idempotent
     */
    private void _teardownViewBindings(SwipeRefreshLayout swipeRefreshLayout) {
        if(calendarDataRefreshStateObservableSubscription != null) {
            calendarDataRefreshStateObservableSubscription.unsubscribe();
        }

        if(swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(null);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * Creates all the bindings from the View to the ViewModel. Good to do this when the View first is created
     * and if the view ever has to leave, its important to call this when it comes back into "view". This function is
     * idempotent
     * @param swipeRefreshLayout
     * @param calendarDataRefreshStateObservable
     */
    private void _setupViewBindings(final SwipeRefreshLayout swipeRefreshLayout,
                                Observable<CalendarDataRefreshState> calendarDataRefreshStateObservable) {
        if(_mSwipeLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(null);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    calendarModel.getRefreshActivatePublishSubject().onNext(new RefreshActivateEvent());
                }
            });
        }

        // To ensure this is idempotent, need to ONLY subscribe if there is no current subscription, or the subscription
        // has been unsubscribed from
        if(calendarDataRefreshStateObservableSubscription == null ||
                calendarDataRefreshStateObservableSubscription.isUnsubscribed()) {

            calendarDataRefreshStateObservableSubscription =
                calendarDataRefreshStateObservable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<CalendarDataRefreshState>() {
                        @Override
                        public void onCompleted() {
                            // Do nothing
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(CalendarDataRefreshState calendarDataRefreshState) {
                            Timber.i("On next called: " + calendarDataRefreshState.type.name());
                            switch(calendarDataRefreshState.type) {
                                case RUNNING:
                                    if(!swipeRefreshLayout.isRefreshing()) {
                                        swipeRefreshLayout.setRefreshing(true);
                                    }
                                    _doViewUpdatedBasedOnCalendarData(calendarDataRefreshState.calendarData);
                                    break;
                                case ERROR:
                                    swipeRefreshLayout.setRefreshing(false);
                                    _doViewUpdatedBasedOnCalendarData(calendarDataRefreshState.calendarData);
                                    _handleRefreshError(calendarDataRefreshState.exception);
                                    break;
                                case INITIAL:
                                case SUCCESS:
                                default:
                                    swipeRefreshLayout.setRefreshing(false);
                                    _doViewUpdatedBasedOnCalendarData(calendarDataRefreshState.calendarData);
                                        break;
                                }
                            }
                        });
        }
    }

    private void _doViewUpdatedBasedOnCalendarData(CalendarData calendarData) {
        Timber.d("Changing Calendar screen based on calendardata received");
        if(UOITLibraryBookingApp.isFirstTimeLaunchSinceUpgradeOrInstall()) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.calendar_content_frame, FirstTimeLoaded.newInstance()).commit();
        }else if(calendarData == null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.calendar_content_frame, SorryCartoon.newInstance()).commit();
        }else {
            _makeNewCalendarLoadedFragmentOrRefreshCurrentOne(calendarData);
        }
    }

    private void _handleRefreshError(Throwable throwable) {
        Resources resources = Resources.getSystem();
        if(throwable.getCause() instanceof TimeoutError) {
            Toast.makeText(getContext(), resources.getString(R.string.TIMEOUT_ERROR_FROM_SERVER),
                    Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getContext(), resources.getString(R.string.GENERAL_ERROR),
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Checks the currently loaded fragment in the calendar_content_frame. If calendar loaded is there
     * it will tell it to refresh with new calendarData, otherwise it will make a new CalendarData fragment
     * @param calendarData must have data in it(not null)
     */
    private void _makeNewCalendarLoadedFragmentOrRefreshCurrentOne(CalendarData calendarData) {
        String CALENDAR_LOADED_FRAGMENT_TAG = "SINGLETON_CALENDAR_LOADED_FRAGMENT_TAG";
        Fragment currentFragmentInContentFrame = getChildFragmentManager().findFragmentById(R.id.calendar_content_frame);

        if(currentFragmentInContentFrame instanceof CalendarLoaded) {
            Timber.d("Calendar content frame already contains CalendarLoaded, will tell it to redraw/refresh itself");
            ((CalendarLoaded) currentFragmentInContentFrame).refreshPagerFragmentsAndViewsIfDataDiffers(calendarData);
        }else{
            Timber.d("Calendar content frame doesn't contain CalendarLoaded, will replace with CalendarLoaded");
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.calendar_content_frame,
                            CalendarLoaded.newInstance(calendarData),
                            CALENDAR_LOADED_FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
    	if(BuildConfig.DEBUG){
        	inflater.inflate(R.menu.calendar_action_icons_menu_debug, menu);
        }
        else{
        	inflater.inflate(R.menu.calendar_action_icons_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(getContext(), ActivitySettings.class);
            startActivity(intent);

        }
        else if(id == R.id.help_calendar){
            googleAnalyticsTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Calendar Home")
                    .setAction("HelpDialog")
                    .setLabel("Pressed by User")
                    .build()
            );
            handleHelpClick();
            return true;
        }
        else if(id == R.id.debug_success){
            Intent intent = new Intent(getContext(), ActivityRoomInteraction.class);
            intent.putExtra("type", "test");
            startActivity(intent);
        }
        else if(id == R.id.debug_booknew){
            Intent intent = new Intent(getContext(), ActivityRoomInteraction.class);
            intent.putExtra("type", "createbooking");
            intent.putExtra("room", "Lib999");
            intent.putExtra("date", "March 15, 1990, Monday");
            startActivity(intent);
        }
        else if(id == R.id.calendar_action_menu_item_debug_gson){
            UserCredentials userCredentials = new UserCredentials("username", "password", "institution");
            UserData userData = new UserData();
            userData.completeBookings = new ArrayList<MyAccountBooking>();
            userData.incompleteBookings = new ArrayList<MyAccountBooking>();
            userData.pastBookings = new ArrayList<MyAccountBooking>();

            Timber.v("JSON-test-before");
            Gson gson = new Gson();
            String s = gson.toJson(userData);
            Timber.v(s);
            String userDataJson = "{}";
            UserData userData1 = gson.fromJson(userDataJson, UserData.class);
            Timber.v("JSON-test-after");
            Timber.v(userData1.toString());
        }
        else {
            // If no match to the action button, let the activity handle it (for back/up buttons)
            getActivity().onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Displays the help dialog
     */
    private void handleHelpClick(){
        String HELP_DIALOG_FRAGMENT_TAG = "SINGLETON_HELP_DIALOG_FRAGMENT_TAG";
        Timber.i("User Clicked Help Dialog");
        if(!mDefaultSharedPreferences.getBoolean(HAS_LEARNED_HELP, false)){
            googleAnalyticsTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Calendar Home")
                    .setAction("HelpDialog")
                    .setLabel("First Time Help Pressed")
                    .build());
            mDefaultSharedPreferencesEditor.putBoolean(HAS_LEARNED_HELP, true).commit();
        }
        new HelpDialogFragment()
                .show(getChildFragmentManager(), HELP_DIALOG_FRAGMENT_TAG);
    }

    private void _showNetworkErrorAlertDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Connectivity Issue")
                .setMessage(R.string.networkerrordialogue)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing special
                    }
                })
                .setIcon(R.drawable.ic_dialog_alert)
                .show();
    }

    public static Calendar newInstance() {
        return new Calendar();
    }
}
