package cn.com.bluemoon.delivery.app.api.model.offline;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2017/5/27.
 */

public class ResultSignDetail extends ResultBase {

    public SignDetailData data;

    public class SignDetailData {
        public long date;
        public String room;
        public List<Course> courses;

        public class Course {
            public String courseCode;
            public String courseName;
            public String planCode;
            public String teacherName;
            public long startTime;
            public long endTime;

        }
    }
}
