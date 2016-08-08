package cn.com.bluemoon.delivery.app.api.model.team;

import java.io.Serializable;

/**
 * Created by bm on 2016/6/22.
 */
public class EmpEdit implements Serializable {
    /**
     * 所属小组编码
     */
    private String groupCode;
    /**
     * 所属小组名称
     */
    private String groupName;
    /**
     * 所属社区编码
     */
    private String communityCode;
    /**
     * 所属社区名称
     */
    private String communityName;
    /**
     * 人员编码
     */
    private String empCode;
    /**
     * 人员名称
     */
    private String empName;
    /**
     * 手机号码
     */
    private String mobileNo;
    /**
     * 人员关系类型<group>,<community>
     */
    private String relType;
    /**
     * 人员操作类型<<add><update>
     */
    private String type;

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getRelType() {
        return relType;
    }

    public void setRelType(String relType) {
        this.relType = relType;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getCommunityCode() {
        return communityCode;
    }

    public void setCommunityCode(String communityCode) {
        this.communityCode = communityCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
