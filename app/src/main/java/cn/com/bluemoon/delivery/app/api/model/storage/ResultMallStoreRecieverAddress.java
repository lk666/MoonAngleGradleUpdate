package cn.com.bluemoon.delivery.app.api.model.storage;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by allenli on 2016/3/29.
 */
public class ResultMallStoreRecieverAddress extends ResultBase {
    private List<MallStoreRecieverAddress> addressList;

    public List<MallStoreRecieverAddress> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<MallStoreRecieverAddress> addressList) {
        this.addressList = addressList;
    }
}
