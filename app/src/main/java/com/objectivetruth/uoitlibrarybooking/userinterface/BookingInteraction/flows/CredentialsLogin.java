package com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction.flows;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionEvent;
import com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction.common.InteractionFragment;

public class CredentialsLogin extends InteractionFragment{
    private BookingInteractionEvent bookingInteractionEvent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_account_login, container, false);
        return view;
    }

    @Override
    protected void setupViewBindings() {

    }

    @Override
    protected void teardownViewBindings() {

    }

    public static CredentialsLogin newInstance(BookingInteractionEvent bookingInteractionEvent) {
        CredentialsLogin fragment = new CredentialsLogin();
        fragment.bookingInteractionEvent = bookingInteractionEvent;
        return fragment;
    }
}
