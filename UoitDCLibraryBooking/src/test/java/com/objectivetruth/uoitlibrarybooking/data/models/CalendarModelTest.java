package com.objectivetruth.uoitlibrarybooking.data.models;

import android.content.Context;
import android.content.SharedPreferences;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarDataRefreshState;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarWebService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import rx.observers.TestSubscriber;

import java.util.List;

import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCES_KEYS.CALENDAR_DATA_JSON;
import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCE_NAMES.CALENDAR_SHARED_PREFERENCES_NAME;
import static com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarDataRefreshStateType.INITIAL;
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
        sharedPreferencesMock = Mockito.mock(SharedPreferences.class);
        sharedPreferencesEditorMock = Mockito.mock(SharedPreferences.Editor.class);
        applicationMock = Mockito.mock(UOITLibraryBookingApp.class);
        calendarWebServiceMock = Mockito.mock(CalendarWebService.class);

        Mockito.when(sharedPreferencesMock.edit()).thenReturn(sharedPreferencesEditorMock);
        Mockito.when(applicationMock.getSharedPreferences(CALENDAR_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE))
                .thenReturn(sharedPreferencesMock);
    }

    @Test
    public void ifNoCalendarDataIsStoredReturnsDefaultState() {
        int EXPECTED_NUMBER_OF_EVENTS_FIRED = 1;
        int INDEX_OF_FIRST_EVENT = 0;
        // Shared Prefs returns null if its the first time the app loads and has no default data
        Mockito.when(sharedPreferencesMock.getString(eq(CALENDAR_DATA_JSON), anyString()))
                .thenReturn(null);
        CalendarModel calendarModel = new CalendarModel(applicationMock, calendarWebServiceMock);

        TestSubscriber<CalendarDataRefreshState> testSubscriber = new TestSubscriber<>();
        calendarModel.getCalendarDataRefreshBehaviourSubject().subscribe(testSubscriber);
        List<CalendarDataRefreshState> calendarDataRefreshStateList = testSubscriber.getOnNextEvents();
        CalendarDataRefreshState initialEvent = calendarDataRefreshStateList.get(INDEX_OF_FIRST_EVENT);

        testSubscriber.assertNoTerminalEvent();
        assertSame(EXPECTED_NUMBER_OF_EVENTS_FIRED, calendarDataRefreshStateList.size());
        assertSame(INITIAL, initialEvent.type);
        assertSame(null, initialEvent.exception);
        assertSame(null, calendarDataRefreshStateList.get(INDEX_OF_FIRST_EVENT).calendarData);
    }
}
