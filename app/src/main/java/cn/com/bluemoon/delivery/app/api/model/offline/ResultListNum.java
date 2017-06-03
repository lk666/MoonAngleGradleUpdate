package cn.com.bluemoon.delivery.app.api.model.offline;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 获取列表角标 培训授课
 * Created by tangqiwei on 2017/6/3.
 */

public class ResultListNum extends ResultBase {
    public Data data;

    public static class Data{
        public StudentAndTeacher student;
        public StudentAndTeacher teacher;

        public static class StudentAndTeacher{
            public int  endClassNum;//	已完成数量	number
            public int  inClassNum;//	待评价数量	number
            public int  waitingClassNum;//	待上课数量	number
        }

    }

}
