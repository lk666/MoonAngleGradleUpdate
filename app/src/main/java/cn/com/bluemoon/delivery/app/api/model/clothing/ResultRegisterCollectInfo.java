package cn.com.bluemoon.delivery.app.api.model.clothing;

import com.loopj.android.http.AsyncHttpResponseHandler;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;


/**
 * {@link cn.com.bluemoon.delivery.app.api.DeliveryApi#registerCollectInfo(String, String, String, String, String, int, String, int, String, String, String, AsyncHttpResponseHandler)}
 * 服务器返回json
 * Created by lk on 2016/7/5.
 */
public class ResultRegisterCollectInfo extends ResultBase {
    public String getCollectCode() {
        return collectCode;
    }

    public void setCollectCode(String collectCode) {
        this.collectCode = collectCode;
    }

    /**
     * 收衣单号
     */
    private String collectCode;
}
