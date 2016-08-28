package com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class CalendarData implements Parcelable {
    public List<CalendarDay> days;
    public int computedHashCode;

    @Override
    public String toString() {
        String returnString = "computerHashCode: " + computedHashCode + ", days:";

        if(days == null) {
            returnString += " NULL";
            return returnString;
        }

        if(_notEmpty(days)) {
            int i = 0;
            for(CalendarDay day: days) {
                if(notNull(day)){
                    returnString += " (" + i + ")" + day.toString();
                }else{
                    returnString += " (" + i + ")NULL";
                }
                i++;
            }
        }else{
            returnString += " EMPTY";
        }
        return returnString;
    }

    private boolean notNull(Object object) {
        return (object != null);
    }

    private boolean _notEmpty(List<CalendarDay> calendarDays) {
        return !calendarDays.isEmpty();
    }

    public boolean isEqualTo(CalendarData calendarData) {
        return this.computedHashCode == calendarData.computedHashCode;
    }

    public boolean isNOTEqualTo(CalendarData calendarData) {
        return !isEqualTo(calendarData);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.days);
        dest.writeInt(this.computedHashCode);
    }

    public CalendarData() {
    }

    protected CalendarData(Parcel in) {
        this.days = in.createTypedArrayList(CalendarDay.CREATOR);
        this.computedHashCode = in.readInt();
    }

    public static final Parcelable.Creator<CalendarData> CREATOR = new Parcelable.Creator<CalendarData>() {
        @Override
        public CalendarData createFromParcel(Parcel source) {
            return new CalendarData(source);
        }

        @Override
        public CalendarData[] newArray(int size) {
            return new CalendarData[size];
        }
    };
}
