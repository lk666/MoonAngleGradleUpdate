package cn.com.bluemoon.delivery.app.api.model.wash.enterprise;

import java.io.Serializable;
import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by liangjiangli on 2017/4/28.
 */

public class ResultEnterpriseDetail extends ResultBase {

    public Employee employeeInfo;
    public EnterpriseOrderInfoBean enterpriseOrderInfo;


    public static class EnterpriseOrderInfoBean implements Serializable{

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
        public List<ClothesInfo> clothesDetails;
    }
}
