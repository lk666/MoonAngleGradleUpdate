/**
 * end deliver goods detail
 * wangshanhai
 */
package cn.com.bluemoon.delivery.app.api.model.inventory;

import java.io.Serializable;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;


public class ResultEndDeliverOrderBean extends ResultBase implements Serializable {
    private ResultEndDeliverOrderVo outOrderDetail;

    public ResultEndDeliverOrderVo getOutOrderDetail() {
        return outOrderDetail;
    }

    public void setOutOrderDetail(ResultEndDeliverOrderVo outOrderDetail) {
        this.outOrderDetail = outOrderDetail;
    }
}


  
