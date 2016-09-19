package cn.com.bluemoon.delivery.app.api.model.wash.incabinet;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2016/9/18.
 */
public class ResultCupboard extends ResultBase{
    /** 分拨柜号 */
    private String cupboardCode;

    public String getCupboardCode() {
        return cupboardCode;
    }
    public void setCupboardCode(String cupboardCode) {
        this.cupboardCode = cupboardCode;
    }
}
