package cn.com.bluemoon.delivery.app.api.model.offline.request;

/**
 * Created by bm on 2017/5/27.
 */

public class CourseListData {
    public long date;
    public String status;

    public CourseListData(long date, String status) {
        this.date = date;
        this.status = status;
    }
}
