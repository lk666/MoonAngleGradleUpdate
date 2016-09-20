package cn.com.bluemoon.delivery.module.card.alarm;

import android.database.Cursor;

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
    private String remindTitle;
    private String remindContent;
    private int remindWeek;

    public Remind(Cursor c) {
        remindId = c.getLong(0);
        hour = c.getInt(1);
        minute = c.getInt(2);
        remindWeek = c.getInt(3);
        remindTime = c.getLong(4);
        isClose = c.getInt(5) == 1;
        remindTitle = c.getString(6);
        remindContent = c.getString(7);
    }

    public Remind() {
        this.remindId = -1;
        this.isClose = false;
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
