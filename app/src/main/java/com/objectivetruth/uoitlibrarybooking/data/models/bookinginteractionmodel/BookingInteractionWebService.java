package com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.CalendarModel;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.requestoptions.BookRequestOptions;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.requestoptions.RequestOptions;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarDay;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCell;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserCredentials;
import rx.Observable;
import rx.functions.Func0;
import timber.log.Timber;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static com.objectivetruth.uoitlibrarybooking.common.constants.LIBRARY.MAIN_CALENDAR_RELATIVE_PATH;
import static com.objectivetruth.uoitlibrarybooking.common.constants.LIBRARY.MAIN_CALENDAR_URL;

public class BookingInteractionWebService {
    @Inject RequestQueue requestQueue;
    @Inject CalendarModel calendarModel;

    public BookingInteractionWebService(UOITLibraryBookingApp mApplication) {
        mApplication.getComponent().inject(this);
    }

    public Observable<String> getRawWebpageWithForm(final TimeCell timeCell) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                try {
                    return Observable.just(_getRawPageWithForm(timeCell));
                } catch (InterruptedException | ExecutionException | UnsupportedEncodingException e) {
                    Timber.e(e, "Error while getting raw form");
                    return Observable.error(e);
                }
            }
        });
    }

    public Observable<String> createNewBookingAndGetWebpage(final CalendarDay calendarDay,
                                                            final TimeCell timeCell,
                                                            final RequestOptions requestOptions,
                                                            final UserCredentials userCredentials) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                try {
                    HashMap<String, String> urlFormData = new HashMap<String, String>();
                    // Construct the URL encoded Form Data
                    urlFormData.put("__VIEWSTATE", calendarDay.extViewStateMain);
                    urlFormData.put("__VIEWSTATEGENERATOR", calendarDay.extViewStateGenerator);
                    urlFormData.put("__EVENTVALIDATION", calendarDay.extEventValidation);
                    urlFormData.put("ctl00$ContentPlaceHolder1$TextBoxName",
                            ((BookRequestOptions) requestOptions).groupName);
                    urlFormData.put("ctl00$ContentPlaceHolder1$TextBoxNotes",
                            ((BookRequestOptions) requestOptions).comments);
                    urlFormData.put("ctl00$ContentPlaceHolder1$TextBoxGroupCode",
                            ((BookRequestOptions) requestOptions).groupCode);
                    urlFormData.put("ctl00$ContentPlaceHolder1$RadioButtonListDuration",
                            ((BookRequestOptions) requestOptions).duration);
                    urlFormData.put("ctl00$ContentPlaceHolder1$TextBoxPassword", userCredentials.password);
                    urlFormData.put("ctl00$ContentPlaceHolder1$TextBoxStudentID", userCredentials.username);
                    urlFormData.put("ctl00$ContentPlaceHolder1$RadioButtonListInstitutions", userCredentials.institutionId);
                    urlFormData.put("ctl00$ContentPlaceHolder1$ButtonReserve", "Create+group");

                    return Observable.just(_getRedirectForPostByBlocking(urlFormData, timeCell));
                } catch (InterruptedException | ExecutionException | ClassCastException e) {
                    Timber.e(e, "Error while getting final message.aspx");
                    return Observable.error(e);
                }
            }
        });
    }

    private String _getRedirectForPostByBlocking(final HashMap<String, String> urlFormData,
                                                 TimeCell timeCell)
            throws InterruptedException, ExecutionException{
        Timber.i("Starting the POST request to the booking endpoint");
        RequestFuture<String> future = RequestFuture.newFuture();
        final String urlPath = MAIN_CALENDAR_RELATIVE_PATH + timeCell.param_next;

        StringRequest stringRequest =
                new StringRequest(Request.Method.POST, urlPath, future, future) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/x-www-form-urlencoded");
                        headers.put("Referer", urlPath);
                        return headers;
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return _getBodyInBytesUsingMap(urlFormData);
                    }
                };
        requestQueue.add(stringRequest);
        Timber.i("POST request to the booking endpoint is finished");
        return future.get();

    }

    private String _getRawPageWithForm(final TimeCell timeCell)
            throws InterruptedException, ExecutionException, UnsupportedEncodingException {
        Timber.d("Starting the GET request to the form");
        RequestFuture<String> future = RequestFuture.newFuture();

        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("starttime", timeCell.param_starttime);
        queryParams.put("room", timeCell.param_room);
        queryParams.put("next", timeCell.param_next);

        String urlPath = MAIN_CALENDAR_RELATIVE_PATH + timeCell.param_get_link + _mapToQueryString(queryParams);
        Timber.v("URL: " + urlPath);
        StringRequest stringRequest =
                new StringRequest(Request.Method.GET, urlPath, future, future) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String>  headers = new HashMap<String, String>();
                        headers.put("Referer", MAIN_CALENDAR_URL);
                        return headers;
                    }
                };
        requestQueue.add(stringRequest);
        Timber.d("GET request finished for getting the form");
        return future.get();
    }

    private byte[] _getBodyInBytesUsingMap(HashMap<String, String> urlEncodedFormElements) {
        try{
            String content = "";
            Set<Map.Entry<String, String>> entrySet = urlEncodedFormElements.entrySet();
            Boolean isFirst = true;
            for(Map.Entry<String, String> entry: entrySet) {
                if(isFirst) {
                    content += (URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                            + URLEncoder.encode(entry.getValue(), "UTF-8"));
                    isFirst = false;
                }else {
                    content += ("&" + URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                            + URLEncoder.encode(entry.getValue(), "UTF-8"));
                }
            }
            Timber.v("Body content will be:");
            Timber.v(content);
            return content.getBytes();
        }catch (UnsupportedEncodingException e) {
            Timber.e(e, "UTF-8 Not supported by URLEncoder");
            return null;
        }

    }

    private String _mapToQueryString(Map<String, String> queryStringMap) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        sb.append("?");
        for(HashMap.Entry<String, String> e : queryStringMap.entrySet()){
            if(sb.length() > 0){
                sb.append('&');
            }
            sb.append(URLEncoder.encode(e.getKey(), "UTF-8")).append('=').append(URLEncoder.encode(e.getValue(), "UTF-8"));
        }
        return sb.toString();
    }
}
