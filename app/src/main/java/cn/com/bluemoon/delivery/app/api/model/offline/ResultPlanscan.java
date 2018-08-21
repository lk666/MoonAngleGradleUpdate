package cn.com.bluemoon.delivery.app.api.model.offline;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by tangqiwei on 2018/8/16.
 */

public class ResultPlanscan extends ResultBase {
    public Data data;

    public static class Data {
        public PlanInfo planInfo;
        public List<Course> courses;

    }

    public static class PlanInfo {
        //        endTime	结束时间	number
        public long endTime;
        //        planCode	排课编码	string
        public String planCode;
        //        startTime	开始时间	number
        public long startTime;
        //        topic	培训主题	string
        public String topic;
        //   qrCodeUrl	排课二维码url	string
        public String qrCodeUrl;
    }

    public static class Course {
        //        address	地址	string	@mock=
        public String address;
        //        courseCode	课程编号	string	@mock=
        public String courseCode;
        //        courseName	课程名称	string	@mock=
        public String courseName;
        //        endTime	结束时间	number	@mock=0
        public long endTime;
        //        enrollNum	报名人数	number
        public int enrollNum;
        //        evaluatedNum	评价人数	number
        public int evaluatedNum;
        //        planCode	排课编号	string	@mock=
        public String planCode;
        //        room	培训室名称	string	@mock=
        public String room;
        //        signNum	签到人数	number
        public int signNum;
        //        signTime	签到时间	number	@mock=0g根据signTIme来判断是否已经签到
        public long signTime;
        //        startTime	开始时间	number	@mock=0
        public long startTime;
        //        status	状态(waitingClass,inClass,endClass,closeClass),	string
        // @mock=如果请求参数status=endClass,返回的状态可能是(endClass,closeClass)
        public String status;
        //        teacherName	教师名称	string	@mock=
        public String teacherName;
    }

}
