package com.objectivetruth.uoitlibrarybooking.calendar.whatsnew;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.Utilities.ResourceLoadingUtilities;

class WhatsNewDialog {
    private static final String WHATS_NEW_DIALOG_TITLE = "What's New";
    private static final String WHATS_NEW_CONTENTS_FILE_NAME = "whatsnew.txt";

    public static void show(Context context){
        String disclaimerString;
        try{
            disclaimerString = ResourceLoadingUtilities.loadAssetTextAsString(context, WHATS_NEW_CONTENTS_FILE_NAME);
        }catch(Exception e) {
            return;
        }

        new AlertDialog.Builder(context)
                .setTitle(WHATS_NEW_DIALOG_TITLE)
                .setMessage(disclaimerString)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Nothing special, by default closes the dialog
                    }
                })
                .setIcon(R.drawable.ic_dialog_alert)
                .show();
    }

}
