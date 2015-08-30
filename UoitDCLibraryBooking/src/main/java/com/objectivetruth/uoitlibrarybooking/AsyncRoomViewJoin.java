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

public class AsyncRoomViewJoin extends AsyncTask<String[], int[], String> {
	CookieManager cookieManager;
	String[] dataInput;
	final String TAG = "AsyncRoomViewJoin";
	Activity mActivity;
	ProgressDialog progDialog;
	CommunicatorRoomInteractions comm;
	String returnMessage;
	AsyncRoomViewJoin mAsyncRoomViewJoin;

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
        progDialog = new ProgressDialog(mActivity);
        progDialog.setMessage("Joining a Reservation");
        progDialog.setIndeterminate(false);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setCancelable(true);
        progDialog.setOnCancelListener(new OnCancelListener(){

			@Override
			public void onCancel(DialogInterface diag) {
				if(mAsyncRoomViewJoin.cancel(true)){
					Log.i(TAG, TAG + " was Canceled Successfully");
				}
				
			}
        	
        	
        });
        progDialog.show();
		super.onPreExecute();
	}
	public AsyncRoomViewJoin(CookieManager cookieManager, Activity mActivity){
		mAsyncRoomViewJoin = this;
		this.comm = (CommunicatorRoomInteractions) mActivity;
		this.cookieManager = cookieManager;
		this.mActivity = mActivity;
	}
	@Override
	protected String doInBackground(String[]... input) {
        Timber.i("==================================");
        Timber.i("AsyncRoomViewJoin Called. Executing...");
			try{
				String[] inputData = input[0];
				String viewState = inputData[0];
				String eventValidation = inputData[1];
				String TextBoxPassword = inputData[2];
				String TextBoxID = inputData[3];
				String ButtonJoinGroup = inputData[4];
                String viewStateGenerator = inputData[5];
                Timber.v("InputArray has " + inputData.length + " elements, Excepted 6");

				
		    	CookieHandler.setDefault(cookieManager);
		        String cookie = cookieManager.getCookieStore().getCookies().get(0).toString();
		        Timber.v("cookie: " + cookie);
		        URL siteUrl = new URL("https://rooms.library.dc-uoit.ca/dc_studyrooms/viewleaveorjoin.aspx");
		        HttpsURLConnection conn = (HttpsURLConnection) siteUrl.openConnection();
		        conn.setInstanceFollowRedirects(true);
				conn.setRequestMethod("POST");
				conn.setUseCaches(false);
				conn.setConnectTimeout(8000);
		        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		        conn.setRequestProperty("Referer", "https://rooms.library.dc-uoit.ca/dc_studyrooms/viewleaveorjoin.aspx");
		        conn.setDoOutput(true);
		        conn.setDoInput(true);
		        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		       	        
		        
				
		        String content = "__VIEWSTATE=" + URLEncoder.encode(viewState, "UTF-8")
		        		+ "&__EVENTVALIDATION=" + URLEncoder.encode(eventValidation, "UTF-8")
                        + "&__VIEWSTATEGENERATOR=" + URLEncoder.encode(viewStateGenerator, "UTF-8")
                        + "&ctl00$ContentPlaceHolder1$TextBoxPassword=" + URLEncoder.encode(TextBoxPassword, "UTF-8")
		        		+ "&ctl00$ContentPlaceHolder1$TextBoxStudentID=" + TextBoxID
		             	+ "&ctl00$ContentPlaceHolder1$ButtonJoinGroup=" + ButtonJoinGroup;
		        
		        Timber.v("Outgoing String = " + content);
		        out.writeBytes(content);
		        out.flush();
		        out.close();
		        InputStream is = conn.getInputStream();
		        BufferedReader in = new BufferedReader(new InputStreamReader(is));
		        StringBuilder total = new StringBuilder(is.available());
		        String line;
		        while ((line = in.readLine()) != null) {
		            total.append(line);
		        }
		        
		        
		        String responseString = total.toString();
		        in.close();
                Timber.v(responseString);
		        //longInfo(TAG + "response", responseString);

		        if(!responseString.contains("LabelError")){
                    Timber.v("Error Label Not Present, calling InteractionSuccess with the following info:");
		        	int stateStart = responseString.indexOf("id=\"ContentPlaceHolder1_LabelMessage\"");
		        	stateStart = responseString.indexOf("<p>", stateStart);
		        	int stateEnd = responseString.indexOf("</p>", stateStart+1);
		        	returnMessage = responseString.substring(stateStart+6, stateEnd).replace("<br>", "\n");
                    Timber.v("returnMessage = " + returnMessage);
		        	return "success";
		        	
		        	/*doc = Jsoup.parse(responseString);
		        	
		        	returnMessage = doc.select("#LabelMessage").text();
		        	return "success";*/
		        }
		        else{
                    Timber.v("Error Label Present, returning error:");
		        	int stateStart = responseString.indexOf("LabelError\"");
		        	stateStart = responseString.indexOf(" size=\"3\">", stateStart);
		        	int stateEnd = responseString.indexOf("</font>", stateStart+1);
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
			}
			return "success";
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
			comm.InteractionSuccess(returnMessage, true);
		}
		else{
			comm.ViewLeaveOrJoinFail(returnMessage);
		}
		progDialog.dismiss();
	}

}
