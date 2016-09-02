package com.objectivetruth.uoitlibrarybooking.data.models;

import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionEvent;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionEventType;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionScreenLoadEvent;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionWebService;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarWebService;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCell;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCellType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class BookingInteractionTest {
    private UOITLibraryBookingApp applicationMock;
    private CalendarWebService calendarWebServiceMock;
    private UserModel userModel;
    private BookingInteractionWebService bookingInteractionWebService;
    private TestSubscriber<BookingInteractionScreenLoadEvent> testSubscriber;
    private TimeCell mockTimeCell = new TimeCell();
    private static final int INDEX_OF_FIRST_EVENT = 0;

    @Before
    public void setUp() throws Exception {
        applicationMock = Mockito.mock(UOITLibraryBookingApp.class);
        calendarWebServiceMock = Mockito.mock(CalendarWebService.class);
        bookingInteractionWebService = Mockito.mock(BookingInteractionWebService.class);
        userModel = Mockito.mock(UserModel.class);
        mockTimeCell = _getMockTimeCell();
        testSubscriber = new TestSubscriber<>();
    }

    @Test
    public void whenNoScreenLoadEventsAreCalledShouldReturnNothing() throws Exception {
        BookingInteractionModel bookingInteractionModel =
                new BookingInteractionModel(applicationMock,
                        bookingInteractionWebService,
                        calendarWebServiceMock,
                        userModel);

        Observable<BookingInteractionScreenLoadEvent> bookingInteractionScreenLoadEventObservable =
                bookingInteractionModel.getBookingInteractionScreenLoadEventObservable();
        bookingInteractionScreenLoadEventObservable.subscribe(testSubscriber);
        List<BookingInteractionScreenLoadEvent> bookingInteractionScreenLoadEvents =
                bookingInteractionScreenLoadEventObservable
                .buffer(300, TimeUnit.MILLISECONDS)
                .toBlocking().next().iterator().next();

        testSubscriber.assertNoTerminalEvent(); // Sequence has not ended
        assertThat(bookingInteractionScreenLoadEvents.size(), is(0)); // Sequence should contain right # items
    }

    @Test
    public void whenScreenLoadEventIsPublishedShouldListenersShouldReceiveThatEvent() throws Exception {
        BookingInteractionModel bookingInteractionModel =
                new BookingInteractionModel(applicationMock,
                        bookingInteractionWebService,
                        calendarWebServiceMock,
                        userModel);

        bookingInteractionModel.getBookingInteractionScreenLoadEventPublishSubject()
                .onNext(new BookingInteractionScreenLoadEvent(mockTimeCell,
                        BookingInteractionEventType.BOOK, "12", "March"));
        Thread.sleep(200);

        BookingInteractionEvent screenLoadEvent =
                bookingInteractionModel.getBookingInteractionEventObservable().first()
                        .toBlocking().toFuture().get(300, TimeUnit.MILLISECONDS);

        assertThat(screenLoadEvent.type, is(BookingInteractionEventType.BOOK));
    }

    private TimeCell _getMockTimeCell() {
        mockTimeCell = new TimeCell();
        mockTimeCell.timeCellType = TimeCellType.BOOKING_OPEN;
        mockTimeCell.param_get_link = "temp.aspx";
        mockTimeCell.param_next = "book.aspx";
        mockTimeCell.param_starttime = "3:00 PM";
        mockTimeCell.param_room = "LIB202B";
        mockTimeCell.param_eventtarget = "ctl00$ContentPlaceHolder1$Calendar1";
        mockTimeCell.param_eventargument = "6085";
        return mockTimeCell;
    }
}
