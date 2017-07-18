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
    public String courseQrCodeMark;//课程二维码标示
    public long endTime;//结束时间
    public String planCode;//排课编号
    public String room;//培训室名称
    public long signTime;//签到时间
    public long startTime;//开始时间
    public String status;//状态(waitingClass,inClass,endClass,closeClass),	string	@mock=如果请求参数status=endClass,返回的状态可能是(endClass,closeClass)
    public String teacherName;//教师名称

    public int enrollNum;//报名人数
    public int signNum;//签到人数
    public int evaluatedNum;//评价人数

//    public CurriculumsTable(){
//
//    }
//    /**
//     * 教师详情转化成item
//     * @param address
//     * @param courseCode
//     * @param courseName
//     * @param endTime
//     * @param planCode
//     * @param room
//     * @param startTime
//     * @param status
//     * @param teacherName
//     * @param enrollNum
//     * @param signNum
//     */
//    public CurriculumsTable(String address,String courseCode,String courseName,long endTime,String planCode,String room,
//                            long startTime,String status,String teacherName,int enrollNum,int signNum){
//        this.address=address;
//        this.courseCode=courseCode;
//        this.courseName=courseName;
//        this.endTime=endTime;
//        this.planCode=planCode;
//        this.room=room;
//        this.signTime=signTime;
//        this.startTime=startTime;
//        this.status=status;
//        this.teacherName=teacherName;
//        this.enrollNum=enrollNum;
//        this.signNum=signNum;
//    }
}
