package cn.com.bluemoon.delivery.app.api.model.offline;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by tangqiwei on 2017/6/4.
 */

public class ResultTeacherDetail extends ResultBase {
    public Data data;

    public static class Data {
        public String address;//	地址	string	@mock=
        public String contactsName;//	联系人名称	string	@mock=
        public String contactsPhone;//	联系人手机	number	@mock=
        public String courseCode;//	课程编号	string	@mock=
        public String courseName;//	课程名称	string	@mock=
        public long endTime;//	课程结束时间	number
        public int enrollNum;//	报名人数	number
        public int evaluateNum;//已评价人数	number
        public String planCode;//	排课编号	string	@mock=
        public String purpose;//	课程目的	string	@mock=
        public long realEndTime;//	实际结束时间	number
        public long realStartTime;//	实际上课时间	number
        public String room;//	培训室	string	@mock=
        public int signNum;//	签到人数	number
        public long startTime;//	开始上课时间	number	@mock=
        public String status;//	课程状态	string	@mock=
        public String teacherName;//	教师名称	string	@mock=
        public String topic;//	主题	string	@mock=
    }
}
