package com.objectivetruth.uoitlibrarybooking;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
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

        SpannableString disclaimerSpan = new SpannableString(disclaimerString);
        Object span = new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER);
        disclaimerSpan.setSpan(span, 0, disclaimerString.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        new AlertDialog.Builder(context)
                .setTitle(WHATS_NEW_DIALOG_TITLE)
                .setMessage(disclaimerSpan)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Nothing special, by default closes the dialog
                    }
                })
                .setIcon(R.drawable.ic_dialog_alert)
                .show();
    }

}
