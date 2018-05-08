package cn.com.bluemoon.delivery.app.api.model.personalinfo;

import android.text.TextUtils;

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
            try {
                if (TextUtils.isEmpty(bean.curCount)) {
                    orderNum = 0;
                } else {
                    orderNum = Integer.parseInt(bean.curCount);
                }
            } catch (Exception e) {
                orderNum = 0;
            }
            productNo = bean.productNo;
        }
    }
}
