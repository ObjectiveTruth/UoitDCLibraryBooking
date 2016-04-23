package com.objectivetruth.uoitlibrarybooking.userinterface.calendar.grid.common;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.grid.tablefixheaders.FixedTableAdapter;

public class GridAdapter extends FixedTableAdapter {
    private int _width_of_cell_in_pixels;
    private int _height_of_cell_in_pixels;

    public GridAdapter(Context context) {
        super(context);
        _width_of_cell_in_pixels = context.getResources().getDimensionPixelSize(R.dimen.table_width);
        _height_of_cell_in_pixels = context.getResources().getDimensionPixelSize(R.dimen.table_height);
    }

    @Override
    public String getCellString(int row, int column) {
        return "Hello World";
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
        return 25;
    }

    @Override
    public int getColumnCount() {
        return 25;
    }

    @Override
    public View getView(int row, int column, View convertView, ViewGroup parent) {
        return inflater.inflate(R.layout.calendar_table_item_actual, parent, false);
    }
}
