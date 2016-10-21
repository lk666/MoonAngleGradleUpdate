package cn.com.bluemoon.delivery.app.api.model.wash.clothescheck;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 8.1还衣清点-封箱标签服务器返回json
 */
public class ResultCloseBoxSign extends ResultBase {

    /**
     * 还衣单号列表
     */
    private ArrayList<BackOrder> backOrderList;

    public ArrayList<BackOrder> getBackOrderList() {
        return backOrderList;
    }

    public void setBackOrderList(ArrayList<BackOrder> backOrderList) {
        this.backOrderList = backOrderList;
    }
}
