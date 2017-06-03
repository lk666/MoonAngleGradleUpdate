package cn.com.bluemoon.delivery.app.api.model.offline;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.utils.Constants;

/**
 * Created by bm on 2017/6/2.
 */

public class ResultStudentDetail extends ResultBase {

    public StudentDetail data = new StudentDetail();

    public class StudentDetail{

        public String address = "地址";
        public String avatar = "https://tmallapi.bluemoon.com.cn/angelUpload/images/app/201605/20160525/20160525115737454.jpg";
        public String contactsName = "联系人名称";
        public String contactsPhone = "13450103113";
        public String courseCode = "课程编号";
        public String courseName = "课程名称";
        public long endTime;
        public String planCode = "排课编号";
        public String purpose = "培训目的";
        public String room = "培训室";
        public long signTime = System.currentTimeMillis();
        public long startTime;
        public String status = Constants.OFFLINE_STATUS_WAITING_CLASS;
        public String teacherName = "教师名称";
        public String topic = "主题";
        public EvaluateDetail evaluateDetail;

    }

}
