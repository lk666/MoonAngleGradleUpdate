package cn.com.bluemoon.delivery.app.api.model.wash.enterprise;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by liangjiangli on 2017/4/28.
 */

public class ResultEnterpriseList extends ResultBase {

    public int count;
    public List<EnterpriseOrderListBean> enterpriseOrderList;
    public long timestamp;

    public static class EnterpriseOrderListBean {

        public int actualCount;
        public String branchCode;
        public String branchName;
        public String collectBrcode;
        public long createTime;
        public String employeeCode;
        public String employeeName;
        public String outerCode;
        public int payTotal;
        public String state;
        public String stateName;
    }
}
