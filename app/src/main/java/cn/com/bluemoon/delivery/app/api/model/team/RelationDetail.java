package cn.com.bluemoon.delivery.app.api.model.team;

/**
 * Created by bm on 2016/6/23.
 */
public class RelationDetail {
    /** 小组编码 */
    private String bpCode;
    /** 社区编码 */
    private String bpCode1;
    /** 小组编码 */
    private String bpName;
    /** 社区名称 */
    private String bpName1;
    /** 小组组长 */
    private String chargeName;
    /** 人员编码 */
    private String empCode;
    /** 人员名称 */
    private String empName;
    /** 结束日期 */
    private long endDate;
    /** 关系类型 */
    private String relType;
    /** 备注 */
    private String remark;
    /** 开始日期 */
    private long startDate;
    /** 作业时长（一位小数） */
    private double workLength;
    /** 作业类型 */
    private String workType;
    /** 手机号码 */
    private String mobileNo;

    public String getBpCode() {
        return bpCode;
    }

    public void setBpCode(String bpCode) {
        this.bpCode = bpCode;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public double getWorkLength() {
        return workLength;
    }

    public void setWorkLength(double workLength) {
        this.workLength = workLength;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public String getRelType() {
        return relType;
    }

    public void setRelType(String relType) {
        this.relType = relType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    public String getChargeName() {
        return chargeName;
    }

    public void setChargeName(String chargeName) {
        this.chargeName = chargeName;
    }

    public String getBpName1() {
        return bpName1;
    }

    public void setBpName1(String bpName1) {
        this.bpName1 = bpName1;
    }

    public String getBpName() {
        return bpName;
    }

    public void setBpName(String bpName) {
        this.bpName = bpName;
    }

    public String getBpCode1() {
        return bpCode1;
    }

    public void setBpCode1(String bpCode1) {
        this.bpCode1 = bpCode1;
    }
}
