package cn.com.bluemoon.delivery.app.api.model.inventory;

import java.io.Serializable;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by summer on 2016/3/31.
 */
public class ResultDetail extends ResultBase implements Serializable {

    private String receiptCode;
    private String outCode;

    public String getReceiptCode() {
        return receiptCode;
    }

    public void setReceiptCode(String receiptCode) {
        this.receiptCode = receiptCode;
    }

    public String getOutCode() {
        return outCode;
    }

    public void setOutCode(String outCode) {
        this.outCode = outCode;
    }
}
