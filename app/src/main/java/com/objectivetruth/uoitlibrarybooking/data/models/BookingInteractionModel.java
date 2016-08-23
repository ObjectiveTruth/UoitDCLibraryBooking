package com.objectivetruth.uoitlibrarybooking.data.models;

import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.*;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarDay;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarParser;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarWebService;
import com.objectivetruth.uoitlibrarybooking.statelessutilities.LeftOrRight;
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
        Timber.i("Booking Request Flow: Starting by calling the initial main webpage");
        calendarWebService.getRawInitialWebPageObs()

                .flatMap(new Func1<String, Observable<CalendarDay>>() {
                    @Override
                    public Observable<CalendarDay> call(String s) {
                        Timber.i("Booking Request Flow: Initial Webpage Received, sending to parser to get state info");
                        return CalendarParser.parseRawWebpageForViewStateGeneratorAndEventValidation(s);
                    }
                })

                .flatMap(new Func1<CalendarDay, Observable<String>>() {
                    @Override
                    public Observable<String> call(CalendarDay calendarDay) {
                        Timber.i("Booking Request Flow: Parsing Complete, clicking the day on the calendar");
                        calendarDay.extEventTarget = userRequest.timeCell.param_eventtarget;
                        calendarDay.extEventArgument = userRequest.timeCell.param_eventargument;

                        calendarDay.extDayOfMonthNumber = userRequest.dayOfMonthNumber;

                        return calendarWebService.getRawClickableCalendarDayPageUsingCalendarDay(calendarDay);
                    }
                })

                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String rawWebpage) {
                        Timber.i("Booking Request Flow: Received webpage for day on calendar, Clicking the time slot " +
                                "requested");
                        return bookingInteractionWebService.getRawWebpageWithEmptyForm(userRequest.timeCell);
                    }
                })

                .flatMap(new Func1<String, Observable<CalendarDay>>() {
                    @Override
                    public Observable<CalendarDay> call(final String rawWebpage) {
                        Timber.i("Booking Request Flow: Received webpage with the empty form for time slot, " +
                                "sending to parser to get state info");
                        return CalendarParser
                                .parseRawWebpageForViewStateGeneratorAndEventValidation(rawWebpage);
                    }
                })

                .flatMap(new Func1<CalendarDay, Observable<LeftOrRight<String, String>>>() {
                    @Override
                    public Observable<LeftOrRight<String, String>> call(CalendarDay calendarDay) {
                        Timber.i("Booking Request Flow: Parsing Complete, doing the final sending of options to server");
                        return bookingInteractionWebService.createNewBookingAndGetWebpage(
                                calendarDay,
                                userRequest.timeCell,
                                userRequest.requestOptions,
                                userModel.getUserCredentialsFromStorage())
                                // Transform by grabbing just the error message or the success message
                                .map(new Func1<String, LeftOrRight<String, String>>() {
                                    @Override
                                    public LeftOrRight<String, String> call(String rawWebpage) {
                                        Timber.i("Booking Request Flow: Received result page, parsing before passing " +
                                                "back. That will be the final step");
                                        return BookingInteractionParser.parseWebpageForMessageLabel(rawWebpage);
                                    }
                                });
                    }
                })

                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Observer<LeftOrRight<String, String>>() {
                    @Override
                    public void onCompleted() {
                        // Do nothing
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.w(e, "Error from booking");

                        BookingInteractionEvent eventToFire =
                                new BookingInteractionEvent(
                                        userRequest.timeCell,
                                        BookingInteractionEventType.ERROR,
                                        userRequest.dayOfMonthNumber,
                                        userRequest.monthWord);
                        eventToFire.message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();

                        getBookingInteractionEventReplaySubject().onNext(eventToFire);
                    }

                    @Override
                    public void onNext(LeftOrRight<String, String> result) {
                        if(result.hasLeft()) {
                            Timber.w("Bad Result from booking: " + result.getLeft());

                            BookingInteractionEvent eventToFire =
                                    new BookingInteractionEvent(
                                            userRequest.timeCell,
                                            BookingInteractionEventType.ERROR,
                                            userRequest.dayOfMonthNumber,
                                            userRequest.monthWord);
                            eventToFire.message = result.getLeft();

                            getBookingInteractionEventReplaySubject().onNext(eventToFire);

                        }else {
                            Timber.i("Good Result from booking: " + result.getRight());

                            BookingInteractionEvent eventToFire =
                                    new BookingInteractionEvent(
                                        userRequest.timeCell,
                                        BookingInteractionEventType.SUCCESS,
                                        userRequest.dayOfMonthNumber,
                                        userRequest.monthWord);
                            eventToFire.message = result.getRight();

                            getBookingInteractionEventReplaySubject().onNext(eventToFire);
                        }
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
