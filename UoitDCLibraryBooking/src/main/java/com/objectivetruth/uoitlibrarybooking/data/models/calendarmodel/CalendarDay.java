package com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class CalendarDay implements Parcelable {
    public String extDayOfMonthNumber;
    public String extMonthWord;
    public String extEventArgument;
    public String extEventTarget;
    public String extViewStateMain;
    public String extEventValidation;
    public String extViewStateGenerator;
    public int rowCountIncludingRowHeadersColumn = -1;
    public int columnCountIncludingRowHeadersColumn = -1;
    public ArrayList<TimeCell> timeCells;


    @Override
    public String toString() {
        String returnString = "";
        returnString += "extDayOfMonthNumber(example: 25): ";
        if(notNull(extDayOfMonthNumber)) {returnString += extDayOfMonthNumber + ", ";} else {returnString += "NULL, ";}

        returnString += "extMonthWord(example: April): ";
        if(notNull(extMonthWord)) {returnString += extMonthWord + ", ";} else {returnString += "NULL, ";}

        returnString += "extEventArgument(example: 5959): ";
        if(notNull(extEventArgument)) {returnString += extEventArgument + ", ";} else {returnString += "NULL, ";}

        returnString += "extEventTarget(example: ctl01$ContentPlaceHolder1$Calendar1): ";
        if(notNull(extEventTarget)) {returnString += extEventTarget + ", ";} else {returnString += "NULL, ";}

        returnString += "extViewStateGenerator: ";
        if(notNull(extViewStateGenerator)) {returnString += extViewStateGenerator + ", ";} else {returnString += "NULL, ";}

        returnString += "extEventvalidation: ";
        if(notNull(extEventValidation)) {returnString += extEventValidation + ", ";} else {returnString += "NULL, ";}

        returnString += "extViewStateMain: ";
        if(notNull(extViewStateMain)) {returnString += extViewStateMain + ", ";} else {returnString += "NULL, ";}

        returnString += "rowCountIncludingRowHeadersColumn: " + rowCountIncludingRowHeadersColumn;

        returnString += "columnCountIncludingRowHeadersColumn: " + columnCountIncludingRowHeadersColumn;

        returnString += "timeCells:";
        if(timeCells == null) {
            returnString += " NULL";
            return returnString;
        }

        if(_notEmpty(timeCells)) {
            int i = 0;
            for(TimeCell timeCell: timeCells) {
                if(notNull(timeCell)){
                    returnString += " (" + i + ")" + timeCell.toString();
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

    private boolean _notEmpty(ArrayList<TimeCell> timeCells) {
        return !timeCells.isEmpty();
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
        dest.writeString(this.extDayOfMonthNumber);
        dest.writeString(this.extMonthWord);
        dest.writeString(this.extEventArgument);
        dest.writeString(this.extEventTarget);
        dest.writeString(this.extViewStateMain);
        dest.writeString(this.extEventValidation);
        dest.writeString(this.extViewStateGenerator);
        dest.writeInt(this.rowCountIncludingRowHeadersColumn);
        dest.writeInt(this.columnCountIncludingRowHeadersColumn);
        dest.writeList(this.timeCells);
    }

    public CalendarDay() {
    }

    protected CalendarDay(Parcel in) {
        this.extDayOfMonthNumber = in.readString();
        this.extMonthWord = in.readString();
        this.extEventArgument = in.readString();
        this.extEventTarget = in.readString();
        this.extViewStateMain = in.readString();
        this.extEventValidation = in.readString();
        this.extViewStateGenerator = in.readString();
        this.rowCountIncludingRowHeadersColumn = in.readInt();
        this.columnCountIncludingRowHeadersColumn = in.readInt();
        this.timeCells = new ArrayList<TimeCell>();
        in.readList(this.timeCells, TimeCell.class.getClassLoader());
    }

    public static final Parcelable.Creator<CalendarDay> CREATOR = new Parcelable.Creator<CalendarDay>() {
        @Override
        public CalendarDay createFromParcel(Parcel source) {
            return new CalendarDay(source);
        }

        @Override
        public CalendarDay[] newArray(int size) {
            return new CalendarDay[size];
        }
    };
}
