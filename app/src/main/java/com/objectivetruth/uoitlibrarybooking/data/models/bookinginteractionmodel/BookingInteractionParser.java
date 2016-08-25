package com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel;

import android.support.v4.util.Pair;
import com.objectivetruth.uoitlibrarybooking.data.models.common.ParseUtilities;
import com.objectivetruth.uoitlibrarybooking.statelessutilities.LeftOrRight;
import rx.Observable;
import timber.log.Timber;

import java.util.HashMap;

public class BookingInteractionParser {

    public static Observable<LeftOrRight<String, String>> parseWebpageForMessageLabel(String rawWebPage) {
        String ERROR_LABEL_ID = "ContentPlaceHolder1_LabelError";
        String SUCCESS_LABEL_ID = "ContentPlaceHolder1_LabelMessage";
        String right = null;
        String left = null;
        if(rawWebPage.contains(ERROR_LABEL_ID)) {
            String search = ParseUtilities.findStringFromStringBetweenSearchTerms(rawWebPage,
                    ERROR_LABEL_ID, "</span>");
            left = search.split(">")[1];
        }else if(rawWebPage.contains(SUCCESS_LABEL_ID)){
            String search = ParseUtilities.findStringFromStringBetweenSearchTerms(rawWebPage,
                    SUCCESS_LABEL_ID, "</span>");
            right = search.substring(search.indexOf(">") + 1);
        }else {
            left = "Page had no result information, try again";
        }
        return Observable.just(new LeftOrRight<>(left, right));
    }

    @SuppressWarnings("Duplicates")
    public static Observable<Pair<HashMap<String, String>, HashMap<String, String>>> parseJoinOrLeaveFormForSpinners(
            String webpage) {
        // Ends in a number, example ContentPlaceHolder1_RadioButtonListJoinOrCreateGroup_1
        // join spinner: ContentPlaceHolder1_RadioButtonListJoinOrCreateGroup_0 is always the create button, ignore it
        // This is NOT true for the leave spinner. 0 should be the first element
        String JOIN_SPINNER_FORM_PREFIX = "ContentPlaceHolder1_RadioButtonListJoinOrCreateGroup_";
        String LEAVE_SPINNER_FORM_PREFIX = "ContentPlaceHolder1_RadiobuttonListLeaveGroup_";

        HashMap<String, String> joinOrLeaveSpinnerForJoin = new HashMap<>();
        HashMap<String, String> joinOrLeaveSpinnerForLeave = new HashMap<>();

        Timber.d("Parsing for Join Spinner Information");
        int joinSpinnerCounter = 1;
        int indexOfJoinSpinner = webpage.indexOf(JOIN_SPINNER_FORM_PREFIX + joinSpinnerCounter);
        while(indexOfJoinSpinner > 0) {
            int endOfline = webpage.indexOf("</td>", indexOfJoinSpinner);
            String currentLineWithSpinnerValue = webpage.substring(indexOfJoinSpinner, endOfline);
            Timber.v("Parsing line: " + currentLineWithSpinnerValue);

            String spinnerValue = ParseUtilities
                    .findStringFromStringBetweenSearchTerms(currentLineWithSpinnerValue, "value=\"", "\"");
            String spinnerLabel = ParseUtilities
                    .findStringFromStringBetweenSearchTerms(currentLineWithSpinnerValue, "<b>", "</b>");
            joinOrLeaveSpinnerForJoin.put(spinnerLabel, spinnerValue);
            Timber.d("Found " + joinSpinnerCounter + " value so far. Label: " + spinnerLabel + ", Value: " + spinnerValue);
            joinSpinnerCounter++;
            indexOfJoinSpinner = webpage.indexOf(JOIN_SPINNER_FORM_PREFIX + joinSpinnerCounter);
        }
        Timber.d("Done parsing for Join Spinner Information");
        Timber.v(joinOrLeaveSpinnerForJoin.toString());

        Timber.d("Parsing for Leave Spinner Information");
        int leaveSpinnerCounter = 0;
        int indexOfLeaveSpinner = webpage.indexOf(LEAVE_SPINNER_FORM_PREFIX + leaveSpinnerCounter);
        while(indexOfLeaveSpinner > 0) {
            int endOfline = webpage.indexOf("</td>", indexOfLeaveSpinner);
            String currentLineWithSpinnerValue = webpage.substring(indexOfLeaveSpinner, endOfline);
            Timber.v("Parsing line: " + currentLineWithSpinnerValue);

            String spinnerValue = ParseUtilities
                    .findStringFromStringBetweenSearchTerms(currentLineWithSpinnerValue, "value=\"", "\"");
            String spinnerLabel = ParseUtilities
                    .findStringFromStringBetweenSearchTerms(currentLineWithSpinnerValue, "<b>", "</b>");
            joinOrLeaveSpinnerForLeave.put(spinnerLabel, spinnerValue);
            Timber.d("Found " + leaveSpinnerCounter + " value so far. Label: " + spinnerLabel + ", Value: " + spinnerValue);
            leaveSpinnerCounter++;
            indexOfLeaveSpinner = webpage.indexOf(LEAVE_SPINNER_FORM_PREFIX + leaveSpinnerCounter);
        }
        Timber.d("Done parsing for Leave Spinner Information");
        Timber.v(joinOrLeaveSpinnerForLeave.toString());

        return Observable.just(new Pair<>(joinOrLeaveSpinnerForJoin, joinOrLeaveSpinnerForLeave));
    }
}
