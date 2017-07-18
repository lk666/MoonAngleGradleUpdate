package cn.com.bluemoon.delivery.app.api.model.offline;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 教师扫码课程详情
 * Created by tangqiwei on 2017/7/18.
 */

public class ResultTeacherScanCourse extends ResultBase {

    public Data data;

    public static class Data{
//        courseInfo	课程信息	object
        public CourseInfo courseInfo;
//        isShowSignButt	是否显示签到按钮	boolean
        public boolean isShowSignButt;
//        planCode	排课编码	string
        public String planCode;
//        userInfo	用户信息	object
        public UserInfo userInfo;
public static class UserInfo{
    //            userMark	用户标示（编码/用户手机号码）	string
    public String userMark;
    //            userName	用户名称	string
    public String userName;
    //            userType	用户类型(外部：external，内部：employee)	string
    public String userType;
}
        public static class CourseInfo{
            //            courseCode	课程编码	string
            public String courseCode;
            //            courseName	课程名称	string
            public String courseName;
            //            endTime	结束时间	number
            public long endTime;
            //            room	培训室名称	string
            public String room;
//            signTime	签到时间	number
            public long signTime;
            //            startTime	开始时间	number
            public  long startTime;
            //            status	课程状态(waitingClass,inClass,endClass,closeClass)	string
            public String status;
        }
    }

}
