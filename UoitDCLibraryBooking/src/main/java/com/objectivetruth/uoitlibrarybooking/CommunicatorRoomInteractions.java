package com.objectivetruth.uoitlibrarybooking;

import java.net.CookieManager;

public interface CommunicatorRoomInteractions {
	public void InteractionSuccess(String successMesssage, boolean isCalendarable);

	public void CreateRoomFail(String errorMessage);

	//public void RoomLeaveSuccess(String returnMessage);

	public void RoomLeaveFail(String returnMessage);

	public void createFromJoinOrLeave(CookieManager cookieManager, String result);
	
	public void ViewLeaveOrJoinFail(String returnMessage);



	
}
