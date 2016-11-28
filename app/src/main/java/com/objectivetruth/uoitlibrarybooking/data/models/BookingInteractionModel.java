package com.objectivetruth.uoitlibrarybooking.data.models;

import android.support.v4.util.Pair;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.*;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarDay;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarParser;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarWebService;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.MyAccountDataLoginState;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.MyAccountDataLoginStateType;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserCredentials;
import com.objectivetruth.uoitlibrarybooking.statelessutilities.LeftOrRight;
import com.objectivetruth.uoitlibrarybooking.statelessutilities.Triple;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;
import timber.log.Timber;

import java.util.HashMap;

public class BookingInteractionModel {
    private BookingInteractionWebService bookingInteractionWebService;
    private CalendarWebService calendarWebService;
    private UserModel userModel;

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
        _initialize();
    }

    /**
     * Initializes observables so they're ready when they are requested
     */
    private void _initialize() {
        getBookingInteractionEventUserRequestSubject();
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
    public PublishSubject<BookingInteractionEventUserRequest> getBookingInteractionEventUserRequestSubject() {
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
                .withLatestFrom(userModel.getLoginStateObservable(),
                        new Func2<BookingInteractionEventUserRequest, MyAccountDataLoginState,
                                Pair<BookingInteractionEventUserRequest, MyAccountDataLoginState>>() {
                    @Override
                    public Pair<BookingInteractionEventUserRequest, MyAccountDataLoginState> call(
                            BookingInteractionEventUserRequest userRequest,
                            MyAccountDataLoginState myAccountDataLoginState) {
                        return new Pair<>(userRequest, myAccountDataLoginState);
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .subscribe(new Action1<Pair<BookingInteractionEventUserRequest, MyAccountDataLoginState>>() {
                    @Override
                    public void call(Pair<BookingInteractionEventUserRequest, MyAccountDataLoginState> p) {
                        _executeBasedOnUserRequestType(p);
                    }
                });
    }

    private void _executeBasedOnUserRequestType(Pair<BookingInteractionEventUserRequest, MyAccountDataLoginState> pair) {
        BookingInteractionEventUserRequest userRequest = pair.first;
        MyAccountDataLoginState loginState = pair.second;
        if(userRequest.type == BookingInteractionEventUserRequestType.JOINORLEAVE_GETTING_SPINNER_VALUES_REQUEST) {
            _doJoinOrLeaveGettingSpinnerValuesRequest(userRequest);
        }else if(loginState.type == MyAccountDataLoginStateType.SIGNED_IN) {
            switch (userRequest.type) {
                case BOOK_REQUEST:
                    _doBookRequest(userRequest);
                    break;
                case JOINORLEAVE_LEAVE_REQUEST:
                    _doJoinOrLeaveLeaveRequest(userRequest);
                    break;
                case JOINORLEAVE_JOIN_REQUEST:
                    _doJoinOrLeaveJoinRequest(userRequest);
                    break;
                default:
                    Timber.e(new Throwable(new IllegalStateException("Unknown Request Type")),
                            "A request was submitted but that request type was unknown: " + userRequest.type);
            }
        }else {
            Timber.w("Aborting a users request because they are not logged in, sending login event to front end");
            _sendEventToShowLoginScreen(userRequest);
        }
    }

    private void _sendEventToShowLoginScreen(BookingInteractionEventUserRequest userRequest) {
        BookingInteractionEvent eventToFire = new BookingInteractionEvent(
                userRequest.timeCell,
                BookingInteractionEventType.CREDENTIALS_LOGIN,
                userRequest.dayOfMonthNumber,
                userRequest.monthWord);
        getBookingInteractionEventReplaySubject().onNext(eventToFire);
    }

    private void _doJoinOrLeaveJoinRequest(final BookingInteractionEventUserRequest userRequest) {
        final String LOG_PREFIX = "JoinOrLeave-Join Flow: ";

        BookingInteractionEvent eventToFire = new BookingInteractionEvent(userRequest.timeCell,
                BookingInteractionEventType.JOIN_OR_LEAVE_JOIN_RUNNING, userRequest.dayOfMonthNumber,
                userRequest.monthWord);
        getBookingInteractionEventReplaySubject().onNext(eventToFire);

        Timber.i(LOG_PREFIX + "Clicking on the room to join and getting page with form to fill username/password");
        bookingInteractionWebService
                .chooseJoinBookingAndGetResultWebpage(userRequest.requestOptions)

                .flatMap(new Func1<String, Observable<CalendarDay>>() {
                    @Override
                    public Observable<CalendarDay> call(String rawWebpage) {
                        Timber.i(LOG_PREFIX + "Parsing for state information");
                        return CalendarParser.parseRawWebpageForViewStateGeneratorAndEventValidation(rawWebpage);
                    }
                })

                .flatMap(new Func1<CalendarDay, Observable<String>>() {
                    @Override
                    public Observable<String> call(CalendarDay calendarDay) {
                        UserCredentials userCredentials = userModel.getUserCredentialsFromStorage();
                        Timber.i(LOG_PREFIX + "Parsing complete, filling user information, and getting page");
                        return bookingInteractionWebService
                                .fillJoinOrLeaveJoinFormAndGetResultWebpage(
                                        userCredentials, userRequest.requestOptions, calendarDay);
                    }
                })

                .flatMap(new Func1<String, Observable<LeftOrRight<String, String>>>() {
                    @Override
                    public Observable<LeftOrRight<String, String>> call(String rawWebpage) {
                        Timber.i(LOG_PREFIX + "User page received, parsing for error or success messages");
                        return BookingInteractionParser.parseWebpageForMessageLabel(rawWebpage);
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
                        Timber.w(e, LOG_PREFIX + "Error");

                        BookingInteractionEvent eventToFire =
                                new BookingInteractionEvent(
                                        userRequest.timeCell,
                                        BookingInteractionEventType.JOIN_OR_LEAVE_JOIN_ERROR,
                                        userRequest.dayOfMonthNumber,
                                        userRequest.monthWord);
                        eventToFire.message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();

                        getBookingInteractionEventReplaySubject().onNext(eventToFire);
                    }

                    @Override
                    public void onNext(LeftOrRight<String, String> result) {
                        if(result.hasLeft()) {
                            Timber.w(LOG_PREFIX + "Bad Result: " + result.getLeft());

                            BookingInteractionEvent eventToFire =
                                    new BookingInteractionEvent(
                                            userRequest.timeCell,
                                            BookingInteractionEventType.JOIN_OR_LEAVE_JOIN_ERROR,
                                            userRequest.dayOfMonthNumber,
                                            userRequest.monthWord);
                            eventToFire.message = result.getLeft();

                            getBookingInteractionEventReplaySubject().onNext(eventToFire);

                        }else {
                            Timber.i(LOG_PREFIX + "Good Result: " + result.getRight());

                            BookingInteractionEvent eventToFire =
                                    new BookingInteractionEvent(
                                            userRequest.timeCell,
                                            BookingInteractionEventType.JOIN_OR_LEAVE_JOIN_SUCCESS,
                                            userRequest.dayOfMonthNumber,
                                            userRequest.monthWord);
                            eventToFire.message = result.getRight();

                            getBookingInteractionEventReplaySubject().onNext(eventToFire);
                        }
                    }
                });
    }

    private void _doJoinOrLeaveLeaveRequest(final BookingInteractionEventUserRequest userRequest) {
        final String LOG_PREFIX = "JoinOrLeave-Leave Flow: ";

        BookingInteractionEvent eventToFire = new BookingInteractionEvent(userRequest.timeCell,
                BookingInteractionEventType.JOIN_OR_LEAVE_LEAVE_RUNNING, userRequest.dayOfMonthNumber,
                userRequest.monthWord);
        getBookingInteractionEventReplaySubject().onNext(eventToFire);

        Timber.i(LOG_PREFIX + "Clicking on the room to leave and getting page with form to fill with username/password");
        bookingInteractionWebService
                .chooseLeaveBookingAndGetResultWebpage(userRequest.requestOptions)

                .flatMap(new Func1<String, Observable<CalendarDay>>() {
                    @Override
                    public Observable<CalendarDay> call(String rawWebpage) {
                        Timber.i(LOG_PREFIX + "Parsing for state information");
                        return CalendarParser.parseRawWebpageForViewStateGeneratorAndEventValidation(rawWebpage);
                    }
                })

                .flatMap(new Func1<CalendarDay, Observable<String>>() {
                    @Override
                    public Observable<String> call(CalendarDay calendarDay) {
                        Timber.i(LOG_PREFIX + "Parsing complete, filling user information, and getting page");
                        UserCredentials userCredentials = userModel.getUserCredentialsFromStorage();
                        return bookingInteractionWebService
                                .fillJoinOrLeaveLeaveFormAndGetResultWebpage(
                                        userCredentials, userRequest.requestOptions, calendarDay);
                    }
                })

                .flatMap(new Func1<String, Observable<LeftOrRight<String, String>>>() {
                    @Override
                    public Observable<LeftOrRight<String, String>> call(String rawWebpage) {
                        Timber.i(LOG_PREFIX + "User page received, parsing for error or success messages");
                        return BookingInteractionParser.parseWebpageForMessageLabel(rawWebpage);
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
                        Timber.w(e, LOG_PREFIX + "Error");

                        BookingInteractionEvent eventToFire =
                                new BookingInteractionEvent(
                                        userRequest.timeCell,
                                        BookingInteractionEventType.JOIN_OR_LEAVE_LEAVE_ERROR,
                                        userRequest.dayOfMonthNumber,
                                        userRequest.monthWord);
                        eventToFire.message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();

                        getBookingInteractionEventReplaySubject().onNext(eventToFire);
                    }

                    @Override
                    public void onNext(LeftOrRight<String, String> result) {
                        if(result.hasLeft()) {
                            Timber.w(LOG_PREFIX + "Bad Result: " + result.getLeft());

                            BookingInteractionEvent eventToFire =
                                    new BookingInteractionEvent(
                                            userRequest.timeCell,
                                            BookingInteractionEventType.JOIN_OR_LEAVE_LEAVE_ERROR,
                                            userRequest.dayOfMonthNumber,
                                            userRequest.monthWord);
                            eventToFire.message = result.getLeft();

                            getBookingInteractionEventReplaySubject().onNext(eventToFire);

                        }else {
                            Timber.i(LOG_PREFIX + "Good Result: " + result.getRight());

                            BookingInteractionEvent eventToFire =
                                    new BookingInteractionEvent(
                                            userRequest.timeCell,
                                            BookingInteractionEventType.JOIN_OR_LEAVE_LEAVE_SUCCESS,
                                            userRequest.dayOfMonthNumber,
                                            userRequest.monthWord);
                            eventToFire.message = result.getRight();

                            getBookingInteractionEventReplaySubject().onNext(eventToFire);
                        }
                    }
                });
    }

    private void _doJoinOrLeaveGettingSpinnerValuesRequest(final BookingInteractionEventUserRequest userRequest) {
        final String LOG_PREFIX = "JoinOrLeave Getting SpinnerValues Flow: ";

        BookingInteractionEvent eventToFire = new BookingInteractionEvent(userRequest.timeCell,
                BookingInteractionEventType.JOIN_OR_LEAVE_GETTING_SPINNER_VALUES_RUNNING, userRequest.dayOfMonthNumber,
                userRequest.monthWord);
        getBookingInteractionEventReplaySubject().onNext(eventToFire);

        Timber.i(LOG_PREFIX + "Starting by calling the initial main webpage");
        calendarWebService.getRawInitialWebPageObs()

                .flatMap(new Func1<String, Observable<CalendarDay>>() {
                    @Override
                    public Observable<CalendarDay> call(String s) {
                        Timber.i(LOG_PREFIX + "Initial Webpage Received, sending to parser to get state info");
                        return CalendarParser.parseRawWebpageForViewStateGeneratorAndEventValidation(s);
                    }
                })

                .flatMap(new Func1<CalendarDay, Observable<String>>() {
                    @Override
                    public Observable<String> call(CalendarDay calendarDay) {
                        Timber.i(LOG_PREFIX + "Parsing Complete, clicking the day on the calendar");
                        calendarDay.extEventTarget = userRequest.timeCell.param_eventtarget;
                        calendarDay.extEventArgument = userRequest.timeCell.param_eventargument;

                        calendarDay.extDayOfMonthNumber = userRequest.dayOfMonthNumber;

                        return calendarWebService.getRawClickableCalendarDayPageUsingCalendarDay(calendarDay);
                    }
                })

                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String rawWebpage) {
                        Timber.i(LOG_PREFIX + "Received webpage for day on calendar, Clicking the time slot requested");
                        return bookingInteractionWebService.getRawWebpageWithEmptyForm(userRequest.timeCell);
                    }
                })

                .flatMap(new Func1<String, Observable<Pair<String, CalendarDay>>>() {
                    @Override
                    public Observable<Pair<String, CalendarDay>> call(final String rawWebpage) {
                        Timber.i(LOG_PREFIX + "Received webpage with the empty form for time slot, " +
                                "sending to parser to get state info");
                        // Enrich the returned state information with the webpage received
                        return CalendarParser
                                .parseRawWebpageForViewStateGeneratorAndEventValidation(rawWebpage)
                                .map(new Func1<CalendarDay, Pair<String, CalendarDay>>() {
                                    @Override
                                    public Pair<String, CalendarDay> call(CalendarDay calendarDay) {
                                        return new Pair<>(rawWebpage, calendarDay);
                                    }
                                });
                    }
                })

                .flatMap(new Func1<Pair<String, CalendarDay>,
                        Observable<Triple<HashMap<String, String>, HashMap<String, String>, CalendarDay>>>() {
                    @Override
                    public Observable<Triple<HashMap<String, String>, HashMap<String, String>, CalendarDay>>
                    call(final Pair<String, CalendarDay> stringCalendarDayPair) {
                        Timber.i(LOG_PREFIX + "State info received, parsing for join and leave spinner values");
                        return BookingInteractionParser.parseJoinOrLeaveFormForSpinners(stringCalendarDayPair.first)
                                // Convert the received Pair into a triple for easy usage in the next step
                                .map(new Func1<Pair<HashMap<String, String>, HashMap<String, String>>, Triple<HashMap<String, String>, HashMap<String, String>, CalendarDay>>() {
                                    @Override
                                    public Triple<HashMap<String, String>, HashMap<String, String>, CalendarDay>
                                    call(Pair<HashMap<String, String>, HashMap<String, String>> SpinnerJoinLeavePair) {
                                        return new Triple<>(
                                                SpinnerJoinLeavePair.first,
                                                SpinnerJoinLeavePair.second,
                                                stringCalendarDayPair.second);
                                    }
                                });
                    }
                })

                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Observer<Triple<HashMap<String, String>, HashMap<String, String>, CalendarDay>>() {
                    @Override
                    public void onCompleted() {
                        // Do nothing
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.w(e, LOG_PREFIX + "Error");

                        BookingInteractionEvent eventToFire =
                                new BookingInteractionEvent(
                                        userRequest.timeCell,
                                        BookingInteractionEventType.JOIN_OR_LEAVE_GETTING_SPINNER_VALUES_ERROR,
                                        userRequest.dayOfMonthNumber,
                                        userRequest.monthWord);
                        eventToFire.message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();

                        getBookingInteractionEventReplaySubject().onNext(eventToFire);
                    }

                    @Override
                    public void onNext(Triple<HashMap<String, String>, HashMap<String, String>, CalendarDay> result) {
                        if(result.getLeft().isEmpty() || result.getMiddle().isEmpty()) {
                            Timber.w(new Throwable(new IllegalStateException("Spinner values cannot be empty")),
                                    "Error when parsing for spinner values, they cannot be empty");

                            BookingInteractionEvent eventToFire =
                                    new BookingInteractionEvent(
                                            userRequest.timeCell,
                                            BookingInteractionEventType.JOIN_OR_LEAVE_GETTING_SPINNER_VALUES_ERROR_NO_VALUES,
                                            userRequest.dayOfMonthNumber,
                                            userRequest.monthWord);
                            getBookingInteractionEventReplaySubject().onNext(eventToFire);
                        }
                        BookingInteractionEvent eventToFire =
                                new BookingInteractionEvent(
                                        userRequest.timeCell,
                                        BookingInteractionEventType.JOIN_OR_LEAVE_GETTING_SPINNER_VALUES_SUCCESS,
                                        userRequest.dayOfMonthNumber,
                                        userRequest.monthWord);
                        eventToFire.joinOrLeaveGetSpinnerResult = result;
                        getBookingInteractionEventReplaySubject().onNext(eventToFire);
                    }
                });
    }

    private void _doBookRequest(final BookingInteractionEventUserRequest userRequest) {
        final String LOG_PREFIX = "Booking Request Flow: ";

        BookingInteractionEvent eventToFire = new BookingInteractionEvent(userRequest.timeCell,
                        BookingInteractionEventType.BOOK_RUNNING, userRequest.dayOfMonthNumber, userRequest.monthWord);
        getBookingInteractionEventReplaySubject().onNext(eventToFire);

        Timber.i(LOG_PREFIX + "Starting by calling the initial main webpage");
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
                        Timber.i(LOG_PREFIX + "Parsing Complete, clicking the day on the calendar");
                        calendarDay.extEventTarget = userRequest.timeCell.param_eventtarget;
                        calendarDay.extEventArgument = userRequest.timeCell.param_eventargument;

                        calendarDay.extDayOfMonthNumber = userRequest.dayOfMonthNumber;

                        return calendarWebService.getRawClickableCalendarDayPageUsingCalendarDay(calendarDay);
                    }
                })

                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String rawWebpage) {
                        Timber.i(LOG_PREFIX + "Received webpage for day on calendar, Clicking the time slot requested");
                        return bookingInteractionWebService.getRawWebpageWithEmptyForm(userRequest.timeCell);
                    }
                })

                .flatMap(new Func1<String, Observable<CalendarDay>>() {
                    @Override
                    public Observable<CalendarDay> call(final String rawWebpage) {
                        Timber.i(LOG_PREFIX + "Received webpage with the empty form for time slot, " +
                                "sending to parser to get state info");
                        return CalendarParser
                                .parseRawWebpageForViewStateGeneratorAndEventValidation(rawWebpage);
                    }
                })

                .flatMap(new Func1<CalendarDay, Observable<String>>() {
                    @Override
                    public Observable<String> call(CalendarDay calendarDay) {
                        Timber.i(LOG_PREFIX + "Parsing Complete, doing the final sending of options to server");
                        return bookingInteractionWebService.createNewBookingAndGetResultWebpage(
                                calendarDay,
                                userRequest.timeCell,
                                userRequest.requestOptions,
                                userModel.getUserCredentialsFromStorage());
                    }
                })

                .flatMap(new Func1<String, Observable<LeftOrRight<String, String>>>() {
                    @Override
                    public Observable<LeftOrRight<String, String>> call(String resultWebpage) {
                        Timber.i(LOG_PREFIX + "Received result page, parsing before passing " +
                                "back. That will be the final step");
                        return BookingInteractionParser.parseWebpageForMessageLabel(resultWebpage);
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
                        Timber.w(e, LOG_PREFIX + "Error from booking");

                        BookingInteractionEvent eventToFire =
                                new BookingInteractionEvent(
                                        userRequest.timeCell,
                                        BookingInteractionEventType.BOOK_ERROR,
                                        userRequest.dayOfMonthNumber,
                                        userRequest.monthWord);
                        eventToFire.message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();

                        getBookingInteractionEventReplaySubject().onNext(eventToFire);
                    }

                    @Override
                    public void onNext(LeftOrRight<String, String> result) {
                        if(result.hasLeft()) {
                            Timber.w(LOG_PREFIX + "Bad Result from booking: " + result.getLeft());

                            BookingInteractionEvent eventToFire =
                                    new BookingInteractionEvent(
                                            userRequest.timeCell,
                                            BookingInteractionEventType.BOOK_ERROR,
                                            userRequest.dayOfMonthNumber,
                                            userRequest.monthWord);
                            eventToFire.message = result.getLeft();

                            getBookingInteractionEventReplaySubject().onNext(eventToFire);

                        }else {
                            Timber.i(LOG_PREFIX + "Good Result from booking: " + result.getRight());

                            BookingInteractionEvent eventToFire =
                                    new BookingInteractionEvent(
                                        userRequest.timeCell,
                                        BookingInteractionEventType.BOOK_SUCCESS,
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
    public ReplaySubject<BookingInteractionEvent> getBookingInteractionEventReplaySubject() {
        if(bookingInteractionEventReplaySubject == null || bookingInteractionEventReplaySubject.hasCompleted()) {
            bookingInteractionEventReplaySubject = ReplaySubject.createWithSize(1);
            bookingInteractionEventObservable = bookingInteractionEventReplaySubject
                    .asObservable()
                    .subscribeOn(Schedulers.computation());
            return bookingInteractionEventReplaySubject;
        }else {
            return bookingInteractionEventReplaySubject;
        }
    }
}
