package cn.com.bluemoon.delivery.app.api.model.personalinfo;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 获取住址信息
 * Created by liangjiangli on 2017/6/15.
 */

public class ResultGetAddressInfo extends ResultBase{

    public AddressInfoBean addressInfo;

    public static class AddressInfoBean {
        /**
         * address	详细地址	string
         * carWait	上班等车点	string
         * cityCode	城市编码	string
         * cityName	城市名称	string
         * countyCode	区县编码	string
         * countyName	区县名称	string
         * provinceCode	省份编码	string
         * provinceName	省份名称	string
         */
        public String address;
        public String carWait;
        public String cityCode;
        public String cityName;
        public String countyCode;
        public String countyName;
        public String provinceCode;
        public String provinceName;
    }
}
