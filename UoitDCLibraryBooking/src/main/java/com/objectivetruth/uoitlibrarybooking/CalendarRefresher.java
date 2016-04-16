package com.objectivetruth.uoitlibrarybooking;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import timber.log.Timber;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

class CalendarRefresher extends AsyncTask<Void, Integer, ArrayList<CalendarMonth>> {
	
	AsyncResponse comm;
	AppCompatActivity mContext;
	ProgressDialog progDialog;
	int mProgress;
	Tracker t;
	long durationPart1 = -1;
	long durationTotalParse = -1;
	long[] durationPerDay = null;
	
	@Override
	protected void onPostExecute(ArrayList<CalendarMonth> result) {
		
		super.onPostExecute(result);
		long postExecStartTime = System.currentTimeMillis();
		try{
		if(durationPart1 > 0.0){

			t.send(new HitBuilders.TimingBuilder()
			.setCategory("Refresh")
			.setVariable("Part 1 of Parse Engine")
			.setValue(durationPart1)
			.build()
			);	
		}
		if(durationTotalParse > 0.0){
			t.send(new HitBuilders.TimingBuilder()
			.setCategory("Refresh")
			.setVariable("Parse Engine Total (no post process)")
			.setValue(durationTotalParse)
			.build()
			);
		}
		if(durationPerDay != null){
			for(int i = 0; i < durationPerDay.length; i++){
				t.send(new HitBuilders.TimingBuilder()
				.setCategory("Refresh")
				.setVariable("Per Day Processing Time")
				.setLabel("Day " + String.valueOf(i + 1))
				.setValue(durationPerDay[i])
				.build()
				);
			}	
			
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		
		

		if(result == null){
			comm.SendMessageToAllGridViews(result);
	        //progDialog.dismiss();
	        Toast.makeText(mContext, "Network Error: Try again",
					Toast.LENGTH_SHORT).show();
	        long duration = System.currentTimeMillis() - postExecStartTime;
	        Timber.i("PostExecute took: " + duration);
	        
			t.send(new HitBuilders.TimingBuilder()
				.setCategory("Refresh")
				.setVariable("Post Execute Network Error took:")
				.setValue(duration)
				.build()
				);
		}
		else if(result.size() > 0){

			comm.SendMessageToAllGridViews(result);
			
			new AsyncDbWriter(mContext).execute(result);
	        //progDialog.dismiss();
	        long duration = System.currentTimeMillis() - postExecStartTime;
	        Timber.i("PostExecute took: " + duration);
	        
	        t.send(new HitBuilders.TimingBuilder()
			.setCategory("Refresh")
			.setVariable("Post Execute Success took:")
			.setValue(duration)
			.build()
			);
		}
		else{
			
			
			comm.SendMessageToAllGridViews(result);
			

	        //progDialog.dismiss();
	        Toast.makeText(mContext, "Network Error: Try again",
					Toast.LENGTH_SHORT).show();
	        long duration = System.currentTimeMillis() - postExecStartTime;
	        Timber.i("PostExecute took: " + duration);
	        
			t.send(new HitBuilders.TimingBuilder()
				.setCategory("Refresh")
				.setVariable("Post Execute Network Error took:")
				.setValue(duration)
				.build()
				);
		}
		//Timber.i("Ignore IllegalStateError: AndroidHttpClient is inside another class, it will get GC'd don't worry");
		
		
        
	}
	

	@Override
	protected void onPreExecute() {
        super.onPreExecute();
        mProgress = 0;
        /*progDialog = new ProgressDialog(mContext);
        progDialog.setMessage("Loading...");
        progDialog.setIndeterminate(false);
        progDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progDialog.setCancelable(true);
        progDialog.setMax(100);
        progDialog.show();
		*/
		
	}	

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
/*		mProgress += mProgress + values[0];
		progDialog.setProgress(mProgress);*/
		
		
	}

	final String TAG = "CalendarRefresher";
	
	public CalendarRefresher(AppCompatActivity comm){
		this.mContext = comm;
		this.comm = (AsyncResponse) comm;
		t = ((UOITLibraryBookingApp) mContext.getApplication()).getTracker();
		

	}
	
	@Override
	protected ArrayList<CalendarMonth> doInBackground(Void... params) {
		long startTime = System.currentTimeMillis();
		ArrayList<CalendarMonth> calendarCache = null;
		int numberOfDays;
		int columnCount = -1;
		publishProgress(5);
		//Document doc;
		URL siteUrl;
		HttpsURLConnection conn;
		String responseString = null;
		String line;
		BufferedReader in;
		InputStream is;
		StringBuilder total;
		ArrayList<SillyNameValuePairClass> calendarDayPairs = new ArrayList<SillyNameValuePairClass>();
		//HttpClient httpclient = null;
        try {			

        	calendarCache = new ArrayList<CalendarMonth>();
			String VIEWSTATEMAIN;
			String EVENTVALIDATION;
            String VIEWSTATEGENERATOR;
			//Elements dayElems = null;
			/*httpclient = AndroidHttpClient.newInstance(System.getProperty("http.agent"));
			//HttpClient httpclient = new DefaultHttpClient();
	        HttpResponse response;
	        
	        HttpGet method = new HttpGet("https://rooms.library.dc-uoit.ca/studyrooms/calendar.aspx");
			response = httpclient.execute(method);*/
			long startParse1 = System.currentTimeMillis();
			siteUrl = new URL("https://rooms.library.dc-uoit.ca/dc_studyrooms/calendar.aspx");
	        conn = (HttpsURLConnection) siteUrl.openConnection();
	        
			conn.setRequestMethod("GET");
			conn.setUseCaches(false);
			conn.setConnectTimeout(8000);
	        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        //conn.setDoOutput(true);
	        conn.setDoInput(true);
/*	        DataOutputStream outTwo = new DataOutputStream(conn.getOutputStream());


	        String content =  "__EVENTTARGET=" + URLEncoder.encode(calendarDayPairs.get(i).eventTarget, "UTF-8")
	        		 + "&__EVENTARGUMENT=" + URLEncoder.encode(calendarDayPairs.get(i).eventArgument, "UTF-8")
	        		 + "&__VIEWSTATE=" + URLEncoder.encode(VIEWSTATEMAIN, "UTF-8")
	        		 + "&__EVENTVALIDATION=" + URLEncoder.encode(EVENTVALIDATION, "UTF-8");
	        
	        //Log.i(TAG + "bigString", content);
	        outTwo.writeBytes(content);
	        outTwo.flush();
	        outTwo.close();*/
	        is = conn.getInputStream();
	        
	        in = new BufferedReader(new InputStreamReader(is));
	        total = new StringBuilder(is.available());
	        
	        while ((line = in.readLine()) != null) {
	            total.append(line);
	        }
	        responseString = total.toString();
            
	        
	        //StatusLine statusLine = response.getStatusLine();
            
            
                
        	
/*            ByteArrayOutputStream out = new ByteArrayOutputStream();
            
            responseEntity = response.getEntity();
            responseEntity.writeTo(out);
            responseEntity.consumeContent();
            out.close();
            
            responseString = out.toString("UTF-8");
            method.abort();*/
            //RequestHandler.longInfo("long", responseString);
            //doc = Jsoup.parse(responseString);
			//Elements elems = doc.select("input");
			//Log.i(TAG, "size is" + elems.size());
			//Scary static String i'm using
			//VIEWSTATEMAIN = elems.get(0).attr("value");
			
            int stateStart = responseString.indexOf("__VIEWSTATE\" value=");
            int stateEnd = responseString.indexOf("/>", stateStart);
            VIEWSTATEMAIN = responseString.substring(stateStart+20, stateEnd-2);
            Timber.v("VIEWSTATEMAIN =" + VIEWSTATEMAIN);

            stateStart = responseString.indexOf("__EVENTVALIDATION\" value=");
            stateEnd = responseString.indexOf("/>", stateStart);
            EVENTVALIDATION = responseString.substring(stateStart+26, stateEnd-2);
            Timber.v("EVENTVALIDATION =" + EVENTVALIDATION);

            stateStart = responseString.indexOf("__VIEWSTATEGENERATOR\" value=");
            stateEnd = responseString.indexOf("/>", stateStart);
            VIEWSTATEGENERATOR = responseString.substring(stateStart+29, stateEnd-2);
            Timber.v("VIEWSTATEGENERATOR =" + VIEWSTATEGENERATOR);

            //Timber.i(responseString);
            int foundAt = responseString.indexOf("href=\"javascript:__doPostBack");
            Timber.i("First Day Added, found in " + responseString.substring(foundAt+31, foundAt+66));
            /*28
            Log.i(TAG, "event target test1 is: "+ responseString.substring(foundAt+43, foundAt+47));
            Log.i(TAG, "event target test3 is: "+ responseString.substring(foundAt+75, foundAt+95));
            Log.i(TAG, "event target test4 is: "+ responseString.substring(foundAt+75, foundAt+95).split("\"")[1].split(" ")[0]);
            Log.i(TAG, "event target test5 is: "+ responseString.substring(foundAt+75, foundAt+95).split("\"")[1].split(" ")[1]);
            */
            calendarDayPairs.add(new SillyNameValuePairClass(responseString.substring(foundAt+31, foundAt+66), //finds the event argument
            		responseString.substring(foundAt+69, foundAt+73),  											//finds the event argument
            		responseString.substring(foundAt+103, foundAt+123).split("\"")[1].split(" ")[0], //this finds the real human month
            		responseString.substring(foundAt+103, foundAt+123).split("\"")[1].split(" ")[1] //this finds the day number
            		));
            Timber.v("First Day Contents: " + calendarDayPairs.get(0).toString());

            numberOfDays = 0;
            if(foundAt >= 0){
                numberOfDays  = 1;
            }
            while(foundAt >= 0) {
                 foundAt = responseString.indexOf("href=\"javascript:__doPostBack", foundAt+1);
                 if(foundAt >=0){
                     numberOfDays ++;
                	 Timber.i("Next Day("+ numberOfDays  + ") Added, found in " + responseString.substring(foundAt+31, foundAt+66));
                     calendarDayPairs.add(new SillyNameValuePairClass(responseString.substring(foundAt+31, foundAt+66), //finds the event argument
                             responseString.substring(foundAt+69, foundAt+73),  											//finds the event argument
                             responseString.substring(foundAt+103, foundAt+123).split("\"")[1].split(" ")[0], //this finds the real human month
                             responseString.substring(foundAt+103, foundAt+123).split("\"")[1].split(" ")[1] //this finds the day number
                     ));

                 }

            }

			if(numberOfDays == 0){
				
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
/*					for(int k = 0; k < tdStore.length; k ++){
						Log.i(TAG, "Element " + k + " is " + tdStore[k]);
					}*/
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
    		

			
				
			}catch(ArrayIndexOutOfBoundsException e){
				e.printStackTrace();
				Timber.i("Something went wrong, probably no days available");
                calendarCache.add(getPreviousCalendarOnlyRooms());
			}catch (Exception e) {
				
				e.printStackTrace();
			}finally{

				
			}
        


		return calendarCache;
	}

    private CalendarMonth getPreviousCalendarOnlyRooms(){
        SQLiteDatabase db = MainActivity.mdbHelper.getReadableDatabase();

        CalendarMonth calendarMonthToReturn = new CalendarMonth("", "", null);
        calendarMonthToReturn.startTime = "3";
        calendarMonthToReturn.eventTarget = "";
        calendarMonthToReturn.eventArgument = "";
        calendarMonthToReturn.viewState = "";
        calendarMonthToReturn.eventValidation = "";
        Timber.i("No Days Available, starting previousCalendar Copy Process");

        String day = "day" + 1;
        Cursor c = db.query(MainActivity.mdbHelper.CALENDAR_TABLE_NAME, new String[]{day}, null, null, null, null, null);
        int columnCount = 0;
        String[] calendarData, calendarSource;
        //There's something in the DB
        if(c.moveToFirst()) {

            //Log.i(TAG, "count = " + String.valueOf(c.getCount()-4));

            c.moveToPosition(4);
//            numberOfItems = Integer.parseInt(c.getString(c.getColumnIndex(day)));

            columnCount = Integer.parseInt(c.getString(c.getColumnIndex(day)));
            Timber.i("Column Count is " + columnCount + " including the topleft blank square");
            //c.moveToNext();
            calendarMonthToReturn.columnCount = columnCount;

            calendarMonthToReturn.dataLength = calendarMonthToReturn.columnCount;
            calendarData = new String[columnCount];
            calendarSource = new String[columnCount];
            calendarData[0] = "";
            int i = 1;
            c.moveToNext();
            calendarData[i] = c.getString(c.getColumnIndex(day));

            i++;
            while (i < calendarData.length) {
                c.moveToNext();
                calendarData[i] = c.getString(c.getColumnIndex(day));
                //Timber.i("Entry: " + calendarData[i] );
                i++;
            }
            //Log.i(TAG, "count = " + String.valueOf(i-1));

            calendarMonthToReturn.data = calendarData;
            calendarMonthToReturn.source = calendarSource;
            for(int j = 0; j < calendarData.length; j++){
                if(calendarData[j] == null){
                    Timber.i("Entry: null");
                }
                else{
                    Timber.i("Entry: " + calendarData[j]);
                }
            }

        }
        return calendarMonthToReturn;
    }
	public static void longInfo(String tag, String str) {
	    if(str.length() > 4000) {
	        Log.i(tag, str.substring(0, 4000));
	        Log.i(tag, str.substring(4000));
	    } else
	        Log.i(tag, str);
	}


}
