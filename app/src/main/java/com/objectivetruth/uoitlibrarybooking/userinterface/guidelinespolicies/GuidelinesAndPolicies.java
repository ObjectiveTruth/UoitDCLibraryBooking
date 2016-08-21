package com.objectivetruth.uoitlibrarybooking.userinterface.guidelinespolicies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.*;
import com.objectivetruth.uoitlibrarybooking.BuildConfig;
import com.objectivetruth.uoitlibrarybooking.MainActivity;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.userinterface.guidelinespolicies.DebugPreferences.DebugPreferences;
import com.objectivetruth.uoitlibrarybooking.userinterface.guidelinespolicies.guidelinesandpoliciesloaded.GuidelinesAndPoliciesLoaded;
import timber.log.Timber;

public class GuidelinesAndPolicies extends Fragment{
    private static final String GUIDELINES_AND_POLICIES_TITLE = "Policies";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if(BuildConfig.DEBUG) { setHasOptionsMenu(true);}

        View view = inflater.inflate(R.layout.guidelinesandpolicies_root, container, false);
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.guidelinesandpolicies_root_content_frame, GuidelinesAndPoliciesLoaded.newInstance(), null)
                .commit();
        return view;
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if(BuildConfig.DEBUG){
            inflater.inflate(R.menu.debug_options, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.debug_options_settings) {
            Fragment currentFragment = getChildFragmentManager()
                    .findFragmentById(R.id.guidelinesandpolicies_root_content_frame);

            if(currentFragment instanceof DebugPreferences) {
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.guidelinesandpolicies_root_content_frame,
                                GuidelinesAndPoliciesLoaded.newInstance())
                        .commit();
            }else{
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.guidelinesandpolicies_root_content_frame, new DebugPreferences(), null)
                        .commit();
            }
            return true;
        }else if(id == R.id.debug_options_button){
            Timber.d("Does Nothing");
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }

    public static GuidelinesAndPolicies newInstance() {
        return new GuidelinesAndPolicies();
    }
}
