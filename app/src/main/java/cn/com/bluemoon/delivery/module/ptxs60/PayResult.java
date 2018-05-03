package cn.com.bluemoon.delivery.module.ptxs60;

import java.io.Serializable;

/**
 * 通知列表页面刷新
 * Created by tangqiwei on 2018/5/2.
 */

public class PayResult implements Serializable {
    public boolean isSuccess;

    public PayResult() {

    }

    public PayResult(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
}
