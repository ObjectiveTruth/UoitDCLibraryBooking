package com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel;

import android.os.Parcel;
import android.os.Parcelable;

public class TimeCell implements Parcelable {
    public TimeCellType timeCellType;
    public String hrefSource;
    public String groupNameForWhenFullyBookedRoom;
    public String timeStringOrRoomName;

    @Override
    public String toString() {
        String returnString = "";
        returnString += "timeCellType: ";
        if(notNull(timeCellType)) {returnString += timeCellType.name() + ", ";} else {returnString += "NULL, ";}

        returnString += "hrefSource: ";
        if(notNull(hrefSource)) {returnString += hrefSource + ", ";} else {returnString += "NULL, ";}

        returnString += "groupNameForWhenFullyBookedRoom: ";
        if(notNull(groupNameForWhenFullyBookedRoom))
        {returnString += groupNameForWhenFullyBookedRoom + ", ";} else {returnString += "NULL, ";}

        returnString += "timeStringOrRoomName: ";
        if(notNull(timeStringOrRoomName)) {returnString += timeStringOrRoomName;} else {returnString += "NULL";}

        return returnString;
    }

    private boolean notNull(Object object) {
        return (object != null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.timeCellType == null ? -1 : this.timeCellType.ordinal());
        dest.writeString(this.hrefSource);
        dest.writeString(this.groupNameForWhenFullyBookedRoom);
        dest.writeString(this.timeStringOrRoomName);
    }

    public TimeCell() {
    }

    protected TimeCell(Parcel in) {
        int tmpTimeCellType = in.readInt();
        this.timeCellType = tmpTimeCellType == -1 ? null : TimeCellType.values()[tmpTimeCellType];
        this.hrefSource = in.readString();
        this.groupNameForWhenFullyBookedRoom = in.readString();
        this.timeStringOrRoomName = in.readString();
    }

    public static final Parcelable.Creator<TimeCell> CREATOR = new Parcelable.Creator<TimeCell>() {
        @Override
        public TimeCell createFromParcel(Parcel source) {
            return new TimeCell(source);
        }

        @Override
        public TimeCell[] newArray(int size) {
            return new TimeCell[size];
        }
    };
}
