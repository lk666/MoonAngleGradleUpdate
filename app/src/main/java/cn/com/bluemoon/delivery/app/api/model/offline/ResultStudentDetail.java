package cn.com.bluemoon.delivery.app.api.model.offline;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.utils.Constants;

/**
 * Created by bm on 2017/6/2.
 */

public class ResultStudentDetail extends ResultBase {

    public StudentDetail data;

    public class StudentDetail{

        public String address;
        public String avatar;
        public String contactsName;
        public String contactsPhone;
        public String courseCode;
        public String courseName;
        public long endTime;
        public String planCode;
        public String purpose;
        public String room;
        public long signTime;
        public long startTime;
        public String status;
        public String teacherName;
        public String topic;
        public EvaluateDetail evaluateDetail;
        public int isEvaluated;

    }

}
