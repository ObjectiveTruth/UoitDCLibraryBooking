package com.objectivetruth.uoitlibrarybooking;

import java.util.ArrayList;

public interface  CommunicatorLoginAsync {
	public void LoginSuccess(ArrayList<String[]> result);
	public void LoginFail(String errorMessage);
}
