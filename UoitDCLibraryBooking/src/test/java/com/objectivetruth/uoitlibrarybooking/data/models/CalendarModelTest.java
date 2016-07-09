package com.objectivetruth.uoitlibrarybooking.data.models;

import android.content.Context;
import android.content.SharedPreferences;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.DataModule;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarWebService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCE_NAMES.CALENDAR_SHARED_PREFERENCES_NAME;
import static org.junit.Assert.assertTrue;

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
        DataModule dataModuleMock = Mockito.mock(DataModule.class);

        Mockito.when(sharedPreferencesMock.edit()).thenReturn(sharedPreferencesEditorMock);
        Mockito.when(applicationMock.getSharedPreferences(CALENDAR_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE))
                .thenReturn(sharedPreferencesMock);
    }

    @Test
    public void calendarModel_emptyArguments_isNotEmpty() {
        assertTrue(true);
    }
}
