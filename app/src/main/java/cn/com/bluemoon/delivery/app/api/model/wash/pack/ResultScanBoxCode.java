package cn.com.bluemoon.delivery.app.api.model.wash.pack;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2016/9/29.
 */
public class ResultScanBoxCode extends ResultBase{

    /** 衣物箱号 */
    private String boxCode;

    public String getBoxCode() {
        return boxCode;
    }
    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }


}
