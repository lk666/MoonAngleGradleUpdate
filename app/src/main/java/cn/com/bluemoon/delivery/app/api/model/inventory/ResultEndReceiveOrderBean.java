/**
 * end receive goods detail
 * wangshanhai
 */
package cn.com.bluemoon.delivery.app.api.model.inventory;

import java.io.Serializable;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;


public class ResultEndReceiveOrderBean extends ResultBase implements Serializable {
    private ResultEndReceiveOrderVo receiptOrderDetail;

    public ResultEndReceiveOrderVo getReceiptOrderDetail() {
        return receiptOrderDetail;
    }

    public void setReceiptOrderDetail(ResultEndReceiveOrderVo receiptOrderDetail) {
        this.receiptOrderDetail = receiptOrderDetail;
    }
}


  
