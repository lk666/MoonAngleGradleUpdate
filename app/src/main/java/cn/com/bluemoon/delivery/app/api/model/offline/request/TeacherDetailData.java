package cn.com.bluemoon.delivery.app.api.model.offline.request;

/**
 * 请求教师课程详情带参
 * Created by tangqiwei on 2017/7/5.
 */

public class TeacherDetailData extends StartOrEndCourseData {

    public boolean isRealEnd;//为了兼容旧版本，如果为false，已结束的课程没有实际上下课时间就返回排课时间

    public TeacherDetailData(String courseCode, String planCode,boolean isRealEnd) {
        super(courseCode, planCode);
        this.isRealEnd=isRealEnd;
    }
}
