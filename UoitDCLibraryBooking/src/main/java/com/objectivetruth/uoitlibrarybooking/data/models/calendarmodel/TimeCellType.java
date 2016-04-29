package com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel;

/**
 * Types of cells to show in the calendar, corresponds to the ones found on the website
 * https://rooms.library.dc-uoit.ca/uoit_studyrooms/calendar.aspx
 */
public enum TimeCellType {

    /**
    * Column header in the table view. Usually the room number example: LIB302
    */
    TABLE_COLUMN_HEADER,

    /**
    * Row header in the table view. Usually the time in 30 min slots
    */
    TABLE_ROW_HEADER,

    /**
     * Top left corner of the entire table, nothing there just taking up space
     */
    TABLE_TOP_LEFT_CELL,

    /**
    * Slot of time that's completely open (nobody has tried to book)
    */
    BOOKING_OPEN,

    /**
    * Slot with at least 1 reservation trying to confirm it
    */
    BOOKING_COMPETING,

    /**
    * Slot that's completely locked. Usually proceeds a BOOKING_CONFIRMED slot
    */
    BOOKING_LOCKED,

    /**
     * Slot is closed because the library is closed at that time
     */
    BOOKING_LIBRARY_CLOSED,

    /**
    * Slot that's been confirmed by someone (not necessarily the user). One can still
    * enter the booking and either add themselves to the already confirmed booking
    * Or remove themselves from the booking if they were part of it previously
    */
    BOOKING_CONFIRMED,

    /**
     * Catch all timecell where it grabbed info that was not processed correctly but may still
     * be useful information (ie, new room opened up)
     */
    UNKNOWN
}
