package cn.com.bluemoon.delivery.app.api.model.offline;

import java.util.ArrayList;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 获取学员的课程列表
 * Created by tangqiwei on 2017/6/1.
 */

public class ResultStudentList extends ResultBase {

    public Data data;

    public static class Data{
        public ArrayList<CurriculumsTable> courses;	//学员课程列表	array<object>
        public int totalCourseNum;//总课程数	number	@mock=
        public String totalDuration;//	总时长	string	@mock=
    }
}
