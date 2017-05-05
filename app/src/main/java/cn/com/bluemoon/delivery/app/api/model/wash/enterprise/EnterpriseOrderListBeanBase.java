package cn.com.bluemoon.delivery.app.api.model.wash.enterprise;

import java.io.Serializable;

/**
 * 8.02手动搜索列表展示,订单信息
 */
public class EnterpriseOrderListBeanBase extends BranchBean {

    /** 收衣袋 */
    public String collectBrcode;
    /** 订单编码 */
    public String outerCode;
}