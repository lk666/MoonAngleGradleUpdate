package cn.com.bluemoon.delivery.app.api.model.offline.request;

/**
 * Created by tangqiwei on 2017/6/3.
 */

public class TeacherEvaluateData {
    public String comment;//	评价内容	string
    public String courseCode;//	课程编号	string	@mock=123
    public String planCode;//	排课编号	string	@mock=123
    public int score;//	分数	number
    public String studentCode;//	学员编号	string
    public String studentName;//	学员名称	string
    public TeacherEvaluateData(String comment,String courseCode,String planCode,int score,String studentCode,String studentName){
        this.comment=comment;
        this.courseCode=courseCode;
        this.planCode=planCode;
        this.score=score;
        this.studentCode=studentCode;
        this.studentName=studentName;
    }
}
