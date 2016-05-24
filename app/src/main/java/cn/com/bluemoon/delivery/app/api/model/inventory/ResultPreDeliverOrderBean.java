/**
 * to deliver order detail
 * wangshanhai
 */
package cn.com.bluemoon.delivery.app.api.model.inventory;

import java.io.Serializable;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;


public class ResultPreDeliverOrderBean extends ResultBase implements Serializable {
    private ResultPreDeliverOrderVo orderDetail;

    public ResultPreDeliverOrderVo getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(ResultPreDeliverOrderVo orderDetail) {
        this.orderDetail = orderDetail;
    }
}


  
