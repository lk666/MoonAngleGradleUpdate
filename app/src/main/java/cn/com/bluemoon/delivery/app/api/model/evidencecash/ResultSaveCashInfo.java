package cn.com.bluemoon.delivery.app.api.model.evidencecash;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.entity.WXPayInfo;

/**
 * Created by ljl on 2016/11/17.
 */
public class ResultSaveCashInfo extends ResultBase{

    private long tradePay;
    private String payInfo;
    private WXPayInfo payInfoObj;

    public long getTradePay() {
        return tradePay;
    }

    public void setTradePay(long tradePay) {
        this.tradePay = tradePay;
    }

    public String getPayInfo() {
        return payInfo;
    }

    public void setPayInfo(String payInfo) {
        this.payInfo = payInfo;
    }

    public WXPayInfo getPayInfoObj() {
        return payInfoObj;
    }

    public void setPayInfoObj(WXPayInfo payInfoObj) {
        this.payInfoObj = payInfoObj;
    }
}
