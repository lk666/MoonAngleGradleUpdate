package cn.com.bluemoon.delivery.app.api.model.offline;

import java.io.Serializable;
import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2017/5/27.
 */

public class ResultSignDetail extends ResultBase  {

    public SignDetailData data;

    public static class SignDetailData implements Serializable{

        public long date;
        public String room;
        public PlanInfo planInfo;
        public String type;
        public List<Course> courses;

        public static class PlanInfo implements Serializable {

            public long startTime;
            public long endTime;
            public long nowDate;
            public String planCode;
            public String topic;
        }

        public static class Course implements Serializable {

            public long startTime;
            public long endTime;
            public String courseName;
            public String teacherName;
            public String courseCode;
            public String planCode;
            public int isDisable;
            public int isSign;
            public String message;
            public String room;
            public String status;
        }
    }
}
