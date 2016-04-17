package com.objectivetruth.uoitlibrarybooking.app;

import com.objectivetruth.uoitlibrarybooking.ActivityRoomInteraction;
import com.objectivetruth.uoitlibrarybooking.Calendar_Generic_Page_Fragment;
import com.objectivetruth.uoitlibrarybooking.DiaFragMyAccount;
import com.objectivetruth.uoitlibrarybooking.MainActivity;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(UOITLibraryBookingApp app);
    void inject(MainActivity mainActivity);
    void inject(Calendar_Generic_Page_Fragment.RoomFragmentDialog roomFragmentDialog);
    void inject(DiaFragMyAccount diaFragMyAccount);
    void inject(DiaFragMyAccount.LoginFragment loginFragment);
    void inject(ActivityRoomInteraction activityRoomInteraction);
}
