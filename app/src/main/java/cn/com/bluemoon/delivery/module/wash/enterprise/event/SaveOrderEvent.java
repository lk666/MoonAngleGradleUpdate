package cn.com.bluemoon.delivery.module.wash.enterprise.event;

/**
 * 保存订单成功事件
 * Created by lk on 2017/5/5.
 */

public class SaveOrderEvent extends CreateOrderEvent {

    public SaveOrderEvent(String outerCode) {
        super(outerCode);
    }
}
