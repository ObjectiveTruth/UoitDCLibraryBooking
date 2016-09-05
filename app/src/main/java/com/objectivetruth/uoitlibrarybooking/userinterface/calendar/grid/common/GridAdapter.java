package com.objectivetruth.uoitlibrarybooking.userinterface.calendar.grid.common;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.BookingInteractionModel;
import com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.BookingInteractionScreenLoadEvent;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.CalendarDay;
import com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCell;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.grid.tablefixheaders.FixedTableAdapter;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.Random;

import static com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.common.Utils.getBookingInteractionEventTypeBasedOnTimeCell;
import static com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel.TimeCellType.BOOKING_CONFIRMED;

public class GridAdapter extends FixedTableAdapter {
    private int _width_of_cell_in_pixels;
    private int _height_of_cell_in_pixels;
    private CalendarDay calendarDay;
    @Inject BookingInteractionModel bookingInteractionModel;

    public GridAdapter(Context context, CalendarDay calendarDay) {
        super(context);
        ((UOITLibraryBookingApp) context.getApplicationContext()).getComponent().inject(this);
        _width_of_cell_in_pixels = context.getResources().getDimensionPixelSize(R.dimen.table_width);
        _height_of_cell_in_pixels = context.getResources().getDimensionPixelSize(R.dimen.table_height);
        this.calendarDay = calendarDay;
    }

    /**
     * Settter for the calendarDay info, will NOT update the UI. Useful, if you want to update the info inside without
     * for example, an onclick on a timecell
     * @param calendarDay
     */
    public void saveNewCalendarDayWontUpdateUI(CalendarDay calendarDay) {
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
            convertView = inflater.inflate(R.layout.calendar_grid_item, parent, false);
            holder = new ViewHolder();
            holder.textViewOnly = (TextView) convertView.findViewById(R.id.calendar_grid_item);
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
                holder.textViewOnly.setText(currentTimeCellForThisViewCall.timeStringOrRoomName);
                holder.textViewOnly.setOnClickListener(null); break;

            case TABLE_ROW_HEADER:
                holder.textViewOnly.setText(currentTimeCellForThisViewCall.timeStringOrRoomName);
                holder.textViewOnly.setOnClickListener(null); break;

            case TABLE_TOP_LEFT_CELL:
                holder.textViewOnly.setText("");
                holder.textViewOnly.setOnClickListener(null); break;

            case BOOKING_LIBRARY_CLOSED:
                holder.textViewOnly.setText(R.string.timecell_calendar_label_closed);
                holder.textViewOnly.setOnClickListener(null); break;

            case BOOKING_OPEN:
                holder.textViewOnly.setText(R.string.timecell_calendar_label_book);
                holder.textViewOnly.setOnClickListener(new TimeCellOnClickListener(currentTimeCellForThisViewCall,
                        bookingInteractionModel, calendarDay)); break;

            case BOOKING_COMPETING:
                holder.textViewOnly.setText(R.string.timecell_calendar_label_book_joinorleave);
                holder.textViewOnly.setOnClickListener(new TimeCellOnClickListener(currentTimeCellForThisViewCall,
                        bookingInteractionModel, calendarDay)); break;

            case BOOKING_CONFIRMED:
                holder.textViewOnly.setText(currentTimeCellForThisViewCall.groupNameForWhenFullyBookedRoom);
                holder.textViewOnly.setOnClickListener(new TimeCellOnClickListener(currentTimeCellForThisViewCall,
                        bookingInteractionModel, calendarDay)); break;

            case BOOKING_LOCKED:
                TimeCell parentTimeCell = _getTimeCellWithGroupNameAboutThisOne(row, column);
                if(parentTimeCell == null) {
                    parentTimeCell = new TimeCell(); parentTimeCell.groupNameForWhenFullyBookedRoom = "";}
                holder.textViewOnly.setText(parentTimeCell.groupNameForWhenFullyBookedRoom);
                holder.textViewOnly.setOnClickListener(null); break;

            default:
                holder.textViewOnly.setText(currentTimeCellForThisViewCall.timeCellType.name());
                holder.textViewOnly.setOnClickListener(null);

        }
        return convertView;
    }

    private static class ViewHolder {
        TextView textViewOnly;
    }

    /**
     * Accounts for the rows and columns coming in a format that is not supported by FixedTableHeaders Library
     * Example of topleft most area of the grid (this is how the row/columns come in, -1 are the headers):
     * | -1,-1 | -1,0 | -1,1 | -->
     * | 0,-1  |  0,0 |  0,1 | -->
     * | 1,-1  |  1,0 |  1,1 | -->
     * |   ||  |  ||  |  ||  |
     * |   ↓↓  |  ↓↓  |  ↓↓  |
     *
     * @param row
     * @param column
     * @return
     */
    private int _convertRowAndColumnToTimeCellIndex(int row, int column) {
        int normalizedRow = row + 1; // Account for the rows starting at -1
        int normalizedColumn = column + 1; // Account for the columns starting at -1
        int totalColumns = getColumnCount() + 1; // Accounts for the extra row header
        return normalizedColumn + (totalColumns * normalizedRow);
    }

    /**
     * In the calendar, a fully booked room displays the name of the group making the booking, but if its more than
     * a 30 minute slot, each slot below it, just has two quotes (""). This function is useful if you're the time cell
     * with nothing in it, and you gotta find who the parent is. It will keep searching above until it finds a
     * BOOKING_CONFIRMED {@code TimeCell}
     * @see TimeCell
     * @param row row of the locked timeCell for which to look up parent for
     * @param column row of the locked timeCell for which to look up parent for
     * @return if null, it means it wasn't found
     */
    @Nullable
    private TimeCell _getTimeCellWithGroupNameAboutThisOne(int row, int column) {
        for(int irow = row; irow > 0; irow--) {
            TimeCell suspect = calendarDay.timeCells.get(_convertRowAndColumnToTimeCellIndex(irow, column));
            if(suspect.timeCellType == BOOKING_CONFIRMED) {
                // Quit early once we find the first BookingConfirmed TimeCell above this one
                return suspect;
            }
        }
        return null; // Incase the correct TimeCell is not found
    }

    private boolean _isTopLeftCell(int row, int column) {
        return (row < 0 && column < 0);
    }

    private static class TimeCellOnClickListener implements View.OnClickListener {
        private TimeCell timeCell;
        private CalendarDay calendarDay;
        BookingInteractionModel bookingInteractionModel;

        TimeCellOnClickListener(TimeCell timeCell, BookingInteractionModel bookingInteractionModel,
                                CalendarDay calendarDay) {
            this.timeCell = timeCell;
            this.bookingInteractionModel = bookingInteractionModel;
            this.calendarDay = calendarDay;
        }

        @Override
        public void onClick(View view) {
            Timber.i("Clicked: " + timeCell.toString());
            bookingInteractionModel.getBookingInteractionScreenLoadEventPublishSubject().onNext(
                    new BookingInteractionScreenLoadEvent(timeCell,
                            getBookingInteractionEventTypeBasedOnTimeCell(timeCell),
                    calendarDay.extDayOfMonthNumber, calendarDay.extMonthWord));
        }
    }

}
