package cn.com.bluemoon.delivery.app.api.model.team;

import java.io.Serializable;

/**
 * Created by bm on 2016/6/22.
 */
public class Emp implements Serializable{
    /** 所属机构编码 */
    private String bpCode;
    /** 所属机构名称 */
    private String bpName;
    /** 人员编码 */
    private String empCode;
    /** 人员名称 */
    private String empName;
    /** 手机号码*/
    private String mobileNo;
    private boolean isCheck;

    public boolean isCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getBpCode() {
        return bpCode;
    }
    public void setBpCode(String bpCode) {
        this.bpCode = bpCode;
    }
    public String getBpName() {
        return bpName;
    }
    public void setBpName(String bpName) {
        this.bpName = bpName;
    }
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

}
