/**
 * to receive order detail
 * wangshanhai
 */
package cn.com.bluemoon.delivery.app.api.model.inventory;

import java.io.Serializable;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;


public class ResultPreReceiveOrderBean extends ResultBase implements Serializable {
    private ResultPreReceiveOrderVo orderDetail;

    public ResultPreReceiveOrderVo getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(ResultPreReceiveOrderVo orderDetail) {
        this.orderDetail = orderDetail;
    }
}


  
