package com.objectivetruth.uoitlibrarybooking;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.BookingInteractionModel;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionScreenLoadEvent;
import com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction.BookingInteraction;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.whatsnew.WhatsNewDialog;
import com.objectivetruth.uoitlibrarybooking.userinterface.common.ActivityBase;
import rx.Observable;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import javax.inject.Inject;

import static com.objectivetruth.uoitlibrarybooking.common.constants.SHARED_PREFERENCES_KEYS.HAS_DISMISSED_WHATSNEW_DIALOG_THIS_VERSION;


public class MainActivity extends ActivityBase {
    private boolean isFirstLoadThisSession = false;
    private CompositeSubscription subscriptions = new CompositeSubscription();
	@Inject SharedPreferences mDefaultSharedPreferences;
	@Inject SharedPreferences.Editor mDefaultSharedPreferencesEditor;
    @Inject BookingInteractionModel bookingInteractionModel;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        if(savedInstanceState == null) {isFirstLoadThisSession = true; Timber.i("First time opening app this session");}

        ((UOITLibraryBookingApp) getApplication()).getComponent().inject(this);

        setContentView(R.layout.app_root);
        initializeAllMainFragmentsAndPreloadToView();
        setupToolbar(R.id.toolbar);
        setupDrawer(R.id.drawer_layout);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if(isFirstLoadThisSession) {
            _goToScreenByMenuID(R.id.drawer_menu_item_calendar);
        }else if(areOnlyDrawerRelatedScreensShowing()){ // A hack for the time being, use git blame to find out more
            _goToScreenByMenuID(getLastMenuItemIDRequested());
        }

        if(_hasNOTDismissedWhatsNewDialogThisVersion()) {
        	WhatsNewDialog
					.show(this)
					.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                                    .putBoolean(HAS_DISMISSED_WHATSNEW_DIALOG_THIS_VERSION, true)
                                    .apply();
                        }
		});}

    }

    private boolean _hasNOTDismissedWhatsNewDialogThisVersion() {
        return !PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getBoolean(HAS_DISMISSED_WHATSNEW_DIALOG_THIS_VERSION, false);
	}

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
        return getActionBarDrawerToggle().onOptionsItemSelected(item);
	}

	private void _goToScreenByMenuID(int menuItemResourceID) {
		MenuItem initialMenuItem = getDrawerView().getMenu().findItem(menuItemResourceID);
		selectDrawerItem(initialMenuItem);
	}

    public boolean isNetworkAvailable(Context ctx){
	    ConnectivityManager cm = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()&& cm.getActiveNetworkInfo().isAvailable()&& cm.getActiveNetworkInfo().isConnected()) 
	    {
	        return true;
	    }
	    else
	    {
	        return false;
	    }
    }

    @Override
    protected void onStart() {
        super.onStart();
        _bindBookingInteractionEventToLoadingBookingInteractionScreen(
                bookingInteractionModel.getBookingInteractionScreenLoadEventObservable());
    }

    @Override
    protected void onStop() {
        if(subscriptions != null) {
            subscriptions.unsubscribe();
        }
        super.onStop();
    }

    private void _bindBookingInteractionEventToLoadingBookingInteractionScreen(
			Observable<BookingInteractionScreenLoadEvent> bookingInteractionEventObservable) {
	    subscriptions
                .add(bookingInteractionEventObservable
                .subscribe(new Action1<BookingInteractionScreenLoadEvent>() {
                    @Override
                    public void call(BookingInteractionScreenLoadEvent bookingInteractionScreenLoadEvent) {
                        addHidingOfAllCurrentFragmentsToTransaction(getSupportFragmentManager().beginTransaction())
                                .add(R.id.mainactivity_content_frame, BookingInteraction.newInstance())
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .addToBackStack(null)
                                .commit();
                    }
        }));
    }
}
