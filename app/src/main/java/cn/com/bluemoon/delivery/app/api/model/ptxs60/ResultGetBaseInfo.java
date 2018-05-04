package cn.com.bluemoon.delivery.app.api.model.ptxs60;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

public class ResultGetBaseInfo extends ResultBase {
    public AddressInfoBean addressInfo;
    public String mendianCode;
    public String mendianName;
    public String recommendCode;
    public String recommendName;
    public String storeCode;
    public String storeName;
    public List<OrderDetailBean> orderDetail;

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
        @JSONField(serialize = false)
        public int curCount = 0;
    }
}
  
