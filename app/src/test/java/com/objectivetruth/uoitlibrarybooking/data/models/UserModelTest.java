package com.objectivetruth.uoitlibrarybooking.data.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.util.Pair;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.MyAccountDataLoginState;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.MyAccountSignoutEvent;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserCredentials;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserWebService;
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

import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCES_KEYS.*;
import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCE_NAMES.USER_SHARED_PREFERENCES_NAME;
import static com.objectivetruth.uoitlibrarybooking.data.models.usermodel.MyAccountDataLoginStateType.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;

public class UserModelTest {
    private SharedPreferences sharedPreferencesMock;
    private SharedPreferences.Editor sharedPreferencesEditorMock;
    private UOITLibraryBookingApp applicationMock;
    private UserWebService userWebServiceMock;
    private TestSubscriber testSubscriber;
    private UserCredentials userCredentialsMock;
    private String MY_RESERVATION_ERROR_MESSAGE_FROM_SERVER =
            "An error occurred: Your student ID and password doesn't match.Logon failure: unknown user name or bad password.";
    private static final int INDEX_OF_FIRST_EVENT = 0;

    @Before
    public void setUp() throws Exception {
        String nullJSON = "null";
        sharedPreferencesMock = Mockito.mock(SharedPreferences.class);
        sharedPreferencesEditorMock = Mockito.mock(SharedPreferences.Editor.class);
        applicationMock = Mockito.mock(UOITLibraryBookingApp.class);
        userWebServiceMock = Mockito.mock(UserWebService.class);
        testSubscriber = new TestSubscriber();
        userCredentialsMock = new UserCredentials("fakeusername", "fakepassword", "fakeinstitution");

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
        // Shared pref is empty so everyting returns null
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

    @Test
    public void whenActivatingSignInReturnedRunningState() throws InterruptedException,
            ExecutionException, TimeoutException {
        // Shared pref is empty so everyting returns null
        Mockito.when(sharedPreferencesMock.getString(eq(USER_USERNAME), anyString())).thenReturn(null);
        Mockito.when(sharedPreferencesMock.getString(eq(USER_PASSWORD), anyString())).thenReturn(null);
        Mockito.when(sharedPreferencesMock.getString(eq(USER_INSTITUTION), anyString())).thenReturn(null);

        Mockito.when(userWebServiceMock.getRawInitialSignInWebPageObs())
                .thenReturn(Observable.<String>never());
        UserModel userModel = new UserModel(applicationMock, userWebServiceMock);
        // Activate a Signin
        userModel.getSigninActivatePublishSubject().onNext(new UserCredentials("username", "password", "uoit"));
        Thread.sleep(100);

        MyAccountDataLoginState currentState =
                userModel.getLoginStateObservable().first()
                        .toBlocking().toFuture().get(300, TimeUnit.MILLISECONDS);

        assertThat(currentState.type, is(RUNNING));
        assertNull(currentState.exception);
        assertNull(currentState.userData);
    }

    @Test
    public void whenActivatingSignoutReturnSignoutState() throws InterruptedException,
            ExecutionException, TimeoutException {
        // Shared pref is empty so everyting returns null
        Mockito.when(sharedPreferencesMock.getString(eq(USER_USERNAME), anyString())).thenReturn("fakeusername");
        Mockito.when(sharedPreferencesMock.getString(eq(USER_PASSWORD), anyString())).thenReturn("fakepassword");
        Mockito.when(sharedPreferencesMock.getString(eq(USER_INSTITUTION), anyString())).thenReturn("fakeinstitution");

        Mockito.when(userWebServiceMock.getRawInitialSignInWebPageObs())
                .thenReturn(Observable.<String>never());
        UserModel userModel = new UserModel(applicationMock, userWebServiceMock);
        // Activate a Signout
        userModel.getSignoutActivatePublishSubject().onNext(new MyAccountSignoutEvent());
        Thread.sleep(100);

        MyAccountDataLoginState currentState =
                userModel.getLoginStateObservable().first()
                        .toBlocking().toFuture().get(300, TimeUnit.MILLISECONDS);

        assertThat(currentState.type, is(SIGNED_OUT));
        assertNull(currentState.exception);
        assertNull(currentState.userData);
    }

    @Test
    public void whenActivatingSigninAndSuccessfulReturnSigninState() throws InterruptedException,
            ExecutionException, TimeoutException, IOException {
        // Shared pref is full because user is signed in
        Mockito.when(sharedPreferencesMock.getString(eq(USER_USERNAME), anyString())).thenReturn("fakeusername");
        Mockito.when(sharedPreferencesMock.getString(eq(USER_PASSWORD), anyString())).thenReturn("fakepassword");
        Mockito.when(sharedPreferencesMock.getString(eq(USER_INSTITUTION), anyString())).thenReturn("fakeinstitution");

        Mockito.when(userWebServiceMock.getRawInitialSignInWebPageObs())
                .thenReturn(Observable.just(_getRawInitialMyReservationsWebpage()));
        Mockito.when(userWebServiceMock.getRawSignedInMyReservationsPageObs(any(UserCredentials.class)))
                .thenReturn(Observable.just(new Pair<String, UserCredentials>(_getRawSuccessSignInNoReservations(),
                        new UserCredentials("fakeusername", "fakepassword", "fakeinstitution"))));

        UserModel userModel = new UserModel(applicationMock, userWebServiceMock);
        // Activate a Signin
        userModel.getSigninActivatePublishSubject().onNext(null);
        Thread.sleep(300);

        MyAccountDataLoginState currentState =
                userModel.getLoginStateObservable().first()
                        .toBlocking().toFuture().get(300, TimeUnit.MILLISECONDS);

        assertThat(currentState.type, is(SIGNED_IN));
        assertNull(currentState.exception);
        assertThat(currentState.userData.completeBookings.size(), is(0));
        assertThat(currentState.userData.incompleteBookings.size(), is(0));
        assertThat(currentState.userData.pastBookings.size(), is(0));
    }

    @Test
    public void whenWrongUsernameAndPasswordReturnsSignoutStateWithErrorInfo() throws InterruptedException,
            ExecutionException, TimeoutException, IOException {
        // Shared pref is empty so everyting returns null
        Mockito.when(sharedPreferencesMock.getString(eq(USER_USERNAME), anyString())).thenReturn(null);
        Mockito.when(sharedPreferencesMock.getString(eq(USER_PASSWORD), anyString())).thenReturn(null);
        Mockito.when(sharedPreferencesMock.getString(eq(USER_INSTITUTION), anyString())).thenReturn(null);

        Mockito.when(userWebServiceMock.getRawInitialSignInWebPageObs())
                .thenReturn(Observable.just(_getRawInitialMyReservationsWebpage()));
        Mockito.when(userWebServiceMock.getRawSignedInMyReservationsPageObs(any(UserCredentials.class)))
                .thenReturn(Observable.just(new Pair<String, UserCredentials>(_getRawWrongUsernamePassword(),
                        userCredentialsMock)));

        UserModel userModel = new UserModel(applicationMock, userWebServiceMock);
        // Activate a Signin
        userModel.getSigninActivatePublishSubject().onNext(userCredentialsMock);
        Thread.sleep(300);

        MyAccountDataLoginState currentState =
                userModel.getLoginStateObservable().first()
                        .toBlocking().toFuture().get(300, TimeUnit.MILLISECONDS);

        assertThat(currentState.type, is(SIGNED_OUT));
        assertThat(currentState.exception.getMessage(), containsString(MY_RESERVATION_ERROR_MESSAGE_FROM_SERVER));
        assertNull(currentState.userData);
    }

    @Test
    public void whenServerTimesoutReturnTimeoutExceptionAsErrorState() throws InterruptedException,
            ExecutionException, TimeoutException, IOException {
        // Shared pref is empty so everyting returns null
        Mockito.when(sharedPreferencesMock.getString(eq(USER_USERNAME), anyString())).thenReturn(null);
        Mockito.when(sharedPreferencesMock.getString(eq(USER_PASSWORD), anyString())).thenReturn(null);
        Mockito.when(sharedPreferencesMock.getString(eq(USER_INSTITUTION), anyString())).thenReturn(null);

        Mockito.when(userWebServiceMock.getRawInitialSignInWebPageObs())
                .thenReturn(Observable.<String>error(new Exception("Main Fake Error",
                        new TimeoutException("Server took too long to respond, try again"))));

        UserModel userModel = new UserModel(applicationMock, userWebServiceMock);
        // Activate a Signin
        userModel.getSigninActivatePublishSubject().onNext(userCredentialsMock);
        Thread.sleep(300);

        MyAccountDataLoginState currentState =
                userModel.getLoginStateObservable().first()
                        .toBlocking().toFuture().get(300, TimeUnit.MILLISECONDS);

        assertThat(currentState.type, is(ERROR));
        assertThat(currentState.exception.getMessage(), containsString("Server took too long to respond, try again"));
        assertNull(currentState.userData);
    }

    private String _getRawInitialMyReservationsWebpage() throws IOException {
        String delim = File.separator;
        String noDaysAvailableFileLocation = ".." + delim + "UoitDCLibraryBooking" + delim + "src" + delim +
                "testResources" + delim + "server_responses" + delim + "my_reservations" + delim +
                "initial_my_reservations.aspx";
        return FileUtils.readFileToString(new File(noDaysAvailableFileLocation), "UTF-8");
    }

    private String _getRawSuccessSignInNoReservations() throws IOException {
        String delim = File.separator;
        String noDaysAvailableFileLocation = ".." + delim + "UoitDCLibraryBooking" + delim + "src" + delim +
                "testResources" + delim + "server_responses" + delim + "my_reservations" + delim + "sign_in" + delim +
                "success_sign_in_no_reservations.aspx";
        return FileUtils.readFileToString(new File(noDaysAvailableFileLocation), "UTF-8");
    }

    private String _getRawWrongUsernamePassword() throws IOException {
        String delim = File.separator;
        String noDaysAvailableFileLocation = ".." + delim + "UoitDCLibraryBooking" + delim + "src" + delim +
                "testResources" + delim + "server_responses" + delim + "my_reservations" + delim + "sign_in" + delim +
                "wrong_username_password.aspx";
        return FileUtils.readFileToString(new File(noDaysAvailableFileLocation), "UTF-8");
    }
}
