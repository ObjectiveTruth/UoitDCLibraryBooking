package com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel;

import com.objectivetruth.uoitlibrarybooking.data.models.common.ParseUtilities;
import com.objectivetruth.uoitlibrarybooking.statelessutilities.LeftOrRight;

public class BookingInteractionParser {

    public static LeftOrRight<String, String> parseWebpageForMessageLabel(String rawWebPage) {
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
        return new LeftOrRight<>(left, right);
    }
}
