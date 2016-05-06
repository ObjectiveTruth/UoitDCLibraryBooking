package com.objectivetruth.uoitlibrarybooking.userinterface.myaccount.myaccountloaded;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.MyAccountBooking;

import java.util.ArrayList;

public class BookingsAdapter extends ArrayAdapter<MyAccountBooking>{
    private ArrayList<MyAccountBooking> myAccountBookings;

    public BookingsAdapter(Context context, ArrayList<MyAccountBooking> myAccountBookings) {
        super(context, 0, myAccountBookings); // 0 is the view to instantiate by default. We'll use our own in getView
        if(myAccountBookings == null ) {
            this.myAccountBookings = new ArrayList<MyAccountBooking>();
        }else{
            this.myAccountBookings = myAccountBookings;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.my_account_loaded_booking_grid_item, parent, false);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.my_account_loaded_grid_item_textview);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MyAccountBooking myAccountBooking = getItem(position / 3);
        int positionInRow = position % 3;
        switch(positionInRow) {
            case 0:
                viewHolder.textView.setText(myAccountBooking.room);
                break;
            case 1:
                viewHolder.textView.setText(myAccountBooking.date);
                break;
            case 2:
                String startAndEndTime = myAccountBooking.startTime + "-" + myAccountBooking.endTime;
                viewHolder.textView.setText(startAndEndTime);
                break;
            default:
                viewHolder.textView.setText("");
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return myAccountBookings.size();
    }

    private static class ViewHolder {
        TextView textView;
    }

}
