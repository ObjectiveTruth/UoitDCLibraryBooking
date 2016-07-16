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
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

public class CalendarModelTest {
    private SharedPreferences sharedPreferencesMock;
    private SharedPreferences.Editor sharedPreferencesEditorMock;
    private UOITLibraryBookingApp applicationMock;
    private CalendarWebService calendarWebServiceMock;
    private TestSubscriber<CalendarDataRefreshState> testSubscriber;
    private static final int INDEX_OF_FIRST_EVENT = 0;
    private static final int INDEX_OF_SECOND_EVENT = 1;
    private static final int INDEX_OF_THIRD_EVENT = 2;

    @Before
    public void setUp() throws Exception {
        String nullJSON = "null";
        sharedPreferencesMock = Mockito.mock(SharedPreferences.class);
        sharedPreferencesEditorMock = Mockito.mock(SharedPreferences.Editor.class);
        applicationMock = Mockito.mock(UOITLibraryBookingApp.class);
        calendarWebServiceMock = Mockito.mock(CalendarWebService.class);
        testSubscriber = new TestSubscriber<>();

        Mockito.when(sharedPreferencesMock.edit()).thenReturn(sharedPreferencesEditorMock);
        Mockito.when(sharedPreferencesEditorMock.putString(CALENDAR_DATA_JSON, nullJSON))
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
    public void whenCalendarDataisBeingRefreshedReturnsRunningState() {
        // Shared Prefs returns null if its the first time the app loads and has no default data
        Mockito.when(sharedPreferencesMock.getString(eq(CALENDAR_DATA_JSON), anyString()))
                .thenReturn(null);
        Mockito.when(calendarWebServiceMock.getRawInitialWebPageObs())
                .thenReturn(Observable.<String>never());
        CalendarModel calendarModel = new CalendarModel(applicationMock, calendarWebServiceMock);

        Observable<CalendarDataRefreshState> calendarDataRefreshStateObservable =
                calendarModel.getCalendarDataRefreshObservable();
        calendarDataRefreshStateObservable.subscribe(testSubscriber);
        Observable<List<CalendarDataRefreshState>> listBlockingObservable = calendarDataRefreshStateObservable
                .buffer(300, TimeUnit.MILLISECONDS);

        // Activate a refresh
        calendarModel.getRefreshActivatePublishSubject().onNext(new RefreshActivateEvent());

        List<CalendarDataRefreshState> listOfSequenceResults =
                listBlockingObservable.toBlocking().next().iterator().next();

        testSubscriber.assertNoTerminalEvent(); //Sequence has not ended
        assertThat(listOfSequenceResults.size(), is(2)); // Sequence should contain right # items
        assertThat(listOfSequenceResults.get(INDEX_OF_FIRST_EVENT).type, is(INITIAL));
        assertNull(listOfSequenceResults.get(INDEX_OF_FIRST_EVENT).exception);
        assertNull(listOfSequenceResults.get(INDEX_OF_FIRST_EVENT).calendarData);

        assertThat(listOfSequenceResults.get(INDEX_OF_SECOND_EVENT).type, is(RUNNING));
        assertNull(listOfSequenceResults.get(INDEX_OF_SECOND_EVENT).exception);
        assertNull(listOfSequenceResults.get(INDEX_OF_SECOND_EVENT).calendarData);
    }

    @Test
    public void whenErrorOccursDuringRefreshReturnsErrorState() {
        // Shared Prefs returns null if its the first time the app loads and has no default data
        Mockito.when(sharedPreferencesMock.getString(eq(CALENDAR_DATA_JSON), anyString()))
                .thenReturn(null);
        Mockito.when(calendarWebServiceMock.getRawInitialWebPageObs())
                .thenReturn(Observable.<String>error(new IllegalStateException("Exception For Testing")));
        CalendarModel calendarModel = new CalendarModel(applicationMock, calendarWebServiceMock);

        Observable<CalendarDataRefreshState> calendarDataRefreshStateObservable =
                calendarModel.getCalendarDataRefreshObservable();
        calendarDataRefreshStateObservable.subscribe(testSubscriber);
        Observable<List<CalendarDataRefreshState>> listBlockingObservable = calendarDataRefreshStateObservable
                .buffer(300, TimeUnit.MILLISECONDS);

        // Activate a refresh
        calendarModel.getRefreshActivatePublishSubject().onNext(new RefreshActivateEvent());

        List<CalendarDataRefreshState> listOfSequenceResults =
                listBlockingObservable.toBlocking().next().iterator().next();

        testSubscriber.assertNoTerminalEvent(); //Sequence has not ended
        assertThat(listOfSequenceResults.size(), is(3)); // Sequence should contain right # items
        assertThat(listOfSequenceResults.get(INDEX_OF_FIRST_EVENT).type, is(INITIAL));
        assertNull(listOfSequenceResults.get(INDEX_OF_FIRST_EVENT).exception);
        assertNull(listOfSequenceResults.get(INDEX_OF_FIRST_EVENT).calendarData);

        assertThat(listOfSequenceResults.get(INDEX_OF_SECOND_EVENT).type, is(RUNNING));
        assertNull(listOfSequenceResults.get(INDEX_OF_SECOND_EVENT).exception);
        assertNull(listOfSequenceResults.get(INDEX_OF_SECOND_EVENT).calendarData);

        assertThat(listOfSequenceResults.get(INDEX_OF_THIRD_EVENT).type, is(ERROR));
        assertThat(listOfSequenceResults.get(INDEX_OF_THIRD_EVENT).exception.getMessage(), is("Exception For Testing"));
        assertNull(listOfSequenceResults.get(INDEX_OF_THIRD_EVENT).calendarData);
    }

    @Test
    public void whenRefreshEventCompletesSuccessfullyReturnsSuccessWithCalendarData() {
        // Shared Prefs returns null if its the first time the app loads and has no default data
        Mockito.when(sharedPreferencesMock.getString(eq(CALENDAR_DATA_JSON), anyString()))
                .thenReturn(null);
        Mockito.when(calendarWebServiceMock.getRawInitialWebPageObs())
                .thenReturn(Observable.<String>just(_getRawInitialWebpageWithNoDaysAvailableFromTestResources()));

        CalendarModel calendarModel = new CalendarModel(applicationMock, calendarWebServiceMock);

        Observable<CalendarDataRefreshState> calendarDataRefreshStateObservable =
                calendarModel.getCalendarDataRefreshObservable();
        calendarDataRefreshStateObservable.subscribe(testSubscriber);
        Observable<List<CalendarDataRefreshState>> listBlockingObservable = calendarDataRefreshStateObservable
                .buffer(300, TimeUnit.MILLISECONDS);

        // Activate a refresh
        calendarModel.getRefreshActivatePublishSubject().onNext(new RefreshActivateEvent());

        List<CalendarDataRefreshState> listOfSequenceResults =
                listBlockingObservable.toBlocking().next().iterator().next();

        testSubscriber.assertNoTerminalEvent(); //Sequence has not ended
        assertThat(listOfSequenceResults.size(), is(3)); // Sequence should contain right # items
        assertThat(listOfSequenceResults.get(INDEX_OF_FIRST_EVENT).type, is(INITIAL));
        assertNull(listOfSequenceResults.get(INDEX_OF_FIRST_EVENT).exception);
        assertNull(listOfSequenceResults.get(INDEX_OF_FIRST_EVENT).calendarData);

        assertThat(listOfSequenceResults.get(INDEX_OF_SECOND_EVENT).type, is(RUNNING));
        assertNull(listOfSequenceResults.get(INDEX_OF_SECOND_EVENT).exception);
        assertNull(listOfSequenceResults.get(INDEX_OF_SECOND_EVENT).calendarData);

        assertThat(listOfSequenceResults.get(INDEX_OF_THIRD_EVENT).type, is(SUCCESS));
        assertNull(listOfSequenceResults.get(INDEX_OF_THIRD_EVENT).exception);
        assertNull(listOfSequenceResults.get(INDEX_OF_THIRD_EVENT).calendarData);
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
