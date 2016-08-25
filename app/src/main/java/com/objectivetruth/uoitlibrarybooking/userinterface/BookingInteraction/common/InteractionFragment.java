package com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction.common;

import android.support.v4.app.Fragment;
import timber.log.Timber;

public abstract class InteractionFragment extends Fragment {
    protected abstract void setupViewBindings();
    protected abstract void teardownViewBindings();

    @Override
    public void onStart() {
        Timber.v(getClass().getSimpleName() + " onStart");
        setupViewBindings();
        super.onStart();
    }

    @Override
    public void onStop() {
        Timber.v(getClass().getSimpleName() + " onStop");
        teardownViewBindings();
        super.onStop();
    }

    protected void popFragmentBackstack() {
        Timber.d("Popping backstack");
        getActivity().getSupportFragmentManager().popBackStack();
    }
}
