package com.objectivetruth.uoitlibrarybooking.data.models;

import android.content.Context;
import android.content.SharedPreferences;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarDataRefreshState;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarWebService;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.RefreshActivateEvent;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCES_KEYS.CALENDAR_DATA_JSON;
import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCE_NAMES.CALENDAR_SHARED_PREFERENCES_NAME;
import static com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarDataRefreshStateType.*;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

public class CalendarModelTest {
    private SharedPreferences sharedPreferencesMock;
    private SharedPreferences.Editor sharedPreferencesEditorMock;
    private UOITLibraryBookingApp applicationMock;
    private CalendarWebService calendarWebServiceMock;

    @Before
    public void setUp() throws Exception {
        String nullJSON = "null";
        sharedPreferencesMock = Mockito.mock(SharedPreferences.class);
        sharedPreferencesEditorMock = Mockito.mock(SharedPreferences.Editor.class);
        applicationMock = Mockito.mock(UOITLibraryBookingApp.class);
        calendarWebServiceMock = Mockito.mock(CalendarWebService.class);

        Mockito.when(sharedPreferencesMock.edit()).thenReturn(sharedPreferencesEditorMock);
        Mockito.when(sharedPreferencesEditorMock.putString(CALENDAR_DATA_JSON, nullJSON))
                .thenReturn(sharedPreferencesEditorMock);
        Mockito.when(applicationMock.getSharedPreferences(CALENDAR_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE))
                .thenReturn(sharedPreferencesMock);
    }

    @Test
    public void whenNoCalendarDataIsStoredReturnsDefaultState() {
        int INDEX_OF_CURRENT_EVENT = 0;
        // Shared Prefs returns null if its the first time the app loads and has no default data
        Mockito.when(sharedPreferencesMock.getString(eq(CALENDAR_DATA_JSON), anyString()))
                .thenReturn(null);
        CalendarModel calendarModel = new CalendarModel(applicationMock, calendarWebServiceMock);

        TestSubscriber<CalendarDataRefreshState> testSubscriber = new TestSubscriber<>();
        calendarModel.getCalendarDataRefreshObservable().subscribe(testSubscriber);
        List<CalendarDataRefreshState> calendarDataRefreshStateList = testSubscriber.getOnNextEvents();
        CalendarDataRefreshState initialEvent = calendarDataRefreshStateList.get(INDEX_OF_CURRENT_EVENT);

        testSubscriber.assertNoTerminalEvent();
        assertSame(INITIAL, initialEvent.type);
        assertSame(null, initialEvent.exception);
        assertSame(null, initialEvent.calendarData);
    }

    @Test
    public void whenCalendarDataisBeingRefreshedReturnsRunningState() {
        int INDEX_OF_CURRENT_EVENT = 0;
        // Shared Prefs returns null if its the first time the app loads and has no default data
        Mockito.when(sharedPreferencesMock.getString(eq(CALENDAR_DATA_JSON), anyString()))
                .thenReturn(null);
        Mockito.when(calendarWebServiceMock.getRawInitialWebPageObs())
                .thenReturn(Observable.<String>never());
        CalendarModel calendarModel = new CalendarModel(applicationMock, calendarWebServiceMock);
        // Activate a refresh
        calendarModel.getRefreshActivatePublishSubject().onNext(new RefreshActivateEvent());

        TestSubscriber<CalendarDataRefreshState> testSubscriber = new TestSubscriber<>();
        calendarModel.getCalendarDataRefreshObservable().subscribe(testSubscriber);
        List<CalendarDataRefreshState> calendarDataRefreshStateList = testSubscriber.getOnNextEvents();
        CalendarDataRefreshState runningEvent = calendarDataRefreshStateList.get(INDEX_OF_CURRENT_EVENT);

        testSubscriber.assertNoTerminalEvent();
        assertSame(RUNNING, runningEvent.type);
        assertSame(null, runningEvent.exception);
        assertSame(null, runningEvent.calendarData);
    }

    @Test
    public void whenErrorOccursDuringRefreshReturnsErrorState() {
        int INDEX_OF_CURRENT_EVENT = 1;
        // Shared Prefs returns null if its the first time the app loads and has no default data
        Mockito.when(sharedPreferencesMock.getString(eq(CALENDAR_DATA_JSON), anyString()))
                .thenReturn(null);
        Mockito.when(calendarWebServiceMock.getRawInitialWebPageObs())
                .thenReturn(Observable.<String>error(new IllegalStateException("Exception For Testing")));

        CalendarModel calendarModel = new CalendarModel(applicationMock, calendarWebServiceMock);
        // Activate a refresh
        calendarModel.getRefreshActivatePublishSubject().onNext(new RefreshActivateEvent());

        TestSubscriber<CalendarDataRefreshState> testSubscriber = new TestSubscriber<>();
        calendarModel.getCalendarDataRefreshObservable().subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent(100, TimeUnit.MILLISECONDS);

        List<CalendarDataRefreshState> calendarDataRefreshStateList = testSubscriber.getOnNextEvents();
        CalendarDataRefreshState errorEvent = calendarDataRefreshStateList.get(INDEX_OF_CURRENT_EVENT);

        testSubscriber.assertNoTerminalEvent();
        assertSame(ERROR, errorEvent.type);
        assertSame("Exception For Testing", errorEvent.exception.getMessage());
        assertSame(null, errorEvent.calendarData);
    }

    @Test
    public void whenRefreshEventCompletesSuccessfullyReturnsSuccessWithCalendarData() {
        int INDEX_OF_CURRENT_EVENT = 1;
        // Shared Prefs returns null if its the first time the app loads and has no default data
        Mockito.when(sharedPreferencesMock.getString(eq(CALENDAR_DATA_JSON), anyString()))
                .thenReturn(null);
        Mockito.when(calendarWebServiceMock.getRawInitialWebPageObs())
                .thenReturn(Observable.<String>just(_getRawInitialWebpageWithNoDaysAvailableFromTestResources()));

        CalendarModel calendarModel = new CalendarModel(applicationMock, calendarWebServiceMock);
        // Activate a refresh
        calendarModel.getRefreshActivatePublishSubject().onNext(new RefreshActivateEvent());

        TestSubscriber<CalendarDataRefreshState> testSubscriber = new TestSubscriber<>();
        calendarModel.getCalendarDataRefreshObservable().subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent(100, TimeUnit.MILLISECONDS);

        List<CalendarDataRefreshState> calendarDataRefreshStateList = testSubscriber.getOnNextEvents();
        CalendarDataRefreshState successEvent = calendarDataRefreshStateList.get(INDEX_OF_CURRENT_EVENT);

        testSubscriber.assertNoTerminalEvent();
        assertSame(SUCCESS, successEvent.type);
        assertSame(null, successEvent.exception);
        assertSame(null, successEvent.calendarData);
    }

    private String _getRawInitialWebpageWithNoDaysAvailableFromTestResources() {
        String delim = File.separator;
        String noDaysAvailableFileLocation = ".." + delim + "UoitDCLibraryBooking" + delim + "src" + delim +
                "testResources" + delim + "server_responses" + delim + "no_days_available.aspx";
        try {
            return FileUtils.readFileToString(new File(noDaysAvailableFileLocation), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
