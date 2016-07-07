package cn.com.bluemoon.delivery.app.api.model.clothing.collect;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * {@link cn.com.bluemoon.delivery.app.api.DeliveryApi#getOuterOrderInfo(String, String, AsyncHttpResponseHandler)}
 * 服务器返回json
 * Created by lk on 2016/6/27.
 */
public class ResultOuterOrderInfo extends ResultBase {

    /**
     * 详细地址
     */
    private String address;
    /**
     * 市
     */
    private String city;
    /**
     * 区/县
     */
    private String county;

    /**
     * 洗衣服务类型列表
     */
    private List<OuterOrderDetail> orderDetail;

    /**
     * 收衣单列表
     */
    private List<OuterOrderReceive> orderReceive;

    /**
     * 洗衣服务订单号
     */
    private String outerCode;
    /**
     * 订单支付金额
     */
    private int payTotal;
    /**
     * 省份
     */
    private String province;
    /**
     * 应收数量
     */
    private int receivableCount;
    /**
     * 收货人姓名
     */
    private String receiveName;
    /**
     * 收货人电话
     */
    private String receivePhone;
    /**
     * 乡镇/街道
     */
    private String street;
    /**
     * 村/社区
     */
    private String village;

    public String getFullAddress() {
        return province + city + county + street + village + address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getOuterCode() {
        return outerCode;
    }

    public void setOuterCode(String outerCode) {
        this.outerCode = outerCode;
    }

    public int getPayTotal() {
        return payTotal;
    }

    public void setPayTotal(int payTotal) {
        this.payTotal = payTotal;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getReceivableCount() {
        return receivableCount;
    }

    public void setReceivableCount(int receivableCount) {
        this.receivableCount = receivableCount;
    }

    public String getReceiveName() {
        return receiveName;
    }

    public void setReceiveName(String receiveName) {
        this.receiveName = receiveName;
    }

    public String getReceivePhone() {
        return receivePhone;
    }

    public void setReceivePhone(String receivePhone) {
        this.receivePhone = receivePhone;
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

    public List<OuterOrderDetail> getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(List<OuterOrderDetail> orderDetail) {
        this.orderDetail = orderDetail;
    }

    public List<OuterOrderReceive> getOrderReceive() {
        return orderReceive;
    }

    public void setOrderReceive(List<OuterOrderReceive> orderReceive) {
        this.orderReceive = orderReceive;
    }
}
