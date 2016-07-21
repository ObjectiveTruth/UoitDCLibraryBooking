package com.objectivetruth.uoitlibrarybooking.userinterface.guidelinespolicies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.objectivetruth.uoitlibrarybooking.MainActivity;
import com.objectivetruth.uoitlibrarybooking.R;
import timber.log.Timber;

public class GuidelinesAndPolicies extends Fragment{
    private static final String GUIDELINES_AND_POLICIES_TITLE = "Policies";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.guidelines_policies, container, false);
    }

    @Override
    public void onHiddenChanged(boolean isNowHidden) {
        if(isNowHidden) {
            Timber.d(getClass().getSimpleName() + " isNowHidden");
        }else {
            Timber.d(getClass().getSimpleName() + " isNowVisible");
            _setTitle(GUIDELINES_AND_POLICIES_TITLE);
        }
        super.onHiddenChanged(isNowHidden);
    }

    private void _setTitle(String title) {
        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    public static GuidelinesAndPolicies newInstance() {
        return new GuidelinesAndPolicies();
    }
}
