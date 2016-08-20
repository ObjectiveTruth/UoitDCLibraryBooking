package com.objectivetruth.uoitlibrarybooking.app;

import com.objectivetruth.uoitlibrarybooking.ActivityRoomInteraction;
import com.objectivetruth.uoitlibrarybooking.MainActivity;
import com.objectivetruth.uoitlibrarybooking.data.DataModule;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionWebService;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarWebService;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserWebService;
import com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction.Book;
import com.objectivetruth.uoitlibrarybooking.userinterface.BookingInteraction.BookingInteraction;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.Calendar;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.grid.Grid;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.grid.common.GridAdapter;
import com.objectivetruth.uoitlibrarybooking.userinterface.myaccount.MyAccount;
import com.objectivetruth.uoitlibrarybooking.userinterface.myaccount.login.LoginFragment;
import com.objectivetruth.uoitlibrarybooking.userinterface.myaccount.myaccountloaded.MyAccountLoaded;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AppModule.class, DataModule.class})
public interface AppComponent {
    void inject(UOITLibraryBookingApp app);
    void inject(MainActivity mainActivity);
    void inject(ActivityRoomInteraction activityRoomInteraction);
    void inject(Calendar calendar);
    void inject(MyAccount myAccount);
    void inject(LoginFragment loginFragment);
    void inject(UserWebService userWebService);
    void inject(BookingInteractionWebService bookingInteractionWebService);
    void inject(BookingInteraction bookingInteraction);
    void inject(CalendarWebService calendarWebService);
    void inject(MyAccountLoaded myAccountLoaded);
    void inject(GridAdapter gridAdapter);
    void inject(Book book);
    void inject(Grid grid);
}
