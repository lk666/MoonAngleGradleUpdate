package cn.com.bluemoon.delivery.app.api.model.wash.expressclosebox;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by ljl on 2016/10/15.
 */
public class ResultScanBackOrder extends ResultBase{
    private String boxCode;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }
}
