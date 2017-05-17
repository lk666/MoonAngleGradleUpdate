package cn.com.bluemoon.delivery.module.wash.enterprise.event;

/**
 * 提交扣款成功事件
 * Created by lk on 2017/5/5.
 */

public class ConfirmEvent {
    private String outerCode;private boolean isSuccess;

    public ConfirmEvent(String outerCode) {
       this(outerCode, true);
    }
    public ConfirmEvent(String outerCode, boolean isSuccess) {
        this.outerCode = outerCode;
        this.isSuccess = isSuccess;
    }
}
