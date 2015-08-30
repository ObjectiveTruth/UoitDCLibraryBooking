package com.objectivetruth.uoitlibrarybooking;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class DrawerListAdapter extends ArrayAdapter<String> {
	String[] stringArr;
	Context mContext;
	int positionInDrawer;
	ViewHolder holder;
	LayoutInflater mInflater;
	Activity mActivity;
	
	public DrawerListAdapter(Context context, int resource, String[] stringArr, int positionInDrawer, Activity mActivity) {
		super(context, resource, stringArr);
		this.stringArr = stringArr;
		this.mActivity = mActivity;
		this.mContext = context;
		this.positionInDrawer = positionInDrawer;
		mInflater = LayoutInflater.from(context);
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.drawer_list_item, null);
			holder = new ViewHolder();
			holder.stripe = (com.objectivetruth.uoitlibrarybooking.RobotoTextView) convertView.findViewById(R.id.drawer_stripe_textview);
			holder.text = (com.objectivetruth.uoitlibrarybooking.RobotoTextView) convertView.findViewById(R.id.drawer_item_textview);
			holder.icon = (ImageView) convertView.findViewById(R.id.drawer_item_imageview);
			convertView.setTag(holder);
		}
		else{
			holder = (ViewHolder) convertView.getTag();
			
		}
		
		if(position == 0)
		{holder.icon.setImageResource(R.drawable.ic_action_calendar_year_zoom);}
		else if(position == 1)
		{holder.icon.setImageResource(R.drawable.ic_action_user_help);}
		else if(position == 2)
		{holder.icon.setImageResource(R.drawable.ic_action_mobile_zoom);}
		else if(position == 3)
		{holder.icon.setImageResource(R.drawable.ic_action_window_pencil);}
		else if(position == 4)
		{holder.icon.setImageResource(R.drawable.ic_action_list_info);}
		else if(position == 5)
		{holder.icon.setImageResource(R.drawable.ic_action_hand_thumbsup);}
		else if(position == 6)
		{holder.icon.setImageResource(R.drawable.ic_action_terminal2_lock_open);}
		
		holder.stripe.setVisibility(View.INVISIBLE);
		holder.text.setText(stringArr[position]);
		if(position == positionInDrawer){
			holder.icon.setBackgroundColor(Color.parseColor("#333333"));
			holder.text.setBackgroundColor(Color.parseColor("#333333"));
			holder.stripe.setVisibility(View.VISIBLE);
		}
/*		if(((UOITLibraryBookingApp)mActivity.getApplication()).getIsPremium() && position == 6){
			holder.icon.setVisibility(View.GONE);
			holder.stripe.setVisibility(View.GONE);
			holder.text.setVisibility(View.GONE);
		}*/
		return convertView;
	}
	static class ViewHolder {
        com.objectivetruth.uoitlibrarybooking.RobotoTextView text;
		 ImageView icon;
        com.objectivetruth.uoitlibrarybooking.RobotoTextView stripe;
		} 
}



