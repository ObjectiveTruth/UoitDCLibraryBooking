package com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel;

import android.support.v4.util.Pair;
import rx.Observable;
import timber.log.Timber;

import java.text.ParseException;
import java.util.ArrayList;

public class CalendarParser {
    static public Observable<CalendarData> parseDataToFindNumberOfDaysInfoObs(String rawWebPage) {
        return Observable.just(_parseDataToFindNumberOfDaysInfo(rawWebPage));
    }

    static public Observable<CalendarData> parseDataToFindAdditionalNumberOfDaysInfoObs(CalendarData calendarData,
                                                                                        String[] rawWebPages) {
        return Observable.just(_parseDataToFindAdditionalNumberOfDaysInfo(calendarData, rawWebPages));
    }

    static public Observable<CalendarData> parseDataToGetClickableDateDetailsObs(Pair<CalendarData, String[]>
                                                                                       calendarDataStringPair) {
        try {
            return Observable.just(_parseDataToGetClickableDateDetails(calendarDataStringPair));
        } catch (ParseException e) {
            Timber.e(e, "Error parsing the Clickable Date pages for Grid info");
            return Observable.error(e);
        }
    }

    static private CalendarData _parseDataToFindAdditionalNumberOfDaysInfo(CalendarData calendarData,
                                                                           String[] rawWebPages){
        int i = 1; //Start at 1 since this is for all pages past the first one, see CalendarModel
        for(String webpage: rawWebPages) {
            calendarData.days.set(i,
                    _parsePageAndReplaceViewStateAndEventValidationOfCalendarDay(webpage,
                            calendarData.days.get(i)));
        }
        return calendarData;
    }

    static private CalendarDay _parsePageAndReplaceViewStateAndEventValidationOfCalendarDay(String rawWebPage,
                                                                                            CalendarDay day) {
        Timber.i("Starting the parsing of the uoitlibrary main webpage for ViewState, ViewStateGenerator, " +
                "and EvenValidation values...");
        int stateStart = rawWebPage.indexOf("__VIEWSTATE\" value=");
        int stateEnd = rawWebPage.indexOf("/>", stateStart);
        day.extViewStateMain = rawWebPage.substring(stateStart+20, stateEnd-2);

        stateStart = rawWebPage.indexOf("__EVENTVALIDATION\" value=");
        stateEnd = rawWebPage.indexOf("/>", stateStart);
        day.extEventValidation = rawWebPage.substring(stateStart+26, stateEnd-2);

        stateStart = rawWebPage.indexOf("__VIEWSTATEGENERATOR\" value=");
        stateEnd = rawWebPage.indexOf("/>", stateStart);
        day.extViewStateGenerator = rawWebPage.substring(stateStart+29, stateEnd-2);

        return day;
    }

    /**
     * Parses the main webpage of the Uoitlibrary site and returns the parsed data as partially filled CalendarData
     * Should contain, viewstate, eventvalidation, viewstategenerator, and basic day information in the .days field
     * @param rawWebPage
     * @return
     */
    static private CalendarData _parseDataToFindNumberOfDaysInfo(String rawWebPage) {
        Timber.i("Starting the parsing of the uoitlibrary main webpage...");

        // Find the first clickable date on the calendar (identified by the doPostBack string)
        // Quit early if we don't find anything
        int foundAt = rawWebPage.indexOf("href=\"javascript:__doPostBack");
        if (_isStringNotFound(foundAt)) {
            Timber.v("No clickable days found");
            Timber.i("Parsing Completed.");
            return null;
        }


        // Will hold all the informatio we parse;
        CalendarData calendarData = new CalendarData();
        calendarData.days = new ArrayList<CalendarDay>(1);
        CalendarDay calendarDay = new CalendarDay();

        Timber.v("Found at least 1 clickable day from the webpage, saving it and continuing to search for more...");

        int stateStart = rawWebPage.indexOf("__VIEWSTATE\" value=");
        int stateEnd = rawWebPage.indexOf("/>", stateStart);
        calendarDay.extViewStateMain = rawWebPage.substring(stateStart+20, stateEnd-2);

        stateStart = rawWebPage.indexOf("__EVENTVALIDATION\" value=");
        stateEnd = rawWebPage.indexOf("/>", stateStart);
        calendarDay.extEventValidation = rawWebPage.substring(stateStart+26, stateEnd-2);

        stateStart = rawWebPage.indexOf("__VIEWSTATEGENERATOR\" value=");
        stateEnd = rawWebPage.indexOf("/>", stateStart);
        calendarDay.extViewStateGenerator = rawWebPage.substring(stateStart+29, stateEnd-2);


        // Each day you can click on in the calendar has parameters that uniquely identify it to the server.
        // Found here:
        calendarDay.extEventTarget = rawWebPage.substring(foundAt+31, foundAt+66);
        calendarDay.extDayOfMonthNumber = rawWebPage.substring(foundAt+103, foundAt+123).split("\"")[1].split(" ")[1];
        calendarDay.extEventArgument = rawWebPage.substring(foundAt+69, foundAt+73);
        calendarDay.extMonthWord = rawWebPage.substring(foundAt+103, foundAt+123).split("\"")[1].split(" ")[0];

        calendarData.days.add(calendarDay);

        foundAt = rawWebPage.indexOf("href=\"javascript:__doPostBack", foundAt+1);

        // Keep doing this until foundAt returns -1 which means it didnt find it
        while(_stringIsFound(foundAt)) {
            Timber.v("Found another clickable day saving it and continuing search for more...");
            CalendarDay addMeToCalendarData = new CalendarDay();

            addMeToCalendarData.extEventTarget = rawWebPage.substring(foundAt+31, foundAt+66);
            addMeToCalendarData.extDayOfMonthNumber = rawWebPage.substring(foundAt+103, foundAt+123).split("\"")[1].split(" ")[1];
            addMeToCalendarData.extEventArgument = rawWebPage.substring(foundAt+69, foundAt+73);
            addMeToCalendarData.extMonthWord = rawWebPage.substring(foundAt+103, foundAt+123).split("\"")[1].split(" ")[0];

            calendarData.days.add(addMeToCalendarData);
            foundAt = rawWebPage.indexOf("href=\"javascript:__doPostBack", foundAt+1);
        }

        Timber.v("No more clickable days found. Results:");
        Timber.v(calendarData.toString());

        Timber.i("Parsing Completed for the uoitlibrary main webpage. Found " + calendarData.days.size() + " days");
        return calendarData;
    }

    static private CalendarData _parseDataToGetClickableDateDetails(Pair<CalendarData, String[]>
                                                                            calendarDataStringPair)
            throws ParseException {
        Timber.i("Starting the parsing of the clickable uoitlibrary webpages to get grid info...");
        CalendarData calendarData = calendarDataStringPair.first;
        String[] rawWebPages = calendarDataStringPair.second;

        if(calendarData.days.size() != rawWebPages.length) {
            throw new ParseException("Number of clickable days found from the original webpage " +
                    "don't match the number of raw webapges downloaded. They need to be the same", 0);
        }

        int i = 0;
        for (String rawWebpage : rawWebPages) {
            calendarData.days.set(i,
                    _getMoreDayDataByParsingRawClickableDayData(calendarData.days.get(i), rawWebpage));
            i++;
        }
        Timber.i("Parsing of clickable uoitlibrary webpages completed.");
        return calendarData;
    }

    static private CalendarDay _getMoreDayDataByParsingRawClickableDayData(CalendarDay calendarDay, String rawWebPage)
            throws ParseException{
        Timber.v("Parsing Day " + calendarDay.extDayOfMonthNumber);

        calendarDay.timeCells = new ArrayList<TimeCell>();
        int startCalendar = rawWebPage.indexOf("ContentPlaceHolder1_Table1");

        String[] tdStore = rawWebPage.substring(startCalendar+5).split("<td");
        // Throw away the first element in tdStore because it is just the end of the table declaration

        Timber.v("The number of <td> elements found is " + tdStore.length);
        int hrefStart;
        int beginningWord;
        int endWord;
        String dayStart = null;

        int columnCount = -1;
        for(int j = 1; j < tdStore.length; j ++){

            // Keeps track of the number of columns
            if(columnCount == -1){
                if(tdStore[j].contains(":")){
                    columnCount = j;
                }
            }


            // Finds partially booked time slots
            if(tdStore[j].contains("Incomplete reservation")){
                TimeCell timeCellToBeAdded = new TimeCell();

                hrefStart = tdStore[j].indexOf("href=");
                timeCellToBeAdded.hrefSource =
                        tdStore[j].substring(hrefStart).split("\"")[1].replace(" ", "%20").replace("&amp;", "&");
                timeCellToBeAdded.timeCellType = TimeCellType.BOOKING_COMPETING;

                calendarDay.timeCells.add(timeCellToBeAdded);
            }
            // Finds Completely OPEN time slots
            else if(tdStore[j].contains("book.aspx")){
                TimeCell timeCellToBeAdded = new TimeCell();

                hrefStart = tdStore[j].indexOf("href=");
                timeCellToBeAdded.hrefSource =
                        tdStore[j].substring(hrefStart).split("\"")[1].replace(" ", "%20").replace("&amp;", "&");
                timeCellToBeAdded.timeCellType = TimeCellType.BOOKING_OPEN;

                calendarDay.timeCells.add(timeCellToBeAdded);
            }
            // Finds the Closed Rooms (the library is closed, nothing can be done)
            else if(tdStore[j].contains("color=\"#C0C000\"")){
                TimeCell timeCellToBeAdded = new TimeCell();

                timeCellToBeAdded.hrefSource = "";
                timeCellToBeAdded.timeCellType = TimeCellType.BOOKING_LIBRARY_CLOSED;

                calendarDay.timeCells.add(timeCellToBeAdded);
            }
            // Finds next time slots for alreaydy booked rooms " <== double quotes symbol The pad lock symbol
            else if(tdStore[j].contains(">\"<")){
                TimeCell timeCellToBeAdded = new TimeCell();

                timeCellToBeAdded.hrefSource = "";
                timeCellToBeAdded.timeCellType = TimeCellType.BOOKING_LOCKED;

                calendarDay.timeCells.add(timeCellToBeAdded);
            }
            // Finds fully booked rooms. Can still join or leave if you're in that booking but can't start a new one
            else if(tdStore[j].contains("viewleaveorjoin.aspx")){
                TimeCell timeCellToBeAdded = new TimeCell();

                hrefStart = tdStore[j].indexOf("href=");
                timeCellToBeAdded.hrefSource =
                        tdStore[j].substring(hrefStart).split("\"")[1].replace(" ", "%20").replace("&amp;", "&");
                beginningWord = tdStore[j].indexOf("color=\"Black\">");
                endWord = tdStore[j].lastIndexOf("</font>");
                timeCellToBeAdded.groupNameForWhenFullyBookedRoom =
                        (String) tdStore[j].subSequence(beginningWord+14, endWord);
                timeCellToBeAdded.timeCellType = TimeCellType.BOOKING_CONFIRMED;

                calendarDay.timeCells.add(timeCellToBeAdded);
            }
            // Everything else, mostly date and room number
            else{
                TimeCell timeCellToBeAdded = new TimeCell();

                timeCellToBeAdded.hrefSource = "";
                beginningWord = tdStore[j].indexOf("color=\"White\" size=\"1\">");
                endWord = tdStore[j].lastIndexOf("</td>");
                // The Top Left Corner?
                if(j==1){
                    timeCellToBeAdded.timeCellType = TimeCellType.TABLE_TOP_LEFT_CELL;
                }
                else{
                    timeCellToBeAdded.timeCellType = TimeCellType.TABLE_ROW_HEADER;
                    timeCellToBeAdded.timeStringOrRoomName =
                            (String) tdStore[j].subSequence(beginningWord+23, endWord-7);
                }
                if((j%11) == 1 && dayStart == null){
                    dayStart = (String) tdStore[j].subSequence(beginningWord+23, endWord-7);
                }

            }

        }

/*        calendarCache.get(i).startTime = null;
        calendarCache.get(i).source = sourceTemporaryForTrim.toArray(new String[sourceTemporaryForTrim.size()]);
        calendarCache.get(i).eventTarget = calendarDayPairs.get(i).eventTarget;
        calendarCache.get(i).eventArgument = calendarDayPairs.get(i).eventArgument;
        calendarCache.get(i).columnCount = columnCount-1;
        calendarCache.get(i).dataLength = temporaryForTrim.size();*/
        Timber.v("Parsing Complete for day " + calendarDay.extDayOfMonthNumber);
        return calendarDay;
    }

    static private boolean _isStringNotFound(int resultOfIndexOf) {
        return (resultOfIndexOf < 0);
    }

    static private boolean _stringIsFound(int resultOfIndexOf) {
        return (resultOfIndexOf >= 0);
    }
}
