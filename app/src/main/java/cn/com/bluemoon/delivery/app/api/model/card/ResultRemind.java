package cn.com.bluemoon.delivery.app.api.model.card;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.card.alarm.Remind;

/**
 * Created by allenli on 2016/9/7.
 */
public class ResultRemind extends ResultBase{
    private List<Remind> remindList;

    public List<Remind> getRemindList() {
        return remindList;
    }

    public void setRemindList(List<Remind> remindList) {
        this.remindList = remindList;
    }
}
