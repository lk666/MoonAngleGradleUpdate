package cn.com.bluemoon.delivery.app.api.model.offline;

import java.io.Serializable;

/**
 * 课程列表-单个实体
 * Created by tangqiwei on 2017/6/1.
 */

public class CurriculumsTable implements Serializable{
    public String address;//地址
    public String courseCode;//课程编号
    public String courseName;//课程名称
    public long endTime;//结束时间
    public String planCode;//排课编号
    public String room;//培训室名称
    public long signTime;//签到时间
    public long startTime;//开始时间
    public String status;//状态(waitingClass,inClass,endClass,closeClass),	string	@mock=如果请求参数status=endClass,返回的状态可能是(endClass,closeClass)
    public String teacherName;//教师名称

    public String enrollNum;//报名人数
    public String signNum;//签到人数
}
