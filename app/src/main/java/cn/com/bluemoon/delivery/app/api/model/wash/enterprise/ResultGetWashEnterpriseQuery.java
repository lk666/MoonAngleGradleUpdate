package cn.com.bluemoon.delivery.app.api.model.wash.enterprise;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * 8.02手动搜索列表展示
 */
public class ResultGetWashEnterpriseQuery extends ResultBase {
    /**
     * 员工信息
     */
    public List<EmployeeListBean> employeeList;
    /**
     * 订单信息
     */
    public List<EnterpriseOrderListBean> enterpriseOrderList;
}
