package cn.com.bluemoon.delivery.app.api.model.hr.personinfo;

import java.io.Serializable;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 获取个人基本信息
 */
public class ResultGetBaseInfo extends ResultBase {
    /**
     * 个人基本信息
     */
    public BaseInfoBean baseInfo;

    /**
     * 员工编码
     */
    public String userCode;
    /**
     * 员工姓名
     */
    public String userName;

    public static class BaseInfoBean implements Serializable {
        /**
         * 户口性质
         */
        public String accountsType;
        /**
         * 银行卡号
         */
        public String bankNo;
        /**
         * 血型
         */
        public String blood;
        /**
         * 最高学历
         */
        public String educationHighest;
        /**
         * 公司邮箱
         */
        public String emailComp;
        /**
         * 毕业学校
         */
        public String gradSchool;
        /**
         * 户口地址
         */
        public String hkAddress;
        /**
         * 身份证
         */
        public String idcard;
        /**
         * 入职日期
         */
        public long inDate;
        /**
         * 专业名称
         */
        public String major;
        /**
         * 婚姻状况
         */
        public String marriage;
        /**
         * 通讯地址
         */
        public String txAddress;
    }

}
