package cn.com.bluemoon.delivery.app.api.model.clothing.collect;

/**
 * 有订单收衣订单
 * Created by lk on 2016/6/13.
 */
public class WithOrderClothingCollectOrder {
    /**
     * 订单类型(洗衣订单: order)
     */
    public final static String OUTERCODE_TYPE_ORDER = "order";
    /**
     * 订单类型(收衣单号:washOrder)
     */
    public final static String OUTERCODE_TYPE_WASHORDER = "washOrder";


    /**
     * 订单状态:待派单(客户端无用)
     */
    public final static  String WASH_STATUS_WAIT_DISPATCH = "WAIT_DISPATCH";
    /**
     * 订单状态:待接单
     */
    public final static  String WASH_STATUS_WAIT_ACCEPT = "WAIT_ACCEPT";
    /**
     * 订单状态:已接单
     */
    public final static  String WASH_STATUS_ALREADY_ACCEPT	 = "ALREADY_ACCEPT";
    /**
     * 订单状态:收衣中(开始收衣)
     */
    public final static  String WASH_STATUS_ANGEL_LAUNDRYING	 = "ANGEL_LAUNDRYING";
    /**
     * 订单状态:继续收衣
     */
    @Deprecated
    public final static  String WASH_STATUS_CONTINUE_LAUNDRYING	 = "CONTINUE_LAUNDRYING";
    /**
     * 订单状态:衣物转交
     */
    public final static  String WASH_STATUS_TRANSFER	 = "TRANSFER";
    /**
     * 订单状态:确认接收
     */
    public final static  String WASH_STATUS_RECEIVE	 = "RECEIVE";


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
     * 订单支付金额（分）
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
     * 洗衣服务订单状态（枚举）
     */
    private String washStatus;

    public String getWashStatus() {
        return washStatus;
    }

    public void setWashStatus(String washStatus) {
        this.washStatus = washStatus;
    }

    /**
     * 订单类型(洗衣订单: order，收衣单号:washOrder)
     */
    private String outerCodeType;

    public String getOuterCodeType() {
        return outerCodeType;
    }

    public void setOuterCodeType(String outerCodeType) {
        this.outerCodeType = outerCodeType;
    }

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

}
