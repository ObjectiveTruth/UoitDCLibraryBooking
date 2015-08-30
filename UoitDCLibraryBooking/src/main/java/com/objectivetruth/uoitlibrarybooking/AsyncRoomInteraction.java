package com.objectivetruth.uoitlibrarybooking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import timber.log.Timber;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class AsyncRoomInteraction extends AsyncTask<String, int[], String> {
	String TAG = "AsyncRoomInteraction";
	Activity mActivity;
	String dayNumber;
	String eventTarget;
	String eventArgument;
	String viewState;
	String eventValidation;
	AsyncRoomInteraction mAsyncRoomInteraction;
	AsyncResponse comm;
	String date;
	String roomNumber;
	ProgressDialog progDialog;
	String currentViewState;
	String currentEventValidation;
    String currentViewStateGenerator;
	CookieManager cookieManager;
	//read from the db
	String weekDayName;
	String calendarDate;
	String calendarMonth;
	String[] joinSpinnerArr;
	String[] leaveSpinnerArr;
	String groupCode;
	String timeRange;
	String institution;
	String notes;
	String groupName;
	int pageNumberInt;
	int shareRow;
	int shareColumn;
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
        progDialog = new ProgressDialog(mActivity);
        progDialog.setMessage("Preparing Room...");
        progDialog.setIndeterminate(false);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setCancelable(true);
        progDialog.setOnCancelListener(new OnCancelListener(){

			@Override
			public void onCancel(DialogInterface diag) {
				if(mAsyncRoomInteraction.cancel(true)){
					Log.i(TAG, TAG + " was Canceled Successfully");
				}
				
			}
        	
        	
        });
        progDialog.show();
		
	}

	@Override
	protected String doInBackground(String... input) {
        Timber.i("==========================================");
        Timber.i("AsyncRoomInteraction Called. Executing...");
		long startTime = System.currentTimeMillis();
		String daySource = "day" + dayNumber + "source";
		String day = "day" + dayNumber;
		SQLiteDatabase db = MainActivity.mdbHelper.getReadableDatabase();
		Cursor c = db.query(MainActivity.mdbHelper.CALENDAR_TABLE_NAME, new String[]{daySource, day}, null, null, null, null, null);
        if(c.moveToFirst()){
        	//Log.i(TAG, c.getString(c.getColumnIndex(daySource)));
        	eventTarget = c.getString(c.getColumnIndex(daySource));
        	weekDayName = c.getString(c.getColumnIndex(day));
        	int i = 0;
        	while(c.moveToNext() && i < 3){
        		
        		if(i == 0){
        			eventArgument = c.getString(c.getColumnIndex(daySource));
        			calendarDate = c.getString(c.getColumnIndex(day));
        			Log.i(TAG, c.getString(c.getColumnIndex(daySource)));
        		}
        		else if(i == 1){
        			viewState = c.getString(c.getColumnIndex(daySource));
        			calendarMonth = c.getString(c.getColumnIndex(day));
        			//Log.i(TAG, c.getString(c.getColumnIndex(daySource)));
        		}
        		else if(i == 2){
        			eventValidation = c.getString(c.getColumnIndex(daySource));
        			//Log.i(TAG, c.getString(c.getColumnIndex(daySource)));
        		}
        		
        		i++;
        	}
        }

        c.close();
        https://rooms.library.dc-uoit.ca/dc_studyrooms/myreservations.aspx
        Log.i(TAG, "DB Reading took " + (System.currentTimeMillis() - startTime));
        Timber.v("LinkString: " + input[0]);
        try{
        	long startTimePart1 = System.currentTimeMillis();
        	cookieManager = new CookieManager();
        	CookieHandler.setDefault(cookieManager);

	        URL siteUrl = new URL("https://rooms.library.dc-uoit.ca/dc_studyrooms/calendar.aspx");
	        HttpsURLConnection conn = (HttpsURLConnection) siteUrl.openConnection();
			conn.setUseCaches(false);
			conn.setConnectTimeout(8000);
			conn.setRequestMethod("POST");
	
	        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        conn.setDoOutput(true);
	        conn.setDoInput(true);
	        DataOutputStream out = new DataOutputStream(conn.getOutputStream());

	        String content = "__EVENTTARGET=" + URLEncoder.encode(eventTarget, "UTF-8")
	        		 + "&__EVENTARGUMENT=" + URLEncoder.encode(eventArgument, "UTF-8")
	        		 + "&__VIEWSTATE=" +  URLEncoder.encode(viewState, "UTF-8")
	        		 + "&__EVENTVALIDATION=" + URLEncoder.encode(eventValidation, "UTF-8");
	        
	        Timber.v("Outgoing String: " + content);
	        out.writeBytes(content);
	        out.flush();
	        out.close();
	        if(conn.getHeaderFields().get("Set-Cookie") == null){
	        	Timber.v("Cookie is null, this shouldn't happen");
	        }
	        else{
	        	Timber.v("cookie before is " + cookieManager.getCookieStore().getCookies().get(0).toString());
	        }
/*	        else{
	        	Log.i(TAG, "Cokie is " + conn.getHeaderFields().get("Set-Cookie").toString());
	        }*/
	        //String cookie = cookieManager.getCookieStore().getCookies().get(0).toString();
	        //InputStream dumpIS = conn.getInputStream();
/*	        BufferedReader in3 = new BufferedReader(new InputStreamReader(is3));
	        StringBuilder total1 = new StringBuilder(is3.available());
	        String line2;
	        while ((line2 = in3.readLine()) != null) {
	            total1.append(line2);
	        }
	        String responseString2 = total1.toString();
*/        
	        //dumpIS.close();
	        //longInfo(TAG + "response", responseString2);
			//doc = Jsoup.parse(responseString);
	        
	        long startTimePart2 = System.currentTimeMillis();
	        //Log.i(TAG, "First Request Took " + (startTimePart2 - startTimePart1));
	        if(input[0].contains("book.aspx")){
                Timber.v("Link contains book.aspx,");
	        	/*Log.i(TAG, "unformatted: "+ input[0]);
		        String formatted = input[0].replace(" ", "%20").replace("&amp;", "&");
		        */
		        URL siteUrl2 = new URL("https://rooms.library.dc-uoit.ca/dc_studyrooms/" + input[0]);
		        //Log.i(TAG, "formatted string " + formatted);
		        HttpsURLConnection conn2 = (HttpsURLConnection) siteUrl2.openConnection();
		        //conn2.setInstanceFollowRedirects(true);
				conn2.setRequestMethod("GET");
				conn2.setUseCaches(false);
				conn2.setConnectTimeout(8000);
		        conn2.setRequestProperty("Referer", "https://rooms.library.dc-uoit.ca/dc_studyrooms/calendar.aspx");
		        conn2.setDoOutput(true);
		        conn2.setDoInput(true);
		        /*DataOutputStream out2 = new DataOutputStream(conn2.getOutputStream());
		        out2.flush();
		        out2.close();
		        */
		        InputStream is = conn2.getInputStream();
		        BufferedReader in2 = new BufferedReader(new InputStreamReader(is));
		        StringBuilder total = new StringBuilder(is.available());
		        String line;
		        while ((line = in2.readLine()) != null) {
		            total.append(line);
		        }
		        String responseString = total.toString();
		        in2.close();
		        longInfo(TAG + "response", responseString);
		        //doc = Jsoup.parse(responseString);
		        //date = doc.select("#TextBoxStartDateTime").attr("value");
		        //roomNumber = doc.select("#TextBoxRoomNo").attr("value");
		        		        
		        int stateStart = responseString.indexOf("__VIEWSTATE\" value=");
                int stateEnd = responseString.indexOf("/>", stateStart);
                currentViewState = responseString.substring(stateStart+20, stateEnd-2);
                Timber.v("ViewState: " + currentViewState);

                stateStart = responseString.indexOf("__EVENTVALIDATION\" value=");
                stateEnd = responseString.indexOf("/>", stateStart);
                currentEventValidation = responseString.substring(stateStart+26, stateEnd-2);
                Timber.v("EventValidation: " + currentEventValidation);

                stateStart = responseString.indexOf("__VIEWSTATEGENERATOR\" value=");
                stateEnd = responseString.indexOf("/>", stateStart);
                currentViewStateGenerator = responseString.substring(stateStart+29, stateEnd-2);
                Timber.v("viewStateGenerator =" + currentViewStateGenerator);

                //Log.i(TAG, responseString.split("TextBoxStartDateTime")[1].split("\"")[4]);
                /*stateStart = responseString.indexOf("Sunday");
                stateEnd = responseString.indexOf("id=", stateStart);
                date = responseString.substring(stateStart, stateEnd-2);*/

                stateStart = input[0].indexOf("time=");
                stateEnd = input[0].indexOf("&", stateStart+2);
                String timeOfEvent = input[0].substring(stateStart+5, stateEnd);
                timeOfEvent = timeOfEvent.replace("%20", " ");
                date = weekDayName + ", " + calendarMonth + " " + calendarDate + "\n" + timeOfEvent;
                Timber.v("Date: " + date);
                //Log.i(TAG, date);


                stateStart = input[0].indexOf("room=");
                stateEnd = input[0].indexOf("&", stateStart+2);
                roomNumber = input[0].substring(stateStart+5, stateEnd);
                Timber.v("roomNumber: " + roomNumber);


                
                //if(responseString.substring(stateStart+20, stateEnd-20))
                
                /*Log.i(TAG, "viewStateMain =" + responseString.substring(stateStart+20, stateEnd-2));
                if(responseString.substring(stateStart+20, stateEnd-2).compareTo(currentViewState) == 0){
                	Log.i(TAG, "viewState passed");
                }*/
                /*Log.i(TAG, "eventvalidation =" + responseString.substring(stateStart+26, stateEnd-2));                
                if(responseString.substring(stateStart+26, stateEnd-2).compareTo(currentEventValidation) == 0){
                	Log.i(TAG, "EventValidation passed");
                }*/
                
                
                
		        //Log.i(TAG, "cookie after is " + cookieManager.getCookieStore().getCookies().get(0).toString());
				
				//Log.i(TAG, "S-book took " + (System.currentTimeMillis() - startTimePart2));
				return "s-book";
	        }
	        else if(input[0].contains("joinorleave.aspx")){
                Timber.v("Link contains joinorleave.aspx,");
		        URL siteUrl2 = new URL("https://rooms.library.dc-uoit.ca/dc_studyrooms/" + input[0]);
		        HttpsURLConnection conn2 = (HttpsURLConnection) siteUrl2.openConnection();
		        
				conn2.setRequestMethod("GET");
				conn2.setUseCaches(false);
				conn2.setConnectTimeout(8000);
		        conn2.setRequestProperty("Referer", "https://rooms.library.dc-uoit.ca/dc_studyrooms/calendar.aspx");
		        conn2.setDoOutput(true);
		        conn2.setDoInput(true);
		        DataOutputStream out2 = new DataOutputStream(conn2.getOutputStream());
		        out2.flush();
		        out2.close();
		        
		        InputStream is = conn2.getInputStream();
		        BufferedReader in2 = new BufferedReader(new InputStreamReader(is));
		        StringBuilder total = new StringBuilder(is.available());
		        String line;
		        while ((line = in2.readLine()) != null) {
		            total.append(line);
		        }
		        String responseString = total.toString();
		        in2.close();
                Timber.v(responseString);
		        //longInfo(TAG + "response", responseString); 
		        //doc = Jsoup.parse(responseString);
		        
		        int stateStart = responseString.indexOf("__VIEWSTATE\" value=");
                int stateEnd = responseString.indexOf("/>", stateStart);
                currentViewState = responseString.substring(stateStart+20, stateEnd-2);
                Timber.v("currentViewState = " + currentViewState);

                stateStart = responseString.indexOf("__EVENTVALIDATION\" value=");
                stateEnd = responseString.indexOf("/>", stateStart);
                currentEventValidation = responseString.substring(stateStart+26, stateEnd-2);
                Timber.v("currentEventValidation = " + currentEventValidation);

                stateStart = responseString.indexOf("__VIEWSTATEGENERATOR\" value=");
                stateEnd = responseString.indexOf("/>", stateStart);
                currentViewStateGenerator = responseString.substring(stateStart+29, stateEnd-2);
                Timber.v("viewStateGenerator =" + currentViewStateGenerator);

                stateStart = input[0].indexOf("room=");
                stateEnd = input[0].indexOf("&", stateStart);
                
		        roomNumber = input[0].substring(stateStart+5, stateEnd);
                Timber.v("roomNumber = " + roomNumber);
		        //Log.i(TAG, "room number is : " + roomNumber);
		        
		        
		        ArrayList<String> spinnerTempArrayList = new ArrayList<String>();
		        int j = 0;
		        String tempString;
		        int lastFoundAt = responseString.indexOf("id=\"ContentPlaceHolder1_RadiobuttonListLeaveGroup_" + j + "\"");
		        stateStart = responseString.indexOf("\">", lastFoundAt+1) +2;

		        stateEnd = responseString.indexOf("</label>", stateStart);
		        tempString = responseString.substring(stateStart, stateEnd).replace("<b>", "");
		        
		        spinnerTempArrayList.add(tempString.replace("</b>", ""));
		        j++;
		        //Log.i(TAG, responseString.substring(lastFoundAt, stateEnd).split("\"")[7]);
		        while(responseString.indexOf("id=\"ContentPlaceHolder1_RadiobuttonListLeaveGroup_" + j + "\"") != -1){
			        lastFoundAt = responseString.indexOf("id=\"ContentPlaceHolder1_RadiobuttonListLeaveGroup_" + j + "\"");
			        stateStart = responseString.indexOf("\">", lastFoundAt+1) +2;
			        
			        
			        /*tempString = responseString.substring(lastFoundAt, stateEnd).split("\"")[7];
			        stateStart = responseString.indexOf("</b>", stateEnd);*/
			        stateEnd = responseString.indexOf("</label>", stateStart);
			        tempString = responseString.substring(stateStart, stateEnd).replace("<b>", "");

			        spinnerTempArrayList.add(tempString.replace("</b>", ""));
		        	j++;

		        }
		        leaveSpinnerArr = spinnerTempArrayList.toArray(new String[spinnerTempArrayList.size()]);
		        
		        /*
		        Elements leavableGroups = doc.select("label[for^=RadioButtonListLeaveGroup]");
		        leaveSpinnerArr = new String[leavableGroups.size()];
		        for(int i = 0; i < leavableGroups.size(); i ++){
		        	leaveSpinnerArr[i] = leavableGroups.get(i).text();
		        }*/
		        
		        //Elements joinableGroups = doc.select("label[for^=RadioButtonListJoinOrCreateGroup]");
		        j = 1;
		        
		        lastFoundAt = responseString.indexOf("id=\"ContentPlaceHolder1_RadioButtonListJoinOrCreateGroup_" + 0 + "\"");
		        lastFoundAt = responseString.indexOf("id=\"ContentPlaceHolder1_RadioButtonListJoinOrCreateGroup_" + j + "\"", lastFoundAt+1);
		        stateStart = responseString.indexOf("\">", lastFoundAt+1) +2;
		        
		        
		        /*tempString = responseString.substring(lastFoundAt, stateEnd).split("\"")[7];
		        stateStart = responseString.indexOf("</b>", stateEnd);*/
		        stateEnd = responseString.indexOf("</label>", stateStart);
		        tempString = responseString.substring(stateStart, stateEnd).replace("<b>", "");
		        
		        

		        
		        spinnerTempArrayList.clear();
		        spinnerTempArrayList.add(tempString.replace("</b>", ""));
		        j++;
		        //Log.i(TAG, responseString.substring(lastFoundAt, stateEnd).split("\"")[7]);
		        while(responseString.indexOf("id=\"ContentPlaceHolder1_RadioButtonListJoinOrCreateGroup_" + j + "\"") != -1){
		        	//Log.i(TAG, "index is " + responseString.indexOf("id=\"RadioButtonListJoinOrCreateGroup_" + j + "\""));
			        lastFoundAt = responseString.indexOf("id=\"ContentPlaceHolder1_RadioButtonListJoinOrCreateGroup_" + j + "\"");
			        stateStart = responseString.indexOf("\">", lastFoundAt+1) +2;
			        
			        
			        /*tempString = responseString.substring(lastFoundAt, stateEnd).split("\"")[7];
			        stateStart = responseString.indexOf("</b>", stateEnd);*/
			        stateEnd = responseString.indexOf("</label>", stateStart);
			        tempString = responseString.substring(stateStart, stateEnd).replace("<b>", "");
			        
			        spinnerTempArrayList.add(tempString.replace("</b>", ""));
		        	j++;
		        }
		        joinSpinnerArr = spinnerTempArrayList.toArray(new String[spinnerTempArrayList.size()]);
		        
		        
		        /*joinSpinnerArr = new String[joinableGroups.size()-1];
		        for(int i = 0; i < (joinableGroups.size()-1); i ++){
		        	joinSpinnerArr[i] = joinableGroups.get(i+1).text();
		        }*/
		        
		        //Log.i(TAG, "cookie after is " + cookieManager.getCookieStore().getCookies().get(0).toString());
				
				
		        //Log.i(TAG, "S-joinorleave took " + (System.currentTimeMillis() - startTimePart2));
				return "s-joinleave";
	        }
            else if(input[0].contains("viewleaveorjoin.aspx")){
                Timber.v("Link contains viewleaveorjoin.aspx,");
                URL siteUrl2 = new URL("https://rooms.library.dc-uoit.ca/dc_studyrooms/" + input[0]);
                HttpsURLConnection conn2 = (HttpsURLConnection) siteUrl2.openConnection();
		        conn2.setInstanceFollowRedirects(true);
				conn2.setRequestMethod("GET");
				conn2.setUseCaches(false);
				conn2.setConnectTimeout(8000);
		        //Log.i(TAG, input[0]);
		        conn2.setRequestProperty("Referer", "https://rooms.library.dc-uoit.ca/dc_studyrooms/calendar.aspx");
		        conn2.setDoOutput(true);
		        conn2.setDoInput(true);
		        DataOutputStream out2 = new DataOutputStream(conn2.getOutputStream());
		        out2.flush();
		        out2.close();
		        
		        InputStream is = conn2.getInputStream();
		        BufferedReader in2 = new BufferedReader(new InputStreamReader(is));
		        StringBuilder total = new StringBuilder(is.available());
		        String line;
		        while ((line = in2.readLine()) != null) {
		            total.append(line);
		        }
		        
		        
		        String responseString = total.toString();
		        in2.close();

                Timber.v(responseString);
		        
		        int stateStart = responseString.indexOf("__VIEWSTATE\" value=");
                int stateEnd = responseString.indexOf("/>", stateStart);
                currentViewState = responseString.substring(stateStart+20, stateEnd-2);
                Timber.v("viewState = " + currentViewState);
                
                stateStart = responseString.indexOf("__EVENTVALIDATION\" value=");
                stateEnd = responseString.indexOf("/>", stateStart);
                currentEventValidation = responseString.substring(stateStart+26, stateEnd-2);
                Timber.v("eventValidation = " + currentEventValidation);

                stateStart = responseString.indexOf("__VIEWSTATEGENERATOR\" value=");
                stateEnd = responseString.indexOf("/>", stateStart);
                currentViewStateGenerator = responseString.substring(stateStart+29, stateEnd-2);
                Timber.v("viewStateGenerator =" + currentViewStateGenerator);

                stateStart = input[0].indexOf("starttime=");
                stateEnd = input[0].indexOf("&", stateStart);
                timeRange = input[0].substring(stateStart + 10, stateEnd).replace("%20", " " );
                Timber.v("starttime = " + timeRange);
                
                stateStart = input[0].indexOf("room=", stateEnd);
                stateEnd = input[0].indexOf("&", stateStart);
                roomNumber = input[0].substring(stateStart+5, stateEnd);
		        Timber.v("roomNumber = " + roomNumber);
		        
		        
		        stateStart = responseString.indexOf("name=\"ctl00$ContentPlaceHolder1$TextBoxDate\" type");
		        stateEnd = responseString.indexOf("id=\"", stateStart);
		        date = responseString.substring(stateStart + 64, stateEnd-2);
		        Timber.v("date = " + date);
		        
		        stateStart = responseString.indexOf("name=\"ctl00$ContentPlaceHolder1$TextBoxGroupCode\" type=");
                stateEnd = responseString.indexOf("id=", stateStart);
                groupCode = responseString.substring(stateStart+69, stateEnd-2);
                Timber.v("groupCode = " + groupCode);
                
		        stateStart = responseString.indexOf("name=\"ctl00$ContentPlaceHolder1$TextBoxName\" type=");
                stateEnd = responseString.indexOf("id=", stateStart);
                groupName = responseString.substring(stateStart+64, stateEnd-2);
                Timber.v("groupName = " + groupName);
                
                stateStart = responseString.indexOf("name=\"ctl00$ContentPlaceHolder1$TextBoxEndTime\" type=");
                stateEnd = responseString.indexOf("id=", stateStart);
                timeRange = timeRange + " - " + responseString.substring(stateStart + 67, stateEnd-2);
                Timber.v("timeRange = " + timeRange);
                //TODO Add notes and see if it shows up properly
                stateStart = responseString.indexOf("name=\"ctl00$ContentPlaceHolder1$TextBoxNotes\" type=");
                stateEnd = responseString.indexOf("id=", stateStart);
                if(responseString.substring(stateStart, stateEnd).contains("value")){
                	notes = responseString.substring(stateStart + 64, stateEnd-2);
                }
                else{notes = "";}
                Timber.v("notes(can be an empty string) = " + notes);
                
                stateStart = responseString.indexOf("name=\"ctl00$ContentPlaceHolder1$TextBoxOrg\" type=");
                stateEnd = responseString.indexOf("id=", stateStart);
                institution = responseString.substring(stateStart + 63, stateEnd-2);
                Timber.v("institution = " + institution);

	        	return "s-viewleaveorjoin";
	        }
			
        }catch(FileNotFoundException e){
        	e.printStackTrace();
        	return "FileNotFound";
        	
        }catch(NegativeArraySizeException e){
        	e.printStackTrace();
        	return "FileNotFound";
        }catch(Exception e){
        	e.printStackTrace();
        	return null;
        }
        
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if(result == null){
			Toast.makeText(mActivity.getApplicationContext(), "Hmm, didn't work, try refreshing",
					Toast.LENGTH_LONG).show();

		}
		else if(result.equalsIgnoreCase("FileNotFound")){
        	Toast.makeText(mActivity.getApplicationContext(), "Couldn't find that time slot, try refreshing calendar",
					Toast.LENGTH_LONG).show();

		}
		else if(result.equalsIgnoreCase("s-book")){
			comm.LaunchRoomInteraction(cookieManager, roomNumber, date, currentViewState, currentEventValidation, shareRow, shareColumn, pageNumberInt, currentViewStateGenerator);

		}
		else if(result.equalsIgnoreCase("s-viewleaveorjoin")){
			comm.LaunchViewLeaveOrJoin(cookieManager, roomNumber, date, groupName, groupCode, timeRange, institution, notes, currentViewState, currentEventValidation, shareRow, shareColumn, pageNumberInt, currentViewStateGenerator );

			
		}
		else if(result.equalsIgnoreCase("s-joinleave")){
			comm.LaunchJoinOrLeave(cookieManager, roomNumber, weekDayName, calendarDate, calendarMonth, currentViewState, currentEventValidation, joinSpinnerArr, leaveSpinnerArr, shareRow, shareColumn, pageNumberInt, currentViewStateGenerator);

		}
		
		progDialog.dismiss();
		
	}

	public AsyncRoomInteraction(Activity mActivity, int pageNumber, int shareRow, int shareColumn){
		mAsyncRoomInteraction = this;
		this.shareRow = shareRow;
		this.shareColumn = shareColumn;
		comm = (AsyncResponse) mActivity;
		this.mActivity = mActivity;
		this.pageNumberInt = pageNumber;
		this.dayNumber = String.valueOf(pageNumber + 1); //because it sends page number and DB uses day number
	}
	
	public static void longInfo(String tag, String str) {
	    if(str.length() > 4000) {
	        Log.i(tag, str.substring(0, 4000));
	        Log.i(tag, str.substring(4000));
	        Log.i(tag, str.substring(8000));
	        //Log.i(tag, str.substring(12000));
	        //Log.i(tag, str.substring(16000));
	    } else
	        Log.i(tag, str);
	}
}
