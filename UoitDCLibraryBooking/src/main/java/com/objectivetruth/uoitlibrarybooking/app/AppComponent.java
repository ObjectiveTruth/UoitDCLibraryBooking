package com.objectivetruth.uoitlibrarybooking.app;

import com.objectivetruth.uoitlibrarybooking.MainActivity;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(UOITLibraryBookingApp app);
    void inject(MainActivity mainActivity);
}
