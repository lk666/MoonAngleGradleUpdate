package cn.com.bluemoon.delivery.app.api.model.personalinfo;

import java.io.Serializable;

import cn.com.bluemoon.delivery.app.api.model.ptxs60.ResultGetBaseInfo;

/**
 * 订单明细
 */

public class RequestOrderDetail implements Serializable {
    public long orderNum;
    public String productNo;

    public RequestOrderDetail(ResultGetBaseInfo.OrderDetailBean bean) {
        if (bean != null) {
            orderNum = bean.curCount;
            productNo = bean.productNo;
        }
    }
}
