package com.objectivetruth.uoitlibrarybooking.userinterface.calendar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.*;
import android.widget.Toast;
import com.android.volley.TimeoutError;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.objectivetruth.uoitlibrarybooking.MainActivity;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.common.constants.Analytics;
import com.objectivetruth.uoitlibrarybooking.data.models.CalendarModel;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarData;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarDataRefreshState;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.RefreshActivateEvent;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.ScrollAtTopOfGridEvent;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.calendarloaded.CalendarLoaded;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.firsttimeloaded.FirstTimeLoaded;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.helpdialog.HelpDialogFragment;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.sorrycartoon.SorryCartoon;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

import javax.inject.Inject;

import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCES_KEYS.HAS_LEARNED_HELP;

public class Calendar extends Fragment {
    private static final String CALENDAR_TITLE = "Calendar";
    @Inject CalendarModel calendarModel;
    @Inject SharedPreferences mDefaultSharedPreferences;
    @Inject SharedPreferences.Editor mDefaultSharedPreferencesEditor;
    @Inject Tracker tracker;
    private static final String HAS_SHOWN_INITIAL_SCREEN_BUNDLE_KEY = "HAS_SHOWN_INTIAL_SCREEN";
    private Subscription calendarDataRefreshStateObservableSubscription;
    private SwipeRefreshLayout _mSwipeLayout;
    private boolean _hasShownInitialScreen = false;
    private Subscription calendarModelScrollAtTopGridObservableSubscription;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((UOITLibraryBookingApp) getActivity().getApplication()).getComponent().inject(this);
        setHasOptionsMenu(true);
        _loadPreviousStateIfAvailable(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar, container, false);
        _mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.calendar_swipe_refresh_layout);
        return view;
    }

    private void _loadPreviousStateIfAvailable(Bundle inState) {
        if(inState != null) {
            _hasShownInitialScreen = inState.getBoolean(HAS_SHOWN_INITIAL_SCREEN_BUNDLE_KEY, false);
        }
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
            Timber.d(getClass().getSimpleName() + " isNowHidden");
            _teardownViewBindings(_mSwipeLayout);
        }else {
            Timber.d(getClass().getSimpleName() + "isNowVisible");
            _setTitle(CALENDAR_TITLE);
            _setupViewBindings(_mSwipeLayout, calendarModel.getCalendarDataRefreshObservable());
            tracker.setScreenName(Analytics.ScreenNames.CALENDAR);
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
        super.onHiddenChanged(isNowHidden);
    }

    @Override
    public void onStop() {
        Timber.d(getClass().getSimpleName() + " Stopped");
        _teardownViewBindings(_mSwipeLayout);
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(HAS_SHOWN_INITIAL_SCREEN_BUNDLE_KEY, _hasShownInitialScreen);
    }

    private void _setTitle(String title) {
        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    /**
     * Undoes all the bindings to the view, this is important so events don't get fired when the view is no longer
     * visible, or "in-view". This function is idempotent
     */
    private void _teardownViewBindings(SwipeRefreshLayout swipeRefreshLayout) {
        if(calendarDataRefreshStateObservableSubscription != null) {
            calendarDataRefreshStateObservableSubscription.unsubscribe();
        }

        if(calendarModelScrollAtTopGridObservableSubscription != null) {
            calendarModelScrollAtTopGridObservableSubscription.unsubscribe();
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

        calendarModelScrollAtTopGridObservableSubscription =
                calendarModel.getScrollAtTopGridObservable().subscribe(new Action1<ScrollAtTopOfGridEvent>() {
            @Override
            public void call(ScrollAtTopOfGridEvent scrollAtTopOfGridEvent) {
                if(_mSwipeLayout != null) {
                    _mSwipeLayout.setEnabled(scrollAtTopOfGridEvent.isScrollAtTop());
                }
            }
        });

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
                            // Do nothing
                        }

                        @Override
                        public void onNext(CalendarDataRefreshState calendarDataRefreshState) {
                            Timber.i("On next called: " + calendarDataRefreshState.type.name());
                            switch(calendarDataRefreshState.type) {
                                case RUNNING:
                                    swipeRefreshLayout.setRefreshing(true);
                                    break;
                                case ERROR:
                                    swipeRefreshLayout.setRefreshing(false);
                                    _doViewUpdatedBasedOnCalendarData(calendarDataRefreshState.calendarData);
                                    _handleRefreshError(calendarDataRefreshState.exception);
                                    break;
                                case SUCCESS:
                                    _playCalendarAnimation(swipeRefreshLayout);
                                case INITIAL:
                                default:
                                    swipeRefreshLayout.setRefreshing(false);
                                    _doViewUpdatedBasedOnCalendarData(calendarDataRefreshState.calendarData);
                                }
                            }
                        });
        }
    }

    private void _playCalendarAnimation(SwipeRefreshLayout swipeRefreshLayout) {
        if(swipeRefreshLayout == null) {return;}

        YoYo.with(Techniques.FadeIn)
                .duration(300)
                .playOn(swipeRefreshLayout);
    }

    private void _doViewUpdatedBasedOnCalendarData(CalendarData calendarData) {
        Timber.d("Changing Calendar screen based on calendardata received");
        if(UOITLibraryBookingApp.isFirstTimeLaunchSinceUpgradeOrInstall() &&
                !_hasShownInitialScreen) {
            Timber.d("Showing initial calendar welcome screen");
            _hasShownInitialScreen = true;
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.calendar_content_frame, FirstTimeLoaded.newInstance()).commit();

        }else if(calendarData == null || calendarData.days == null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.calendar_content_frame, SorryCartoon.newInstance()).commit();
        }else {
            _makeNewCalendarLoadedFragmentOrRefreshCurrentOne(calendarData);
        }
    }

    private void _handleRefreshError(Throwable throwable) {
        if(throwable.getCause() instanceof TimeoutError) {
            Toast.makeText(getContext(), R.string.ERROR_TIMEOUT_FROM_SERVER, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getContext(), R.string.ERROR_GENERAL, Toast.LENGTH_LONG).show();
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
        	inflater.inflate(R.menu.calendar_action_icons_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.help_calendar){
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Calendar Home")
                    .setAction("HelpDialog")
                    .setLabel("Pressed by User")
                    .build()
            );
            handleHelpClick();
            return true;
        } else {
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
            tracker.send(new HitBuilders.EventBuilder()
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
