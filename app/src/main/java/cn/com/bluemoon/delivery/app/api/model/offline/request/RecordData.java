package cn.com.bluemoon.delivery.app.api.model.offline.request;

/**
 * Created by bm on 2017/7/3.
 */

public class RecordData {
    public String comment;
    public String courseCode;
    public String courseName;
    public long date;
    public String planCode;
    public long realEndTime;
    public long realStartTime;

    public RecordData(String courseCode, String planCode, long realStartTime, long realEndTime,
                      String comment) {
        this.courseCode = courseCode;
        this.planCode = planCode;
        this.realEndTime = realEndTime;
        this.realStartTime = realStartTime;
        this.comment = comment;
    }
}
