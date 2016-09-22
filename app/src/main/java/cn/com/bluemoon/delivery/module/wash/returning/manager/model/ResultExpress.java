package cn.com.bluemoon.delivery.module.wash.returning.manager.model;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by ljl on 2016/9/19.
 */
public class ResultExpress extends ResultBase{


    private int pageFlag;

    private List<ExpressListBean> expressList;

    public int getPageFlag() {
        return pageFlag;
    }

    public void setPageFlag(int pageFlag) {
        this.pageFlag = pageFlag;
    }

    public List<ExpressListBean> getExpressList() {
        return expressList;
    }

    public void setExpressList(List<ExpressListBean> expressList) {
        this.expressList = expressList;
    }

    public static class ExpressListBean {
        private int backOrderNum;
        private String companyCode;
        private String companyName;
        private String expressCode;

        public int getBackOrderNum() {
            return backOrderNum;
        }

        public void setBackOrderNum(int backOrderNum) {
            this.backOrderNum = backOrderNum;
        }

        public String getCompanyCode() {
            return companyCode;
        }

        public void setCompanyCode(String companyCode) {
            this.companyCode = companyCode;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getExpressCode() {
            return expressCode;
        }

        public void setExpressCode(String expressCode) {
            this.expressCode = expressCode;
        }
    }
}
