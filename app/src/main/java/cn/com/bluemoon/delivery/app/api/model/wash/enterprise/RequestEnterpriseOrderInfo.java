package cn.com.bluemoon.delivery.app.api.model.wash.enterprise;

import java.io.Serializable;

/**
 * 8.04企业收衣创建订单
 */

public class RequestEnterpriseOrderInfo implements Serializable {
    public RequestEnterpriseOrderInfo(String branchCode, String collectBrcode, String
            employeeCode, String employeeExtension, String remark) {
        this.branchCode = branchCode;
        this.collectBrcode = collectBrcode;
        this.employeeCode = employeeCode;
        this.employeeExtension = employeeExtension;
        this.remark = remark;
    }

    /**机构编码*/
    public String branchCode;
    /**收衣袋*/
    public String collectBrcode;
    /**账户id*/
    public String employeeCode;
    /**分机号*/
    public String employeeExtension;
    /**备注*/
    public String remark;
}
