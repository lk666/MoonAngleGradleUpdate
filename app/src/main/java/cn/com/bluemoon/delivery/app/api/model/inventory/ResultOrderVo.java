package cn.com.bluemoon.delivery.app.api.model.inventory;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.entity.BaseAPIResult;

/**
 * Created by allenli on 2016/3/23.
 */
public class ResultOrderVo extends ResultBase {

    private int orderTotalNum;
    private long orderTotalMoney;
    private double orderTotalCase;
   private List<OrderVo> orderList;

    public List<OrderVo> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<OrderVo> orderList) {
        this.orderList = orderList;
    }

    public int getOrderTotalNum() {
        return orderTotalNum;
    }

    public void setOrderTotalNum(int orderTotalNum) {
        this.orderTotalNum = orderTotalNum;
    }

    public long getOrderTotalMoney() {
        return orderTotalMoney;
    }

    public void setOrderTotalMoney(long orderTotalMoney) {
        this.orderTotalMoney = orderTotalMoney;
    }

    public double getOrderTotalCase() {
        return orderTotalCase;
    }

    public void setOrderTotalCase(double orderTotalCase) {
        this.orderTotalCase = orderTotalCase;
    }
}
