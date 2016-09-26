package cn.com.bluemoon.delivery.module.card.alarm;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by allenli on 2016/9/19.
 */
public class ResultRemindId extends ResultBase {
    private long 	remindId;

    public long getRemindId() {
        return remindId;
    }

    public void setRemindId(long remindId) {
        this.remindId = remindId;
    }
}
