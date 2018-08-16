package cn.com.bluemoon.delivery.app.api.model.offline.request;

import java.util.List;

/**
 * Created by bm on 2017/5/27.
 */

public class AssignData {

    public String courseCode;
    public String planCode;
    public List<String> courseCodeList;

    public AssignData(String courseCode,String planCode){
        this.courseCode = courseCode;
        this.planCode = planCode;
    }

    public AssignData(String courseCode,List<String> courseCodeList, String planCode){
        this.courseCode = courseCode;
        this.courseCodeList = courseCodeList;
        this.planCode = planCode;
    }

}
