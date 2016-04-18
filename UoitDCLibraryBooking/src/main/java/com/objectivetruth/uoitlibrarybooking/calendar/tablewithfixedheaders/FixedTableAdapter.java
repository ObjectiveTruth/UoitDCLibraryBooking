package com.objectivetruth.uoitlibrarybooking.calendar.tablewithfixedheaders;

import android.content.Context;
import android.view.LayoutInflater;

/**
 * This class implements the main functionalities of the TableAdapter in
 * Mutuactivos.
 * 
 * 
 * @author Brais Gabon
 */
public abstract class FixedTableAdapter extends BaseTableAdapter {
	private final Context context;
	protected final LayoutInflater inflater;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            The current context.
	 */
	public FixedTableAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	/**
	 * Returns the context associated with this array adapter. The context is
	 * used to create views from the resource passed to the constructor.
	 * 
	 * @return The Context associated with this adapter.
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * Quick access to the LayoutInflater instance that this Adapter retreived
	 * from its Context.
	 * 
	 * @return The shared LayoutInflater.
	 */
	public LayoutInflater getInflater() {
		return inflater;
	}

	/**
	 * @param row
	 *            the title of the row of this header. If the column is -1
	 *            returns the title of the row header.
	 * @param column
	 *            the title of the column of this header. If the column is -1
	 *            returns the title of the column header.
	 * @return the string for the cell [row, column]
	 */
	public abstract String getCellString(int row, int column);

	public abstract int getLayoutResource(int row, int column);
}
