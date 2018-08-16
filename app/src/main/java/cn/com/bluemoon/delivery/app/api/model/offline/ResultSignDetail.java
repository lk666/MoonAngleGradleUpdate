package cn.com.bluemoon.delivery.app.api.model.offline;

import java.io.Serializable;
import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2017/5/27.
 */

public class ResultSignDetail extends ResultBase {

    public SignDetailData data;

    public static class SignDetailData implements Serializable{

        public long date;
        public PlanInfo planInfo;
        public String room;
        public String type;
        public List<Course> courses;

        public static class PlanInfo implements Serializable{

            public int endTime;
            public long nowDate;
            public String planCode;
            public int startTime;
            public String topic;
        }

        public static class Course implements Serializable{

            public String courseCode;
            public String courseName;
            public long endTime;
            public int isSign;
            public String message;
            public String planCode;
            public String room;
            public long startTime;
            public String status;
            public String teacherName;
        }
    }
}
