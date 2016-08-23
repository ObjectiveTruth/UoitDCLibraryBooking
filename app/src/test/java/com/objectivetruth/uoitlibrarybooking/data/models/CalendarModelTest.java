package com.objectivetruth.uoitlibrarybooking.data.models;

import android.content.Context;
import android.content.SharedPreferences;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.*;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCES_KEYS.CALENDAR_DATA_JSON;
import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCE_NAMES.CALENDAR_SHARED_PREFERENCES_NAME;
import static com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarDataRefreshStateType.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;

public class CalendarModelTest {
    private SharedPreferences sharedPreferencesMock;
    private SharedPreferences.Editor sharedPreferencesEditorMock;
    private UOITLibraryBookingApp applicationMock;
    private CalendarWebService calendarWebServiceMock;
    private TestSubscriber<CalendarDataRefreshState> testSubscriber;
    private static final int INDEX_OF_FIRST_EVENT = 0;

    @Before
    public void setUp() throws Exception {
        String nullJSON = "null";
        sharedPreferencesMock = Mockito.mock(SharedPreferences.class);
        sharedPreferencesEditorMock = Mockito.mock(SharedPreferences.Editor.class);
        applicationMock = Mockito.mock(UOITLibraryBookingApp.class);
        calendarWebServiceMock = Mockito.mock(CalendarWebService.class);
        testSubscriber = new TestSubscriber<>();

        Mockito.when(sharedPreferencesMock.edit()).thenReturn(sharedPreferencesEditorMock);
        Mockito.when(sharedPreferencesEditorMock.putString(eq(CALENDAR_DATA_JSON), eq(nullJSON)))
                .thenReturn(sharedPreferencesEditorMock);
        Mockito.when(sharedPreferencesEditorMock.putString(eq(CALENDAR_DATA_JSON), anyString()))
                .thenReturn(sharedPreferencesEditorMock);
        Mockito.when(applicationMock.getSharedPreferences(CALENDAR_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE))
                .thenReturn(sharedPreferencesMock);
    }

    @Test
    public void whenNoCalendarDataIsStoredReturnsDefaultState() throws Exception {
        // Shared Prefs returns null if its the first time the app loads and has no default data
        Mockito.when(sharedPreferencesMock.getString(eq(CALENDAR_DATA_JSON), anyString()))
                .thenReturn(null);
        CalendarModel calendarModel = new CalendarModel(applicationMock, calendarWebServiceMock);

        Observable<CalendarDataRefreshState> calendarDataRefreshStateObservable =
                calendarModel.getCalendarDataRefreshObservable();
        calendarDataRefreshStateObservable.subscribe(testSubscriber);
        List<CalendarDataRefreshState> calendarDataRefreshStateListTestResults = calendarDataRefreshStateObservable
                .buffer(300, TimeUnit.MILLISECONDS)
                .toBlocking().next().iterator().next();
        CalendarDataRefreshState firstItem = calendarDataRefreshStateListTestResults.get(INDEX_OF_FIRST_EVENT);

        testSubscriber.assertNoTerminalEvent(); // Sequence has not ended
        assertThat(calendarDataRefreshStateListTestResults.size(), is(1)); // Sequence should contain right # items
        assertThat(firstItem.type, is(INITIAL));
        assertNull(firstItem.exception);
        assertNull(firstItem.calendarData);
    }

    @Test
    public void whenCalendarDataisBeingRefreshedReturnsRunningState() throws
            InterruptedException, TimeoutException, ExecutionException {
        // Shared Prefs returns null if its the first time the app loads and has no default data
        Mockito.when(sharedPreferencesMock.getString(eq(CALENDAR_DATA_JSON), anyString()))
                .thenReturn(null);
        Mockito.when(calendarWebServiceMock.getRawInitialWebPageObs())
                .thenReturn(Observable.<String>never());
        CalendarModel calendarModel = new CalendarModel(applicationMock, calendarWebServiceMock);
        // Activate a refresh
        calendarModel.getRefreshActivatePublishSubject().onNext(new RefreshActivateEvent());
        Thread.sleep(100);

        CalendarDataRefreshState currentState =
                calendarModel.getCalendarDataRefreshObservable().first()
                        .toBlocking().toFuture().get(300, TimeUnit.MILLISECONDS);

        assertThat(currentState.type, is(RUNNING));
        assertNull(currentState.exception);
        assertNull(currentState.calendarData);
    }

    @Test
    public void whenErrorOccursDuringRefreshReturnsErrorState() throws
            InterruptedException, TimeoutException, ExecutionException {
        // Shared Prefs returns null if its the first time the app loads and has no default data
        Mockito.when(sharedPreferencesMock.getString(eq(CALENDAR_DATA_JSON), anyString()))
                .thenReturn(null);
        Mockito.when(calendarWebServiceMock.getRawInitialWebPageObs())
                .thenReturn(Observable.<String>error(new IllegalStateException("Exception For Testing")));
        CalendarModel calendarModel = new CalendarModel(applicationMock, calendarWebServiceMock);
        // Activate a refresh
        calendarModel.getRefreshActivatePublishSubject().onNext(new RefreshActivateEvent());
        Thread.sleep(100);

        CalendarDataRefreshState currentState =
                calendarModel.getCalendarDataRefreshObservable().first()
                        .toBlocking().toFuture().get(300, TimeUnit.MILLISECONDS);

        assertThat(currentState.type, is(ERROR));
        assertThat(currentState.exception.getMessage(), is("Exception For Testing"));
        assertNull(currentState.calendarData);
    }

    @Test
    public void whenRefreshEventCompletesSuccessfullyReturnsSuccessWithCalendarData() throws
            InterruptedException, ExecutionException, TimeoutException {
        // Shared Prefs returns null if its the first time the app loads and has no default data
        Mockito.when(sharedPreferencesMock.getString(eq(CALENDAR_DATA_JSON), anyString()))
                .thenReturn(null);
        Mockito.when(calendarWebServiceMock.getRawInitialWebPageObs())
                .thenReturn(Observable.<String>just(_getRawInitialWebpageWithNoDaysAvailableFromTestResources()));
        CalendarModel calendarModel = new CalendarModel(applicationMock, calendarWebServiceMock);
        // Activate a refresh
        calendarModel.getRefreshActivatePublishSubject().onNext(new RefreshActivateEvent());
        Thread.sleep(100);

        CalendarDataRefreshState currentState =
                calendarModel.getCalendarDataRefreshObservable().first()
                        .toBlocking().toFuture().get(300, TimeUnit.MILLISECONDS);

        assertThat(currentState.type, is(SUCCESS));
        assertNull(currentState.exception);
        assertNull(currentState.calendarData);
    }

    @Test
    public void whenRefreshEventCompletesSuccessfullyWithMockDataReturnsSuccessWithCalendarDataParsedCorrectly() throws
            InterruptedException, ExecutionException, TimeoutException {
        // Shared Prefs returns null if its the first time the app loads and has no default data
        Mockito.when(sharedPreferencesMock.getString(eq(CALENDAR_DATA_JSON), anyString()))
                .thenReturn(null);
        Mockito.when(calendarWebServiceMock.getRawInitialWebPageObs())
                .thenReturn(Observable.<String>just(_getRawInitialWebpageWithONEDaysAvailableFromTestResources()));
        Mockito.when(calendarWebServiceMock.getRawClickableDatesWebPagesObs(any(CalendarData.class)))
                .thenReturn(Observable.<String[]>just(
                        new String[]{_getRawClickableDateHalfClosedHalfOpenFromTestResources()}));

        CalendarModel calendarModel = new CalendarModel(applicationMock, calendarWebServiceMock);
        // Activate a refresh
        calendarModel.getRefreshActivatePublishSubject().onNext(new RefreshActivateEvent());
        Thread.sleep(300);

        CalendarDataRefreshState currentState =
                calendarModel.getCalendarDataRefreshObservable().first()
                        .toBlocking().toFuture().get(300, TimeUnit.MILLISECONDS);

        // The hash represents the entire object's contents, so comparing we know the object is the same
        int TEST_DATA_HASH = 1068626024;
        assertThat(currentState.type, is(SUCCESS));
        assertNull(currentState.exception);
        assertThat(currentState.calendarData.computedHashCode, is(TEST_DATA_HASH));
    }

    @Test
    public void whenNoGridScrollingOccursReturnsDefaultState() throws Exception {
        // Shared Prefs returns null if its the first time the app loads and has no default data
        Mockito.when(sharedPreferencesMock.getString(eq(CALENDAR_DATA_JSON), anyString()))
                .thenReturn(null);
        TestSubscriber<ScrollAtTopOfGridEvent> testSubscriber = new TestSubscriber<>();
        CalendarModel calendarModel = new CalendarModel(applicationMock, calendarWebServiceMock);

        Observable<ScrollAtTopOfGridEvent> scrollAtTopGridObservable =
                calendarModel.getScrollAtTopGridObservable();
        scrollAtTopGridObservable.subscribe(testSubscriber);
        List<ScrollAtTopOfGridEvent> scrollAtTopOfGridEvents = scrollAtTopGridObservable
                .buffer(300, TimeUnit.MILLISECONDS)
                .toBlocking().next().iterator().next();
        ScrollAtTopOfGridEvent firstItem = scrollAtTopOfGridEvents.get(INDEX_OF_FIRST_EVENT);

        testSubscriber.assertNoTerminalEvent(); // Sequence has not ended
        assertThat(scrollAtTopOfGridEvents.size(), is(1)); // Sequence should contain right # items
        assertThat(firstItem.isScrollAtTop(), is(true));
    }

    @Test
    public void whenEventChangesScrollPositionReturnsTheNewState() throws
            InterruptedException, ExecutionException, TimeoutException {
        // Shared Prefs returns null if its the first time the app loads and has no default data
        Mockito.when(sharedPreferencesMock.getString(eq(CALENDAR_DATA_JSON), anyString()))
                .thenReturn(null);
        CalendarModel calendarModel = new CalendarModel(applicationMock, calendarWebServiceMock);
        // Activate a scrollTopChange
        calendarModel.getScrollAtTopOfGridBehaviourSubject().onNext(new ScrollAtTopOfGridEvent(false));
        Thread.sleep(100);

        ScrollAtTopOfGridEvent scrollAtTopOfGridEvent =
                calendarModel.getScrollAtTopGridObservable().first()
                        .toBlocking().toFuture().get(300, TimeUnit.MILLISECONDS);

        assertThat(scrollAtTopOfGridEvent.isScrollAtTop(), is(false));
    }

    private String _getRawInitialWebpageWithNoDaysAvailableFromTestResources() {
        String delim = File.separator;
        String noDaysAvailableFileLocation = ".." + delim + "app" + delim + "src" + delim +
                "testResources" + delim + "server_responses" + delim + "no_days_available.aspx";
        try {
            return FileUtils.readFileToString(new File(noDaysAvailableFileLocation), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String _getRawInitialWebpageWithONEDaysAvailableFromTestResources() {
        String delim = File.separator;
        String noDaysAvailableFileLocation = ".." + delim + "app" + delim + "src" + delim +
                "testResources" + delim + "server_responses" + delim + "1_day_available.aspx";
        try {
            return FileUtils.readFileToString(new File(noDaysAvailableFileLocation), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String _getRawClickableDateHalfClosedHalfOpenFromTestResources() {
        String delim = File.separator;
        String noDaysAvailableFileLocation = ".." + delim + "app" + delim + "src" + delim +
                "testResources" + delim + "server_responses" + delim + "clickabledate" + delim +
                "half_closed_half_open_8am-330pm.aspx";
        try {
            return FileUtils.readFileToString(new File(noDaysAvailableFileLocation), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
