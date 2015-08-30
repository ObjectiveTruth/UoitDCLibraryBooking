package com.objectivetruth.uoitlibrarybooking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class RoomInfoRefresher extends AsyncTask<String[], int[], ArrayList<String[]>> {
	final static String TAG = "RoomInfoRefresher";
	ProgressDialog progDialog;
	Activity mActivity;
	RoomInfoRefresher mRoomInfoRefresher;
	//CommunicatorLoginAsync comm;
	
	public RoomInfoRefresher(Activity mActivity){
		this.mActivity = mActivity;
		mRoomInfoRefresher = this;
		
	}
	
	@Override
	protected ArrayList<String[]> doInBackground(String[]... params) {
		String[] ROOMSMASTERLIST = params[0];
		ArrayList<String[]> result = new ArrayList<String[]>();
		String[] perRoomArr;
		Bitmap bitmap;
		String imageEncoded = null;
        HttpsURLConnection conn;
		try{
			for(int i = 0; i < ROOMSMASTERLIST.length; i++){
                InputStream is;
                BufferedReader in;
				perRoomArr = new String[7];
                StringBuilder total;
                String line;
                String responseString;
                URL siteUrl = new URL("https://rooms.library.dc-uoit.ca/studyrooms/room.aspx?room=" + ROOMSMASTERLIST[i]);
                conn = (HttpsURLConnection) siteUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setUseCaches(false);
                conn.setConnectTimeout(800);
                conn.setDoInput(true);

                is = conn.getInputStream();

                in = new BufferedReader(new InputStreamReader(is));
                total = new StringBuilder(is.available());

                while ((line = in.readLine()) != null) {
                    total.append(line);
                }
                responseString = total.toString();

                URL newurl = new URL("https://rooms.library.dc-uoit.ca/studyrooms/images/" + ROOMSMASTERLIST[i] +".jpg");
                bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());

                String FILENAME = ROOMSMASTERLIST[i] + ".jpg";

                result.add(perRoomArr);
		            
					
			}
			Log.i(TAG, "Update Complete");
			return result;
		}catch(Exception e){
			Log.i("RefreshTableAndImages", e.toString());
			return null;
		}
	}

	@Override
	protected void onPreExecute() {
        super.onPreExecute();
    	Toast.makeText(mActivity, "Starting First Time Server Sync...",
				Toast.LENGTH_LONG).show();
	
        progDialog = new ProgressDialog(mActivity);
        progDialog.setMessage("Downloading...");
        progDialog.setIndeterminate(false);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setCancelable(true);
        progDialog.setOnCancelListener(new OnCancelListener(){

			@Override
			public void onCancel(DialogInterface diag) {
				if(mRoomInfoRefresher.cancel(true)){
					Log.i(TAG, TAG + " was Canceled Successfully");
				}
				
			}
        	
        	
        });
        progDialog.show();
	}
	
	@Override
	protected void onPostExecute(ArrayList<String[]> result) {
		super.onPostExecute(result);
		ActivityRoomInfo.mdbHelper.UpdateRoomInfoDatabase(result);
		ActivityRoomInfo.defaultPrefsEditor.putBoolean("firstLaunchRooms", false);
		ActivityRoomInfo.defaultPrefsEditor.commit();
        progDialog.dismiss();
        Toast.makeText(mActivity, "Done",
				Toast.LENGTH_SHORT).show();

		
	}
	
	
}
