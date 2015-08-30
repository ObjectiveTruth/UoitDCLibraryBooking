package com.objectivetruth.uoitlibrarybooking;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by ObjectiveTruth on 8/22/2014.
 */
public class DiaFragGeneric extends DialogFragment{
    String titleText;
    String descriptionText;

    /**
     * Creates a generic Dialog Fragment in the flavour of this app
     * @param titleText large blue text at the top of the dialog
     * @param descriptionText string to write in the body of the dialog
     */
    public void setArguments(String titleText, String descriptionText){
        this.titleText = titleText;
        this.descriptionText = descriptionText;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.diafrag_generic, container, false);
        Button okButton = (Button) rootView.findViewById(R.id.ok_button_generic_diafrag);
        TextView descriptionTV = (TextView) rootView.findViewById(R.id.description_generic_diafrag);
        TextView titleTV = (TextView) rootView.findViewById(R.id.title_generic_diafrag);
        descriptionTV.setText(descriptionText);
        titleTV.setText(titleText);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();

            }
        });
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setTitle(titleText);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DiafragGenericAnimation;
        return rootView;
    }
}
