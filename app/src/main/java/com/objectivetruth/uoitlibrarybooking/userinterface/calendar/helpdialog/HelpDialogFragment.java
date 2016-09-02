package com.objectivetruth.uoitlibrarybooking.userinterface.calendar.helpdialog;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import com.objectivetruth.uoitlibrarybooking.R;
import timber.log.Timber;

public class HelpDialogFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("Help Dialog being created...");
        View rootView = inflater.inflate(R.layout.calendar_dialog_help, container, false);
        Button okButton = (Button) rootView.findViewById(R.id.help_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();

            }
        });
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setTitle("Help");
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.ActionBarIconDialogAnimation;

        Timber.i("Help Dialog Created, showing to user");
        return rootView;
    }
}

