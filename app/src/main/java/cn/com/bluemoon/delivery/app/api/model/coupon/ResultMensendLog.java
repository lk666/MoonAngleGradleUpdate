package cn.com.bluemoon.delivery.app.api.model.coupon;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2016/4/20.
 */
public class ResultMensendLog extends ResultBase{

    private int total;
    private List<MensendLog> mensendLogs;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<MensendLog> getMensendLogs() {
        return mensendLogs;
    }

    public void setMensendLogs(List<MensendLog> mensendLogs) {
        this.mensendLogs = mensendLogs;
    }

}
