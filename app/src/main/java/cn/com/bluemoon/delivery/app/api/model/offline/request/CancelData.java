package cn.com.bluemoon.delivery.app.api.model.offline.request;

/**
 * Created by bm on 2017/5/27.
 */

public class CancelData {
    public String courseCode;
    public String planCode;
    public String studentCode;

    public CancelData(String courseCode,String planCode,String studentCode){
        this.courseCode = courseCode;
        this.planCode = planCode;
        this.studentCode = studentCode;
    }
}
