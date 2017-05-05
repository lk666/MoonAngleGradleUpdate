package cn.com.bluemoon.delivery.app.api.model.wash.enterprise;

import java.io.Serializable;

/**
 * 专属卡金额
 */

public class AmountInfo implements Serializable{
    /**
     * 账户余额
     */
    public int accountBalance;
    /**
     * 剩余点值
     */
    public int integralBalance;

}
