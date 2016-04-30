package com.objectivetruth.uoitlibrarybooking.userinterface.calendar.grid.common;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarDay;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCell;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.grid.tablefixheaders.FixedTableAdapter;

import java.util.Random;

public class GridAdapter extends FixedTableAdapter {
    private int _width_of_cell_in_pixels;
    private int _height_of_cell_in_pixels;
    private CalendarDay calendarDay;

    public GridAdapter(Context context, CalendarDay calendarDay) {
        super(context);
        _width_of_cell_in_pixels = context.getResources().getDimensionPixelSize(R.dimen.table_width);
        _height_of_cell_in_pixels = context.getResources().getDimensionPixelSize(R.dimen.table_height);
        this.calendarDay = calendarDay;
    }

    @Override
    public String getCellString(int row, int column) {
        return new Random().nextInt() + "";
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
        //account for the inclusion of the row header column. getRowCount() wants the number NOT including the Header
        return calendarDay.rowCountIncludingRowHeadersColumn - 1;
    }

    @Override
    public int getColumnCount() {
        //account for the inclusion of the column header row. getColumnCount() wants the number NOT including the Header
        return calendarDay.columnCountIncludingRowHeadersColumn - 1;
    }

    @Override
    public View getView(int row, int column, View recycleView, ViewGroup parent) {
        // ViewHolder Pattern. Recommended to make listviews that use the recycler faster
        View convertView = recycleView;
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.calendar_table_item_actual, parent, false);
            holder = new ViewHolder();
            holder.textViewOnly = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        // End View Holder Pattern

        // Start Business Logic
        TimeCell currentTimeCellForThisViewCall =
                calendarDay.timeCells.get(_convertRowAndColumnToTimeCellIndex(row, column));

        switch(currentTimeCellForThisViewCall.timeCellType) {
            case TABLE_COLUMN_HEADER:
                holder.textViewOnly.setText(currentTimeCellForThisViewCall.timeStringOrRoomName); break;

            case TABLE_ROW_HEADER:
                holder.textViewOnly.setText(currentTimeCellForThisViewCall.timeStringOrRoomName); break;

            case TABLE_TOP_LEFT_CELL:
                holder.textViewOnly.setText(""); break;

            case BOOKING_OPEN:
                holder.textViewOnly.setText("Open"); break;

            case BOOKING_LIBRARY_CLOSED:
                holder.textViewOnly.setText("Closed"); break;

            case BOOKING_COMPETING:
                holder.textViewOnly.setText("Open"); break;

            case BOOKING_LOCKED:
                holder.textViewOnly.setText(currentTimeCellForThisViewCall.groupNameForWhenFullyBookedRoom); break;

            default:
                holder.textViewOnly.setText(currentTimeCellForThisViewCall.timeCellType.name());


        }
        return convertView;
    }

    private static class ViewHolder {
        TextView textViewOnly;
    }

    private int _convertRowAndColumnToTimeCellIndex(int row, int column) {
        // Example of topleft most area of the grid (this is how the row/columns come in):
        // | -1,-1 | -1,0 | -1,1 | -->
        // |  0,-1 |  0,0 |  0,1 | -->
        // |  1,-1 |  1,0 |  1,1 | -->
        // |   ||  |  ||  |  ||  |
        // |   \/  |  \/  |  \/  |
        int normalizedRow = row + 1; // Account for the rows starting at -1
        int normalizedColumn = column + 1; // Account for the columns starting at -1
        int totalColumns = getColumnCount() + 1; // Accounts for the extra row header
        return normalizedColumn + (totalColumns * normalizedRow);
    }

    private boolean _isTopLeftCell(int row, int column) {
        return (row < 0 && column < 0);
    }
}
