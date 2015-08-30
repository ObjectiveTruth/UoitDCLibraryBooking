package com.objectivetruth.uoitlibrarybooking;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class Calendar_Generic_GridView_Fragment extends Fragment {
	int pageNumberInt;
	String pageNumberStr;
	final String TAG = "Calendar_Generic_GridView_Fragment";
	GridView gridView = null;
	String[] arrayToUse = null;
	String[] correspondingArr = null;
	AsyncResponse comm;
	ArrayList<CalendarMonth> calendarCache = null;
	final String[] numbers = new String[] {
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
			"YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY", "YYYYYYYY",
	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		gridView = (GridView) inflater.inflate(R.layout.calendar_gridview, container, false);
		//ArrayList<CalendarMonth> calendarCache = RequestHandler.getCalendarCache();
		
		//if(calendarCache == null){

		try{
			String day = "day" + String.valueOf(pageNumberInt + 1);
			SQLiteDatabase db = MainActivity.mdbHelper.getReadableDatabase();
			Cursor c = db.query(MainActivity.mdbHelper.CALENDAR_TABLE_NAME, new String[]{day, day+"source"}, null, null, null, null, null);
			if(c.moveToFirst()){
				String[] calendarData = new String[c.getCount()-4];
				correspondingArr = new String[c.getCount()-4];
				//Log.i(TAG, "count = " + String.valueOf(c.getCount()-4));
				int i = 0;
				c.moveToPosition(3);
				while(c.moveToNext()){
					//Log.i(TAG, c.getString(c.getColumnIndex(day)));
					calendarData[i] = c.getString(c.getColumnIndex(day));
					correspondingArr[i] = c.getString(c.getColumnIndex(day+"source"));
					i++;
				}
				//Log.i(TAG, "count = " + String.valueOf(i-1));
				arrayToUse = calendarData;
			}
			else{
				arrayToUse = numbers;
			}
			c.close();
		}catch(Exception e){
			e.printStackTrace();
			arrayToUse = numbers;
		}
		ArrayAdapter<String> adapter = new CalendarArrayAdapter(inflater.getContext(),
				android.R.layout.simple_list_item_1, arrayToUse);
		
		gridView.setAdapter(adapter);
		gridView.setNumColumns(11);


		return gridView;
	}
	
/*	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
    	gridView.setOnScrollListener(new OnScrollListener() {
		    @Override
		    public void onScrollStateChanged(AbsListView view, int scrollState) {

		        }
		    
		    @Override
		    public void onScroll(AbsListView view, int firstVisibleItem, 
		    		int visibleItemCount, int totalItemCount){
		    	//Log.i(TAG, String.valueOf(firstVisibleItem/10));
		    	//Log.i(TAG, String.valueOf(gridView.getY()));
		    	comm.ChangeScrollPosition(firstVisibleItem/10, pageNumberInt, (float) 0.00);
		    	
			   
		    	
		    }
		});
    	gridView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(!correspondingArr[position].isEmpty()){

						new AsyncRoomInteraction(getActivity(), pageNumberInt).execute(correspondingArr[position]);
						
				}else{
					Toast.makeText(getActivity().getApplicationContext(), "Can't click that", 
							   Toast.LENGTH_SHORT).show();
				}
				

				
			}
    		
    	});
		//super.onViewCreated(view, savedInstanceState);
	}*/

/*	public void refreshGridView(ArrayList<CalendarMonth> calendarCache){
		
		
		arrayToUse = calendarCache.get(pageNumberInt).data;
		correspondingArr = calendarCache.get(pageNumberInt).source;
		ArrayAdapter<String> adapter = new CalendarArrayAdapter(getActivity().getBaseContext(),
				android.R.layout.simple_list_item_1, arrayToUse){
			
		};
		gridView.setAdapter(adapter);
		Log.i(TAG, "GridView " + pageNumberStr + " Refreshing Successful!!");
		
		

	}*/
	
	public Calendar_Generic_GridView_Fragment newInstance(int pageNumberInt, ActionBarActivity mActivity ){
		Calendar_Generic_GridView_Fragment fragment = new Calendar_Generic_GridView_Fragment();
		fragment.pageNumberInt = pageNumberInt;
		fragment.pageNumberStr = String.valueOf(pageNumberInt);
		fragment.comm = (AsyncResponse) mActivity;
		
		return fragment;
	}
	public Calendar_Generic_GridView_Fragment newInstance(int pageNumberInt, ActionBarActivity mActivity, ArrayList<CalendarMonth> calendarCache ){
		this.calendarCache = calendarCache;
		Calendar_Generic_GridView_Fragment fragment = new Calendar_Generic_GridView_Fragment();
		fragment.pageNumberInt = pageNumberInt;
		fragment.pageNumberStr = String.valueOf(pageNumberInt);
		fragment.comm = (AsyncResponse) mActivity;
		
		return fragment;
	}
	
	public class CalendarArrayAdapter extends ArrayAdapter<String> {
		LayoutInflater mInflater;
		ViewHolder holder;
		String[] stringArr;
		int furthestClosedPosition;
		int furthestClosedRow; //0 is first row
		int NUMBEROFCOLUMNS = 11;
		
		public CalendarArrayAdapter(Context context, int resource,
				String[] stringArr) {
			super(context, resource, stringArr);
			mInflater = LayoutInflater.from(context);
			this.stringArr = stringArr;
			for(int i = 0; i < stringArr.length; i ++){
				if(stringArr[i].compareTo("Closed") == 0){
					furthestClosedPosition = i;
				}
			}
			furthestClosedRow = furthestClosedPosition / NUMBEROFCOLUMNS;

		}
		@Override
	    public boolean isEnabled(int position) {
			if(position <= furthestClosedPosition){
				return false;
			}
			else if(position <12){
				return false;
			}
			else if((position % 11) == 0){
				return false;
			}
	        return true;

	    }
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			boolean isReservationTitle = false;

			if(convertView == null){
				convertView = mInflater.inflate(R.layout.gridview_calendar_item, null);
				holder = new ViewHolder();

				holder.icon = (ImageView) convertView.findViewById(R.id.gridview_imageview);
				holder.text = (TextView) convertView.findViewById(R.id.gridview_text_view);
				convertView.setTag(holder);
			}
			else{
				holder = (ViewHolder) convertView.getTag();
				
			}
			if(position < 11){
				holder.text.setVisibility(View.VISIBLE);
				holder.icon.setVisibility(View.GONE);
				holder.text.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
				holder.text.setText(stringArr[position]);
				holder.text.setTextColor(getResources().getColor(android.R.color.white));
			}
			/*if(position == 0){
				holder.text.setVisibility(View.VISIBLE);
				holder.text.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
				holder.text.setText("");
				holder.icon.setVisibility(View.GONE);
			}*/
			else if(position % 11 == 0){
				holder.text.setVisibility(View.VISIBLE);
				holder.icon.setVisibility(View.GONE);
				holder.text.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
				holder.text.setText(stringArr[position]);
				holder.text.setTextColor(getResources().getColor(android.R.color.white));
			}
			
			else if(stringArr[position].compareTo("Closed")==0){
				holder.text.setVisibility(View.GONE);
				holder.icon.setVisibility(View.VISIBLE);
				holder.icon.setImageResource(R.drawable.ic_fileclose);
			}
			else if(stringArr[position].compareTo("Open")==0){
				holder.text.setVisibility(View.GONE);
				holder.icon.setVisibility(View.VISIBLE);
				holder.icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_open_sign));
				holder.icon.setBackgroundResource(android.R.color.background_light);
			}
			else if(stringArr[position].compareTo("Open(i)")==0){
				holder.text.setVisibility(View.GONE);
				holder.icon.setVisibility(View.VISIBLE);
				holder.icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_open_maybe));
				holder.icon.setBackgroundResource(android.R.color.background_light);
			}
			else if(stringArr[position].compareTo("\"") == 0){
				holder.icon.setVisibility(View.VISIBLE);
				holder.text.setVisibility(View.GONE);
				holder.icon.setImageResource(R.drawable.ic_lock_closed_small);
				holder.icon.setBackgroundResource(android.R.color.background_light);
			}
			else{
				isReservationTitle = true;
				holder.text.setBackgroundResource(android.R.color.background_light);
				holder.text.setVisibility(View.VISIBLE);
				holder.text.setText(stringArr[position]);
				holder.icon.setVisibility(View.VISIBLE);
				holder.icon.setImageResource(R.drawable.ic_lock_closed_small);
				holder.text.setTextColor(getResources().getColor(android.R.color.black));
				holder.icon.setBackgroundResource(android.R.color.transparent);
				
			}
			//if its in the past, turn it darkergray
			if(position <= furthestClosedPosition || (position / NUMBEROFCOLUMNS) == furthestClosedRow){
				if(isReservationTitle == false){
					holder.icon.setBackgroundResource(android.R.color.darker_gray);
					holder.text.setBackgroundResource(android.R.color.darker_gray);
				}
				else{
					holder.text.setBackgroundResource(android.R.color.darker_gray);
				}
				
			}
			
			return convertView;
		}

	}
	static class ViewHolder {            
		 TextView text;
		 ImageView icon;
		} 
}
	