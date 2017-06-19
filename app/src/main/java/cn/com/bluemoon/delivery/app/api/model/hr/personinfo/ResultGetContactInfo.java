package cn.com.bluemoon.delivery.app.api.model.hr.personinfo;

import java.io.Serializable;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
* 获取联系方式信息
*/
public class ResultGetContactInfo extends ResultBase {
    /** 联系信息 */
    public ContactInfoBean contactInfo;

    public static class ContactInfoBean implements Serializable {
        /** 紧急联系人手机号 */
        public String contactMobile;
        /** 紧急联系人手机号2 */
        public String contactMobile2;
        /** 紧急联系人姓名 */
        public String contactName;
        /** 紧急联系人姓名2 */
        public String contactName2;
        /** 紧急联系人关系 */
        public String contactRelation;
        /** 紧急联系人关系2 */
        public String contactRelation2;
        /** 个人邮箱 */
        public String emailPers;
        /** 办公分机号 */
        public String officePhone;
        /** 办公点 */
        public String officePlace;
        /** 办公座位号 */
        public String officeSeat;
        /** 微信号 */
        public String weichat;
    }
}
