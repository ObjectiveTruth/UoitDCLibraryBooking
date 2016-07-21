package com.objectivetruth.uoitlibrarybooking;

import java.net.CookieManager;
import java.util.ArrayList;

public interface AsyncResponse {
	public void SendMessageToAllGridViews(ArrayList<CalendarMonth> calendarCache);
	public void ClearResetIcon();
	
	void ChangeScrollPosition(int firstVisibleItem, int pageNumberInt, float coord);
	public void LaunchRoomInteraction(CookieManager cookieManager, String roomNumber, String date, String currentViewState, String currentEventValidation, int shareRow, int shareColumn, int pageNumberInt, String currentViewStateGenerator);
	public void LaunchJoinOrLeave(CookieManager cookieManager,
								  String roomNumber, String weekDayName, String calendarDate,
								  String calendarMonth, String currentViewState,
								  String currentEventValidation, String[] joinSpinnerArr, String[] leaveSpinnerArr, int shareRow, int shareColumn, int pageNumberInt,
								  String currentViewStateGenerator);
	public void LaunchViewLeaveOrJoin(CookieManager cookieManager,
									  String roomNumber, String date, String groupName, String groupCode, String timeRange,
									  String institution, String notes, String currentViewState,
									  String currentEventValidation, int shareRow, int shareColumn, int pageNumberInt,
									  String currentViewStateGenerator);
}
