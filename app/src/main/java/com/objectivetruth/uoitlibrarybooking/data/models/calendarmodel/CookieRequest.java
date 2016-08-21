package com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.net.HttpCookie;

public class CookieRequest extends Request<HttpCookie> {
    private final Response.Listener<HttpCookie> listener;

    public CookieRequest(int method, String url,
                         Response.Listener<HttpCookie> listener,
                         Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = listener;
    }

    @Override
    protected Response<HttpCookie> parseNetworkResponse(NetworkResponse response) {
        String cookieResponse = response.headers.get("SET-COOKIE");
        HttpCookie uoitCookie = HttpCookie.parse(cookieResponse).get(0);
        return Response.success(uoitCookie, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(HttpCookie response) {
        listener.onResponse(response);
    }
}
