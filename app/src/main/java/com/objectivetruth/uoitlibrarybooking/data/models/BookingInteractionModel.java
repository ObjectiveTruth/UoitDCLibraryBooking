package com.objectivetruth.uoitlibrarybooking.data.models;

import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionEvent;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionScreenLoadEvent;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionWebService;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookinginteractionEventWithDateInfo;
import rx.Observable;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;

import javax.inject.Inject;

public class BookingInteractionModel {
    @Inject BookingInteractionWebService bookingInteractionWebService;

    private ReplaySubject<BookinginteractionEventWithDateInfo> bookingInteractionEventReplaySubject;
    private Observable<BookinginteractionEventWithDateInfo> bookingInteractionEventObservable;

    private PublishSubject<BookingInteractionScreenLoadEvent> bookingInteractionScreenLoadEventPublishSubject;
    private Observable<BookingInteractionScreenLoadEvent> bookingInteractionScreenLoadEventObservable;

    public BookingInteractionModel(UOITLibraryBookingApp mApplication,
                                   BookingInteractionWebService bookingInteractionWebService) {
        this.bookingInteractionWebService = bookingInteractionWebService;
    }

    /**
     * Can be used to request a Screen change by sending an event with your request
     * @see BookingInteractionEvent
     * @return
     */
    public ReplaySubject<BookinginteractionEventWithDateInfo> getBookingInteractionEventReplaySubject() {
        if(bookingInteractionEventReplaySubject == null || bookingInteractionEventReplaySubject.hasCompleted()) {
            bookingInteractionEventReplaySubject = ReplaySubject.createWithSize(1);
            bookingInteractionEventObservable = bookingInteractionEventReplaySubject.asObservable();
            return bookingInteractionEventReplaySubject;
        }else {
            return bookingInteractionEventReplaySubject;
        }
    }

    /**
     * Can be used to request a Screen change to the Booking Interaction Screen, will also fire an event on the paired
     * Replay Subject. This subject is intended to be used by MainActivity whereas
     * getBookingInteractionEventReplaySubject is to be used why the BookingInteraction Fragment itself to
     * monitor its own states
     * @see BookingInteractionScreenLoadEvent
     * @return
     */
    public PublishSubject<BookingInteractionScreenLoadEvent> getBookingInteractionScreenLoadEventPublishSubject() {
        if(bookingInteractionScreenLoadEventPublishSubject == null ||
                bookingInteractionScreenLoadEventPublishSubject.hasCompleted()) {

            bookingInteractionScreenLoadEventPublishSubject = PublishSubject.create();
            bookingInteractionScreenLoadEventObservable = bookingInteractionScreenLoadEventPublishSubject.asObservable();
            _bindScreenLoadEventToBookingEvent(bookingInteractionScreenLoadEventObservable);

            return bookingInteractionScreenLoadEventPublishSubject;
        }else {
            return bookingInteractionScreenLoadEventPublishSubject;
        }
    }

    /**
     * Bind the screen load event to fire the same event to the replay subject so when the
     * Booking Interaction screen is loaded, it shows something right away
     * @param s
     */
    private void _bindScreenLoadEventToBookingEvent(Observable<BookingInteractionScreenLoadEvent> s) {
        s.subscribe(new Action1<BookingInteractionScreenLoadEvent>() {
                    @Override
                    public void call(BookingInteractionScreenLoadEvent b) {
                        getBookingInteractionEventReplaySubject()
                                .onNext(new BookinginteractionEventWithDateInfo(b.timeCell,
                                        b.type,
                                        b.dayOfMonthNumber,
                                        b.monthWord));
                    }
                });
    }

    /**
     * Can be used to monitor any requests to show the Booking Interaction Fragment
     * @return
     */
    public Observable<BookingInteractionScreenLoadEvent> getBookingInteractionScreenLoadEventObservable() {
        getBookingInteractionScreenLoadEventPublishSubject();
        return bookingInteractionScreenLoadEventObservable;
    }

    /**
     * Like the get version of Booking Interaction Event but returns an observable instead of a subject
     * @see BookingInteractionEvent
     * @return
     */
    public Observable<BookinginteractionEventWithDateInfo> getBookingInteractionEventObservable() {
        getBookingInteractionEventReplaySubject(); //Sets up all the references before we return to ensure its created
        return bookingInteractionEventObservable;
    }
}
