package cn.com.bluemoon.delivery.app.api.model.ptxs60;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 【03】根据输入团购支数求和，返回对应的区间单价和总金额
 */
public class ResultGetUnitPriceByNum extends ResultBase {
    public long orderTotalMoney;
    public long orderTotalNum;
    public long orderUnitPrice;
}
  
