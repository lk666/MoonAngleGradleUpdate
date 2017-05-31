package cn.com.bluemoon.delivery.app.api.model.offline.request;

/**
 * Created by bm on 2017/5/27.
 */

public class AssignData {

    public String courseCode;
    public String planCode;

    public AssignData(String courseCode,String planCode){
        this.courseCode = courseCode;
        this.planCode = planCode;
    }
}
