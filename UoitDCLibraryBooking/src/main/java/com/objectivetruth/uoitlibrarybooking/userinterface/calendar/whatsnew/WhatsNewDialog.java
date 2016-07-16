package com.objectivetruth.uoitlibrarybooking.userinterface.calendar.whatsnew;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.statelessutilities.ResourceLoadingUtilities;

public class WhatsNewDialog {
    private static final String WHATS_NEW_DIALOG_TITLE = "What's New";
    private static final String WHATS_NEW_CONTENTS_FILE_NAME = "whatsnew.txt";
    private static final String WHATS_NEW_MINOR_CONTENTS_FILE_NAME = "whatsnew_minor.txt";
    private static final String WHATS_NEW_THIS_VERSION_CONTENTS_FILE_NAME = "whatsnew_thisversion.txt";

    public static void show(Context context){
        String whatsNewString;
        try{
            whatsNewString = ResourceLoadingUtilities.loadAssetTextAsString(context, WHATS_NEW_CONTENTS_FILE_NAME) +
                    "\n\n" +
                    ResourceLoadingUtilities.loadAssetTextAsString(context, WHATS_NEW_THIS_VERSION_CONTENTS_FILE_NAME) +
                    "\n\n" +
                    ResourceLoadingUtilities.loadAssetTextAsString(context, WHATS_NEW_MINOR_CONTENTS_FILE_NAME);
        }catch(Exception e) {
            whatsNewString = "<Error: Couldn't load whatsnew.txt asset>";
        }

        new AlertDialog.Builder(context)
                .setTitle(WHATS_NEW_DIALOG_TITLE)
                .setMessage(whatsNewString)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Nothing special, by default closes the dialog
                    }
                })
                .setIcon(R.drawable.ic_dialog_alert)
                .show();
    }

}
