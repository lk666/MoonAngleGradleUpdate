package cn.com.bluemoon.delivery.module.wash.enterprise.event;

/**
 * 提交扣款成功事件
 * Created by lk on 2017/5/5.
 */

public class ConfirmEvent {
    private String outerCode;

    public ConfirmEvent(String outerCode) {
        this.outerCode = outerCode;
    }
}
