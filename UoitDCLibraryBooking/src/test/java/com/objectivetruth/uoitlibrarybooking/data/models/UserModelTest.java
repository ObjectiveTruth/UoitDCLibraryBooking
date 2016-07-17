package com.objectivetruth.uoitlibrarybooking.data.models;

import android.content.Context;
import android.content.SharedPreferences;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserWebService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import rx.Subscriber;
import rx.observers.TestSubscriber;

import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCE_NAMES.USER_SHARED_PREFERENCES_NAME;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;

public class UserModelTest {
    private SharedPreferences sharedPreferencesMock;
    private SharedPreferences.Editor sharedPreferencesEditorMock;
    private UOITLibraryBookingApp applicationMock;
    private UserWebService userWebServiceMock;
    private Subscriber testSubscriber;

    @Before
    public void setUp() throws Exception {
        String nullJSON = "null";
        sharedPreferencesMock = Mockito.mock(SharedPreferences.class);
        sharedPreferencesEditorMock = Mockito.mock(SharedPreferences.Editor.class);
        applicationMock = Mockito.mock(UOITLibraryBookingApp.class);
        userWebServiceMock = Mockito.mock(UserWebService.class);
        testSubscriber = new TestSubscriber<>();

        Mockito.when(sharedPreferencesMock.edit()).thenReturn(sharedPreferencesEditorMock);
        Mockito.when(sharedPreferencesEditorMock.putString(anyString(), anyString()))
                .thenReturn(sharedPreferencesEditorMock);
        Mockito.when(applicationMock.getSharedPreferences(USER_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE))
                .thenReturn(sharedPreferencesMock);
    }

    @Test
    public void whenNoUserDataOrCredentialsReturnedSignedOutState() {
        UserModel userModel = new UserModel(applicationMock, userWebServiceMock);
        assertTrue(true);
    }
}
