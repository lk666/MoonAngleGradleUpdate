package cn.com.bluemoon.delivery.app.api.model.wash.manager;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by ljl on 2016/9/20.
 */
public class ResultExpressDetail extends ResultBase{


    private String expressStatus;

    private List<ExpressInfoListBean> expressInfoList;

    public String getExpressStatus() {
        return expressStatus;
    }

    public void setExpressStatus(String expressStatus) {
        this.expressStatus = expressStatus;
    }

    public List<ExpressInfoListBean> getExpressInfoList() {
        return expressInfoList;
    }

    public void setExpressInfoList(List<ExpressInfoListBean> expressInfoList) {
        this.expressInfoList = expressInfoList;
    }

    public static class ExpressInfoListBean {
        private String opDetails;
        private long opTime;

        public String getOpDetails() {
            return opDetails;
        }

        public void setOpDetails(String opDetails) {
            this.opDetails = opDetails;
        }

        public long getOpTime() {
            return opTime;
        }

        public void setOpTime(long opTime) {
            this.opTime = opTime;
        }
    }
}
