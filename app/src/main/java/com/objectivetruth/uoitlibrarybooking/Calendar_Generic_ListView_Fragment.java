package com.objectivetruth.uoitlibrarybooking;



import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Calendar_Generic_ListView_Fragment extends ListFragment {
	final static String TAG = "Calendar_Generic_ListView_Fragment";
	int pageNumberInt;
	String pageNumberStr;
	AsyncResponse comm;
	ListView listView;
	boolean isCreated = false;
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		isCreated = true;
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.calendar_listview, container, false);
		listView = (ListView) v;
		//View v = super.onCreateView(inflater, container, savedInstanceState); 
		
        //listView = (ListView)super.onCreateView(inflater, container, savedInstanceState); 
		String[] times = getActivity().getResources().getStringArray(R.array.times);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, times);
        setListAdapter(adapter);
        //listView.setVerticalScrollBarEnabled(false);
        
        return listView;
		
	}
	
	public Calendar_Generic_ListView_Fragment newInstance(int pageNumberInt, ActionBarActivity mActivity){

		Calendar_Generic_ListView_Fragment fragment = new Calendar_Generic_ListView_Fragment();
		fragment.pageNumberInt = pageNumberInt;
		fragment.pageNumberStr = String.valueOf(pageNumberInt);
		fragment.comm = (AsyncResponse) mActivity;
		return fragment;
		
	}

	public void changeTheScroll(int firstVisibleItem, float ycoord) {
		//Log.i(TAG, "I got here");
		//if(listView == null){Log.i(TAG, "GOOFED");}
		if(isCreated == true){
			//Log.i(TAG, String.valueOf(ycoord));
			listView.setSelectionFromTop(firstVisibleItem, listView.getChildAt(0).getTop());
			
		}
		
		
	}




}
