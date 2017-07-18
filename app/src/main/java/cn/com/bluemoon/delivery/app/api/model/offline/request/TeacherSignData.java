package cn.com.bluemoon.delivery.app.api.model.offline.request;

import java.util.List;

/**
 * 教师签到（扫学员二维码签到）
 * Created by tangqiwei on 2017/7/17.
 */

public class TeacherSignData {
//    courseCodes	课程编码	array<string>
    public List<String> courseCodes;
//    planCode	排课编码（非必填）	string
    public String planCode;
//    userInfo	用户信息	object
    public UserInfo userInfo;

    public TeacherSignData(String planCode,List<String> courseCodes,String userMark, String userType){
        this.courseCodes=courseCodes;
        this.planCode=planCode;
        this.userInfo=new UserInfo(userMark,userType);
    }


    public static class UserInfo{
        //    userMark	用户标示（编码/用户手机号码）	string
        public String userMark;
        //    userType	用户类型	string	外部：external，内部：employee
        public String userType;
        public UserInfo(String userMark, String userType){
            this.userMark=userMark;
            this.userType=userType;
        }
    }
}
