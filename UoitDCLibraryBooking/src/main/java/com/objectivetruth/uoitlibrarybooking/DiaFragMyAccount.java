package com.objectivetruth.uoitlibrarybooking;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.*;
import android.widget.*;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nineoldandroids.animation.Animator;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.squareup.otto.Subscribe;
import timber.log.Timber;

import javax.inject.Inject;

import static com.objectivetruth.uoitlibrarybooking.MainActivity.SHARED_PREF_KEY_PASSWORD;
import static com.objectivetruth.uoitlibrarybooking.MainActivity.SHARED_PREF_KEY_USERNAME;
import static com.objectivetruth.uoitlibrarybooking.constants.SHARED_PREFERENCES_KEYS.SHARED_PREF_INSTITUTION;
import static com.objectivetruth.uoitlibrarybooking.constants.SHARED_PREFERENCES_KEYS.SHARED_PREF_KEY_BOOKINGS_LEFT;


public class DiaFragMyAccount extends DialogFragment {
    public static final String MY_ACCOUNT_LOGIN_FRAGMENT_TAG = "my_account_login_fragment";
    private static final String MY_ACCOUNT_PROGRESS_FRAGMENT_TAG = "my_account_progress_fragment";
    private static final int MY_ACCOUNT_AUTO_REFRESH = 0;
    public static final int MY_ACCOUNT_USER_INITIATED = 1;
    private static final String MY_ACCOUNT_PAGERADAPTER_TAG = "my_account_pageradapter_tag";
    private View smallProgressBar = null;
    private TextView bookingsLeftTextView;
    private TextView loggedInAsTextView;
    private ImageButton logoutButton;
    private long fragmentOpenTime = 0L;
    ListView listView;
    String roomNameString;
    int pageNumberInt;
    String diagTitle;
    String linkString;
    int shareRow;
    int shareColumn;
    @Inject Tracker googleAnalyticsTracker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((UOITLibraryBookingApp) getActivity().getApplication()).getComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        OttoBusSingleton.getInstance().register(this);
        fragmentOpenTime = System.currentTimeMillis();
        View rootView = inflater.inflate(R.layout.diafrag_myaccount, container, false);
        bookingsLeftTextView = (TextView) rootView.findViewById(R.id.bookings_left);
        loggedInAsTextView = (TextView) rootView.findViewById(R.id.logged_in_as);
        logoutButton = (ImageButton) rootView.findViewById(R.id.my_account_logout);
        smallProgressBar = rootView.findViewById(R.id.my_account_small_progressBar);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor shareEditor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                shareEditor.remove(SHARED_PREF_KEY_USERNAME);
                shareEditor.remove(SHARED_PREF_KEY_PASSWORD);
                shareEditor.remove(SHARED_PREF_KEY_BOOKINGS_LEFT);
                shareEditor.commit();
                googleAnalyticsTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("MyAccount")
                        .setAction("Logout Event")
                        .build());
                OttoBusSingleton.getInstance().post(new MyAccountLogoutEvent());
            }
        });
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setTitle(R.string.my_account_frag_title);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.ActionBarIconDialogAnimation;


        SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String username = defaultPreferences.getString(SHARED_PREF_KEY_USERNAME, null);
        String password = defaultPreferences.getString(SHARED_PREF_KEY_PASSWORD, null);
        String institution = defaultPreferences.getString(SHARED_PREF_INSTITUTION, null);
        //There's a previously saved username/password combo
        if(MainActivity.mLoginAsyncTask == null && username !=null && password != null && institution != null){

            Timber.i("LoginAsynkTask is not running and there is a password/username combo in sharedPrefs, sending MyAccountLoginTaskStart event");
            OttoBusSingleton.getInstance().post(new MyAccountLoginTaskStart(new String[]{username, password, institution}, MY_ACCOUNT_AUTO_REFRESH));
            showAccountInfoFragment();
            logoutButton.setVisibility(View.VISIBLE);

        }
        //No username or password present, so show the login dialog
        else if(MainActivity.mLoginAsyncTask == null && (username == null || password == null || institution == null)){
            Timber.i("LoginAsynkTask is not running and there is NO username/password combo in SharedPrefs, showing login fragment");
            FragmentManager fragmentManager = getChildFragmentManager();
            LoginFragment mLoginFragment = (LoginFragment) fragmentManager.findFragmentByTag(MY_ACCOUNT_LOGIN_FRAGMENT_TAG);
            if(mLoginFragment == null){
                Timber.i("LoginFragment is null, .newInstance() and starting transaction");
                mLoginFragment = LoginFragment.newInstance();
            }
            else{
                Timber.i("LoginFragment is NOT null, beginning transaction");
            }
            fragmentManager.beginTransaction()
                    .replace(R.id.my_account_movement_frame, mLoginFragment, MY_ACCOUNT_LOGIN_FRAGMENT_TAG)
                    .commit();
            logoutButton.setVisibility(View.GONE);
        }
        //LoginAsynkTask is running, show progress bar
        else{
            if(MainActivity.mLoginAsyncTask.options == MY_ACCOUNT_AUTO_REFRESH){
                Timber.i("LoginAsynkTask is running, but it was an auto_refresh, showing AccountInfoFrag and setting the progress bar");
                logoutButton.setVisibility(View.VISIBLE);
                if(smallProgressBar != null){
                    smallProgressBar.setVisibility(View.VISIBLE);
                }
                showAccountInfoFragment();

            }
            else{
                logoutButton.setVisibility(View.GONE);
                Timber.i("LoginAsynkTask is running, show progressbar");
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.my_account_movement_frame, ProgressSpinnerFragment.newInstance(), MY_ACCOUNT_PROGRESS_FRAGMENT_TAG)
                        .commit();
            }

        }

        bookingsLeftTextView.setText(getBookingsLeftSpan());
        loggedInAsTextView.setText(getLoggedInAsSpan());

        return rootView;
    }

    @Subscribe
    public void LogoutTask(MyAccountLogoutEvent event){
        if(MainActivity.mLoginAsyncTask != null){
            MainActivity.mLoginAsyncTask.cancel(true);
        }
        if(logoutButton != null){
            logoutButton.setEnabled(false);
            YoYo.with(Techniques.FadeOut).duration(700).withListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    logoutButton.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).playOn(logoutButton);

        }
        if(bookingsLeftTextView != null){
            YoYo.with(Techniques.FadeOut).duration(700).withListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    bookingsLeftTextView.setText(getBookingsLeftSpan());
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).playOn(bookingsLeftTextView);

        }
        if(loggedInAsTextView != null){
            YoYo.with(Techniques.FadeOut).duration(700).withListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    loggedInAsTextView.setText(getLoggedInAsSpan());
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).playOn(loggedInAsTextView);
        }
        FragmentManager fragmentManager = getChildFragmentManager();
        LoginFragment mLoginFragment = (LoginFragment) fragmentManager.findFragmentByTag(MY_ACCOUNT_LOGIN_FRAGMENT_TAG);
        if(mLoginFragment == null){
            Timber.i("LoginFragment is null, .newInstance() and starting transaction");
            mLoginFragment = LoginFragment.newInstance();
        }
        else{
            Timber.i("LoginFragment is NOT null, beginning transaction");
        }
        fragmentManager.beginTransaction()
                .replace(R.id.my_account_movement_frame, mLoginFragment, MY_ACCOUNT_LOGIN_FRAGMENT_TAG)
                .commit();


    }

    @Subscribe
    public void LoginTaskStart(MyAccountLoginTaskStart event){
        if(logoutButton != null){
            logoutButton.setVisibility(View.GONE);
        }
        Timber.i("Received Task start, switching login dialog to progress bar");
        if(event.options == MY_ACCOUNT_USER_INITIATED){
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.my_account_movement_frame, ProgressSpinnerFragment.newInstance(), MY_ACCOUNT_PROGRESS_FRAGMENT_TAG)
                    .commit();
        }
        else if(event.options == MY_ACCOUNT_AUTO_REFRESH){
            if(smallProgressBar != null){
                smallProgressBar.setVisibility(View.VISIBLE);
            }
        }
        else{
            Timber.e(new IllegalStateException(), "LoginTaskStart was caught from the OttoBus but it didn't contain valid option parameters");
        }

    }
    @Subscribe
    public void LoginResults(MyAccountLoginResultEvent event) {
        Timber.i("Received MyAccountLoginResultEvent");
        //Means there's an error and error message attached
        if(!event.errorMessage.isEmpty()){
            Timber.i("Login Failed with error " + event.errorMessage);
            if(event.errorMessage.startsWith("Er24")){
                if(bookingsLeftTextView != null){
                    bookingsLeftTextView.setText(getBookingsLeftSpan());
                }
                if(loggedInAsTextView != null){
                    loggedInAsTextView.setText(getLoggedInAsSpan());
                }
                showAccountInfoFragment();
                if(logoutButton != null ){
                    logoutButton.setVisibility(View.VISIBLE);
                }
            }
            else{
                FragmentManager fragmentManager = getChildFragmentManager();
                LoginFragment mLoginFragment = (LoginFragment) fragmentManager.findFragmentByTag(MY_ACCOUNT_LOGIN_FRAGMENT_TAG);
                if(mLoginFragment == null){
                    Timber.i("LoginFragment is null, .newInstance() and starting transaction");
                    mLoginFragment = LoginFragment.newInstance();
                }
                else{
                    Timber.i("LoginFragment is NOT null, beginning transaction");
                }
                mLoginFragment.setErrorMessage(event.errorMessage);
                fragmentManager.beginTransaction()
                        .replace(R.id.my_account_movement_frame, mLoginFragment, MY_ACCOUNT_LOGIN_FRAGMENT_TAG)
                        .commit();
                if(logoutButton != null){
                    logoutButton.setVisibility(View.GONE);
                }
            }

        }
        //There are valid results.. hopefully
        else if(event.result != null){
            Timber.i("Login Success");
            if(bookingsLeftTextView != null){
                if(bookingsLeftTextView.getText().toString().isEmpty()){
                    YoYo.with(Techniques.FadeIn).duration(1000).playOn(bookingsLeftTextView);
                }
                bookingsLeftTextView.setText(getBookingsLeftSpan());
            }
            if(loggedInAsTextView != null){
                if(loggedInAsTextView.getText().toString().isEmpty()){
                    YoYo.with(Techniques.FadeIn).duration(1000).playOn(loggedInAsTextView );
                }
                loggedInAsTextView.setText(getLoggedInAsSpan());
            }
            showAccountInfoFragment();
            if(logoutButton != null ){
                if(logoutButton.getVisibility() == View.GONE){
                    YoYo.with(Techniques.FadeIn).duration(1000).playOn(logoutButton);
                }
                logoutButton.setVisibility(View.VISIBLE);
            }


        }
        //WTF??
        else{
            Timber.e(new IllegalStateException("The results received from MyAccountLogin was contained neither an error message or a non null ArrayList<String> result, WTF?"),
                    "The results received from MyAccountLogin was contained neither an error message or a non null ArrayList<String> result, WTF?"
            );
        }
        if(smallProgressBar != null){
            smallProgressBar.setVisibility(View.GONE);

        }
    }

    /**
     * Replaces the my_account_movement_frame with the AccountInfoFragment which loads its info from the database
     */
    private void showAccountInfoFragment(){
        FragmentManager fragman = getChildFragmentManager();
        ViewPagerFragment mViewPagerFragment = (ViewPagerFragment) fragman.findFragmentByTag(MY_ACCOUNT_PAGERADAPTER_TAG);
        if(mViewPagerFragment ==null ){
            mViewPagerFragment = ViewPagerFragment.newInstance(new CustomViewPagerAdapter(fragman));
            fragman.beginTransaction().
                    replace(R.id.my_account_movement_frame, mViewPagerFragment, MY_ACCOUNT_PAGERADAPTER_TAG)
                    .commit();
        }
        else{
             //Do nothing Each Tab is registered to Auto to receive an update success callback

        }


    }

    public static class ViewPagerFragment extends Fragment{
        ViewPager mViewPager;
        CustomViewPagerAdapter mViewPagerAdapter;
        Tracker t;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.mybookings_viewpager, null);
            mViewPager = (ViewPager) v.findViewById(R.id.viewpager);
            mViewPager.setAdapter(mViewPagerAdapter);
            mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i2) {

                }

                @Override
                public void onPageSelected(int i) {
                    Timber.i("AccountInfoFragment page " + i + " selected");
                    t.send(new HitBuilders.EventBuilder()
                            .setCategory("MyAccount")
                            .setAction("AccountInfoFragment Page Changed to")
                            .setLabel(String.valueOf(i))
                            .build());
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });
            //return super.onCreateView(inflater, container, savedInstanceState);
            return v;
        }

        public static ViewPagerFragment newInstance(CustomViewPagerAdapter adapter){
            ViewPagerFragment fragment = new ViewPagerFragment();
            fragment.mViewPagerAdapter = adapter;
            return fragment;
        }

    }

    public class CustomViewPagerAdapter extends FragmentStatePagerAdapter {
        public CustomViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:  return "Confirmed";
                case 1:  return "Pending";
                default:  return "Past";
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

/*        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }*/

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:  return new Tab_Complete();
                case 1:  return new Tab_Incomplete();
                default:  return new Tab_Past();

            }
        }


    }

    public static class ProgressSpinnerFragment extends Fragment{
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.progress_spinner, container, false);

            return rootView;

        }
        public static ProgressSpinnerFragment newInstance() {
            ProgressSpinnerFragment fragment = new ProgressSpinnerFragment();

            return fragment;

        }
    }

    public static class LoginFragment extends Fragment {

        private final String TAG = "LoginFragment";
        TextView errorTextView;
        CheckBox rememberMe;
        EditText usernameField;
        EditText passwordField;
        String errorMessage;
        RadioGroup institutionRadio;
        @Inject Tracker googleAnalyticsTracker;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            ((UOITLibraryBookingApp) getActivity().getApplication()).getComponent().inject(this);
        }

        public static LoginFragment newInstance() {
            LoginFragment fragment = new LoginFragment();
            fragment.errorMessage = MainActivity.errorMessageFromLogin;
            return fragment;

        }

        public void setErrorMessage(String errorMessage) {
            Timber.i("Setting Error TextView and ErrorText variable in fragment to " + errorMessage);
            this.errorMessage = errorMessage;
            if(errorTextView !=null){
                errorTextView.setText(errorMessage);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View v = inflater.inflate(R.layout.my_bookings_login, container, false);

            return v;
        }
        /**
         * Checks the username and password dittext boxes for valid entries, if not valid, does an animation hint ont he invalid
         * view using YoYo
         * @return true if both edit texts are valid, false if not valid
         */
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


            SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor defaultPrefsEditor = defaultPreferences.edit();

            institutionRadio = (RadioGroup) view.findViewById(R.id.radioInstitution);
            usernameField = (EditText) view.findViewById(R.id.editTextUserNameToLogin);
            passwordField = (EditText) view.findViewById(R.id.editTextPasswordToLogin);
            Button signInButton = (Button) view.findViewById(R.id.buttonSignIn);
            errorTextView = (TextView) view.findViewById(R.id.error_textview);
            errorTextView.setGravity(Gravity.CENTER);
            errorTextView.setText(errorMessage);
            ImageButton passwordImageButton = (ImageButton) view.findViewById(R.id.info_password);

            passwordImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fragMan = getChildFragmentManager();
                    DiaFragGeneric frag = new DiaFragGeneric();
                    frag.setArguments("Password Information", getString(R.string.credentials_instructions));
                    frag.show(fragMan, MainActivity.PASSWORD_INFO_DIALOGFRAGMENT_TAG);
                }
            });


            signInButton.setOnClickListener(new View.OnClickListener() {
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

            });
        }

    }



    /**
     * Formats a string to be used in a textView
     * @return formatted SpannableString from my_account_logged_in_as_word + student number if applicable or (not logged in) if not
     */
    private SpannableString getLoggedInAsSpan(){
        Resources resources = getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String username = sharedPreferences.getString(SHARED_PREF_KEY_USERNAME, null);

        String loggedInAsWord= resources.getString(R.string.my_account_logged_in_as_word);
        //Number 3 means "bookings left: ??"  that string would have 3 from the end, to keep consistency
        int lengthOfWords = loggedInAsWord.length();
        SpannableString loggedInAsFullString;

        //No username thus not logged in
        if(username == null){
            loggedInAsFullString = new SpannableString("");


        /*
            loggedInAsFullString = new SpannableString(loggedInAsWord + "(not logged in)");
            Object span = new ForegroundColorSpan(resources.getColor(R.color.booked_red));
            loggedInAsFullString.setSpan(span, lengthOfWords, loggedInAsFullString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            span = new RelativeSizeSpan(0.7f);
            loggedInAsFullString.setSpan(span, lengthOfWords, loggedInAsFullString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);*/
        }
        else{
            loggedInAsFullString = new SpannableString(loggedInAsWord + username);
            Object span = new ForegroundColorSpan(resources.getColor(R.color.blue_font));
            loggedInAsFullString.setSpan(span, lengthOfWords, lengthOfWords + username.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            span = new RelativeSizeSpan(1.3f);
            loggedInAsFullString.setSpan(span, lengthOfWords, lengthOfWords + username.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return loggedInAsFullString;
    }

    private SpannableString getBookingsLeftSpan(){
        Resources resources = getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int bookingsLeft = sharedPreferences.getInt(SHARED_PREF_KEY_BOOKINGS_LEFT, -1);
        //No saved bookings left info
        String bookingsLeftWord = resources.getString(R.string.my_account_bookings_left_word);
        //Number 3 means "bookings left: ??"  that string would have 3 from the end, to keep consistency
        int lengthOfWords = bookingsLeftWord.length();
        SpannableString bookingsLeftFullString;
        if(bookingsLeft == -1){
            bookingsLeftFullString = new SpannableString("");
            //bookingsLeftFullString = new SpannableString(bookingsLeftWord + "??");
        }
        else{
            bookingsLeftFullString = new SpannableString(bookingsLeftWord + bookingsLeft);
            Object span = new ForegroundColorSpan(resources.getColor(R.color.blue_font));
            bookingsLeftFullString.setSpan(span, lengthOfWords, bookingsLeftFullString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            span = new RelativeSizeSpan(1.3f);
            bookingsLeftFullString.setSpan(span, lengthOfWords, bookingsLeftFullString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return bookingsLeftFullString;

    }

    @Override
    public void onStop() {
        long myAccountSessionDuration = fragmentOpenTime - System.currentTimeMillis();
        googleAnalyticsTracker.send(new HitBuilders.EventBuilder()
                .setCategory("MyAccount")
                .setAction("MyAccount Open For")
                .setValue(myAccountSessionDuration)
                .build());
        super.onStop();
    }

    @Override
    public void onDestroy() {
        OttoBusSingleton.getInstance().unregister(this);
        Timber.i("Destroying My Account Dialog Fragment");
        super.onDestroy();
    }
}
