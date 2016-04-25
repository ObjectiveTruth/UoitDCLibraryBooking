package com.objectivetruth.uoitlibrarybooking.app.networking;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;

class CustomCookieJar implements CookieJar {
    private List<Cookie> cookies;

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        Timber.v("Cookies Received:");
        for(Cookie cookie: cookies) {
            Timber.v(cookie.toString());
        }
        this.cookies =  cookies;
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        Timber.v("Cookies requested");
        if (cookies != null){
            return cookies;
        }
        return new ArrayList<Cookie>();

    }
}
