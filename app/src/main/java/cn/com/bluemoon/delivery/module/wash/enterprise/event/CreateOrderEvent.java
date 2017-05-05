package cn.com.bluemoon.delivery.module.wash.enterprise.event;

/**
 * 创建订单成功事件
 * Created by lk on 2017/5/5.
 */

public class CreateOrderEvent {
    private String outerCode;

    public CreateOrderEvent(String outerCode) {
        this.outerCode = outerCode;
    }
}
