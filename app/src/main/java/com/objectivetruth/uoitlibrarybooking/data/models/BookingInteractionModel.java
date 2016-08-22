package com.objectivetruth.uoitlibrarybooking.data.models;

import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.*;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarDay;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarParser;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarWebService;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;
import timber.log.Timber;

public class BookingInteractionModel {
    BookingInteractionWebService bookingInteractionWebService;
    CalendarWebService calendarWebService;
    UserModel userModel;

    private ReplaySubject<BookingInteractionEvent> bookingInteractionEventReplaySubject;
    private Observable<BookingInteractionEvent> bookingInteractionEventObservable;

    private PublishSubject<BookingInteractionScreenLoadEvent> bookingInteractionScreenLoadEventPublishSubject;
    private Observable<BookingInteractionScreenLoadEvent> bookingInteractionScreenLoadEventObservable;

    private PublishSubject<BookingInteractionEventUserRequest> bookingInteractionEventUserRequestPublishSubject;

    public BookingInteractionModel(UOITLibraryBookingApp mApplication,
                                   BookingInteractionWebService bookingInteractionWebService,
                                   CalendarWebService calendarWebService,
                                   UserModel userModel) {
        this.bookingInteractionWebService = bookingInteractionWebService;
        this.calendarWebService = calendarWebService;
        this.userModel = userModel;
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
    public Observable<BookingInteractionEvent> getBookingInteractionEventObservable() {
        getBookingInteractionEventReplaySubject(); //Sets up all the references before we return to ensure its created
        return bookingInteractionEventObservable;
    }

    /**
     * Used to signal when a request is initiated by user. For example, actually DOING the request in the booking
     * Interaction Flow
     * @return
     */
    public PublishSubject<BookingInteractionEventUserRequest> getBookingInteractionEventUserRequest() {
        if(bookingInteractionEventUserRequestPublishSubject == null ||
                bookingInteractionEventUserRequestPublishSubject.hasCompleted()) {
            bookingInteractionEventUserRequestPublishSubject = PublishSubject.create();
            _bindUserRequestEventToWebCalls(bookingInteractionEventUserRequestPublishSubject);
            return bookingInteractionEventUserRequestPublishSubject;
        }else {
            return bookingInteractionEventUserRequestPublishSubject;
        }

    }

    private void _bindUserRequestEventToWebCalls(
            PublishSubject<BookingInteractionEventUserRequest> bookingInteractionEventUserRequestPublishSubject) {
        bookingInteractionEventUserRequestPublishSubject
                .subscribe(new Action1<BookingInteractionEventUserRequest>() {
                    @Override
                    public void call(BookingInteractionEventUserRequest s) {
                        _executeBasedOnUserRequestType(s);
                    }
                });
    }

    private void _executeBasedOnUserRequestType(BookingInteractionEventUserRequest userRequest) {
        switch(userRequest.type) {
            case BOOK_REQUEST:
                _doBookRequest(userRequest);
        }
    }

    private void _doBookRequest(final BookingInteractionEventUserRequest userRequest) {
        Timber.i("Starting the booking Request Web call flow");
        calendarWebService.getRawInitialWebPageObs() // Get the initial webpage

                // Extract the state information/validators
                .flatMap(new Func1<String, Observable<CalendarDay>>() {
                    @Override
                    public Observable<CalendarDay> call(String s) {
                        return CalendarParser.parseRawWebpageForViewStateGeneratorAndEventValidation(s);
                    }
                })

                // Simulate Clicking on the Day on the calendar to get the list of open slots
                .flatMap(new Func1<CalendarDay, Observable<String>>() {
                    @Override
                    public Observable<String> call(CalendarDay calendarDay) {
                        calendarDay.extEventTarget = userRequest.timeCell.param_eventtarget;
                        calendarDay.extEventArgument = userRequest.timeCell.param_eventargument;

                        calendarDay.extDayOfMonthNumber = userRequest.dayOfMonthNumber;

                        return calendarWebService.getRawCaledarDayPageUsingCalendarDay(calendarDay);
                    }
                })

                // Get the page with the form to fill out
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String rawWebpage) {
                        return bookingInteractionWebService.getRawWebpageWithForm(userRequest.timeCell);
                    }
                })

/*                // Parse the form webpage for State information/validators
                .flatMap(new Func1<Pair<String, HttpCookie>, Observable<Pair<CalendarDay, HttpCookie>>>() {
                    @Override
                    public Observable<Pair<CalendarDay, HttpCookie>> call(final Pair<String, HttpCookie> stringHttpCookiePair) {
                        return CalendarParser
                                .parseRawWebpageForViewStateGeneratorAndEventValidation(stringHttpCookiePair.first)
                                .map(new Func1<CalendarDay, Pair<CalendarDay, HttpCookie>>() {
                                    @Override
                                    public Pair<CalendarDay, HttpCookie> call(CalendarDay calendarDay) {
                                        return new Pair<CalendarDay, HttpCookie>(calendarDay,
                                                stringHttpCookiePair.second);
                                    }
                                });
                    }
                })

                // Do the Post that actually attempts to book the room
                .flatMap(new Func1<Pair<CalendarDay, HttpCookie>, Observable<String>>() {
                    @Override
                    public Observable<String> call(Pair<CalendarDay, HttpCookie> calendarDayHttpCookiePair) {
                        return bookingInteractionWebService.createNewBookingAndGetWebpage(
                                calendarDayHttpCookiePair.first,
                                userRequest.timeCell,
                                userRequest.requestOptions,
                                userModel.getUserCredentialsFromStorage(),
                                calendarDayHttpCookiePair.second);
                    }
                })*/

                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Yo this is whack");
                    }

                    @Override
                    public void onNext(String s) {
                        Timber.v(s);
                        getBookingInteractionEventReplaySubject()
                                .onNext(new BookingInteractionEvent(userRequest.timeCell,
                                        BookingInteractionEventType.SUCCESS,
                                        userRequest.dayOfMonthNumber,
                                        userRequest.monthWord));

                    }
                });
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
                                .onNext(new BookingInteractionEvent(b.timeCell,
                                        b.type,
                                        b.dayOfMonthNumber,
                                        b.monthWord));
                    }
                });
    }

    /**
     * Can be used to monitor for how the bookingInteraction flow is progressing.
     * @see BookingInteractionEvent
     * @return
     */
    private ReplaySubject<BookingInteractionEvent> getBookingInteractionEventReplaySubject() {
        if(bookingInteractionEventReplaySubject == null || bookingInteractionEventReplaySubject.hasCompleted()) {
            bookingInteractionEventReplaySubject = ReplaySubject.createWithSize(1);
            bookingInteractionEventObservable = bookingInteractionEventReplaySubject.asObservable();
            return bookingInteractionEventReplaySubject;
        }else {
            return bookingInteractionEventReplaySubject;
        }
    }
}
