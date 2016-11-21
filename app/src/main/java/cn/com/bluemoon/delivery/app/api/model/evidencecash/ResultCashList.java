package cn.com.bluemoon.delivery.app.api.model.evidencecash;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by ljl on 2016/11/17.
 */
public class ResultCashList extends ResultBase {

    private List<CashListBean> cashList;

    public List<CashListBean> getCashList() {
        return cashList;
    }

    public void setCashList(List<CashListBean> cashList) {
        this.cashList = cashList;
    }
}
