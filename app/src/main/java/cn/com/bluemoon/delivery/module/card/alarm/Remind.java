package cn.com.bluemoon.delivery.module.card.alarm;

import java.io.Serializable;

/**
 * Created by allenli on 2016/9/18.
 */
public class Remind implements Serializable {


    private long remindId;
    public boolean isClose;
    private int hour;
    private int minute;

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    private long remindTime;
    private String  remindTitle;
    private String remindContent;
    private int  remindWeek;

    public Remind(){
        this.remindId=-1;
        this.isClose=false;
        this.setRemindTitle("");
        this.setRemindContent("");
        this.setRemindTime(0);
        this.setRemindWeek(0);
    }

    public long getRemindId() {
        return remindId;
    }

    public void setRemindId(long remindId) {
        this.remindId = remindId;
    }

    public long getRemindTime() {
        return remindTime;
    }

    public void setRemindTime(long remindTime) {
        this.remindTime = remindTime;
    }

    public String getRemindTitle() {
        return remindTitle;
    }

    public void setRemindTitle(String remindTitle) {
        this.remindTitle = remindTitle;
    }

    public String getRemindContent() {
        return remindContent;
    }

    public void setRemindContent(String remindContent) {
        this.remindContent = remindContent;
    }

    public int getRemindWeek() {
        return remindWeek;
    }

    public void setRemindWeek(int remindWeek) {
        this.remindWeek = remindWeek;
    }
}
