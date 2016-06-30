package cn.com.bluemoon.delivery.app.api.model.clothing;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * {@link cn.com.bluemoon.delivery.app.api.DeliveryApi#getClothesTypeInfos(String, String, AsyncHttpResponseHandler)}
 * 服务器返回json
 * Created by luokai on 2016/6/30.
 */
public class ResultClothesTypeInfos extends ResultBase {
    /**
     * 服务类型
     */
    private List<ClothesTypeInfo> clothesTypeInfos;

    public List<ClothesTypeInfo> getClothesTypeInfos() {
        return clothesTypeInfos;
    }

    public void setClothesTypeInfos(List<ClothesTypeInfo> clothesTypeInfos) {
        this.clothesTypeInfos = clothesTypeInfos;
    }
}
