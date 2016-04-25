package com.objectivetruth.uoitlibrarybooking.userinterface.calendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.CalendarModel;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarData;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.calendarloaded.CalendarLoaded;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.sorrycartoon.SorryCartoon;
import com.objectivetruth.uoitlibrarybooking.userinterface.loading.Loading;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import javax.inject.Inject;

public class Calendar extends Fragment {
    @Inject CalendarModel calendarModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        ((UOITLibraryBookingApp) getActivity().getApplication()).getComponent().inject(this);

        // Place the loading fragment into the view while we wait for loading in the next step
        getFragmentManager().beginTransaction()
                .add(R.id.mainactivity_content_frame, new Loading()).commit();

        getLatestDataAndCreateOrRefreshCalendarUI();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void getLatestDataAndCreateOrRefreshCalendarUI() {
        Timber.i("Calendar loading starting...");

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
                                .replace(R.id.mainactivity_content_frame, SorryCartoon.newInstance()).commit();
                    }

                    @Override
                    public void onNext(CalendarData calendarData) {
                        Timber.i("Calendar loading complete");
                        // Place the loading fragment into the view while we wait for loading
                        if (calendarData == null) {
                            Timber.v("Calendar Data request is empty, showing sorry cartoon");
                            getFragmentManager().beginTransaction()
                                    .replace(R.id.mainactivity_content_frame, SorryCartoon.newInstance()).commit();
                        }else {
                            Timber.v("CalendarData has data, showing calendar");
                            getFragmentManager().beginTransaction()
                                    .replace(R.id.mainactivity_content_frame,
                                            CalendarLoaded.newInstance(calendarData)).commit();
                        }
                    }
                });
    }
}
