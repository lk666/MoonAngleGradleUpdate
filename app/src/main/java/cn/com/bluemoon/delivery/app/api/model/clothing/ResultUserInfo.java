package cn.com.bluemoon.delivery.app.api.model.clothing;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by allenli on 2016/6/22.
 */
public class ResultUserInfo extends ResultBase {
    private String empCode;
    private String  empName;
    private String phone;

    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
