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
//    userInfo	用户信息	object
    public UserInfo userInfo;
    public TeacherScanCourseData(String planCode,String courseCode,String userMark,String userType){
        this.planCode=planCode;
        this.courseCode=courseCode;
        this.userInfo=new UserInfo(userMark,userType);
    }
    public static class UserInfo{
//        userMark	用户标示	string
        public String userMark;
//        userType	用户类型	string
        public String userType;

        public UserInfo(String userMark,String userType){
            this.userMark=userMark;
            this.userType=userType;
        }
    }
}
