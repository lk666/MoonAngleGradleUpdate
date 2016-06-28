package cn.com.bluemoon.delivery.app.api.model.team;

/**
 * Created by bm on 2016/6/22.
 */
public class Emp {
    /** 所属机构编码 */
    private String bpCode;
    /** 所属机构名称 */
    private String bpName;
    /** 人员编码 */
    private String empCode;
    /** 人员名称 */
    private String empName;
    private boolean isCheck;

    public boolean isCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
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
