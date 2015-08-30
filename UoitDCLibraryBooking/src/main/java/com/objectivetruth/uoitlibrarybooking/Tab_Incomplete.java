package com.objectivetruth.uoitlibrarybooking;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.otto.Subscribe;
import timber.log.Timber;

import java.util.ArrayList;

public class Tab_Incomplete extends Fragment {
	final String TAG = "Tab_Incomplete";
	GridView gridView;
	String[] arrayToUse = new String[0];
	SQLiteDatabase db;
    RelativeLayout noInfo;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        OttoBusSingleton.getInstance().register(this);
        try {
            updateArrayToUseFromDb();
        }catch(Exception e){
            Timber.e(e, "Coudln't get accountinfo from Database");
            arrayToUse = new String[0];
        }

        View rootView = inflater.inflate(R.layout.my_bookings_tab, container, false);
        gridView = (GridView)rootView.findViewById(R.id.my_bookings_gridview);
        noInfo = (RelativeLayout)rootView.findViewById(R.id.no_info);
		if(arrayToUse.length > 1){
			gridView.setVisibility(View.VISIBLE);
			noInfo.setVisibility(View.GONE);
		}
		else{
			gridView.setVisibility(View.GONE);
			noInfo.setVisibility(View.VISIBLE);
		}
    	ChangeColorGridAdapter adapter = new ChangeColorGridAdapter(getActivity(),
				arrayToUse);
    	gridView.setAdapter(adapter);
        return rootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

    @Subscribe
    public void LoginResults(MyAccountLoginResultEvent event) {
        if(!event.errorMessage.isEmpty() && event.result != null) {
            ArrayList<String> arrayToUseTemp = new ArrayList<String>();
            String[] arrayToUseTempTemp = event.result.get(0);
            for (int i = 0; i < arrayToUseTempTemp.length; i++) {
                arrayToUseTemp.add(arrayToUseTempTemp[i]);
            }

            int arrayToUseTempSize = arrayToUseTemp.size(); //This is set here, so that it doesn't change as the array gets smaller below
            for (int j = 0; j < arrayToUseTempSize; j++) {
                if (j > 0) {
                    if (((j + 1) % 4) == 0) {
                        arrayToUseTemp.set(j - 1, arrayToUseTemp.get(j - 1) + " - " + arrayToUseTemp.get(j));
                        arrayToUseTemp.remove(j);
                    }
                }

            }
            arrayToUse = arrayToUseTemp.toArray(new String[arrayToUseTemp.size()]);
            if (event.errorMessage.isEmpty() && event.result != null) {
                if (getActivity() != null) {
                    if (arrayToUse.length > 1) {
                        gridView.setVisibility(View.VISIBLE);
                        noInfo.setVisibility(View.GONE);
                    } else {
                        gridView.setVisibility(View.GONE);
                        noInfo.setVisibility(View.VISIBLE);
                    }
                    gridView.setAdapter(new ChangeColorGridAdapter(getActivity(), arrayToUse));
                }

            }
        }
    }

    private void updateArrayToUseFromDb(){
        db = MainActivity.mdbHelper.getReadableDatabase();
        Cursor c = db.query(MainActivity.mdbHelper.MY_BOOKINGS_TABLE_NAME, new String[]{MainActivity.mdbHelper.PENDING_BOOKINGS}, null, null, null, null, null);
        //Log.i(TAG, "count is " + String.valueOf(c.getCount()));

        if((c.moveToNext() == false) || (c.getString(c.getColumnIndex(MainActivity.mdbHelper.PENDING_BOOKINGS)) == null )){
            arrayToUse = new String[0];
        }
        else{
            arrayToUse = new String[c.getCount()];
            ArrayList<String> arrayToUseTemp = new ArrayList<String>();

            //Log.i(TAG, String.valueOf(c.getCount()));
            arrayToUseTemp.add(c.getString(c.getColumnIndex(MainActivity.mdbHelper.PENDING_BOOKINGS)));
            //int i = 1;
            String toAdd;
            while(c.moveToNext()){
                toAdd = c.getString(c.getColumnIndex(MainActivity.mdbHelper.PENDING_BOOKINGS));
                if(toAdd != null && !toAdd.isEmpty()){
                    arrayToUseTemp.add(toAdd);
                }
                //i++;
            }
            int arrayToUseTempSize = arrayToUseTemp.size(); //This is set here, so that it doesn't change as the array gets smaller below
            for(int j = 0; j < arrayToUseTempSize; j ++){
                if(j>0){
                    if(((j+1) % 4) == 0){
                        arrayToUseTemp.set(j-1, arrayToUseTemp.get(j-1) + "\n-" + arrayToUseTemp.get(j));
                        arrayToUseTemp.remove(j);
                    }
                }

            }
            arrayToUse = arrayToUseTemp.toArray(new String[arrayToUseTemp.size()]);
            c.close();
        }
    }

    @Override
    public void onDestroy() {
        OttoBusSingleton.getInstance().unregister(this);
        super.onDestroy();
    }
	public class ChangeColorGridAdapter extends BaseAdapter {
		private Context context;
		private String[] items;
		LayoutInflater inflater;
		TextView txtView;
		@Override
	    public int getCount() {
	        return items.length;
	    }

	    @Override
	    public Object getItem(int position) {
	        return items[position];
	    }
		public ChangeColorGridAdapter(Context context, String[] items) {
	        this.context = context;
	        this.items = items;
	        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }

	    @Override
	    public long getItemId(int position) {
	        return position;
	    }

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

	        if (convertView == null) {
	            convertView = inflater.inflate(R.layout.my_bookings_item, null);

	            txtView = (TextView) convertView.findViewById(R.id.my_booking_item_color);
	        }
	        
	        txtView.setTextColor(Color.parseColor("#0099CC"));
	        txtView.setText(items[position]);

	        return convertView;
		}
		
	}
}
