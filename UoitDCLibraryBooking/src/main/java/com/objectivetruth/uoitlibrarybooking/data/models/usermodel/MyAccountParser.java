package com.objectivetruth.uoitlibrarybooking.data.models.usermodel;

import rx.Observable;
import timber.log.Timber;

import static com.objectivetruth.uoitlibrarybooking.data.models.common.ParseUtilities.findStringFromStringBetweenSearchTerms;

public class MyAccountParser {
    static public Observable<UserCredentials> parseRawInitialWebPageToGetStateInfo(String rawMyReservationWebPage,
                                                                                   UserCredentials userCredentials) {
        return Observable.just(_parseRawInitialWebPageToGetStateInfo(userCredentials, rawMyReservationWebPage));
    }

    static public Observable<UserData> parseRawSignedInMyReservationsWebPageForUserData(String rawMyReservationWebPage){
        return Observable.just(_parseRawSignedInMyReservationsWebPageForUserData(rawMyReservationWebPage));
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
        Timber.i("Finished parsing of the raw Webpage from my reservation sign in attempt");
        return new UserData();
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
        String ERROR_LABEL_START_TAG =
                "<span id=\"ContentPlaceHolder1_LabelError\" style=\"color:Red;font-size:11pt;z-index: 102; " +
                        "left: 2px; position: absolute; top: 74px; width: 406px; height: 34px;\">";
        String ERROR_LABEL_END_TAG = "</span>";
        String errorMessageToReturn =
                findStringFromStringBetweenSearchTerms(rawWebPage, ERROR_LABEL_START_TAG, ERROR_LABEL_END_TAG);
        if(errorMessageToReturn == null || errorMessageToReturn.isEmpty()){
            errorMessageToReturn = "Something went wrong, try again";
        }
        return errorMessageToReturn;
    }

}
