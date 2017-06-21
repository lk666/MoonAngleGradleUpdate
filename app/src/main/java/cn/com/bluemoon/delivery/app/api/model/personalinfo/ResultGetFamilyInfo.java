package cn.com.bluemoon.delivery.app.api.model.personalinfo;

import java.io.Serializable;
import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 获取家庭情况
 * Created by liangjiangli on 2017/6/15.
 */

public class ResultGetFamilyInfo extends ResultBase{

    public List<FamilyListBean> familyList;

    public static class FamilyListBean implements Serializable {
        /**
         * birthday 生日 long
         * empId 员工Id int
         * familyId 家庭Id int
         * gender 性别 String 必填
         * menberPosition 职位 string
         * name 名 String
         * relationship 关系 String 必填
         * surname 姓 String 必填
         * workPlace 工作单位 String
         */
        public long birthday;
        public int empId;
        public String familyId;
        public String fullName;
        public String gender;
        public String menberPosition;
        public String name;
        public String relationship;
        public String surname;
        public String workPlace;

    }
}
