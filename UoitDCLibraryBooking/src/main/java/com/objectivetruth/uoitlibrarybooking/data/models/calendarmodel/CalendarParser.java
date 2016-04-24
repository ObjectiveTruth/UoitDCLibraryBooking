package com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel;

import rx.Observable;
import timber.log.Timber;

import java.util.ArrayList;

public class CalendarParser {
    static public Observable<CalendarData> parseData(String rawWebPage) {
        return Observable.just(_parseData(rawWebPage));
    }

    static private CalendarData _parseData(String rawWebPage) {
        // Will hold all the informatio we parse;
        CalendarData calendarData = new CalendarData();
        Timber.i("Starting the parsing of the webpage...");

        int stateStart = rawWebPage.indexOf("__VIEWSTATE\" value=");
        int stateEnd = rawWebPage.indexOf("/>", stateStart);
        calendarData.viewstatemain = rawWebPage.substring(stateStart+20, stateEnd-2);

        stateStart = rawWebPage.indexOf("__EVENTVALIDATION\" value=");
        stateEnd = rawWebPage.indexOf("/>", stateStart);
        calendarData.eventvalidation = rawWebPage.substring(stateStart+26, stateEnd-2);

        stateStart = rawWebPage.indexOf("__VIEWSTATEGENERATOR\" value=");
        stateEnd = rawWebPage.indexOf("/>", stateStart);
        calendarData.viewstategenerator = rawWebPage.substring(stateStart+29, stateEnd-2);

        // Find the first clickable date on the calendar (identified by the doPostBack string)
        int foundAt = rawWebPage.indexOf("href=\"javascript:__doPostBack");
        if (_isStringNotFound(foundAt)) {
            Timber.v("No clickable days found");
            Timber.i("Parsing Completed.");
            return null;
        }

        Timber.v("Found at least 1 clickable day from the webpage, saving it and continuing to search for more...",
                rawWebPage.substring(foundAt+31, foundAt+66));

        CalendarDay calendarDay = new CalendarDay();
        // Each day you can click on in the calendar has parameters that uniquely identify it to the server.
        // Found here:
        calendarDay.extEventArgument = rawWebPage.substring(foundAt+31, foundAt+66);
        calendarDay.extDayOfMonthNumber = rawWebPage.substring(foundAt+103, foundAt+123).split("\"")[1].split(" ")[1];
        calendarDay.extEventMonth = rawWebPage.substring(foundAt+69, foundAt+73);
        calendarDay.extMonthWord = rawWebPage.substring(foundAt+103, foundAt+123).split("\"")[1].split(" ")[0];

        calendarData.days = new ArrayList<CalendarDay>();
        calendarData.days.add(calendarDay);

        // Keep doing this until foundAt returns -1 which means it didnt find it
        while(_stringIsFound(foundAt)) {
            foundAt = rawWebPage.indexOf("href=\"javascript:__doPostBack", foundAt+1);

            if(_stringIsFound(foundAt)){
                Timber.v("Found another clickable day, saving it and continuing search for more...",
                        rawWebPage.substring(foundAt+31, foundAt+66));
                CalendarDay addMeToCalendarData = new CalendarDay();

                calendarDay.extEventArgument = rawWebPage.substring(foundAt+31, foundAt+66);
                calendarDay.extDayOfMonthNumber = rawWebPage.substring(foundAt+103, foundAt+123).split("\"")[1].split(" ")[1];
                calendarDay.extEventMonth = rawWebPage.substring(foundAt+69, foundAt+73);
                calendarDay.extMonthWord = rawWebPage.substring(foundAt+103, foundAt+123).split("\"")[1].split(" ")[0];

                calendarData.days.add(addMeToCalendarData);
            }
        }

        Timber.v("No more clickable days found. Total number of clickable days to be saved is: " +
                calendarData.days.size());
        Timber.v(calendarData.toString());

        Timber.i("Parsing Completed.");

        return calendarData;

/*        if(numberOfDays == 0){

            throw new ArrayIndexOutOfBoundsException("__docallback wasn't found to do the parsing or no dates available for booking");

        }
        else{

            durationPerDay = new long[numberOfDays];
            durationPart1 = System.currentTimeMillis() - startParse1;
            Timber.i("The first parse took: " + durationPart1);
            for(int i = 0; i < numberOfDays; i++){
                long durationPart2Start = System.currentTimeMillis();
                siteUrl = new URL("https://rooms.library.dc-uoit.ca/dc_studyrooms/calendar.aspx");
                conn = (HttpsURLConnection) siteUrl.openConnection();
                conn.setUseCaches(false);
                conn.setConnectTimeout(8000);
                conn.setRequestMethod("POST");

                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                DataOutputStream outTwo = new DataOutputStream(conn.getOutputStream());


                String content =  "__EVENTTARGET=" + URLEncoder.encode(calendarDayPairs.get(i).eventTarget, "UTF-8")
                        + "&__EVENTARGUMENT=" + URLEncoder.encode(calendarDayPairs.get(i).eventArgument, "UTF-8")
                        + "&__VIEWSTATE=" + URLEncoder.encode(VIEWSTATEMAIN, "UTF-8")
                        + "&__EVENTVALIDATION=" + URLEncoder.encode(EVENTVALIDATION, "UTF-8")
                        + "&__VIEWSTATEGENERATOR=" + URLEncoder.encode(VIEWSTATEGENERATOR, "UTF-8");

                //Log.i(TAG + "bigString", content);
                outTwo.writeBytes(content);
                outTwo.flush();
                outTwo.close();
                is = conn.getInputStream();

                in = new BufferedReader(new InputStreamReader(is));
                total = new StringBuilder(is.available());

                while ((line = in.readLine()) != null) {
                    total.append(line);
                }
                responseString = total.toString();


                Timber.i("New Parse starting...");
                int startCalendar = responseString.indexOf("ContentPlaceHolder1_Table1");
                //Log.i(TAG, "Table 1 is: " +responseString.subSequence(startCalendar, startCalendar+10 ));

                String[] tdStore = responseString.substring(startCalendar+5).split("<td");
                //first element throw away REMEMBER THAT it is just the end of the table declaration
                for(int k = 0; k < tdStore.length; k ++){
                    Log.i(TAG, "Element " + k + " is " + tdStore[k]);
                }
                Timber.i("The number of <td> elements is " + tdStore.length);
                ArrayList<String> temporaryForTrim = new ArrayList<String>();
                ArrayList<String> sourceTemporaryForTrim = new ArrayList<String>();
                int hrefStart;
                int beginningWord;
                int endWord;
                String dayStart = null;

                columnCount = -1;
                for(int j = 1; j < tdStore.length; j ++){

                    if(columnCount == -1){
                        if(tdStore[j].contains(":")){
                            columnCount = j;
                        }
                    }
                    if(tdStore[j].contains("Incomplete reservation")){
                        //Finds partially booked time slots
                        hrefStart = tdStore[j].indexOf("href=");
                        //Log.i(TAG, "Incomplete Test: " + tdStore[j].substring(hrefStart).split("\"")[1]);
                        sourceTemporaryForTrim.add(tdStore[j].substring(hrefStart).split("\"")[1].replace(" ", "%20").replace("&amp;", "&"));

                        temporaryForTrim.add("Open");
                    }
                    else if(tdStore[j].contains("book.aspx")){
                        //finds OPEN time slots
                        hrefStart = tdStore[j].indexOf("href=");
                        //Log.i(TAG, "Open test: " + tdStore[j].substring(hrefStart).split("\"")[1]);
                        sourceTemporaryForTrim.add(tdStore[j].substring(hrefStart).split("\"")[1].replace(" ", "%20").replace("&amp;", "&"));

                        temporaryForTrim.add("Open");
                    }

                    else if(tdStore[j].contains("color=\"#C0C000\"")){
                        //finds the Closed Time slots
                        sourceTemporaryForTrim.add("");
                        temporaryForTrim.add("Closed");
                    }
                    else if(tdStore[j].contains(">\"<")){
                        //Finds next time slots for alreaydy booked rooms " <== double quotes symbol
                        sourceTemporaryForTrim.add("");
                        temporaryForTrim.add("\"");
                    }
                    else if(tdStore[j].contains("viewleaveorjoin.aspx")){
                        //finds fully booked rooms
                        hrefStart = tdStore[j].indexOf("href=");
                        //Log.i(TAG, "viewleaveorjoin: " + tdStore[j].substring(hrefStart).split("\"")[1]);

                        sourceTemporaryForTrim.add(tdStore[j].substring(hrefStart).split("\"")[1].replace(" ", "%20").replace("&amp;", "&"));
                        beginningWord = tdStore[j].indexOf("color=\"Black\">");
                        endWord = tdStore[j].lastIndexOf("</font>");
                        //Timber.v("view leave or join word: " + tdStore[j].subSequence(beginningWord+14, endWord));
                        temporaryForTrim.add((String) tdStore[j].subSequence(beginningWord+14, endWord));
                    }
                    else{
                        //Everything else, mostly date and room number
                        sourceTemporaryForTrim.add("");
                        beginningWord = tdStore[j].indexOf("color=\"White\" size=\"1\">");
                        endWord = tdStore[j].lastIndexOf("</td>");
                        //Log.i(TAG, "else word: " + tdStore[j].subSequence(beginningWord+23, endWord-7));
                        if(j==1){
                            temporaryForTrim.add("");
                        }
                        else{
                            temporaryForTrim.add((String) tdStore[j].subSequence(beginningWord+23, endWord-7));
                        }
                        if((j%11) == 1 && dayStart == null){
                            dayStart = (String) tdStore[j].subSequence(beginningWord+23, endWord-7);
                        }

                    }

                }

                String[] month_space_day = new String[]{calendarDayPairs.get(i).monthName, calendarDayPairs.get(i).dayNumber};

                calendarCache.add(new CalendarMonth(month_space_day[0],
                        month_space_day[1],
                        temporaryForTrim.toArray(new String[temporaryForTrim.size()])));

                calendarCache.get(i).startTime = null;
                calendarCache.get(i).source = sourceTemporaryForTrim.toArray(new String[sourceTemporaryForTrim.size()]);
                calendarCache.get(i).eventTarget = calendarDayPairs.get(i).eventTarget;
                calendarCache.get(i).eventArgument = calendarDayPairs.get(i).eventArgument;
                calendarCache.get(i).viewState = VIEWSTATEMAIN;
                calendarCache.get(i).columnCount = columnCount-1;
                calendarCache.get(i).eventValidation = EVENTVALIDATION;
                calendarCache.get(i).dataLength = temporaryForTrim.size();
                long durationPart2 = System.currentTimeMillis() - durationPart2Start;
                Timber.i("RefreshPass 2 took :" + durationPart2);
                durationPerDay[i] = durationPart2;
            }

        }
        durationTotalParse = System.currentTimeMillis() - startTime;
        Timber.i("Refresh Engine took: " + durationTotalParse);
        return calendarCache;*/

    }

    static private boolean _isStringNotFound(int resultOfIndexOf) {
        return (resultOfIndexOf < 0);
    }

    static private boolean _stringIsFound(int resultOfIndexOf) {
        return (resultOfIndexOf >= 0);
    }
}
