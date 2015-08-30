package com.objectivetruth.uoitlibrarybooking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.widget.Toast;
import timber.log.Timber;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.URL;

public class AsyncModifiedBookOnly extends AsyncTask<String, int[], String> {
	Activity mActivity;
	final String TAG = "AsyncModifiedBookOnly";
	CookieManager cookieManager;
	String currentViewState;
	String currentEventValidation;
    String currentViewStateGenerator;
	String date;
	String roomNumber;
	ProgressDialog progDialog;
	AsyncResponse comm;
	AsyncModifiedBookOnly mAsyncModifiedBookOnly;
	
	public AsyncModifiedBookOnly(Activity mActivity, CookieManager cookieManager) {
		this.mActivity = mActivity;
		this.cookieManager = cookieManager;
		comm = (AsyncResponse) mActivity;
	}

	@Override
	protected String doInBackground(String... input) {
        Timber.i("========================================");
        Timber.i("AsyncModifiedBookOnly Called, executing...");
		try{
			URL siteUrl2 = new URL("https://rooms.library.dc-uoit.ca/dc_studyrooms/" + input[0]);
			CookieManager.setDefault(cookieManager);
	        //Log.i(TAG, "formatted string " + formatted);
	        HttpsURLConnection conn2 = (HttpsURLConnection) siteUrl2.openConnection();
	        //conn2.setInstanceFollowRedirects(true);
			conn2.setRequestMethod("GET");
	        
	        conn2.setRequestProperty("Referer", "https://rooms.library.dc-uoit.ca/dc_studyrooms/joinorleave.aspx");
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
	        //longInfo(TAG + "response", responseString); 
	        //doc = Jsoup.parse(responseString);
	        //date = doc.select("#TextBoxStartDateTime").attr("value");
	        //roomNumber = doc.select("#TextBoxRoomNo").attr("value");
	        		        
	        int stateStart = responseString.indexOf("__VIEWSTATE\" value=");
	        int stateEnd = responseString.indexOf("/>", stateStart);
	        currentViewState = responseString.substring(stateStart+20, stateEnd-2);
            Timber.v("currentViewState =" + currentViewState);

	        stateStart = responseString.indexOf("__EVENTVALIDATION\" value=");
	        stateEnd = responseString.indexOf("/>", stateStart);
	        currentEventValidation = responseString.substring(stateStart+26, stateEnd-2);
            Timber.v("currentEventValidation =" + currentEventValidation);

            stateStart = responseString.indexOf("__VIEWSTATEGENERATOR\" value=");
            stateEnd = responseString.indexOf("/>", stateStart);
            currentViewStateGenerator= responseString.substring(stateStart+29, stateEnd-2);
            Timber.v("currentViewStateGenerator =" + currentViewStateGenerator);

	        //Log.i(TAG, responseString.split("TextBoxStartDateTime")[1].split("\"")[4]);
	        date = responseString.split("TextBoxStartDateTime")[1].split("\"")[4];
	        Timber.v("date = " + date);
            

            stateStart = input[0].indexOf("room=");
            stateEnd = input[0].indexOf("&", stateStart);
            roomNumber = input[0].substring(stateStart+5, stateEnd);
            Timber.v("roomNumber = " + roomNumber);

		}catch(Exception e){
			e.printStackTrace();
		}
		
		return "s-book";
		
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if(result == null){
			Toast.makeText(mActivity.getApplicationContext(), "Welp.. something didn't work quite right..",
					Toast.LENGTH_LONG).show();
            Timber.i("AsyncModifiedBookOnly Exited WITH Errors");
		}
		else if(result.equalsIgnoreCase("s-book")){
			comm.LaunchRoomInteraction(cookieManager, roomNumber, date, currentViewState, currentEventValidation, -1, -1, -1, currentViewStateGenerator);
			Toast.makeText(mActivity.getApplicationContext(), "Room setup done",
					Toast.LENGTH_LONG).show();
			
/*			Tracker t = ((UOITLibraryBookingApp) mActivity.getApplication()).getTracker();
			t.send(new HitBuilders.EventBuilder()
			.setCategory("CalendarInteraction")
			.setAction("Booking - Modified")
			.build()
			);*/
			progDialog.dismiss();
            Timber.i("AsyncModifiedBookOnly Exited WITHOUT Errors");
		}
	}
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
				if(mAsyncModifiedBookOnly.cancel(true)){
					//Log.i(TAG, TAG + " was Canceled Successfully");
				}
				
			}
        	
        	
        });
        progDialog.show();
		
	}


}