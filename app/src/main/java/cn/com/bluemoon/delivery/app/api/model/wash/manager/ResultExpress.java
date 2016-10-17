package cn.com.bluemoon.delivery.app.api.model.wash.manager;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by ljl on 2016/9/19.
 */
public class ResultExpress extends ResultBase{


    private long pageFlag;
    private int expressSum;

    public int getExpressSum() {
        return expressSum;
    }

    public void setExpressSum(int expressSum) {
        this.expressSum = expressSum;
    }

    private List<ExpressListBean> expressList;

    public long getPageFlag() {
        return pageFlag;
    }

    public void setPageFlag(long pageFlag) {
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
        private String receiver;//	收货人
        private String receiverCode; //编号

        public String getReceiver() {
            return receiver;
        }

        public void setReceiver(String receiver) {
            this.receiver = receiver;
        }

        public String getReceiverCode() {
            return receiverCode;
        }

        public void setReceiverCode(String receiverCode) {
            this.receiverCode = receiverCode;
        }


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
