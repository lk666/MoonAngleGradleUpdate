package cn.com.bluemoon.delivery.app.api.model.offline;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2017/5/27.
 */

public class ResultSignDetail extends ResultBase {

    public SignDetailData data = new SignDetailData();

    public class SignDetailData {
        public long date;
        public String planCode = String.valueOf(new Random().nextLong());
        public String room = "培训室名称";
        public List<Course> courses = getCourseList();

        public class Course {
            public String courseCode = String.valueOf(new Random().nextLong());
            public String courseName = "课程名称";
            public String teacherName = "教师名称";
            public long startTime;
            public long endTime = System.currentTimeMillis();

        }

        private List<Course> getCourseList(){
            List<Course> list = new ArrayList<>();
            list.add(new Course());
            list.add(new Course());
            list.add(new Course());
            list.add(new Course());
            return list;
        }

    }
}
