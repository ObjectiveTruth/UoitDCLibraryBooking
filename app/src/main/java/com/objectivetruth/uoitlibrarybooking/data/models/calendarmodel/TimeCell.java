package com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel;

import android.os.Parcel;
import android.os.Parcelable;

public class TimeCell implements Parcelable {
    public TimeCellType timeCellType;
    public String groupNameForWhenFullyBookedRoom;
    public String timeStringOrRoomName;
    // Used when doing the interactions with the server
    public String param_next;
    public String param_get_link;
    public String param_starttime;
    public String param_room;
    public String param_eventargument;
    public String param_eventtarget;

    @Override
    public String toString() {
        String returnString = "";
        returnString += "timeCellType: ";
        if(notNull(timeCellType)) {returnString += timeCellType.name() + ", ";} else {returnString += "NULL, ";}

        returnString += "param_get_link: ";
        if(notNull(param_get_link)) {returnString += param_get_link + ", ";} else {returnString += "NULL, ";}

        returnString += "param_next: ";
        if(notNull(param_next)) {returnString += param_next + ", ";} else {returnString += "NULL, ";}

        returnString += "param_starttime: ";
        if(notNull(param_starttime)) {returnString += param_starttime + ", ";} else {returnString += "NULL, ";}

        returnString += "param_room: ";
        if(notNull(param_room)) {returnString += param_room + ", ";} else {returnString += "NULL, ";}

        returnString += "param_eventargument: ";
        if(notNull(param_eventargument)) {returnString += param_eventargument + ", ";} else {returnString += "NULL, ";}

        returnString += "param_eventtarget: ";
        if(notNull(param_eventtarget)) {returnString += param_eventtarget + ", ";} else {returnString += "NULL, ";}

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
        dest.writeString(this.param_next);
        dest.writeString(this.param_get_link);
        dest.writeString(this.param_starttime);
        dest.writeString(this.param_room);
        dest.writeString(this.param_eventargument);
        dest.writeString(this.param_eventtarget);
        dest.writeString(this.groupNameForWhenFullyBookedRoom);
        dest.writeString(this.timeStringOrRoomName);
    }

    public TimeCell() {
    }

    protected TimeCell(Parcel in) {
        int tmpTimeCellType = in.readInt();
        this.timeCellType = tmpTimeCellType == -1 ? null : TimeCellType.values()[tmpTimeCellType];
        this.param_next = in.readString();
        this.param_get_link = in.readString();
        this.param_starttime = in.readString();
        this.param_room = in.readString();
        this.param_eventargument = in.readString();
        this.param_eventtarget = in.readString();
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
