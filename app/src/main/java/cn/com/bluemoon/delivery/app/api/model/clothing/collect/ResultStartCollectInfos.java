package cn.com.bluemoon.delivery.app.api.model.clothing.collect;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * {@link cn.com.bluemoon.delivery.app.api.DeliveryApi#startCollectInfo(String, String, String, AsyncHttpResponseHandler)}
 * 服务器返回json
 * Created by lk on 2016/6/21.
 */
public class ResultStartCollectInfos extends ResultBase {
    /**
     * 洗衣单条码
     */
    private String collectBrcode;

    /**
     * 洗衣服务订单号
     */
    private String outerCode;

   private  String customerName;
    private  String customerPhone;
    /**
     * 省份
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 区/县
     */
    private String county;
    /**
     * 乡镇/街道
     */
    private String street;
    /**
     * 村/社区
     */
    private String village;
    /**
     * 详细地址
     */
    private String address;
    /**
     * 订单支付金额
     */
    private int payTotal;
    /**
     * 应收数量
     */
    private int receivableCount;
    /**
     * 实收数量
     */
    private int actualCount;
    /**
     * 是否加急
     */
    private int isUrgent;
    /**
     * 预约还衣时间
     */
    private long appointBackTime;
    /**
     * 衣物类型列表
     */
    List<OrderDetail> orderDetail;
    /**
     * 收衣明细列表
     */
    OrderReceiveItem orderReceive;

    /**
     * 收衣单号
     */
    private String collectCode;

    public String getCollectCode() {
        return collectCode;
    }

    public void setCollectCode(String collectCode) {
        this.collectCode = collectCode;
    }

    public String getOuterCode() {
        return outerCode;
    }

    public void setOuterCode(String outerCode) {
        this.outerCode = outerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPayTotal() {
        return payTotal;
    }

    public void setPayTotal(int payTotal) {
        this.payTotal = payTotal;
    }

    public int getReceivableCount() {
        return receivableCount;
    }

    public void setReceivableCount(int receivableCount) {
        this.receivableCount = receivableCount;
    }

    public String getCollectBrcode() {
        return collectBrcode;
    }

    public void setCollectBrcode(String collectBrcode) {
        this.collectBrcode = collectBrcode;
    }

    public int getActualCount() {
        return actualCount;
    }

    public void setActualCount(int actualCount) {
        this.actualCount = actualCount;
    }

    public int getIsUrgent() {
        return isUrgent;
    }

    public void setIsUrgent(int isUrgent) {
        this.isUrgent = isUrgent;
    }

    public long getAppointBackTime() {
        return appointBackTime;
    }

    public void setAppointBackTime(long appointBackTime) {
        this.appointBackTime = appointBackTime;
    }

    public List<OrderDetail> getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(List<OrderDetail> orderDetail) {
        this.orderDetail = orderDetail;
    }

    public OrderReceiveItem getOrderReceive() {
        return orderReceive;
    }

    public void setOrderReceive(OrderReceiveItem orderReceive) {
        this.orderReceive = orderReceive;
    }
}
