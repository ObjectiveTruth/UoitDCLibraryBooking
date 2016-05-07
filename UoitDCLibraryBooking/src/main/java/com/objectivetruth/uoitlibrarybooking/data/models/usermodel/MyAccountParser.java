package com.objectivetruth.uoitlibrarybooking.data.models.usermodel;

import android.support.v4.util.Pair;
import rx.Observable;
import timber.log.Timber;

import java.util.ArrayList;

import static com.objectivetruth.uoitlibrarybooking.data.models.common.ParseUtilities.findStringFromStringBetweenSearchTerms;
import static com.objectivetruth.uoitlibrarybooking.data.models.usermodel.MyAccountBookingType.COMPLETE_BOOKING;
import static com.objectivetruth.uoitlibrarybooking.data.models.usermodel.MyAccountBookingType.INCOMPLETE_BOOKING;
import static com.objectivetruth.uoitlibrarybooking.data.models.usermodel.MyAccountBookingType.PAST_BOOKING;

public class MyAccountParser {
    static public Observable<UserCredentials> parseRawInitialWebPageToGetStateInfo(String rawMyReservationWebPage,
                                                                                   UserCredentials userCredentials) {
        return Observable.just(_parseRawInitialWebPageToGetStateInfo(userCredentials, rawMyReservationWebPage));
    }

    static public Observable<Pair<UserData, UserCredentials>> parseRawSignedInMyReservationsWebPageForUserData(
            Pair<String, UserCredentials> rawWebpageUserCredentialsPair){
        return Observable.just(
                new Pair<>(
                        _parseRawSignedInMyReservationsWebPageForUserData(rawWebpageUserCredentialsPair.first),
                        rawWebpageUserCredentialsPair.second));
    }

    static private UserCredentials _parseRawInitialWebPageToGetStateInfo(UserCredentials userCredentials,
                                                                           String rawMyReservationsWebPage){
        Timber.i("Starting the parsing of the uoitlibrary my reservations main webpage for ViewState, " +
                "ViewStateGenerator, and EvenValidation values...");

        userCredentials.viewState = findStringFromStringBetweenSearchTerms(rawMyReservationsWebPage,
                "__VIEWSTATE\" value=\"", "\" />");
        Timber.v("viewstate: " + userCredentials.viewState);

        userCredentials.viewStateGenerator = findStringFromStringBetweenSearchTerms(rawMyReservationsWebPage,
                "__VIEWSTATEGENERATOR\" value=\"", "\" />");
        Timber.v("viewStateGenerator: " + userCredentials.viewStateGenerator);


        userCredentials.eventValidation = findStringFromStringBetweenSearchTerms(rawMyReservationsWebPage,
                "__EVENTVALIDATION\" value=\"", "\" />");
        Timber.v("eventValidation: " + userCredentials.eventValidation);

        Timber.i("Finished parsing of the uoitlibrary my reservations main webpage for ViewState, " +
                "ViewStateGenerator, and EvenValidation values...");

        return userCredentials;
    }

    static private UserData _parseRawSignedInMyReservationsWebPageForUserData(String rawWebPage) {
        Timber.i("Starting the parsing of the raw webpage from my reservations sign-in attempt");

        if(_doesRawMyReservationsSignInResultContainsError(rawWebPage)) {
            Timber.i("Finished parsing of the raw Webpage from my reservation sign in attempt");
            return _getUserDataObjectWithErrorMessage(_getErrorMessageFromSignInResultWebPage(rawWebPage));
        }

        UserData returnUserData = new UserData();
        returnUserData.completeBookings = _getBookingsList(COMPLETE_BOOKING, rawWebPage);
        returnUserData.incompleteBookings = _getBookingsList(INCOMPLETE_BOOKING, rawWebPage);
        returnUserData.pastBookings = _getBookingsList(PAST_BOOKING, rawWebPage);

        Timber.i("Finished parsing of the raw Webpage from my reservation sign in attempt");
        return returnUserData;
    }

    static private Pair<String, String> _getSearchTermForBookingType(MyAccountBookingType bookingType) {
        switch(bookingType) {
            case INCOMPLETE_BOOKING:
                return new Pair<>("<table id=\"ContentPlaceHolder1_TableInComplete\" cellspacing=\"1\" " +
                    "cellpadding=\"2\" style=\"border-width:1px;border-style:Outset;font-size:12pt;\">", "</table>");

            case COMPLETE_BOOKING:
                return new Pair<>("<table id=\"ContentPlaceHolder1_TableComplete\" cellspacing=\"1\" " +
                    "cellpadding=\"2\" style=\"border-width:1px;border-style:Outset;font-size:12pt;\">", "</table>");

            case PAST_BOOKING:
                return new Pair<>("<table id=\"ContentPlaceHolder1_TablePast\" cellspacing=\"1\" " +
                    "cellpadding=\"2\" style=\"border-width:1px;border-style:Outset;font-size:12pt;\">", "</table>");

            default:
                return new Pair<>("", "");
        }
    }

    static private ArrayList<MyAccountBooking> _getBookingsList(MyAccountBookingType bookingType,
                                                                String rawWebpage) {
        Timber.i("Parsing for " + bookingType.name() + "...");

        Pair<String, String> searchTerms = _getSearchTermForBookingType(bookingType);
        String bookingTableSection = findStringFromStringBetweenSearchTerms(rawWebpage, searchTerms.first,
                searchTerms.second);

        if(bookingTableSection.contains("<tr")) {
            String[] trStore = bookingTableSection.split("<tr");
            ArrayList<MyAccountBooking> returnArrayList = new ArrayList<MyAccountBooking>();

            // Start at 2 because 0 is the empty string and 1 is the headers in the table
            // Example: "<tr".split("<tr")    =>    {"", "<tr"}
            // Example:
            // Header Row: // <tr><td>Room</td><td>Date</td><td>From</td><td>To</td></tr>
            for(int i = 2; i < trStore.length; i++) {
                MyAccountBooking returnMyAccountBooking = new MyAccountBooking();
                String[] tdElements = trStore[2].split("<td");

                // Will be returned in the form ">$$$$$</td>" where $ is the target string
                returnMyAccountBooking.room =
                        findStringFromStringBetweenSearchTerms(tdElements[1], ">", "</td");
                returnMyAccountBooking.date =
                        findStringFromStringBetweenSearchTerms(tdElements[2], ">", "</td");
                returnMyAccountBooking.startTime =
                        findStringFromStringBetweenSearchTerms(tdElements[3], ">", "</td");
                returnMyAccountBooking.endTime =
                        findStringFromStringBetweenSearchTerms(tdElements[4], ">", "</td");

                returnArrayList.add(returnMyAccountBooking);
            }
            Timber.d(bookingType.name() + " Found: " + returnArrayList.size());
            Timber.i("Parsing Completed for " + bookingType.name() + "...");
            return returnArrayList;
        }else {
            // No InComplete Bookings
            Timber.d("No " + bookingType.name() + " found for User");
            Timber.i("Parsing Completed for " + bookingType.name() + "...");
            return new ArrayList<MyAccountBooking>();
        }
    }

    static private boolean _doesRawMyReservationsSignInResultContainsError(String rawWebpage) {
        String ERROR_LABEL_REGEX = "id=\"ContentPlaceHolder1_LabelError\"";
        return rawWebpage.contains(ERROR_LABEL_REGEX);
    }

    static private UserData _getUserDataObjectWithErrorMessage(String errorMessage) {
        UserData userData = new UserData();
        userData.errorMessage = errorMessage;
        return userData;
    }

    static private String _getErrorMessageFromSignInResultWebPage(String rawWebPage) {
        String errorMessageToReturn;
        String STYLE_ERROR_LABEL_START_TAG =
                "<span id=\"ContentPlaceHolder1_LabelError\" style=\"color:Red;font-size:11pt;z-index: 102; " +
                        "left: 2px; position: absolute; top: 74px; width: 406px; height: 34px;\">";
        String STYLE_ERROR_LABEL_END_TAG = "</span>";

        errorMessageToReturn =
                findStringFromStringBetweenSearchTerms(rawWebPage,
                        STYLE_ERROR_LABEL_START_TAG, STYLE_ERROR_LABEL_END_TAG);

        errorMessageToReturn = _removeNewLineCharacters(errorMessageToReturn);

        // Check if there's any strangeness, so we can return something intelligable to the user
        if(errorMessageToReturn == null || errorMessageToReturn.isEmpty() || errorMessageToReturn.length() > 200){
            errorMessageToReturn = "Something went wrong, try again";
        }
        return errorMessageToReturn;
    }

    static private String _removeNewLineCharacters(String subject) {
        return subject.replace("\r\n", " ").replace("\n", " ");
    }
}
