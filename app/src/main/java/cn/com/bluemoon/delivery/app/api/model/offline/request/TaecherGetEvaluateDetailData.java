package cn.com.bluemoon.delivery.app.api.model.offline.request;

/**
 * 教师获取评价信息
 * Created by tangqiwei on 2017/6/3.
 */

public class TaecherGetEvaluateDetailData {
    public String courseCode;//	课程编号	string
    public String planCode;	//排课编号	string
    public String studentCode;//	学员编号	string
    public  TaecherGetEvaluateDetailData(String courseCode,String planCode,String studentCode){
        this.courseCode=courseCode;
        this.planCode=planCode;
        this.studentCode=studentCode;
    }
}
