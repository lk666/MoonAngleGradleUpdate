package cn.com.bluemoon.delivery.app.api.model.wash.enterprise;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by liangjiangli on 2017/4/28.
 */

public class ResultEnterpriseDetail extends ResultBase {

    public EmployeeInfoBean employeeInfo;
    public EnterpriseOrderInfoBean enterpriseOrderInfo;

    public static class EmployeeInfoBean {
        public String branchCode;
        public String branchName;
        public String employeeCode;
        public String employeeExtension;
        public String employeeName;
        public String employeePhone;
    }

    public static class EnterpriseOrderInfoBean {

        public int actualCount;
        public long cancelTime;
        public String collectBrcode;
        public long createTime;
        public int integralAmount;
        public String outerCode;
        public long payTime;
        public int payTotal;
        public String remark;
        public String state;
        public String stateName;
        public int sumAmount;
        public List<ClothesDetailsBean> clothesDetails;

        public static class ClothesDetailsBean {

            public String clothesId;
            public String imgPath;
            public int memberPrice;
            public String washCode;
            public String washName;
        }
    }
}
