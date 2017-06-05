package cn.com.bluemoon.delivery.app.api.model.offline;

/**
 * Created by bm on 2017/6/2.
 */

public class EvaluateDetail {

    public String comment = "培训评价";
    public String courseCode = "课程编号";
    public int courseStar;
    public String planCode = "排课编号";
    public int teacherStar = 3;
    public String courseName = "课程名称";
    public String teacherName = "培训讲师";
    public long startTime;
    public long endTime = System.currentTimeMillis();
    public int score;
    public long signTime;
    public String studentCode = "学员编号";
    public String studentName = "学员名称";

}
