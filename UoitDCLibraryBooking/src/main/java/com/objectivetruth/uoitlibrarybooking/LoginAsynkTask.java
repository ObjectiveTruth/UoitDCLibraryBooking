package com.objectivetruth.uoitlibrarybooking;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.squareup.okhttp.*;
import timber.log.Timber;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import static com.objectivetruth.uoitlibrarybooking.MainActivity.SHARED_PREF_KEY_PASSWORD;
import static com.objectivetruth.uoitlibrarybooking.MainActivity.SHARED_PREF_KEY_USERNAME;
import static com.objectivetruth.uoitlibrarybooking.constants.SHARED_PREFERENCES_KEYS.SHARED_PREF_INSTITUTION;
import static com.objectivetruth.uoitlibrarybooking.constants.SHARED_PREFERENCES_KEYS.SHARED_PREF_KEY_BOOKINGS_LEFT;

public class LoginAsynkTask extends AsyncTask<String[], int[], ArrayList<String[]>> {
	final String TAG = "LoginAsyncTask";
	//ProgressDialog progDialog;
	Context mContext;
	CommunicatorLoginAsync comm;
	String errorMessage = "";
    String password;
    String studentID;
    String institution;
    int options;
    long loginAsynkTaskStartTime = 0L;
	//LoginAsynkTask mLoginAsynkTask;
	


	public LoginAsynkTask(Context mContext){
        this.mContext = mContext;
		//mLoginAsynkTask = this;
/*		this.mActivity = mActivity;
		this.comm = (CommunicatorLoginAsync) mActivity;*/
	}
	
	@Override
	protected void onPreExecute() {
        super.onPreExecute();
        Timber.i("LoginAsynkTask Called");
        loginAsynkTaskStartTime = System.currentTimeMillis();
/*        progDialog = new ProgressDialog(mActivity);
        progDialog.setMessage("Loading...");
        progDialog.setIndeterminate(false);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setCancelable(true);
        progDialog.setOnCancelListener(new OnCancelListener(){

			@Override
			public void onCancel(DialogInterface diag) {
				if(mLoginAsynkTask.cancel(true)){
					Log.i(TAG, TAG + " was Canceled Successfully");
				}
				
			}
        	
        	
        });
        progDialog.show();*/
		
		
	}	
	
	@Override
	protected void onPostExecute(ArrayList<String[]> result) {
		super.onPostExecute(result);
        //Success
        if(errorMessage.isEmpty() && result !=null){
            MainActivity.mdbHelper.UpdateMyBookingsDatabase(result);
            SharedPreferences.Editor sharedPreferencesEditor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
            int bookingsUsed = 0;
            for(String[] arr : result){
                bookingsUsed = bookingsUsed + (arr.length /3);
            }
            int bookingsLeft = MainActivity.MAX_BOOKINGS_ALLOWED - bookingsUsed;
            Timber.i("New update shows that " + bookingsUsed + " bookings have been used out of " + MainActivity.MAX_BOOKINGS_ALLOWED);
            Timber.v("Storing to sharedPrefs. bookingsLeft: " + bookingsLeft + ", studentID, password, institution: " + institution);
            sharedPreferencesEditor.putInt(SHARED_PREF_KEY_BOOKINGS_LEFT, bookingsLeft);
            sharedPreferencesEditor.putString(SHARED_PREF_KEY_USERNAME, studentID);
            sharedPreferencesEditor.putString(SHARED_PREF_KEY_PASSWORD, password);
            sharedPreferencesEditor.putString(SHARED_PREF_INSTITUTION, institution);
            sharedPreferencesEditor.commit();
            Tracker t = ((UOITLibraryBookingApp)mContext.getApplicationContext()).getTracker();
            t.send(new HitBuilders.EventBuilder()
                    .setCategory("MyAccount")
                    .setAction("Bookings Left")
                    .setValue(bookingsLeft)
                    .build());
            if(options == DiaFragMyAccount.MY_ACCOUNT_USER_INITIATED){
                long loginTaskDuration = loginAsynkTaskStartTime - System.currentTimeMillis();

                t.send(new HitBuilders.EventBuilder()
                        .setCategory("MyAccount")
                        .setAction("Login Success - Initiated By User")
                        .setLabel("AsyncTask Duration")
                        .setValue(loginTaskDuration)
                        .build());
            }
            Timber.i("LoginAsynkTask finished with NO Errors");
        }
        //FAIL
        else{
            //Network Error
            if(errorMessage.startsWith("Er24")){
                Timber.v("Network ERROR with LoginAsynk Task");
            }
            //User Error
            else{
                Timber.v("User made an ERRORs with inputs in LoginAsynk Task");
                SharedPreferences.Editor sharedPreferencesEditor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
                sharedPreferencesEditor.remove(SHARED_PREF_KEY_USERNAME);
                sharedPreferencesEditor.remove(SHARED_PREF_KEY_PASSWORD);
                sharedPreferencesEditor.remove(SHARED_PREF_KEY_BOOKINGS_LEFT);
                sharedPreferencesEditor.remove(SHARED_PREF_INSTITUTION);
                sharedPreferencesEditor.commit();
            }
            Timber.i("LoginAsynkTask finished WITH Errors");
        }

        MainActivity.mLoginAsyncTask = null;
        OttoBusSingleton.getInstance().post(new MyAccountLoginResultEvent(errorMessage, result));

/*		if(result != null){

		}
		else{
            OttoBusSingleton.getInstance().post(new MyAccountLoginResultEvent(errorMessage, result));
		}*/
//        progDialog.dismiss();

		
	}
	
	@Override
	protected ArrayList<String[]> doInBackground(String[]... arg0) {
		ArrayList<String[]> Results = new ArrayList<String[]>();
		//Results holds all the data in the form of 0 = Completed Bookings, 1 is Incomplete
		//Bokings and 2 is Past Bookings. It only Holds the Data, not the headings of each
		//table, it reads from left to right
		
		//Document doc;
	
        try {			
        		password = arg0[0][1];
        		studentID = arg0[0][0];
                institution = arg0[0][2];
				String __viewStateMain;
				String __eventValidationMain;
                String __viewStateGenerator;
				

		        OkHttpClient httpclient = new OkHttpClient();
                Request request = new Request.Builder()
                    .url("https://rooms.library.dc-uoit.ca/dc_studyrooms/myreservations.aspx")
                    .build();
                Response response = httpclient.newCall(request).execute();
		        String responseString = response.body().string();

                Timber.v("Response from https://rooms.library.dc-uoit.ca/dc_studyrooms/myreservations.aspx: " + responseString);
                int stateStart = responseString.indexOf("__VIEWSTATE\" value=");
                int stateEnd = responseString.indexOf("/>", stateStart);
                __viewStateMain = responseString.substring(stateStart+20, stateEnd-2);
                Timber.v("VIEWSTATEMAIN =" + __viewStateMain);

                stateStart = responseString.indexOf("__EVENTVALIDATION\" value=");
                stateEnd = responseString.indexOf("/>", stateStart);
                __eventValidationMain = responseString.substring(stateStart+26, stateEnd-2);
                Timber.v("EVENTVALIDATION =" + __eventValidationMain);

                stateStart = responseString.indexOf("__VIEWSTATEGENERATOR\" value=");
                stateEnd = responseString.indexOf("/>", stateStart);
                __viewStateGenerator = responseString.substring(stateStart+29, stateEnd-2);
                Timber.v("VIEWSTATEGENERATOR =" + responseString.substring(stateStart+29, stateEnd-2));

                String content =  "__VIEWSTATE=" + URLEncoder.encode(__viewStateMain, "UTF-8")
                    + "&__EVENTVALIDATION=" + URLEncoder.encode(__eventValidationMain, "UTF-8")
                    + "&__VIEWSTATEGENERATOR=" + URLEncoder.encode(__viewStateGenerator, "UTF-8")
                    + "&ctl00$ContentPlaceHolder1$TextBoxPassword=" + URLEncoder.encode(password, "UTF-8")
                    + "&ctl00$ContentPlaceHolder1$TextBoxID=" + URLEncoder.encode(studentID, "UTF-8")
                    + "&ctl00$ContentPlaceHolder1$ButtonListBookings=" + URLEncoder.encode("My Bookings", "UTF-8");

                MediaType formEncode = MediaType.parse("application/x-www-form-urlencoded");
                RequestBody body = RequestBody.create(formEncode, content);

                request = new Request.Builder()
                        .url("https://rooms.library.dc-uoit.ca/dc_studyrooms/myreservations.aspx")
                        .post(body)
                        .build();

                response = httpclient.newCall(request).execute();
                responseString = response.body().string();
                Timber.v("Response part 2 from https://rooms.library.dc-uoit.ca/dc_studyrooms/myreservations.aspx: " + responseString);
		        if(!responseString.contains("id=\"ContentPlaceHolder1_LabelError\"")){
                    Timber.v("No Errors on LoginAsynkTask");

					String[] completedBookingsArr;
					String[] incompleteBookingsArr;
					String[] pastBookingsArr;
					String timeRange;
					
					//TABLE COMPLETE
					stateStart = responseString.indexOf("id=\"ContentPlaceHolder1_TableComplete\"");
					stateEnd = responseString.indexOf("</table>", stateStart);
					String stateSubString = responseString.substring(stateStart, stateEnd);
					int lastFoundAt = 0;
					if(stateSubString.indexOf("<td", lastFoundAt) < 0){
						completedBookingsArr = new String[0];
                        Timber.v("TableComplete is empty");
					}
					else{
						lastFoundAt = stateSubString.indexOf("<td", lastFoundAt);
						lastFoundAt = stateSubString.indexOf("<td", lastFoundAt+1);
						lastFoundAt = stateSubString.indexOf("<td", lastFoundAt+1);
						lastFoundAt = stateSubString.indexOf("<td", lastFoundAt+1);
						ArrayList<String> completedBookingTempArray = new ArrayList<String>();
						while(stateSubString.indexOf("<td>", lastFoundAt+1) > 0){
							lastFoundAt = stateSubString.indexOf("<td", lastFoundAt+1);
							stateStart = stateSubString.indexOf(">", lastFoundAt + 5);
							stateEnd = stateSubString.indexOf("</font>", stateStart +1);
							completedBookingTempArray.add(stateSubString.substring(stateStart+1, stateEnd));	
				
							
						}
						completedBookingsArr = completedBookingTempArray.toArray(new String[completedBookingTempArray.size()]);
                        Timber.v("===");
                        Timber.v("CompletedTable Contents:");

                        for(String completedString : completedBookingsArr){
                            Timber.v(completedString);
                        }
                        Timber.v("===");
					}



					//TABLE INCOMPLETE
					stateStart = responseString.indexOf("id=\"ContentPlaceHolder1_TableInComplete\"");
					stateEnd = responseString.indexOf("</table>", stateStart);
					stateSubString = responseString.substring(stateStart, stateEnd);
					lastFoundAt = 0;
				
					if(stateSubString.indexOf("<td", lastFoundAt) < 0){
						incompleteBookingsArr = new String[0];
                        Timber.v("TableInComplete is empty");
					}
					else{
						lastFoundAt = stateSubString.indexOf("<td", lastFoundAt);
						lastFoundAt = stateSubString.indexOf("<td", lastFoundAt+1);
						lastFoundAt = stateSubString.indexOf("<td", lastFoundAt+1);
						lastFoundAt = stateSubString.indexOf("<td", lastFoundAt+1);
						ArrayList<String> incompletedBookingTempArray = new ArrayList<String>();
						while(stateSubString.indexOf("<td>", lastFoundAt+1) > 0){
							lastFoundAt = stateSubString.indexOf("<td", lastFoundAt+1);
							stateStart = stateSubString.indexOf(">", lastFoundAt + 5);
							stateEnd = stateSubString.indexOf("</font>", stateStart +1);
							incompletedBookingTempArray.add(stateSubString.substring(stateStart+1, stateEnd));	
				
							
						}
						incompleteBookingsArr = incompletedBookingTempArray.toArray(new String[incompletedBookingTempArray.size()]);
                        Timber.v("===");
                        Timber.v("IncompleteTable Contents:");

                        for(String incompleteString : incompleteBookingsArr){
                            Timber.v(incompleteString);
                        }
                        Timber.v("===");
					}
					
					//TABLE PAST
					stateStart = responseString.indexOf("id=\"ContentPlaceHolder1_TablePast\"");
					stateEnd = responseString.indexOf("</table>", stateStart);
					stateSubString = responseString.substring(stateStart, stateEnd);
					lastFoundAt = 0;
					if(stateSubString.indexOf("<td", lastFoundAt) < 0){
						pastBookingsArr = new String[0];
                        Timber.v("TablePast is empty");
					}
					else{
						lastFoundAt = stateSubString.indexOf("<td", lastFoundAt);
						lastFoundAt = stateSubString.indexOf("<td", lastFoundAt+1);
						lastFoundAt = stateSubString.indexOf("<td", lastFoundAt+1);
						ArrayList<String> pastBookingTempArray = new ArrayList<String>();
						while(stateSubString.indexOf("<td>", lastFoundAt+1) > 0){
							lastFoundAt = stateSubString.indexOf("<td", lastFoundAt+1);
							stateStart = stateSubString.indexOf(">", lastFoundAt + 5);
							stateEnd = stateSubString.indexOf("</font>", stateStart +1);
							pastBookingTempArray.add(stateSubString.substring(stateStart+1, stateEnd));	

							
						}
						pastBookingsArr = pastBookingTempArray.toArray(new String[pastBookingTempArray.size()]);
                        Timber.v("===");
                        Timber.v("PastTable Contents:");
                        for(String pastString : pastBookingsArr){
                            Timber.v(pastString);
                        }
                        Timber.v("===");
					}

					
					Results.add(incompleteBookingsArr);
					Results.add(completedBookingsArr);
					Results.add(pastBookingsArr);

					return Results;
		        	
		        	/*doc = Jsoup.parse(responseString);
		        	
		        	returnMessage = doc.select("#LabelMessage").text();
		        	return "success";*/
		        }
		        else{
                    Timber.v("Received Error from LoginAsynkTask, Outputting Error");
		        	stateStart = responseString.indexOf("id=\"ContentPlaceHolder1_LabelError\"");
		        	stateStart = responseString.indexOf(" size=\"3\">", stateStart);
		        	stateEnd = responseString.indexOf("</font>", stateStart+1);
		        	errorMessage = responseString.substring(stateStart+10, stateEnd);
		        	if(errorMessage.isEmpty()){
		        		errorMessage = "Something went wrong, try again";
		        	}
		        	return null;

		        }

													
				        
        	}
            catch(IOException e){
                String errorDescript = "Something went wrong with the login values or Internet Connection";
                Timber.e(e, errorDescript);
                errorMessage = "Er24 : Internet Connection Issues: Try Again";

                return null;
            }

            catch(Exception e){
                Timber.e(e, "Er24: Might be something up with the parsing in MyAccountInfo, LoginAsynkTask, might be serious");
            	errorMessage = "Incorrect format for server response, try again";
            	return null;
            }
	}


	public static void longInfo(String tag, String str) {
	    if(str.length() > 4000) {
	        Log.i(tag, str.substring(0, 4000));
	        Log.i(tag, str.substring(4000));
	    } else
	        Log.i(tag, str);
	}

}
