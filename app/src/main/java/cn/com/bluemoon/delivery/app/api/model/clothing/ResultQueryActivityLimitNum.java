package cn.com.bluemoon.delivery.app.api.model.clothing;

import com.loopj.android.http.AsyncHttpResponseHandler;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * {@link cn.com.bluemoon.delivery.app.api.DeliveryApi#queryActivityLimitNum(String, String, AsyncHttpResponseHandler)}
 * 服务器返回json
 * Created by luokai on 2016/6/28.
 */
public class ResultQueryActivityLimitNum extends ResultBase {
    /**
     * 限制数量
     */
    private int limitNum;

    public int getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(int limitNum) {
        this.limitNum = limitNum;
    }
}
