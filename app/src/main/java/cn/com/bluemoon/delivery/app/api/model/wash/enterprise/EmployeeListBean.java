package cn.com.bluemoon.delivery.app.api.model.wash.enterprise;

import java.io.Serializable;

public class EmployeeListBean implements Serializable {

        /** 机构编码 */
        public String branchCode;
        /** 机构名称 */
        public String branchName;
        /** 账户id */
        public String employeeCode;
        /** 分机号 */
        public String employeeExtension;
        /** 员工姓名 */
        public String employeeName;
        /** 员工手机号 */
        public String employeePhone;
    }