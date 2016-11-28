package com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction.common;

import android.support.v4.app.Fragment;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCell;
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

    /**
     * Takes the timeCell given and searches for he image in the resources directory for it. If its not found it will
     * return the unknown room picture resource ID
     * @param timeCell
     * @return int resourceID
     */
    protected int getResourceIDForRoomOrDefault(TimeCell timeCell) {
        final String DRAWABLE_RESOURCE_TYPE = "drawable";
        int libraryRoomResourceID = getResources().getIdentifier(timeCell.param_room.toLowerCase(),
                DRAWABLE_RESOURCE_TYPE, getActivity().getPackageName());
        libraryRoomResourceID = libraryRoomResourceID <= 0 ? R.drawable.unknown_room : libraryRoomResourceID;
        return libraryRoomResourceID;
    }
}
