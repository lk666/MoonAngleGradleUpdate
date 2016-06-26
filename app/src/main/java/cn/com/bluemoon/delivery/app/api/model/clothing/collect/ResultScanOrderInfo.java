package cn.com.bluemoon.delivery.app.api.model.clothing.collect;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.clothing.collect.ClothingPic;

/**
 * {@link cn.com.bluemoon.delivery.app.api.DeliveryApi#scanOrderInfo(String, String, AsyncHttpResponseHandler)}
 * 服务器返回json
 * Created by lk on 2016/6/24.
 */
public class ResultScanOrderInfo extends ResultBase {
    /**
     * 编码类型（订单编码）
     */
    public final static String CODE_TYPE_OUTER_CODE = "outerCode";
    /**
     * 编码类型（衣物编码）
     */
    public final static String CODE_TYPE_CLOTHES_CODE = "clothesCode";

    /**
     * 编码类型（订单编码：outerCode 衣物编码：clothesCode）
     */
    private String codeType;
    /**
     * 收衣单号
     */
    private String collectCode;

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public String getCollectCode() {
        return collectCode;
    }

    public void setCollectCode(String collectCode) {
        this.collectCode = collectCode;
    }

}
