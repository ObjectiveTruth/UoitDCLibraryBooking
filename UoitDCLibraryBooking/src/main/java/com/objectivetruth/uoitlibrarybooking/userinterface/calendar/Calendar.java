package com.objectivetruth.uoitlibrarybooking.userinterface.calendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.calendarloaded.CalendarLoaded;
import com.objectivetruth.uoitlibrarybooking.userinterface.loading.Loading;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Calendar extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Place the loading fragment into the view while we wait for loading
        getFragmentManager().beginTransaction()
                .add(R.id.mainactivity_content_frame, new Loading()).commit();

        Observable.timer(3000L, MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        Timber.i("Calendar Loading Complete");
                        // Place the loading fragment into the view while we wait for loading
                        getFragmentManager().beginTransaction()
                                .replace(R.id.mainactivity_content_frame, new CalendarLoaded()).commit();
                    }
                });
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
