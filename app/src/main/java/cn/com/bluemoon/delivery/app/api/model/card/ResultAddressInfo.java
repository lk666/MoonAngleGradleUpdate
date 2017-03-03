package cn.com.bluemoon.delivery.app.api.model.card;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by allenli on 2017/2/20.
 */
public class ResultAddressInfo extends ResultBase {
    private  AddressInfo addressInfo;

    public AddressInfo getAddressInfo() {
        return addressInfo;
    }

    public void setAddressInfo(AddressInfo addressInfo) {
        this.addressInfo = addressInfo;
    }
}
