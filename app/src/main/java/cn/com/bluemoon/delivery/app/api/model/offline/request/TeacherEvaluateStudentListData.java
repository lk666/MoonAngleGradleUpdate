package cn.com.bluemoon.delivery.app.api.model.offline.request;

/**
 * 教师评价学员列表
 * Created by tangqiwei on 2017/6/3.
 */

public class TeacherEvaluateStudentListData {
    public String courseCode;//	课程编号	string	@mock=123
    public int isEvaluated;//	是否评价（选填）	number	1已评价，0未评价
    public int isSign;//	是否签到（选填）	number	1已签到，0未签到
    public int pageSize;//	分页条数	number
    public String planCode;//	排课编号	string	@mock=123
    public long timeStamp;//	时间戳	number
    public int type;//	1为已签到，2为已评价，3为未评价	number

    public TeacherEvaluateStudentListData(String courseCode, int isEvaluated, int isSign, int pageSize, String planCode, long timeStamp, int type) {
        this.courseCode = courseCode;
        this.isEvaluated = isEvaluated;
        this.isSign = isSign;
        this.pageSize = pageSize;
        this.planCode = planCode;
        this.timeStamp = timeStamp;
        this.type = type;
    }
}
