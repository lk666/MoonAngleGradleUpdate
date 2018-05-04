package cn.com.bluemoon.delivery.app.api.model.ptxs60;

import java.io.Serializable;
import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 【06】订单列表发起支付查询
 */
public class ResultRePay extends ResultBase {
    /**
     * 支付信息
     */
    public PayInfo payInfo;

    public static class PayInfo implements Serializable{
        /**
         * 订单支付金额（分）
         */
        public int payTotal;
        /**
         * 支付流水号
         */
        public String paymentTransaction;
        /**
         * 支付方式
         */
        public List<Payment> paymentList;

        public static class Payment implements Serializable{
            /**
             * 支付平台名称
             */
            public String name;
            /**
             * 支付平台编码，参考枚举：支付方式payType
             */
            public String platform;
        }
    }
}