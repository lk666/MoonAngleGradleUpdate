package cn.com.bluemoon.delivery.app.api.model.offline.request;

/**
 * 教师扫码课程详情
 * Created by tangqiwei on 2017/7/17.
 */

public class TeacherScanCourseData {
//    courseCode	课程编码	string
    public String courseCode;
//    planCode	排课编码	string
    public String planCode;
//    userCode	员工编码	string
    public String userCode;
    public TeacherScanCourseData(String planCode,String courseCode,String userCode){
        this.planCode=planCode;
        this.courseCode=courseCode;
        this.userCode=userCode;
    }
}
