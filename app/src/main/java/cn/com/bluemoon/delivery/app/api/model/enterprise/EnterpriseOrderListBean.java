package cn.com.bluemoon.delivery.app.api.model.enterprise;

import java.io.Serializable;

/**
 * 8.02手动搜索列表展示,订单信息
 */
public class EnterpriseOrderListBean implements Serializable {

    /** 机构编码 */
    public String branchCode;
    /** 机构名称 */
    public String branchName;
    /** 收衣袋 */
    public String collectBrcode;
    /** 订单编码 */
    public String outerCode;
}