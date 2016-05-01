package com.objectivetruth.uoitlibrarybooking.userinterface.myaccount.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.android.gms.analytics.Tracker;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import timber.log.Timber;

import javax.inject.Inject;

public class LoginFragment extends Fragment {
    TextView errorTextView;
    CheckBox rememberMe;
    EditText usernameField;
    EditText passwordField;
    String errorMessage;
    RadioGroup institutionRadio;
    @Inject
    Tracker googleAnalyticsTracker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((UOITLibraryBookingApp) getActivity().getApplication()).getComponent().inject(this);
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.i("Creating a new View");
        return inflater.inflate(R.layout.my_account_login, container, false);
    }

    public void setErrorMessage(String errorMessage) {
        Timber.i("Setting Error TextView and ErrorText variable in fragment to " + errorMessage);
        this.errorMessage = errorMessage;
        if(errorTextView !=null){
            errorTextView.setText(errorMessage);
        }
    }

/*    *//**
     * Checks the username and password dittext boxes for valid entries, if not valid, does an animation hint ont he invalid
     * view using YoYo
     * @return true if both edit texts are valid, false if not valid
     *//*
    private boolean isInputValid(){
        if(usernameField != null && passwordField != null && institutionRadio != null){
            String usernameInput = usernameField.getText().toString();
            String passwordInput = passwordField.getText().toString();
            int institutionInput = institutionRadio.getCheckedRadioButtonId();
            boolean inputValid = true;
            if(usernameInput.trim().isEmpty()){
                YoYo.with(Techniques.Shake).duration(1000).playOn(usernameField);
                inputValid = false;
            }
            if(passwordInput.trim().isEmpty()){
                YoYo.with(Techniques.Shake).delay(100).duration(900).playOn(passwordField);
                inputValid = false;
            }
            if(institutionInput < 0){
                YoYo.with(Techniques.Shake).delay(200).duration(800).playOn(institutionRadio);
                inputValid = false;
            }

            return inputValid;
        }
        return false;

    }*/
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        institutionRadio = (RadioGroup) view.findViewById(R.id.radioInstitution);
        usernameField = (EditText) view.findViewById(R.id.editTextUserNameToLogin);
        passwordField = (EditText) view.findViewById(R.id.editTextPasswordToLogin);
        Button signInButton = (Button) view.findViewById(R.id.buttonSignIn);
/*        errorTextView = (TextView) view.findViewById(R.id.error_textview);
        errorTextView.setGravity(Gravity.CENTER);
        errorTextView.setText(errorMessage);*/
        //ImageButton passwordImageButton = (ImageButton) view.findViewById(R.id.info_password);

/*        passwordImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragMan = getChildFragmentManager();
                DiaFragGeneric frag = new DiaFragGeneric();
                frag.setArguments("Password Information", getString(R.string.credentials_instructions));
                frag.show(fragMan, MainActivity.PASSWORD_INFO_DIALOGFRAGMENT_TAG);
            }
        });*/


/*        signInButton.setOnClickListener(new View.OnClickListener() {
            public boolean isNetworkAvailable(Context ctx) {
                ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnectedOrConnecting() && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void onClick(View arg0) {
                SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor defaultPrefsEditor = defaultPreferences.edit();
                String[] loginInput = new String[3];
                errorTextView.setText("");
                loginInput[0] = usernameField.getText().toString();
                loginInput[1] = passwordField.getText().toString();


                if (isNetworkAvailable(getActivity())) {
                    if(isInputValid()){
                        String institutionId;
                        switch(institutionRadio.getCheckedRadioButtonId()){
                            case R.id.uoit_radio : institutionId = "uoit";
                                Timber.i("My Account Uoit Selected, Adding to sharedPrefs");
                                break;
                            default:                 institutionId = "dc";
                                Timber.i("My Account DC Selected, Adding to sharedPrefs");
                                break;
                        }
                        loginInput[2] = institutionId;
                        OttoBusSingleton.getInstance().post(new MyAccountLoginTaskStart(loginInput, MY_ACCOUNT_USER_INITIATED));
                        googleAnalyticsTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("MyAccount")
                                .setAction("Login Start Event - User Initiated")
                                .build());
                        MainActivity.errorMessageFromLogin = "";
                    }




                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Connectivity Issue")
                            .setMessage(R.string.networkerrordialogue)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Do nothing
                                }
                            })
                            .setIcon(R.drawable.ic_dialog_alert)
                            .show();
                }


            }

        });*/
    }

}

