package com.objectivetruth.uoitlibrarybooking.userinterface.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.objectivetruth.uoitlibrarybooking.BuildConfig;
import com.objectivetruth.uoitlibrarybooking.MainActivity;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.common.constants.Analytics;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class About extends Fragment{
    private static final String ABOUT_TITLE = "About";
    @Inject public Tracker tracker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        HashMap<String, String> librariesList = new HashMap<>();


        Element versionElement = _makeElement("Version " + BuildConfig.VERSION_NAME);

        Element joinBetaElement = _makeElement("Join Open Beta",
                "https://play.google.com/apps/testing/com.objectivetruth.uoitlibrarybooking");

        Element openSourceElement = _makeElement("Visit Open Source Project Page",
                "https://github.com/ObjectiveTruth/UoitDCLibraryBooking");

        Element feedbackElement = _makeElement("Make a suggestion/Submit a bug",
                "https://github.com/ObjectiveTruth/UoitDCLibraryBooking/issues");

        AboutPage aboutPage = new AboutPage(getActivity())
                .setDescription(getResources().getString(R.string.about_description))

                .isRTL(false)
                .setImage(R.drawable.ic_launch)
                .addItem(versionElement)
                .addItem(openSourceElement)
                .addItem(joinBetaElement)
                .addItem(feedbackElement)

                .addGroup("Connect")
                .addEmail("uoitdclibrarybooking@objectivetruth.ca")

                .addGroup("Libraries Used");
        librariesList.put("Dagger 2",                       "http://google.github.io/dagger/");
        librariesList.put("RxJava",                         "https://github.com/ReactiveX/RxJava");
        librariesList.put("RxAndroid",                      "https://github.com/ReactiveX/RxAndroid");
        librariesList.put("OKHttp",                         "https://github.com/square/okhttp");
        librariesList.put("GSON",                           "https://github.com/google/gson");
        librariesList.put("Otto",                           "https://github.com/square/otto");
        librariesList.put("RoadRunner",                     "https://github.com/glomadrian/RoadRunner");
        librariesList.put("Timber",                         "https://github.com/JakeWharton/timber");
        librariesList.put("NineoldAndroid",                 "https://github.com/JakeWharton/NineOldAndroids");
        librariesList.put("Anroid Animations",              "https://github.com/daimajia/AndroidViewAnimations");
        librariesList.put("Android About Page",             "https://github.com/medyo/android-about-page");

        _addItemsInHashMapToEndOfAboutPage(librariesList, aboutPage);

        return aboutPage.create();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((UOITLibraryBookingApp) getActivity().getApplication()).getComponent().inject(this);
    }

    @Override
    public void onHiddenChanged(boolean isNowHidden) {
        if(isNowHidden) {
            Timber.d(getClass().getSimpleName() + " isNowHidden");
        }else {
            Timber.d(getClass().getSimpleName() + " isNowVisible");
            _setTitle(ABOUT_TITLE);
            tracker.setScreenName(Analytics.ScreenNames.ABOUT);
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
        super.onHiddenChanged(isNowHidden);
    }

    private void _setTitle(String title) {
        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    private Element _makeElement(String description) {
        Element element = new Element();
        element.setTitle(description);
        return element;
    }

    private Element _makeElement(String description, String url) {
        Element element = new Element();
        element.setTitle(description);
        _addUrlIntentToElement(url, element);
        return element;
    }

    private void _addItemsInHashMapToEndOfAboutPage(HashMap<String, String> librariesList, AboutPage aboutPage) {
        for (Map.Entry<String, String> lib : librariesList.entrySet()) {
            Element libElement = new Element();
            libElement.setTitle(lib.getKey());

            _addUrlIntentToElement(lib.getValue(), libElement);

            aboutPage.addItem(libElement);
        }
    }

    private void _addUrlIntentToElement(String url, Element element) {
        Intent libIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        element.setIntent(libIntent);
    }

    public static About newInstance() {
        return new About();
    }
}
