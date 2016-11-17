package cn.com.bluemoon.delivery.app.api.model.evidencecash;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by ljl on 2016/11/17.
 */
public class ResultSaveCashInfo extends ResultBase{

    private long tradePay;
    private String payInfo;
    private List<?> payInfoObj;

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

    public List<?> getPayInfoObj() {
        return payInfoObj;
    }

    public void setPayInfoObj(List<?> payInfoObj) {
        this.payInfoObj = payInfoObj;
    }
}
