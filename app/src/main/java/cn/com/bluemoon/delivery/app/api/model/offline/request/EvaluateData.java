package cn.com.bluemoon.delivery.app.api.model.offline.request;

/**
 * Created by bm on 2017/5/27.
 */

public class EvaluateData {
    public String comment;
    public String courseCode;
    public float courseStar;
    public String planCode;
    public float teacherStar;

    public EvaluateData(String comment, String courseCode, float courseStar,
                        String planCode, float teacherStar){
        this.comment = comment;
        this.courseCode = courseCode;
        this.courseStar = courseStar;
        this.planCode = planCode;
        this.teacherStar = teacherStar;
    }

}
