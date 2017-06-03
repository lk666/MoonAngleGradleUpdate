package cn.com.bluemoon.delivery.app.api.model.offline;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2017/6/2.
 */

public class ResultStudentDetail extends ResultBase {

    public StudentDetail data = new StudentDetail();

    public class StudentDetail{

        public String address = "地址";
        public String avatar = "头像";
        public String contactsName = "联系人名称";
        public String contactsPhone = "13450103113";
        public String courseCode = "课程编号";
        public String courseName = "课程名称";
        public long endTime;
        public String planCode = "排课编号";
        public String purpose = "培训目的";
        public String room = "培训室";
        public long signTime;
        public long startTime;
        public String status = "课程状态";
        public String teacherName = "教师名称";
        public String topic = "主题";
        public EvaluateDetail evaluateDetail = new EvaluateDetail();

    }

}
