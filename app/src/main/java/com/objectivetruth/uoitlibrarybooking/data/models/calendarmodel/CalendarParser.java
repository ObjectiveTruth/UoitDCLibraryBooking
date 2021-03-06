package com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel;

import android.support.v4.util.Pair;
import rx.Observable;
import timber.log.Timber;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.objectivetruth.uoitlibrarybooking.data.models.common.ParseUtilities.findStringFromStringBetweenSearchTerms;

public class CalendarParser {
    static public Observable<CalendarData> parseDataToFindNumberOfDaysInfoObs(String rawWebPage) {
        return Observable.just(_parseDataToFindNumberOfDaysInfo(rawWebPage));
    }

    /**
     * Returns a CalendarDay partially filled. Will contain the 3: ViewState, ViewStateGenerator, and EventValidation
     * @param rawWebpage
     * @return
     */
    static public Observable<CalendarDay> parseRawWebpageForViewStateGeneratorAndEventValidation(String rawWebpage) {
        return Observable.just(
                _parsePageAndReplaceViewStateAndEventValidationOfCalendarDay(rawWebpage, new CalendarDay()));
    }

    static public Observable<CalendarData> parseDataToFindAdditionalNumberOfDaysInfoObs(CalendarData calendarData,
                                                                                        String[] rawWebPages) {
        return Observable.just(_parseDataToFindAdditionalNumberOfDaysInfo(calendarData, rawWebPages));
    }

    static public Observable<CalendarData> parseDataToGetClickableDateDetailsObs(Pair<CalendarData, String[]>
                                                                                       calendarDataStringPair) {
        try {
            return Observable.just(_parseDataToGetClickableDateDetails(calendarDataStringPair));
        } catch (Throwable t) {
            Timber.e(t, "Error parsing the Clickable Date pages for Grid info");
            return Observable.error(t);
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

        Timber.i("Finished parsing of the uoitlibrary main webpage for ViewState, ViewStateGenerator, " +
                "and EvenValidation values...");

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
            Timber.d("No clickable days found");
            Timber.i("Parsing Completed.");
            return null;
        }


        // Will hold all the informatio we parse;
        CalendarData calendarData = new CalendarData();
        calendarData.days = new ArrayList<CalendarDay>(1);
        CalendarDay calendarDay = new CalendarDay();

        Timber.d("Found at least 1 clickable day from the webpage, saving it and continuing to search for more...");

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
            Timber.d("Found another clickable day saving it and continuing search for more...");
            CalendarDay addMeToCalendarData = new CalendarDay();

            addMeToCalendarData.extEventTarget = rawWebPage.substring(foundAt+31, foundAt+66);
            addMeToCalendarData.extDayOfMonthNumber = rawWebPage.substring(foundAt+103, foundAt+123).split("\"")[1].split(" ")[1];
            addMeToCalendarData.extEventArgument = rawWebPage.substring(foundAt+69, foundAt+73);
            addMeToCalendarData.extMonthWord = rawWebPage.substring(foundAt+103, foundAt+123).split("\"")[1].split(" ")[0];

            calendarData.days.add(addMeToCalendarData);
            foundAt = rawWebPage.indexOf("href=\"javascript:__doPostBack", foundAt+1);
        }

        Timber.d("No more clickable days found");
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
        Timber.d("Parsing Day " + calendarDay.extDayOfMonthNumber);

        calendarDay.timeCells = new ArrayList<TimeCell>();
        String[] tdStore = _getArrayOfAllTableDataElementsInPage(rawWebPage);

        Timber.d("The number of <td> elements found is " + tdStore.length);
        if (tdStore.length < 2) {
            Timber.w("Suspiciously low number of <td> elements found in page. This may be an error");
        }

        int beginningWord;
        int endWord;
        String currentTableDataElement = null;
        String dayStart = null;

        //will keep track of the number of rows/columns in the loop
        int columnCountIncludingRowHeadersColumn = 1;
        int rowCountIncludingColumnHeadersRow = 1;


        // Start at 1 to ignore the first element in tdStore because it is just the end of the table declaration
        for(int iterationIndex = 1; iterationIndex < tdStore.length; iterationIndex ++){

            currentTableDataElement = tdStore[iterationIndex];
            Timber.v("Parsing: " + currentTableDataElement);
            TimeCell timeCellToBeAdded = new TimeCell();

            // Populate timecell with info from CalendarDay
            timeCellToBeAdded.param_eventtarget = calendarDay.extEventTarget;
            timeCellToBeAdded.param_eventargument = calendarDay.extEventArgument;

            // Is the very first cell thats being parsed means its the top left cell
            if(iterationIndex == 1) { timeCellToBeAdded.timeCellType = TimeCellType.TABLE_TOP_LEFT_CELL;}

            // Finds partially booked time slots
            else if(currentTableDataElement.contains("Incomplete reservation")){
                int hrefStart = currentTableDataElement.indexOf("href=");
                String anchorHREF = currentTableDataElement.substring(hrefStart).split("\"")[1].replace("&amp;", "&");
                timeCellToBeAdded.param_room =          _extractRoomParamFromAnchorHREF(anchorHREF);
                timeCellToBeAdded.param_get_link =      _extractGETLinkParamFromAnchorHREF(anchorHREF);
                timeCellToBeAdded.param_starttime =     _extractStartTimeParamFromAnchorHREF(anchorHREF);
                timeCellToBeAdded.param_next =          _extractNextParamFromAnchorHREF(anchorHREF);
                timeCellToBeAdded.timeCellType = TimeCellType.BOOKING_COMPETING;
            }

            // Finds Completely OPEN time slots
            else if(currentTableDataElement.contains("book.aspx")){
                int hrefStart = currentTableDataElement.indexOf("href=");
                String anchorHREF = currentTableDataElement.substring(hrefStart).split("\"")[1].replace("&amp;", "&");
                timeCellToBeAdded.param_room =          _extractRoomParamFromAnchorHREF(anchorHREF);
                timeCellToBeAdded.param_get_link =      _extractGETLinkParamFromAnchorHREF(anchorHREF);
                timeCellToBeAdded.param_starttime =     _extractStartTimeParamFromAnchorHREF(anchorHREF);
                timeCellToBeAdded.param_next =          _extractNextParamFromAnchorHREF(anchorHREF);
                timeCellToBeAdded.timeCellType = TimeCellType.BOOKING_OPEN;
            }

            // Finds the Closed Rooms (the library is closed, nothing can be done by user)
            else if(currentTableDataElement.contains("color=\"#C0C000\"")){
                timeCellToBeAdded.timeCellType = TimeCellType.BOOKING_LIBRARY_CLOSED;
            }

            // Finds next time slots for alreaydy booked rooms " <== double quotes symbol The pad lock symbol
            else if(currentTableDataElement.contains(">\"<")){
                timeCellToBeAdded.timeCellType = TimeCellType.BOOKING_LOCKED;
            }

            // Finds fully booked rooms. Can still join or leave if you're in that booking but can't start a new one
            else if(currentTableDataElement.contains("viewleaveorjoin.aspx")){
                int hrefStart = currentTableDataElement.indexOf("href=");
                String anchorHREF = currentTableDataElement.substring(hrefStart).split("\"")[1].replace("&amp;", "&");
                timeCellToBeAdded.param_room =          _extractRoomParamFromAnchorHREF(anchorHREF);
                timeCellToBeAdded.param_get_link =      _extractGETLinkParamFromAnchorHREF(anchorHREF);
                timeCellToBeAdded.param_starttime =     _extractStartTimeParamFromAnchorHREF(anchorHREF);
                timeCellToBeAdded.param_next =          _extractNextParamFromAnchorHREF(anchorHREF);

                beginningWord = currentTableDataElement.indexOf("color:Black;\">");
                endWord = currentTableDataElement.lastIndexOf("</a>");
                timeCellToBeAdded.groupNameForWhenFullyBookedRoom =
                        (String) currentTableDataElement.subSequence(beginningWord+14, endWord);
                timeCellToBeAdded.timeCellType = TimeCellType.BOOKING_CONFIRMED;
            }

            // Row header cell or Column header cell
            else if(_isRowHeaderOrColumnHeaderCell(currentTableDataElement)){
                Timber.v("Cell is either, row header or columns header");
                // Must be row header
                if(_doesContainTimeInfo(currentTableDataElement)) {
                    timeCellToBeAdded.timeCellType = TimeCellType.TABLE_ROW_HEADER;
                    timeCellToBeAdded.timeStringOrRoomName =
                            _getTimeStringOrRoomNameFromString(currentTableDataElement);
                    rowCountIncludingColumnHeadersRow++;
                // Must be column header
                }else{
                    timeCellToBeAdded.timeCellType = TimeCellType.TABLE_COLUMN_HEADER;
                    timeCellToBeAdded.timeStringOrRoomName =
                            _getTimeStringOrRoomNameFromString(currentTableDataElement);
                    columnCountIncludingRowHeadersColumn++;
                }
            }

            // Anything that's not caught should be pulled but put in unknown
            else{ timeCellToBeAdded.timeCellType = TimeCellType.UNKNOWN; }

            Timber.v("Cell was determined to be: " + timeCellToBeAdded.timeCellType.name());
            calendarDay.timeCells.add(timeCellToBeAdded);
        }

        calendarDay.columnCountIncludingRowHeadersColumn = columnCountIncludingRowHeadersColumn;
        calendarDay.rowCountIncludingRowHeadersColumn = rowCountIncludingColumnHeadersRow;

        Timber.i("DayStart is" + dayStart);
        Timber.d("Parsing Complete for day " + calendarDay.extDayOfMonthNumber);
        return calendarDay;
    }

    static private boolean _isStringNotFound(int resultOfIndexOf) {
        return (resultOfIndexOf < 0);
    }

    static private boolean _stringIsFound(int resultOfIndexOf) {
        return (resultOfIndexOf >= 0);
    }

    static private boolean _doesContainTimeInfo(String subject) {
        String TIME_REGEX = "\\d?\\d:\\d\\d [AP]M"; //finds all in the form of "10:30 PM" or "5:39 AM" with any prefix
        final Matcher matcher = Pattern.compile(TIME_REGEX).matcher(subject);
        return matcher.find();
    }

    static private String[] _getArrayOfAllTableDataElementsInPage(String rawWebPage) {
        int startOfCalendar = rawWebPage.indexOf("ContentPlaceHolder1_Table1");
        return rawWebPage.substring(startOfCalendar + 5).split("<td");
    }

    static private boolean _isRowHeaderOrColumnHeaderCell(String subject) {
        String fontStyleWebpageSearchString = "font-size:8pt;\">";
        return subject.contains(fontStyleWebpageSearchString);
    }

    static private String _getTimeStringOrRoomNameFromString(String subject) {
        String webPageSearchString = "font-size:8pt;\">";
        return findStringFromStringBetweenSearchTerms(subject, webPageSearchString, "</td>");
    }

    /**
     * Extracts the get link parameter. Assumes this format
     * temp.aspx?starttime=4:00 PM&room=LIB309&next=book
     * @param anchorHREF
     * @return
     */
    static private String _extractGETLinkParamFromAnchorHREF(String anchorHREF) {
        return anchorHREF.split("\\?")[0];
    }

    /**
     * Extracts the starttime query parameter. Assumes this format
     * temp.aspx?starttime=4:00 PM&room=LIB309&next=book
     * @param anchorHREF
     * @return
     */
    static private String _extractStartTimeParamFromAnchorHREF(String anchorHREF) {
        String STARTTIME_QUERY_PARAM_NAME = "starttime";
        String[] queryParams = anchorHREF.split("\\?")[1].split("&");
        for(String s: queryParams) {
            String[] arrayWithBothSidesOfTheEqualSign = s.split("=");
            if(arrayWithBothSidesOfTheEqualSign[0].contentEquals(STARTTIME_QUERY_PARAM_NAME)) {
                return arrayWithBothSidesOfTheEqualSign[1];
            }
        }
        return "";
    }

    /**
     * Extracts the room query parameter. Assumes this format
     * temp.aspx?starttime=4:00 PM&room=LIB309&next=book
     * @param anchorHREF
     * @return
     */
    static private String _extractRoomParamFromAnchorHREF(String anchorHREF) {
        String ROOM_QUERY_PARAM_NAME = "room";
        String[] queryParams = anchorHREF.split("\\?")[1].split("&");
        for(String s: queryParams) {
            String[] arrayWithBothSidesOfTheEqualSign = s.split("=");
            if(arrayWithBothSidesOfTheEqualSign[0].contentEquals(ROOM_QUERY_PARAM_NAME)) {
                return arrayWithBothSidesOfTheEqualSign[1];
            }
        }
        return "";
    }

    /**
     * Extracts the next query parameter. Assumes this format
     * temp.aspx?starttime=4:00 PM&room=LIB309&next=book
     * @param anchorHREF
     * @return
     */
    static private String _extractNextParamFromAnchorHREF(String anchorHREF) {
        String NEXT_QUERY_PARAM_NAME = "next";
        String[] queryParams = anchorHREF.split("\\?")[1].split("&");
        for(String s: queryParams) {
            String[] arrayWithBothSidesOfTheEqualSign = s.split("=");
            if(arrayWithBothSidesOfTheEqualSign[0].contentEquals(NEXT_QUERY_PARAM_NAME)) {
                return arrayWithBothSidesOfTheEqualSign[1];
            }
        }
        return "";
    }
}
