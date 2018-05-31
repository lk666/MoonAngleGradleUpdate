package cn.com.bluemoon.delivery.app.api.model.ptxs60;

import java.io.Serializable;
import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 【08】订单详情查看接口
 */
public class ResultGetOrderDetail extends ResultBase {
    public AddressInfoBean addressInfo;
    public String mendianCode;
    public String mendianName;
    public String pinTuanCode;
    public String pinTuanName;
    public String recommendCode;
    public String recommendName;
    public String storeCode;
    public String storeName;
    public long ordeUnitPrice;
    public List<OrderDetailBean> orderDetail;
    public boolean isStoreResource;
    public long orderTotalMoney;
    public long orderTotalNum;
    public String storeResourceInfo;
    public String orderCode;
    public String payStatus;
    public String payStatusDesc;

    public static class AddressInfoBean implements Serializable {
        public String cityCode;
        public String cityName;
        public String contactPhone;
        public String countryCode;
        public String countryName;
        public String provinceCode;
        public String provinceName;
        public String receiverAddress;
        public String receiverName;
    }

    public static class OrderDetailBean implements Serializable {
        public String productDesc;
        public String productNo;
        public long orderNum;
    }
}
  
