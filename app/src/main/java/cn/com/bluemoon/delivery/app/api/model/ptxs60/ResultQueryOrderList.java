package cn.com.bluemoon.delivery.app.api.model.ptxs60;

import java.io.Serializable;
import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

public class ResultQueryOrderList extends ResultBase {
    public static final String PAY_STATUS_WAIT = "wait";
    public static final String PAY_STATUS_SUCCESS = "success";
    public static final String PAY_STATUS_FAIL = "fail";
    public static final String PAY_STATUS_CANCEL  = "cancel ";
    public List<OrderListBean> orderList;

    public static class OrderListBean implements Serializable{
        public String orderCode;
        public long orderPayTime;
        public String orderSeq;
        public long orderTotalMoney;
        public long orderTotalNum;
        public String payStatus;
        public List<OrderDetailBean> orderDetail;

        public static class OrderDetailBean implements Serializable{
            public long orderNum;
            public String productDesc;
            public String productNo;
        }
    }
}
  
