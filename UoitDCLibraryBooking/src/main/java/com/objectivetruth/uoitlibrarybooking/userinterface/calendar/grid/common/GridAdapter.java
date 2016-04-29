package com.objectivetruth.uoitlibrarybooking.userinterface.calendar.grid.common;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarDay;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCell;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.grid.tablefixheaders.FixedTableAdapter;

public class GridAdapter extends FixedTableAdapter {
    private int _width_of_cell_in_pixels;
    private int _height_of_cell_in_pixels;
    private CalendarDay calendarDay;
    ViewHolder holder;

    public GridAdapter(Context context, CalendarDay calendarDay) {
        super(context);
        _width_of_cell_in_pixels = context.getResources().getDimensionPixelSize(R.dimen.table_width);
        _height_of_cell_in_pixels = context.getResources().getDimensionPixelSize(R.dimen.table_height);
        holder = new ViewHolder();
        this.calendarDay = calendarDay;
    }

    @Override
    public String getCellString(int row, int column) {
        return "nothing";
    }

    @Override
    public int getLayoutResource(int row, int column) {
        return R.layout.item_table1_header;
    }

    @Override
    public int getWidth(int column) {
        return _width_of_cell_in_pixels;
    }

    @Override
    public int getHeight(int row) {
        return _height_of_cell_in_pixels;
    }

    @Override
    public int getItemViewType(int row, int column) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getRowCount() {
        return calendarDay.rowCountIncludingRowHeadersColumn -1; //account for the inclusion of the row header column
    }

    @Override
    public int getColumnCount() {
        return calendarDay.columnCountIncludingRowHeadersColumn -1; //account for the inclusion of the column header row
    }

    @Override
    public View getView(int row, int column, View recycleView, ViewGroup parent) {
        View convertView = recycleView;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.calendar_table_item_actual, parent, false);
            holder.textViewOnly = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
/*        if(row < 0) {
            holder.textViewOnly.setText("ROOM");
            return convertView;
        }

        if(column < 0) {
            holder.textViewOnly.setText("TIME");
            return convertView;
        }*/

        TimeCell timeCellForThisViewCall =
                calendarDay.timeCells.get(_convertRowColumnToTimeCellIndex(row, column));

        holder.textViewOnly.setText(timeCellForThisViewCall.timeCellType.name());
        //holder.textViewOnly.setText(row + ", " + column);
        return convertView;
    }

    private static class ViewHolder {
        TextView textViewOnly;
    }

    private int _convertRowColumnToTimeCellIndex(int row, int column) {
        return column + (getColumnCount() * row);
    }
}
