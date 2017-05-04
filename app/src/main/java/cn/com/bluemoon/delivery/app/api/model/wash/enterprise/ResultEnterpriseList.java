package cn.com.bluemoon.delivery.app.api.model.wash.enterprise;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by liangjiangli on 2017/4/28.
 */

public class ResultEnterpriseList extends ResultBase {

    private int count;
    private List<EnterpriseOrderListBean> enterpriseOrderList;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<EnterpriseOrderListBean> getEnterpriseOrderList() {
        return enterpriseOrderList;
    }

    public void setEnterpriseOrderList(List<EnterpriseOrderListBean> enterpriseOrderList) {
        this.enterpriseOrderList = enterpriseOrderList;
    }

    public static class EnterpriseOrderListBean {

        private int actualCount;
        private String branchCode;
        private String branchName;
        private String collectBrcode;
        private long createTime;
        private String employeeCode;
        private String employeeName;
        private String outerCode;
        private int payTotal;
        private String state;
        private String stateName;

        public int getActualCount() {
            return actualCount;
        }

        public void setActualCount(int actualCount) {
            this.actualCount = actualCount;
        }

        public String getBranchCode() {
            return branchCode;
        }

        public void setBranchCode(String branchCode) {
            this.branchCode = branchCode;
        }

        public String getBranchName() {
            return branchName;
        }

        public void setBranchName(String branchName) {
            this.branchName = branchName;
        }

        public String getCollectBrcode() {
            return collectBrcode;
        }

        public void setCollectBrcode(String collectBrcode) {
            this.collectBrcode = collectBrcode;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public String getEmployeeCode() {
            return employeeCode;
        }

        public void setEmployeeCode(String employeeCode) {
            this.employeeCode = employeeCode;
        }

        public String getEmployeeName() {
            return employeeName;
        }

        public void setEmployeeName(String employeeName) {
            this.employeeName = employeeName;
        }

        public String getOuterCode() {
            return outerCode;
        }

        public void setOuterCode(String outerCode) {
            this.outerCode = outerCode;
        }

        public int getPayTotal() {
            return payTotal;
        }

        public void setPayTotal(int payTotal) {
            this.payTotal = payTotal;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getStateName() {
            return stateName;
        }

        public void setStateName(String stateName) {
            this.stateName = stateName;
        }
    }
}
