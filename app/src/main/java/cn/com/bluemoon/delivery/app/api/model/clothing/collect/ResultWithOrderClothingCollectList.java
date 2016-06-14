package cn.com.bluemoon.delivery.app.api.model.clothing.collect;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

// TODO: lk 2016/6/13 接口待定 
/**
 * 有订单收衣服务器返回json（不包含timestamp）
 * Created by luokai on 2016/6/13.
 */
public class ResultWithOrderClothingCollectList extends ResultBase {
   private long timestamp;
     private List<WithOrderClothingCollectOrder> orderList;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<WithOrderClothingCollectOrder> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<WithOrderClothingCollectOrder> orderList) {
        this.orderList = orderList;
    }
}
