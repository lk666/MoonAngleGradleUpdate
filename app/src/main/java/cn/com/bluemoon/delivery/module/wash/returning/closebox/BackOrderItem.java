package cn.com.bluemoon.delivery.module.wash.returning.closebox;

import java.io.Serializable;

/**
 * 清点还衣单的列表项
 * Created by lk on 2016/9/21.
 */
public class BackOrderItem implements Serializable {
    public String code;
    /**
     * 0-未扫描；1-已扫描
     */
    public int state = 0;

    public BackOrderItem(String code) {
        this.code = code;
        this.state = 0;
    }
}
