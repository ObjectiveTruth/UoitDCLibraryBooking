package com.objectivetruth.uoitlibrarybooking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.widget.Toast;
import timber.log.Timber;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLEncoder;

public class AsyncBridgeJoinOrLeaveToCreate extends
		AsyncTask<Void, int[], String> {
	String TAG = "AsyncBridgeJoinOrLeaveToCreate";
	ProgressDialog progDialog;
	CommunicatorRoomInteractions comm;
	String viewState;
	String eventValidation;
	Activity mActivity;
	AsyncBridgeJoinOrLeaveToCreate mAsyncBridgeJoinOrLeaveToCreate;
    String viewStateGenerator;
	CookieManager cookieManager;
	
	
	
	
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
				if(mAsyncBridgeJoinOrLeaveToCreate.cancel(true)){
					//Log.i(TAG, TAG + " was Canceled Successfully");
				}
				
			}
        	
        	
        });
        progDialog.show();
		
	}
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		progDialog.dismiss();
		if(result != null){
			comm.createFromJoinOrLeave(cookieManager, result);	
		}
		else{
			Toast.makeText(mActivity, "Network Error: Try again",
					Toast.LENGTH_LONG).show();
		}
		
		

		
	}
	
	public AsyncBridgeJoinOrLeaveToCreate(CookieManager cookieManager, Activity mActivity, String viewState, String eventValidation, String viewStateGenerator){
		this.viewState = viewState;
        this.viewStateGenerator = viewStateGenerator;
		this.eventValidation = eventValidation;
		mAsyncBridgeJoinOrLeaveToCreate = this;
		this.cookieManager = cookieManager;
		this.mActivity = mActivity;
		comm = (CommunicatorRoomInteractions) mActivity;
		
	}

	@Override
	protected String doInBackground(Void... arg0) {
		String firstRedirect = null;
        Timber.i("===================================================");
		Timber.i("AsyncBridgeJoinOrLeaveToCreate Called, Executing...");

        
		try {
		URL siteUrl;
		siteUrl = new URL("https://rooms.library.dc-uoit.ca/dc_studyrooms/joinorleave.aspx");
		CookieHandler.setDefault(cookieManager);
        HttpsURLConnection conn = (HttpsURLConnection) siteUrl.openConnection();
        conn.setInstanceFollowRedirects( false );
		conn.setRequestMethod("POST");
		conn.setUseCaches(false);
		conn.setConnectTimeout(8000);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Referer", "https://rooms.library.dc-uoit.ca/dc_studyrooms/joinorleave.aspx");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
       	        
        

        String content = "__VIEWSTATE=" +  URLEncoder.encode(viewState, "UTF-8")
        		 + "&__EVENTVALIDATION=" + URLEncoder.encode(eventValidation, "UTF-8")
                + "&__VIEWSTATEGENERATOR=" + URLEncoder.encode(viewStateGenerator, "UTF-8")
        		 + "&ctl00$ContentPlaceHolder1$RadioButtonListJoinOrCreateGroup=" + "invalid_code"
        		 + "&ctl00$ContentPlaceHolder1$ButtonJoinOrCreate=" + URLEncoder.encode("Create or Join a Group", "UTF-8");
        
        Timber.v("Outgoing String: " + content);
        out.writeBytes(content);
        out.flush();
        out.close();
/*
            InputStream is = conn.getInputStream();
            BufferedReader in2 = new BufferedReader(new InputStreamReader(is));
            StringBuilder total = new StringBuilder(is.available());
            String line;
            while ((line = in2.readLine()) != null) {
                total.append(line);
            }
            String responseString = total.toString();
            in2.close();
            Timber.v(responseString);*/
        //Log.i(TAG + "bigString", content);
/*        out.writeBytes(content);
        out.flush();
        out.close();*/

        firstRedirect = conn.getHeaderField("Location").replace(" ", "%20");
        Timber.v("NewLinkString: " + firstRedirect);

        
        
        Timber.i("Exited Without Errors");
		} catch (Exception e) {
			e.printStackTrace();
            Timber.i("Exited With Errors");
			return null;
			
		}
		
		
		return firstRedirect;
	}
}
