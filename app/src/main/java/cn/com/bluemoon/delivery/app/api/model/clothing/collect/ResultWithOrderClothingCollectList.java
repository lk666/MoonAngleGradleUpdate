package cn.com.bluemoon.delivery.app.api.model.clothing.collect;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 有订单收衣服务器返回json
 * Created by luokai on 2016/6/13.
 */
public class ResultWithOrderClothingCollectList extends ResultBase {
    private List<WithOrderClothingCollectOrder> orderInfos;

    public List<WithOrderClothingCollectOrder> getOrderInfos() {
        return orderInfos;
    }

    public void setOrderInfos(List<WithOrderClothingCollectOrder> orderInfos) {
        this.orderInfos = orderInfos;
    }
}
