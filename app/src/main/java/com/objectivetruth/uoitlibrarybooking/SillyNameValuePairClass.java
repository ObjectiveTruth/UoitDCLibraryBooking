package com.objectivetruth.uoitlibrarybooking;

public class SillyNameValuePairClass {
	public String eventTarget;
	public String eventArgument;
	public String monthName;
	public String dayNumber;

	public SillyNameValuePairClass(String eventTarget, String eventArgument, String monthName, String dayNumber){
		this.eventTarget = eventTarget;
		this.eventArgument = eventArgument;
		this.monthName = monthName;
		this.dayNumber = dayNumber;
	}

    @Override
    public String toString(){
        return "eventTarget=" + eventTarget + ", eventArgument=" + eventArgument + ", monthName=" + monthName
                + ", dayNumber=" + dayNumber;
    }
}
