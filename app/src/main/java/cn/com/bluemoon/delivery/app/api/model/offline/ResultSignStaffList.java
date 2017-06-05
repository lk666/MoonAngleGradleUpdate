package cn.com.bluemoon.delivery.app.api.model.offline;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 教师评价学员列表
 * Created by tangqiwei on 2017/6/4.
 */

public class ResultSignStaffList extends ResultBase {
    public Data data;

    public static class Data{
        public long lastTimeStamp;//	最后时间戳	number
        public ArrayList<Students> students;//	学员列表	array<object>

        public static class Students{
            public long assignTime;//	签到时间	number
            public String comment;//	评价内容	string
            public int score;//	分数	number
            public String studentCode;//	学员编号	string
            public String studentName;//	学员姓名	string
        }
    }
}
