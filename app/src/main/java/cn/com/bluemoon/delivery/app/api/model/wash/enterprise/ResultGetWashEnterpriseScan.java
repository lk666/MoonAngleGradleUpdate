package cn.com.bluemoon.delivery.app.api.model.wash.enterprise;

import java.io.Serializable;
import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 8.03企业收衣扫一扫
 */
public class ResultGetWashEnterpriseScan extends ResultBase {
    /**
     * 专属卡金额
     */
    public AmountInfo amountInfo;
    /**
     * 员工信息
     */
    public Employee employeeInfo;
    /**
     * 订单信息
     */
    public EnterpriseOrderInfoBean enterpriseOrderInfo;
    /**
     * 机构列表（所有）
     */
    public List<BranchListBean> branchList;


    public static class EnterpriseOrderInfoBean implements Serializable {
        /**
         * 实收数量
         */
        public int actualCount;
        /**
         * 收衣袋
         */
        public String collectBrcode;
        /**
         * 订单单号
         */
        public String outerCode;
        /**
         * 备注
         */
        public String remark;
        /**
         * 衣物详情
         */
        public List<ClothesInfo> clothesDetails;
    }

    public static class BranchListBean {
        /**
         * 机构编码
         */
        public String branchCode;
        /**
         * 机构名称
         */
        public String branchName;
    }
}
