package cn.com.bluemoon.delivery.app.api.model.clothing.collect;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * {@link cn.com.bluemoon.delivery.app.api.DeliveryApi#startCollectInfo(String, String, String, AsyncHttpResponseHandler)}
 * 服务器返回json
 * Created by lk on 2016/6/21.
 */
public class ResultStartCollectInfo extends ResultBase {

    /**
     * 洗衣服务订单号
     */
    private String outerCode;

    public String getOuterCode() {
        return outerCode;
    }

    public void setOuterCode(String outerCode) {
        this.outerCode = outerCode;
    }

    /**
     * 消费者姓名
     */
    private String customerName;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * 消费者电话
     */
    private String customerPhone;

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    /**
     * 省份
     */
    private String province;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * 市
     */
    private String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 区/县
     */
    private String county;

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    /**
     * 乡镇/街道
     */
    private String street;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * 村/社区
     */
    private String village;

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    /**
     * 详细地址
     */
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 订单支付金额
     */
    private int payTotal;

    public int getPayTotal() {
        return payTotal;
    }

    public void setPayTotal(int payTotal) {
        this.payTotal = payTotal;
    }

    /**
     * 应收数量
     */
    private int receivableCount;

    public int getReceivableCount() {
        return receivableCount;
    }

    public void setReceivableCount(int receivableCount) {
        this.receivableCount = receivableCount;
    }

    /**
     * 实收数量
     */
    private int actualCount;

    public int getActualCount() {
        return actualCount;
    }

    public void setActualCount(int actualCount) {
        this.actualCount = actualCount;
    }

    /**
     * 是否加急
     */
    private int isUrgent;

    public int getIsUrgent() {
        return isUrgent;
    }

    public void setIsUrgent(int isUrgent) {
        this.isUrgent = isUrgent;
    }

    /**
     * 预约还衣时间
     */
    private long appointBackTime;

    public long getAppointBackTime() {
        return appointBackTime;
    }

    public void setAppointBackTime(long appointBackTime) {
        this.appointBackTime = appointBackTime;
    }

    /**
     * 衣物类型列表
     */
    List<OrderDetailItem> orderDetail;

    public List<OrderDetailItem> getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(List<OrderDetailItem> orderDetail) {
        this.orderDetail = orderDetail;
    }

    /**
     * 收衣明细列表
     */
    OrderReceiveItem orderReceive;

    public OrderReceiveItem getOrderReceive() {
        return orderReceive;
    }

    public void setOrderReceive(OrderReceiveItem orderReceive) {
        this.orderReceive = orderReceive;
    }
}
