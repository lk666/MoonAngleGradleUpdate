package cn.com.bluemoon.delivery.app.api.model.clothing;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * {@link cn.com.bluemoon.delivery.app.api.DeliveryApi#registerCreatedCollectInfo(String, String,
 * long, String, List, String, String, String, String, int, int, String, String, String, String,
 * AsyncHttpResponseHandler)}
 * 服务器返回json
 * Created by luokai on 2016/6/29.
 */
public class ResultRegisterCreateCollectInfo extends ResultBase {

    /**
     * 收衣单号
     */
    private String collectCode;

    public String getCollectCode() {
        return collectCode;
    }

    public void setCollectCode(String collectCode) {
        this.collectCode = collectCode;
    }

}
