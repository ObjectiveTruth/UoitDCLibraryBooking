package com.objectivetruth.uoitlibrarybooking.data.models;

import android.content.Context;
import android.content.SharedPreferences;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.MyAccountDataLoginState;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserWebService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCES_KEYS.*;
import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCE_NAMES.USER_SHARED_PREFERENCES_NAME;
import static com.objectivetruth.uoitlibrarybooking.data.models.usermodel.MyAccountDataLoginStateType.SIGNED_OUT;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

public class UserModelTest {
    private SharedPreferences sharedPreferencesMock;
    private SharedPreferences.Editor sharedPreferencesEditorMock;
    private UOITLibraryBookingApp applicationMock;
    private UserWebService userWebServiceMock;
    private TestSubscriber testSubscriber;
    private static final int INDEX_OF_FIRST_EVENT = 0;

    @Before
    public void setUp() throws Exception {
        String nullJSON = "null";
        sharedPreferencesMock = Mockito.mock(SharedPreferences.class);
        sharedPreferencesEditorMock = Mockito.mock(SharedPreferences.Editor.class);
        applicationMock = Mockito.mock(UOITLibraryBookingApp.class);
        userWebServiceMock = Mockito.mock(UserWebService.class);
        testSubscriber = new TestSubscriber();

        Mockito.when(sharedPreferencesMock.edit()).thenReturn(sharedPreferencesEditorMock);
        Mockito.when(sharedPreferencesEditorMock.putString(anyString(), anyString()))
                .thenReturn(sharedPreferencesEditorMock);
        Mockito.when(sharedPreferencesEditorMock.remove(anyString()))
                .thenReturn(sharedPreferencesEditorMock);
        Mockito.when(applicationMock.getSharedPreferences(USER_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE))
                .thenReturn(sharedPreferencesMock);
    }

    @Test
    public void whenNoUserDataOrCredentialsReturnedSignedOutState() {
        Mockito.when(sharedPreferencesMock.getString(eq(USER_USERNAME), anyString())).thenReturn(null);
        Mockito.when(sharedPreferencesMock.getString(eq(USER_PASSWORD), anyString())).thenReturn(null);
        Mockito.when(sharedPreferencesMock.getString(eq(USER_INSTITUTION), anyString())).thenReturn(null);

        UserModel userModel = new UserModel(applicationMock, userWebServiceMock);

        Observable<MyAccountDataLoginState> myAccountDataLoginStateObservable = userModel.getLoginStateObservable();
        myAccountDataLoginStateObservable.subscribe(testSubscriber);
        List<MyAccountDataLoginState> myAccountDataLoginStateResults = myAccountDataLoginStateObservable
                .buffer(300, TimeUnit.MILLISECONDS)
                .toBlocking().next().iterator().next();
        MyAccountDataLoginState firstItem = myAccountDataLoginStateResults.get(INDEX_OF_FIRST_EVENT);

        testSubscriber.assertNoTerminalEvent(); // Sequence has not ended
        assertThat(myAccountDataLoginStateResults.size(), is(1)); // Sequence should contain right # items
        assertThat(firstItem.type, is(SIGNED_OUT));
        assertNull(firstItem.exception);
        assertNull(firstItem.userData);
    }
}
