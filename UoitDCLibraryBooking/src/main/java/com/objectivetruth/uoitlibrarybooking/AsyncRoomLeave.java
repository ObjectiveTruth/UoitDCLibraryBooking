package com.objectivetruth.uoitlibrarybooking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
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

public class AsyncRoomLeave extends AsyncTask<String[], int[], String> {
	CookieManager cookieManager;
	String[] dataInput;
	final String TAG = "AsyncRoomLeave";
	Activity mActivity;
	ProgressDialog progDialog;
	CommunicatorRoomInteractions comm;
	String returnMessage;
	AsyncRoomLeave mAsyncRoomLeave;

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
        progDialog = new ProgressDialog(mActivity);
        progDialog.setMessage("Canceling Reservation...");
        progDialog.setIndeterminate(false);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setCancelable(true);
        progDialog.setOnCancelListener(new OnCancelListener(){

			@Override
			public void onCancel(DialogInterface diag) {
				if(mAsyncRoomLeave.cancel(true)){
					Log.i(TAG, TAG + " was Canceled Successfully");
				}
				
			}
        	
        	
        });
        progDialog.show();
		super.onPreExecute();
	}
	public AsyncRoomLeave(CookieManager cookieManager, Activity mActivity){
		mAsyncRoomLeave = this;
		this.comm = (CommunicatorRoomInteractions) mActivity;
		this.cookieManager = cookieManager;
		this.mActivity = mActivity;
	}
	@Override
	protected String doInBackground(String[]... input) {
            Timber.i("==================================");
            Timber.i("AsyncRoomLeave Called. Executing...");

			try{
				String[] inputData = input[0];
				String viewState = inputData[0];
				String eventValidation = inputData[1];
				
				
				String RadioButtonListJoinOrCreateGroup = inputData[2];
				String RadiobuttonListLeaveGroup = inputData[3];
				String ButtonLeave = inputData[4];
				String TextBoxID = inputData[5];
				String TextBoxPassword = inputData[6];
                String viewStateGenerator = inputData[7];
                Timber.v("InputArray has " + inputData.length + " elements, Excepted 8");
				
/*				Log.i(TAG, viewState);
				Log.i(TAG, eventValidation);
				Log.i(TAG, RadioButtonListJoinOrCreateGroup);
				Log.i(TAG, RadiobuttonListLeaveGroup);
				Log.i(TAG, ButtonLeave);
				Log.i(TAG, TextBoxID);
				Log.i(TAG, TextBoxPassword);*/


				
				
		    	CookieHandler.setDefault(cookieManager);
		        String cookie = cookieManager.getCookieStore().getCookies().get(0).toString();
		        URL siteUrl = new URL("https://rooms.library.dc-uoit.ca/dc_studyrooms/joinorleave.aspx");
		        HttpsURLConnection conn = (HttpsURLConnection) siteUrl.openConnection();
		        conn.setInstanceFollowRedirects( false );
				conn.setRequestMethod("POST");
				
		        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		        conn.setRequestProperty("Referer", "https://rooms.library.dc-uoit.ca/dc_studyrooms/joinorleave.aspx");
				conn.setUseCaches(false);
				conn.setConnectTimeout(8000);
		        conn.setDoOutput(true);
		        conn.setDoInput(true);
		        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		       	        
		        
		
		        String content = "__VIEWSTATE=" + URLEncoder.encode(viewState, "UTF-8")
		        		+ "&__EVENTVALIDATION=" + URLEncoder.encode(eventValidation, "UTF-8")
		        		//+ "&RadioButtonListJoinOrCreateGroup=" + URLEncoder.encode(RadioButtonListJoinOrCreateGroup, "UTF-8")
		        		+ "&ctl00$ContentPlaceHolder1$RadiobuttonListLeaveGroup=" + URLEncoder.encode(RadiobuttonListLeaveGroup, "UTF-8")
		        		+ "&ctl00$ContentPlaceHolder1$ButtonLeave=" + URLEncoder.encode(ButtonLeave, "UTF-8");

                Timber.v("Outgoing String = " + content);
		        out.writeBytes(content);
		        out.flush();
		        out.close();
/*		        if(conn.getHeaderFields().get("Set-Cookie") == null){
		        	Log.i(TAG, "cookie is null");
		        }
		        else{
		        	Log.i(TAG, "Cokie is " + conn.getHeaderFields().get("Set-Cookie").toString());
		        }*/
                cookie = cookieManager.getCookieStore().getCookies().get(0).toString();
                Timber.v("End cookie: " + cookie);
                String firstRedirect = conn.getHeaderField("Location");
                Timber.v("=========================================================");
                Timber.v("====================First Redirect======================");
                Timber.v("=========================================================");
		        //Needed to do redirect by hand because redirect url wasn't urlencoded so space scrwed it up
		        //===================================================================
		        // =====================FIRST REDIRECT================================
		        //=====================================================================
		        //Log.i(TAG, "Redirect Start Cookie: " + cookie);
		        String formatted = firstRedirect.replace(" ", "%20");
		        Timber.v("First Redirect(URLEncoded): " + formatted);
		        siteUrl = new URL("https://rooms.library.dc-uoit.ca/dc_studyrooms/" + formatted);
		        conn = (HttpsURLConnection) siteUrl.openConnection();
		        conn.setInstanceFollowRedirects( true );
				conn.setRequestMethod("GET");
				conn.setUseCaches(false);
				conn.setConnectTimeout(8000);
		        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		        conn.setRequestProperty("Referer", "https://rooms.library.dc-uoit.ca/dc_studyrooms/joinorleave.aspx");
		        //conn.setDoOutput(true);
		        conn.setDoInput(true);
		/*        out = new DataOutputStream(conn.getOutputStream());
		       	        
		        
		
		        content = "__VIEWSTATE=" + URLEncoder.encode(viewState, "UTF-8")
		        		+ "&__EVENTVALIDATION=" + URLEncoder.encode(eventValidation, "UTF-8") 
		        		//+ "&RadioButtonListJoinOrCreateGroup=" + URLEncoder.encode(RadioButtonListJoinOrCreateGroup, "UTF-8")
		        		+ "&RadiobuttonListLeaveGroup=" + URLEncoder.encode(RadiobuttonListLeaveGroup, "UTF-8")
		        		+ "&ButtonLeave=" + URLEncoder.encode(ButtonLeave, "UTF-8");
		        
		        Log.i(TAG + "bigString", content);
		        out.writeBytes(content);
		        out.flush();
		        out.close();*/
		        InputStream is = conn.getInputStream();
		        BufferedReader in = new BufferedReader(new InputStreamReader(is));
		        StringBuilder total = new StringBuilder(is.available());
		        String line;
		        while ((line = in.readLine()) != null) {
		            total.append(line);
		        }
		        
		        
		        String responseString = total.toString();
		        in.close();
		        //longInfo(TAG + "response", responseString);
		        //doc = Jsoup.parse(responseString);
		        int stateStart = responseString.indexOf("__VIEWSTATE\" value=");
                int stateEnd = responseString.indexOf("/>", stateStart);
                viewState = responseString.substring(stateStart+20, stateEnd-2);
                Timber.v("VIEWSTATE = " + viewState);

                stateStart = responseString.indexOf("__EVENTVALIDATION\" value=");
                stateEnd = responseString.indexOf("/>", stateStart);
                eventValidation = responseString.substring(stateStart+26, stateEnd-2);
                Timber.v("EVENTVALIDATION = "+ eventValidation);

                stateStart = responseString.indexOf("__VIEWSTATEGENERATOR\" value=");
                stateEnd = responseString.indexOf("/>", stateStart);
                viewStateGenerator = responseString.substring(stateStart+29, stateEnd-2);
                Timber.v("viewStateGenerator = " + viewStateGenerator);

                stateStart = responseString.indexOf("__VIEWSTATEGENERATOR\" value=");
                stateEnd = responseString.indexOf("/>", stateStart);
                viewStateGenerator = responseString.substring(stateStart+29, stateEnd-2);
                Timber.v("viewStateGenerator = " + viewStateGenerator);

                ButtonLeave = "Leave+the+Group";

                cookie = cookieManager.getCookieStore().getCookies().get(0).toString();
                Timber.v("Cookie: " + cookie);
                Timber.v("=========================================================");
                Timber.v("====================ActualLEAVE Connection================");
                Timber.v("=========================================================");
		      //==========================================================
		        //=============ActualLeaving connection===================
		        //==========================================================
		        Log.i(TAG, "Redirect Start Cookie: " + cookie);
		        siteUrl = new URL("https://rooms.library.dc-uoit.ca/dc_studyrooms/leavegroup.aspx");
		        conn = (HttpsURLConnection) siteUrl.openConnection();
		        
				conn.setRequestMethod("POST");
				conn.setUseCaches(false);
				conn.setConnectTimeout(8000);
		        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		        conn.setRequestProperty("Referer", "https://rooms.library.dc-uoit.ca/dc_studyrooms/leavegroup.aspx");
		        conn.setDoOutput(true);
		        conn.setDoInput(true);
		        out = new DataOutputStream(conn.getOutputStream());
		
		        content = "__VIEWSTATE=" + URLEncoder.encode(viewState, "UTF-8")
		        		+ "&__EVENTVALIDATION=" + URLEncoder.encode(eventValidation, "UTF-8")
                        + "&__VIEWSTATEGENERATOR=" + URLEncoder.encode(viewStateGenerator, "UTF-8")
		        		+ "&ctl00$ContentPlaceHolder1$TextBoxID=" + URLEncoder.encode(TextBoxID, "UTF-8")
		        		+ "&ctl00$ContentPlaceHolder1$TextBoxPassword=" + URLEncoder.encode(TextBoxPassword, "UTF-8")
		             	+ "&ctl00$ContentPlaceHolder1$ButtonLeave=" + ButtonLeave;
		        
		        //Log.i(TAG + "bigString", content);
		        out.writeBytes(content);
		        out.flush();
		        out.close();
		        is = conn.getInputStream();
		        in = new BufferedReader(new InputStreamReader(is));
		        total = new StringBuilder(is.available());
		        while ((line = in.readLine()) != null) {
		            total.append(line);
		        }
		        
		        
		        responseString = total.toString();       
		        in.close();

		        //longInfo(TAG + "response", responseString);
/*		        doc = Jsoup.parse(responseString);
		        returnMessage = doc.select("#LabelMessage").text();
		        cookie = cookieManager.getCookieStore().getCookies().get(0).toString();
		        Log.i(TAG, "End cookie: " + cookie);
		        Elements errorLabel = doc.select("#LabelError");*/

                cookie = cookieManager.getCookieStore().getCookies().get(0).toString();
                Timber.v("cookie: " + cookie);

		        if(!responseString.contains("LabelError")){
                    Timber.v("Error Label Not Present, calling InteractionSuccess with the following info:");
		        	stateStart = responseString.indexOf("id=\"ContentPlaceHolder1_LabelMessage\"");
		        	stateStart = responseString.indexOf("<p>", stateStart);
		        	stateEnd = responseString.indexOf("</p>", stateStart+1);
		        	returnMessage = responseString.substring(stateStart+6, stateEnd).replace("<br>", "\n");
                    Timber.v("returnMessage = "+ returnMessage);
		        	return "success";
		        	
		        	/*doc = Jsoup.parse(responseString);
		        	
		        	returnMessage = doc.select("#LabelMessage").text();
		        	return "success";*/
		        }
		        else{
                    Timber.v("Error Label Present, returning error:");
		        	stateStart = responseString.indexOf("LabelError\"");
		        	stateStart = responseString.indexOf(" size=\"3\">", stateStart);
		        	stateEnd = responseString.indexOf("</font>", stateStart+1);
		        	returnMessage = responseString.substring(stateStart+10, stateEnd);
		        	if(returnMessage.isEmpty()){
		        		returnMessage = "Something went wrong, try again";
		        	}
                    Timber.v("returnMessage = "+ returnMessage);
		        	return "ah, crap";

		        }
				
			}catch(FileNotFoundException e){
				Log.i(TAG, "FileNotFound");
				e.printStackTrace();
				return "FileNotFound";
			}
			catch(Exception e){
				e.printStackTrace();
				return null;
			}

	}
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if(result == null){
			Toast.makeText(mActivity.getApplicationContext(), "Welp.. that didn't work..Try again",
					Toast.LENGTH_LONG).show();
		}
		else if(result.equalsIgnoreCase("FileNotFound")){
        	Toast.makeText(mActivity.getApplicationContext(), "Couldn't find that, try refreshing calendar",
					Toast.LENGTH_LONG).show();
		}
		else if(result.equalsIgnoreCase("success")){
			comm.InteractionSuccess(returnMessage, false);
		}
		else{
			comm.RoomLeaveFail(returnMessage);
		}
		progDialog.dismiss();
	}

}
