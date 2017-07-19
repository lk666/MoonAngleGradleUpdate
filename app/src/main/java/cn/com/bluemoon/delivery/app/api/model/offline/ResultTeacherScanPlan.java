package cn.com.bluemoon.delivery.app.api.model.offline;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 教师扫码排课信息
 * Created by tangqiwei on 2017/7/18.
 */

public class ResultTeacherScanPlan extends ResultBase {

    public Data data;

    public static class Data{
//        courses	课程信息	array<object>
        public List<Courses> courses;
//        isTeacher	是否教师	boolean
        public boolean isTeacher;
//        planInfo	排课信息	object
        public PlanInfo planInfo;
//        userInfo	用户信息	object
        public UserInfo userInfo;

        public static class PlanInfo{
//            planCode	排课编码	string
            public String planCode;
//            topic	培训主题	string
            public String topic;
        }
        public static class UserInfo{
//            userMark	用户标示（编码/用户手机号码）	string
            public String userMark;
//            userName	用户名称	string
            public String userName;
//            userType	用户类型(外部：external，内部：employee)	string
            public String userType;
        }
        public static class Courses{
//            courseCode	课程编码	string
            public String courseCode;
//            courseName	课程名称	string
            public String courseName;
//            endTime	结束时间	number
            public long endTime;
//            isSign	是否已签到	boolean
            public boolean isSign;
//            room	培训室名称	string
            public String room;
//            startTime	开始时间	number
            public  long startTime;
//            status	课程状态(waitingClass,inClass,endClass,closeClass)	string
            public String status;
//            teacherName	教师名称	string
            public String teacherName;
//            isDisable	是否禁用（1已禁用，0未禁用）
            public int isDisable;
//            message	禁用原因(只能当iisDisable=1才会显示)
            public String message;


        }
    }
}
