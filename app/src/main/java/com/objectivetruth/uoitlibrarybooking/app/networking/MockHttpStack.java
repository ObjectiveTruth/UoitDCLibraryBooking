package com.objectivetruth.uoitlibrarybooking.app.networking;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCES_KEYS;
import com.objectivetruth.uoitlibrarybooking.statelessutilities.ResourceLoadingUtilities;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import timber.log.Timber;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;

import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.POST;
import static com.objectivetruth.uoitlibrarybooking.common.constants.LIBRARY.*;

public class MockHttpStack extends OkHttp3Stack{
    private static final int SIMULATED_DELAY_MS = 300;
    private final Context context;
    private SharedPreferences sharedPreferences;

    public MockHttpStack(Context context) {
        super();
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public HttpResponse performRequest(Request<?> request, Map<String, String> stringStringMap)
            throws IOException, AuthFailureError {
        if(_hasUserNOTRequestedMockingOfHTTP()) {return super.performRequest(request, stringStringMap);}
        URL url = new URL(request.getUrl());
        String baseUrlAndPath = url.getProtocol() + url.getHost() + url.getPath();

        switch(baseUrlAndPath) {
            case MY_RESERVATIONS_SIGNIN_ABSOLUTE_URL: switch(request.getMethod()) {
                    case GET: return _simulateResponseWithBody(request, _getInitialReservationWebpageEntity());
                    case POST: return _simulateResponseWithBody(request, _getSignInFailReservationWebpageEntity());
                } break;

            case CALENDAR_ABSOLUTE_URL: switch(request.getMethod()) {
                    case GET: return _simulateResponseWithBody(request, _getInitialWebpageEntity());
                    case POST: return _simulateResponseWithBody(request, _getClickableDateEntity());
                } break;
            case "temp.aspx": switch(request.getMethod()) {
                    case GET: return _simulateResponseWithBody(request, _getBookWebpageEntity());
                } break;
            case BOOK_ABSOLUTE_URL: switch(request.getMethod()) {
                    case POST: return _simulateResponseWithBody(request, _getBookSuccessWebpageEntity());
                } break;

        }
        Timber.w("No valid mock available for method: " + request.getMethod() + ", URL: "
                + request.getUrl() + ". Delegating to real HTTP Stack");
        return super.performRequest(request, stringStringMap);
    }

    private HttpResponse _simulateResponseWithBody(Request<?> request, HttpEntity bodyEntity) throws AuthFailureError {
        HttpResponse response
                = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));
        response.setLocale(Locale.CANADA);
        response.setEntity(bodyEntity);
        request.getHeaders(); //Simulate getting the headers
        request.getBody(); // Simulate getting the request body

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
            Timber.e(e, "Error delaying in MockHTTPStack");
        }

        return response;
    }

    private HttpEntity _getBookSuccessWebpageEntity() throws UnsupportedEncodingException {
        return new StringEntity(ResourceLoadingUtilities.loadAssetTextAsString(context, "mock_responses/book_success_message.aspx"));
    }

    private HttpEntity _getBookWebpageEntity() throws UnsupportedEncodingException {
        return new StringEntity(ResourceLoadingUtilities.loadAssetTextAsString(context, "mock_responses/book.aspx"));
    }

    private HttpEntity _getSignInFailReservationWebpageEntity() throws UnsupportedEncodingException {
        String FAKE_SIGN_IN_FAILURE_RESPONSE_FILENAME = "wrong_username_password.aspx";
        String rawWebPage = ResourceLoadingUtilities.loadAssetTextAsString(context,
                FAKE_SIGN_IN_FAILURE_RESPONSE_FILENAME);
        return new StringEntity(rawWebPage);
    }

    private HttpEntity _getInitialReservationWebpageEntity() throws UnsupportedEncodingException {
        String FAKE_INITIAL_MY_RESERVATIONS_RESPONSE_FILENAME = "initial_my_reservations.aspx";
        String rawWebPage = ResourceLoadingUtilities.loadAssetTextAsString(context,
                FAKE_INITIAL_MY_RESERVATIONS_RESPONSE_FILENAME);
        return new StringEntity(rawWebPage);
    }

    private HttpEntity _getInitialWebpageEntity() throws UnsupportedEncodingException {
        String FAKE_1_CLICKABLE_DATE_RESPONSE_FILENAME = "1_day_available.aspx";
        String rawWebPage = ResourceLoadingUtilities.loadAssetTextAsString(context,
                FAKE_1_CLICKABLE_DATE_RESPONSE_FILENAME);
        return new StringEntity(rawWebPage);
    }

    private HttpEntity _getClickableDateEntity() throws UnsupportedEncodingException {
        String FAKE_HALF_CLOSED_HALF_OPEN_RESPONSE_FILENAME = "half_closed_half_open_8am-330pm.aspx";
        String rawWebPage = ResourceLoadingUtilities.loadAssetTextAsString(context,
                FAKE_HALF_CLOSED_HALF_OPEN_RESPONSE_FILENAME);
        return new StringEntity(rawWebPage);
    }

    private boolean _hasUserNOTRequestedMockingOfHTTP() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return !sharedPreferences.getBoolean(SHARED_PREFERENCES_KEYS.DEBUG_SHOULD_MOCK_HTTP_CALLS, false);
    }
}
