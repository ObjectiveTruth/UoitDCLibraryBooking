package com.objectivetruth.uoitlibrarybooking.userinterface.myaccount.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.analytics.Tracker;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.MyAccountDataLoginState;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserCredentials;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import javax.inject.Inject;

public class LoginFragment extends Fragment {
    private TextView errorTextView;
    private EditText usernameField;
    private EditText passwordField;
    private RadioGroup institutionRadio;
    private PublishSubject<UserCredentials> signInClickSubject;
    private MyAccountDataLoginState myAccountDataLoginState;
    private Subscription currentLoginErrorSubscription;
    @Inject Tracker googleAnalyticsTracker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((UOITLibraryBookingApp) getActivity().getApplication()).getComponent().inject(this);
    }

    public static LoginFragment newInstance(PublishSubject<UserCredentials> signInClickSubject,
                                            MyAccountDataLoginState myAccountDataLoginState) {
        LoginFragment returnFragment = new LoginFragment();
        returnFragment.signInClickSubject = signInClickSubject;
        returnFragment.myAccountDataLoginState = myAccountDataLoginState;
        return returnFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_account_login, container, false);
    }

    /**
     * Checks the username and password dittext boxes for valid entries, if not valid, does an animation hint ont he invalid
     * view using YoYo
     * @return true if both edit texts are valid, false if not valid
     **/
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
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        institutionRadio = (RadioGroup) view.findViewById(R.id.radioInstitution);
        usernameField = (EditText) view.findViewById(R.id.editTextUserNameToLogin);
        passwordField = (EditText) view.findViewById(R.id.editTextPasswordToLogin);
        errorTextView = (TextView) view.findViewById(R.id.my_account_login_error_notice);
        Button signInButton = (Button) view.findViewById(R.id.my_account_login_sign_in_button);

        if(myAccountDataLoginState.exception != null) {
            String errorMessage = myAccountDataLoginState.exception.getMessage();
            errorTextView.setText(errorMessage);
        }

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInputValid()) {
                    _clearErrorText(errorTextView);
                    signInClickSubject.onNext(new UserCredentials(
                            usernameField.getText().toString().trim(),
                            passwordField.getText().toString(),
                            _getInsitutionIdFromRadioView(institutionRadio)));
                }
            }
        });
    }

    private void _clearErrorText(TextView errorTextView) {
        if(errorTextView != null) {
            errorTextView.setText("");
        }
    }

    private String _getInsitutionIdFromRadioView(RadioGroup radioGroup) {
        switch(radioGroup.getCheckedRadioButtonId()){
            case R.id.uoit_radio :
                Timber.d("My Account Login: Uoit Selected");
                return "uoit";
            default:
                Timber.d("My Account Login: DC Selected");
                return "dc";
        }
    }

    private void _bindLoginErrorSubjectToErrorTextView(PublishSubject<String> loginErroSubject,
                                                              final TextView errorTextView) {
        currentLoginErrorSubscription =  loginErroSubject
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String errorMessageToDisplay) {
                Timber.d("Received new error message to display in Login: " + errorMessageToDisplay);
                if(errorTextView != null) {
                    errorTextView.setText(errorMessageToDisplay);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        if(currentLoginErrorSubscription != null) {currentLoginErrorSubscription.unsubscribe();}
        super.onDestroyView();
    }
}

