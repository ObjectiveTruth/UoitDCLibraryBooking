package com.objectivetruth.uoitlibrarybooking.userinterface.calendar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.*;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.objectivetruth.uoitlibrarybooking.*;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.CalendarModel;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarData;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.calendarloaded.CalendarLoaded;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.helpdialog.HelpDialogFragment;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.sorrycartoon.SorryCartoon;
import com.objectivetruth.uoitlibrarybooking.userinterface.loading.Loading;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import javax.inject.Inject;

import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCES_KEYS.HAS_LEARNED_HELP;
import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCES_KEYS.HAS_LEARNED_REFRESH;

public class Calendar extends Fragment {
    @Inject CalendarModel calendarModel;
    @Inject SharedPreferences mDefaultSharedPreferences;
    @Inject SharedPreferences.Editor mDefaultSharedPreferencesEditor;
    @Inject Tracker googleAnalyticsTracker;
    private PublishSubject<Object> refreshClickSubject;
    private final static String SAVED_BUNDLE_KEY_IS_FIRST_LOAD = "IS_FIRST_LOAD";
    private String CALENDAR_LOADED_FRAGMENT_TAG = "SINGLETON_CALENDAR_LOADED_FRAGMENT_TAG";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((UOITLibraryBookingApp) getActivity().getApplication()).getComponent().inject(this);

        setHasOptionsMenu(true); // Notifies activity that this fragment will interact with the action/options menu
        return inflater.inflate(R.layout.calendar, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getLatestDataAndCreateOrRefreshCalendarUI();
/*        Fragment calendarLoadedFragment = getFragmentManager()
                .findFragmentByTag(CALENDAR_LOADED_FRAGMENT_TAG);
        if(calendarLoadedFragment == null) {
        }else{
            getFragmentManager().beginTransaction()
                    .replace(R.id.calendar_content_frame, calendarLoadedFragment, CALENDAR_LOADED_FRAGMENT_TAG)
                    .commit();
        }*/
    }

    public void getLatestDataAndCreateOrRefreshCalendarUI() {
        Timber.i("Calendar loading starting...");

        Timber.d("Showing Loading screen.");
        getFragmentManager().beginTransaction()
                .replace(R.id.calendar_content_frame, new Loading()).commit();

        calendarModel.getCalendarDataObs()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CalendarData>() {
                    @Override
                    public void onCompleted() {
                        // Will auto-complete itself when onNext is called Once
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Error getting the data required to show the calendar");
                        // Replace it with the sorry cartoon since something went wrong
                        getFragmentManager().beginTransaction()
                                .replace(R.id.calendar_content_frame, SorryCartoon.newInstance()).commit();
                    }

                    @Override
                    public void onNext(CalendarData calendarData) {
                        Timber.i("Calendar loading complete");
                        // Place the loading fragment into the view while we wait for loading
                        if (calendarData == null) {
                            Timber.d("Calendar Data request is empty, showing sorry cartoon");
                            getFragmentManager().beginTransaction()
                                    .replace(R.id.calendar_content_frame, SorryCartoon.newInstance()).commit();
                        }else {
                            Timber.d("CalendarData has data, showing calendar");
                            getFragmentManager().beginTransaction()
                                    .replace(R.id.calendar_content_frame,
                                            CalendarLoaded.newInstance(calendarData), CALENDAR_LOADED_FRAGMENT_TAG)
                                    .commit();
                        }
                    }
                });
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
        MenuItem refreshItem = menu.findItem(R.id.refresh_calendar);

        getRefreshClickSubject().subscribe(new Observer<Object>() {
            @Override
            public void onCompleted() {
                // Nothing, will clean itself up
            }

            @Override
            public void onError(Throwable e) {
                _showNetworkErrorAlertDialog(getContext());
            }

            @Override
            public void onNext(Object o) {
                getLatestDataAndCreateOrRefreshCalendarUI();
            }
        });


/*        if(_hasUserNotLearnedRefresh(mDefaultSharedPreferences)){
            ImageView iv = (ImageView) getActivity().getLayoutInflater().inflate(R.layout.action_bar_refresh_imageview,
                    null);
            Timber.i("User has Not Learned Refresh");
            startActionBarBounceAnimation(iv);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleRefreshClick();

                }
            });
            refreshItem.setActionView(iv);
        }
        clickedObs()*/

        //Prepare MyAccount Actionview if has learned refresh and has NOT learned myaccount
/*        if(mDefaultSharedPreferences.getBoolean(HAS_LEARNED_REFRESH, false)
                && !mDefaultSharedPreferences.getBoolean(HAS_LEARNED_MYACCOUNT, false)){
            Timber.i("User has not Learned MyAccount");
            LayoutInflater inflater = this.getLayoutInflater();
            ImageView iv = (ImageView) inflater.inflate(R.layout.action_bar_myaccount_imageview,
                    null);
            startActionBarBounceAnimation(iv);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleMyAccountClick();
                }
            });
            myAccountItem.setActionView(iv);
        }*/
/*        if(mCalendarRefresher != null && isFront == true){
            if(refreshItem != null){
                refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
            }
        }*/
    }

    private boolean _hasUserNotLearnedRefresh(SharedPreferences mDefaultSharedPreferences) {
         return !mDefaultSharedPreferences.getBoolean(HAS_LEARNED_REFRESH, false);
    }

    @Override
    public void onPause() {
        getRefreshClickSubject().onCompleted();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
        else if(id == R.id.refresh_calendar){
            googleAnalyticsTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Calendar Home")
                    .setAction("Refresh")
                    .setLabel("Pressed by User")
                    .build()
            );
            getRefreshClickSubject().onNext(new Object());
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
        else {
            ((MainActivity) getActivity()).onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private PublishSubject<Object> getRefreshClickSubject() {
        if (refreshClickSubject == null || refreshClickSubject.hasCompleted()) {
            return refreshClickSubject = PublishSubject.create();
        }else {
            return refreshClickSubject;
        }
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
                .show(getFragmentManager(), HELP_DIALOG_FRAGMENT_TAG);
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

}
