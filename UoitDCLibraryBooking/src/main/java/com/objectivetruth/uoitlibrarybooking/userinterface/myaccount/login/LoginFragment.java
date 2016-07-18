package com.objectivetruth.uoitlibrarybooking.userinterface.myaccount.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.analytics.Tracker;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.UserModel;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.MyAccountDataLoginState;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.MyAccountDataLoginStateType;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserCredentials;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

import javax.inject.Inject;

public class LoginFragment extends Fragment {
    private TextView errorTextView;
    private EditText usernameField;
    private EditText passwordField;
    private RadioGroup institutionRadio;
    private MyAccountDataLoginState myAccountDataLoginState;
    private Button signInButton;
    private Subscription myAccountDataLoginStateERRORObservableSubscription;
    @Inject Tracker googleAnalyticsTracker;
    @Inject UserModel userModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((UOITLibraryBookingApp) getActivity().getApplication()).getComponent().inject(this);
    }

    public static LoginFragment newInstance(MyAccountDataLoginState myAccountDataLoginState) {
        LoginFragment returnFragment = new LoginFragment();
        returnFragment.myAccountDataLoginState = myAccountDataLoginState;
        return returnFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_account_login, container, false);

        institutionRadio = (RadioGroup) view.findViewById(R.id.radioInstitution);
        usernameField = (EditText) view.findViewById(R.id.editTextUserNameToLogin);
        passwordField = (EditText) view.findViewById(R.id.editTextPasswordToLogin);
        errorTextView = (TextView) view.findViewById(R.id.my_account_login_error_notice);
        signInButton = (Button) view.findViewById(R.id.my_account_login_sign_in_button);

        return view;
    }

    @Override
    public void onStart() {
        Timber.d(this.getClass().getSimpleName() + " onStart");
        _setupViewBindings(signInButton, errorTextView, myAccountDataLoginState, usernameField,
                passwordField, institutionRadio, userModel.getLoginStateObservable());
        super.onStart();
    }
    @Override
    public void onHiddenChanged(boolean isNowHidden) {
        if(isNowHidden) {
            Timber.d(this.getClass().getSimpleName() + " isNowHidden");
            _teardownViewBindings(signInButton);
        }else {
            Timber.d(this.getClass().getSimpleName() + " isNowVisible");
            _setupViewBindings(signInButton, errorTextView, myAccountDataLoginState, usernameField,
                    passwordField, institutionRadio, userModel.getLoginStateObservable());
        }
        super.onHiddenChanged(isNowHidden);
    }

    @Override
    public void onStop() {
        Timber.d(this.getClass().getSimpleName() + " onStop");
        _teardownViewBindings(signInButton);
        super.onStop();
    }

    /**
     * Checks the username and password edit boxes for valid entries, if not valid, does an animation hint ont he invalid
     * view using YoYo
     * @return true if both edit texts are valid, false if not valid
     **/
    private boolean _isInputValid(EditText usernameField, EditText passwordField, RadioGroup institutionRadio){
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

    private void _teardownViewBindings(Button signInButton) {
        if(signInButton == null) {return;} //quit early if doesn't exist

        signInButton.setOnClickListener(null);
        if(myAccountDataLoginStateERRORObservableSubscription != null &&
                !myAccountDataLoginStateERRORObservableSubscription.isUnsubscribed()) {
            myAccountDataLoginStateERRORObservableSubscription.unsubscribe();
        }
    }

    private void _setupViewBindings(Button signinButton, final TextView errorTextView,
                                    MyAccountDataLoginState myAccountDataLoginState,
                                    final EditText usernameField, final EditText passwordField,
                                    final RadioGroup institutionRadio,
                                    Observable<MyAccountDataLoginState> myAccountDataLoginStateObservable) {
        if(myAccountDataLoginState.exception != null && errorTextView != null) {
            String errorMessage = myAccountDataLoginState.exception.getMessage();
            errorTextView.setText(errorMessage);
        }

        if(signinButton == null || errorTextView == null || usernameField == null ||
                passwordField == null || institutionRadio == null) {
            return; //quit early if we can't verify the state
        }

        if(myAccountDataLoginStateERRORObservableSubscription == null ||
                myAccountDataLoginStateERRORObservableSubscription.isUnsubscribed()) {

            myAccountDataLoginStateERRORObservableSubscription = myAccountDataLoginStateObservable
                    .filter(new Func1<MyAccountDataLoginState, Boolean>() {
                        @Override
                        public Boolean call(MyAccountDataLoginState myAccountDataLoginState) {
                            return (myAccountDataLoginState.type == MyAccountDataLoginStateType.ERROR) ||
                            (myAccountDataLoginState.type == MyAccountDataLoginStateType.SIGNED_OUT);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<MyAccountDataLoginState>() {
                        @Override
                        public void call(MyAccountDataLoginState myAccountDataLoginState) {
                            if(myAccountDataLoginState.exception != null) {
                                errorTextView.setText(myAccountDataLoginState.exception.getMessage());
                            }
                            passwordField.setText("");
                        }
                    });
        }

        signInButton.setOnClickListener(null); //Ensures the function is idempotent

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_isInputValid(usernameField, passwordField, institutionRadio)) {
                    _clearErrorText(errorTextView);
                    userModel.getSigninActivatePublishSubject().onNext(new UserCredentials(
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
}

