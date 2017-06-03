package cn.com.bluemoon.delivery.app.api.model.offline;

import java.util.ArrayList;

/**
 * Created by tangqiwei on 2017/6/3.
 */

public class ListData {
    public ArrayList<CurriculumsTable> courses;	//学员课程列表	array<object>
    public int totalCourseNum;//总课程数	number	@mock=
    public String totalDuration;//	总时长	string	@mock=
}
