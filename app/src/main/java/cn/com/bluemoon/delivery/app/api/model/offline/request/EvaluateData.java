package cn.com.bluemoon.delivery.app.api.model.offline.request;

/**
 * Created by bm on 2017/5/27.
 */

public class EvaluateData {
    public String comment;
    public String courseCode;
    public int courseStar;
    public String planCode;
    public int teacherStar;

    public EvaluateData(String comment, String courseCode, int courseStar,
                        String planCode, int teacherStar){
        this.comment = comment;
        this.courseCode = courseCode;
        this.courseStar = courseStar;
        this.planCode = planCode;
        this.teacherStar = teacherStar;
    }

}
