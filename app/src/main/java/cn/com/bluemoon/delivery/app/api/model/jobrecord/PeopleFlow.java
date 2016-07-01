package cn.com.bluemoon.delivery.app.api.model.jobrecord;

import java.io.Serializable;

/**
 * Created by liangjiangli on 2016/6/22.
 */
public class PeopleFlow implements Serializable{
    private String address;
    private long createDate;
    private long endTime;
    private int peopleFlow;
    private String peopleStatus;
    private long startTime;
    private String weekday;
    private String recordStatus;

    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getPeopleFlow() {
        return peopleFlow;
    }

    public void setPeopleFlow(int peopleFlow) {
        this.peopleFlow = peopleFlow;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getPeopleStatus() {
        return peopleStatus;
    }

    public void setPeopleStatus(String peopleStatus) {
        this.peopleStatus = peopleStatus;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }
}
