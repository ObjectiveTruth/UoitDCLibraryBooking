package com.objectivetruth.uoitlibrarybooking.data.models.bookinginteractionmodel.requestoptions;


public class BookRequestOptions implements RequestOptions{
    public String groupName = "";
    public String groupCode = "";
    public String duration = "";
    public String comments = "";

    public BookRequestOptions(String groupName, String groupCode, String duration, String comments) {
        this.groupName = groupName;
        this.groupCode = groupCode;
        this.duration = duration;
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "BookRequestOptions{" +
                "groupName='" + groupName + '\'' +
                ", groupCode='" + groupCode + '\'' +
                ", duration='" + duration + '\'' +
                ", comments='" + comments + '\'' +
                '}';
    }
}
