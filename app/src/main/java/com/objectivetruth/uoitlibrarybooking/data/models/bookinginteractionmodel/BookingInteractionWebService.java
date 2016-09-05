package com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.CalendarModel;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.requestoptions.BookRequestOptions;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.requestoptions.JoinOrLeaveRequest;
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

import static com.objectivetruth.uoitlibrarybooking.common.constants.LIBRARY.*;

public class BookingInteractionWebService {
    @Inject RequestQueue requestQueue;
    @Inject CalendarModel calendarModel;

    public BookingInteractionWebService(UOITLibraryBookingApp mApplication) {
        mApplication.getComponent().inject(this);
    }

    public Observable<String> getRawWebpageWithEmptyForm(final TimeCell timeCell) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                try {
                    return Observable.just(_getRawPageWithForm(timeCell));
                } catch (InterruptedException | ExecutionException e) {
                    Timber.w(e, "Error while getting raw form");
                    return Observable.error(e);
                } catch (UnsupportedEncodingException e) {
                    Timber.e(e, "Error while getting raw form. Really? Unsupported Encoding on UTF-8??");
                    return Observable.error(e);
                }
            }
        });
    }

    public Observable<String> createNewBookingAndGetResultWebpage(final CalendarDay calendarDay,
                                                            final TimeCell timeCell,
                                                            final RequestOptions requestOptions,
                                                            final UserCredentials userCredentials) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                try {
                    HashMap<String, String> urlFormData = new HashMap<>();
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
                    urlFormData.put("ctl00$ContentPlaceHolder1$ButtonReserve", "Create group");

                    return Observable.just(_getResultWebpageForPostByBlocking(urlFormData, timeCell.param_next, null));
                } catch (InterruptedException | ExecutionException e) {
                    Timber.w(e, "Error while getting final message.aspx");
                    return Observable.error(e);
                } catch (ClassCastException e) {
                    Timber.e(e, "Error while getting request options");
                    return Observable.error(e);
                }
            }
        });
    }

    public Observable<String> chooseLeaveBookingAndGetResultWebpage(final RequestOptions requestOptions) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                try {
                    JoinOrLeaveRequest joinOrLeaveLeaveOptions = (JoinOrLeaveRequest) requestOptions;
                    CalendarDay calendarDay = joinOrLeaveLeaveOptions.calendarDay;
                    TimeCell timeCell = joinOrLeaveLeaveOptions.timeCell;
                    String LEAVE_BUTTON_OPTION = "Leave the Group";

                    HashMap<String, String> urlFormData = new HashMap<>();
                    // Construct the URL encoded Form Data
                    urlFormData.put("__VIEWSTATE", calendarDay.extViewStateMain);
                    urlFormData.put("__VIEWSTATEGENERATOR", calendarDay.extViewStateGenerator);
                    urlFormData.put("__EVENTVALIDATION", calendarDay.extEventValidation);
                    urlFormData.put("ctl00$ContentPlaceHolder1$RadiobuttonListLeaveGroup",
                            joinOrLeaveLeaveOptions.groupValue);
                    urlFormData.put("ctl00$ContentPlaceHolder1$ButtonLeave", LEAVE_BUTTON_OPTION);
                    return Observable.just(_getResultWebpageForPostByBlocking(urlFormData, timeCell.param_next, null));
                } catch (InterruptedException | ExecutionException e) {
                    Timber.e(e, "Error while choosing the group to leave: " +
                            ((JoinOrLeaveRequest) requestOptions).timeCell.toString());
                    return Observable.error(e);
                } catch (ClassCastException e) {
                    Timber.e(e, "Error while getting request options");
                    return Observable.error(e);
                }
            }
        });
    }

    public Observable<String> chooseJoinBookingAndGetResultWebpage(final RequestOptions requestOptions) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                try {
                    JoinOrLeaveRequest joinOrLeaveRequest = (JoinOrLeaveRequest) requestOptions;
                    CalendarDay calendarDay = joinOrLeaveRequest.calendarDay;
                    TimeCell timeCell = joinOrLeaveRequest.timeCell;
                    String LEAVE_BUTTON_OPTION = "Create or Join a Group";

                    HashMap<String, String> urlFormData = new HashMap<>();
                    // Construct the URL encoded Form Data
                    urlFormData.put("__VIEWSTATE", calendarDay.extViewStateMain);
                    urlFormData.put("__VIEWSTATEGENERATOR", calendarDay.extViewStateGenerator);
                    urlFormData.put("__EVENTVALIDATION", calendarDay.extEventValidation);
                    urlFormData.put("ctl00$ContentPlaceHolder1$RadioButtonListJoinOrCreateGroup",
                            joinOrLeaveRequest.groupValue);
                    urlFormData.put("ctl00$ContentPlaceHolder1$ButtonJoinOrCreate", LEAVE_BUTTON_OPTION);
                    return Observable.just(_getResultWebpageForPostByBlocking(urlFormData, timeCell.param_next, null));
                } catch (InterruptedException | ExecutionException e) {
                    Timber.e(e, "Error while choosing the group to join: " +
                            ((JoinOrLeaveRequest) requestOptions).timeCell.toString());
                    return Observable.error(e);
                } catch (ClassCastException e) {
                    Timber.e(e, "Error while getting request options");
                    return Observable.error(e);
                }
            }
        });
    }

    public Observable<String> fillJoinOrLeaveLeaveFormAndGetResultWebpage(final UserCredentials userCredentials,
                                                                          final RequestOptions requestOptions,
                                                                          final CalendarDay calendarDay) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                try {
                    JoinOrLeaveRequest joinOrLeaveLeaveOptions = (JoinOrLeaveRequest) requestOptions;
                    String groupName = joinOrLeaveLeaveOptions.groupLabel;
                    String leaveButtonOption = "Leave " + groupName;
                    String refererOverride = LEAVE_GROUP_ABSOLUTE_URL;

                    HashMap<String, String> urlFormData = new HashMap<>();
                    // Construct the URL encoded Form Data
                    urlFormData.put("__VIEWSTATE", calendarDay.extViewStateMain);
                    urlFormData.put("__VIEWSTATEGENERATOR", calendarDay.extViewStateGenerator);
                    urlFormData.put("__EVENTVALIDATION", calendarDay.extEventValidation);
                    urlFormData.put("ctl00$ContentPlaceHolder1$TextBoxID", userCredentials.username);
                    urlFormData.put("ctl00$ContentPlaceHolder1$TextBoxPassword", userCredentials.password);
                    urlFormData.put("ctl00$ContentPlaceHolder1$ButtonLeave", leaveButtonOption);
                    return Observable.just(_getResultWebpageForPostByBlocking(urlFormData, LEAVE_GROUP_WEBPAGE,
                            refererOverride));
                } catch (InterruptedException | ExecutionException e) {
                    Timber.w(e, "Error while doing a leave for " + ((JoinOrLeaveRequest) requestOptions).timeCell.toString());
                    return Observable.error(e);
                } catch (ClassCastException e) {
                    Timber.e(e, "Error while getting request options");
                    return Observable.error(e);
                }
            }
        });
    }

    public Observable<String> fillJoinOrLeaveJoinFormAndGetResultWebpage(final UserCredentials userCredentials,
                                                                          final RequestOptions requestOptions,
                                                                          final CalendarDay calendarDay) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                try {
                    JoinOrLeaveRequest joinOrLeaveLeaveOptions = (JoinOrLeaveRequest) requestOptions;
                    String groupName = joinOrLeaveLeaveOptions.groupLabel;
                    String joinButtonOption = "Join " + groupName;
                    String refererOverride = JOIN_GROUP_ABSOLUTE_URL;

                    HashMap<String, String> urlFormData = new HashMap<>();
                    // Construct the URL encoded Form Data
                    urlFormData.put("__VIEWSTATE", calendarDay.extViewStateMain);
                    urlFormData.put("__VIEWSTATEGENERATOR", calendarDay.extViewStateGenerator);
                    urlFormData.put("__EVENTVALIDATION", calendarDay.extEventValidation);
                    urlFormData.put("ctl00$ContentPlaceHolder1$TextBoxID", userCredentials.username);
                    urlFormData.put("ctl00$ContentPlaceHolder1$TextBoxPassword", userCredentials.password);
                    urlFormData.put("ctl00$ContentPlaceHolder1$ButtonJoin", joinButtonOption);
                    return Observable.just(_getResultWebpageForPostByBlocking(urlFormData, JOIN_GROUP_WEBPAGE,
                            refererOverride));
                } catch (InterruptedException | ExecutionException e) {
                    Timber.w(e, "Error while doing a join for " + ((JoinOrLeaveRequest) requestOptions).timeCell.toString());
                    return Observable.error(e);
                } catch (ClassCastException e) {
                    Timber.e(e, "Error while getting request options");
                    return Observable.error(e);
                }
            }
        });
    }

    /**
     * Does a post request to the calendar application and returns the webpage as a string
     * @param urlFormData Form data as a map
     * @param nextUrl Ending path, example(temp.aspx    =>   something/temp.aspx)
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private String _getResultWebpageForPostByBlocking(final HashMap<String, String> urlFormData,
                                                 String nextUrl, String refererOverride)
            throws InterruptedException, ExecutionException{
        RequestFuture<String> future = RequestFuture.newFuture();
        final String urlPath = MAIN_LIBRARY_URL + nextUrl;
        Timber.i("Starting the POST request to: " + urlPath);
        final String refererHeader = refererOverride == null ? urlPath : refererOverride;

        StringRequest stringRequest =
                new StringRequest(Request.Method.POST, urlPath, future, future) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/x-www-form-urlencoded");
                        headers.put("Referer", refererHeader);
                        return headers;
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return _getBodyInBytesUsingMap(urlFormData);
                    }
                };
        requestQueue.add(stringRequest);
        String webpage = future.get();
        Timber.i("POST request finished to: " + urlPath);
        Timber.v(webpage);
        return webpage;

    }

    private String _getRawPageWithForm(final TimeCell timeCell)
            throws InterruptedException, ExecutionException, UnsupportedEncodingException {
        Timber.d("Starting a GET request for empty form...");
        RequestFuture<String> future = RequestFuture.newFuture();

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("starttime", timeCell.param_starttime);
        queryParams.put("room", timeCell.param_room);
        queryParams.put("next", timeCell.param_next);

        String urlPath = MAIN_LIBRARY_URL + timeCell.param_get_link + _mapToQueryString(queryParams);
        StringRequest stringRequest =
                new StringRequest(Request.Method.GET, urlPath, future, future) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String>  headers = new HashMap<String, String>();
                        headers.put("Referer", CALENDAR_ABSOLUTE_URL);
                        return headers;
                    }
                };
        requestQueue.add(stringRequest);
        String webpage = future.get();
        Timber.d("Finished Get request for empty form");
        Timber.v(webpage);
        return webpage;
    }

    /**
     * Converts a map of values into the URL encoded body required for post requests. Example: hello -> foo bar will give
     * byte array of hello=foo+bar
     * @param urlEncodedFormElements
     * @return
     */
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
