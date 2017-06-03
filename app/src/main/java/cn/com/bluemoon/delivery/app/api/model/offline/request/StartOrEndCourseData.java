package cn.com.bluemoon.delivery.app.api.model.offline.request;

/**
 * 开始授课 结束授课 教师课程详情 获取评价学员页面的评价数量
 * Created by tangqiwei on 2017/6/3.
 */

public class StartOrEndCourseData {
    public String courseCode;//	课程编号	string	@mock=123
    public String planCode;    //排课编号	string	@mock=123

    public StartOrEndCourseData(String courseCode, String planCode) {
        this.courseCode = courseCode;
        this.planCode = planCode;
    }
}
