package cn.com.bluemoon.delivery.app.api.model.team;

/**
 * Created by bm on 2016/6/23.
 */
public class GroupDetail {
    /** 小组/人员编码 */
    private String bpCode;
    /** 小组/人员名称 */
    private String bpName;
    /** 社区编码 */
    private String communityCode;
    /** 社区名称 */
    private String communityName;
    /** 手机号码 */
    private String mobileNo;
    /** 职位名称 */
    private String posiName;
    /** 加入日期 */
    private String startDate;
    /** 工作时长 */
    private double workLength;
    /** 工作性质 */
    private String workType;

    public String getBpCode() {
        return bpCode;
    }

    public void setBpCode(String bpCode) {
        this.bpCode = bpCode;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getPosiName() {
        return posiName;
    }

    public void setPosiName(String posiName) {
        this.posiName = posiName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getBpName() {
        return bpName;
    }

    public void setBpName(String bpName) {
        this.bpName = bpName;
    }

    public String getCommunityCode() {
        return communityCode;
    }

    public void setCommunityCode(String communityCode) {
        this.communityCode = communityCode;
    }
}
