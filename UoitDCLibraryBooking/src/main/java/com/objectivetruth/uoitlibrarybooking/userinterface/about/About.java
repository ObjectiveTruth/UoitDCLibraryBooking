package com.objectivetruth.uoitlibrarybooking.userinterface.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.objectivetruth.uoitlibrarybooking.BuildConfig;
import com.objectivetruth.uoitlibrarybooking.R;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

import java.util.HashMap;
import java.util.Map;

public class About extends Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Element versionElement = _makeElement("Version " + BuildConfig.VERSION_NAME);

        Element joinBetaElement = _makeElement("Join Open Beta To See New Updates",
                "https://play.google.com/apps/testing/com.objectivetruth.uoitlibrarybooking");

        Element openSourceElement = _makeElement("View Open Source Project Page",
                "https://github.com/ObjectiveTruth/UoitDCLibraryBooking");

        Element feedbackElement = _makeElement("Make a suggestion/Submit a bug",
                "https://github.com/ObjectiveTruth/UoitDCLibraryBooking/issues");

        AboutPage aboutPage = new AboutPage(getActivity())
                .isRTL(false)
                .setImage(R.drawable.ic_launch)
                .addItem(versionElement)
                .addItem(openSourceElement)
                .addItem(joinBetaElement)
                .addItem(feedbackElement)

                .addGroup("Connect")
                .addEmail("uoitdclibrarybooking@objectivetruth.ca")

                .addGroup("Libraries Used");
        HashMap<String, String> librariesList = new HashMap<>();

        librariesList.put("Fancy Buttons",              "https://github.com/medyo/Fancybuttons");
        librariesList.put("Once",                       "https://github.com/jonfinerty/Once");
        librariesList.put("Android Iconics",            "https://github.com/mikepenz/Android-Iconics");

        _addItemsInHashMapToEndOfAboutPage(librariesList, aboutPage);

        return aboutPage.create();
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
}
