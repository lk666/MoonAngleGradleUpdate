package cn.com.bluemoon.delivery.app.api.model.wash.appointment;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 7.4至尊洗衣商品列表
 */
public class ResultWashGoodsList extends ResultBase {

    /**
     * 商品列表
     */
    private List<TwoLevel> twoLevelList;

    public List<TwoLevel> getTwoLevelList() {
        return twoLevelList;
    }

    public void setTwoLevelList(List<TwoLevel> twoLevelList) {
        this.twoLevelList = twoLevelList;
    }
}

