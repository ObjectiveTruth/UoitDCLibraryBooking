package com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel;

public class TimeCell {
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

}
