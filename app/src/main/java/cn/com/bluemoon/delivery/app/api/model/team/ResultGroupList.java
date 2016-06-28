package cn.com.bluemoon.delivery.app.api.model.team;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2016/6/22.
 */
public class ResultGroupList extends ResultBase{

    /** 实际总人数 */
    private int actualTotalPopulation;
    /** 小组数组对象 */
    private List<TeamGroup> itemList;
    /** 规划总人数 */
    private int planTotalPopulation;
    /** 当前登录人角色编码<CEO><chargeMan> */
    private String roleCode;
    /** 最后一条数组的时间戳 */
    private long timestamp;
    /** 总小组数量 */
    private int totalGroup;

    public int getActualTotalPopulation() {
        return actualTotalPopulation;
    }
    public void setActualTotalPopulation(int actualTotalPopulation) {
        this.actualTotalPopulation = actualTotalPopulation;
    }
    public boolean getIsSuccess() {
        return isSuccess;
    }
    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
    public List<TeamGroup> getItemList() {
        return itemList;
    }
    public void setItemList(List<TeamGroup> itemList) {
        this.itemList = itemList;
    }
    public int getPlanTotalPopulation() {
        return planTotalPopulation;
    }
    public void setPlanTotalPopulation(int planTotalPopulation) {
        this.planTotalPopulation = planTotalPopulation;
    }
    public String getRoleCode() {
        return roleCode;
    }
    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public int getTotalGroup() {
        return totalGroup;
    }
    public void setTotalGroup(int totalGroup) {
        this.totalGroup = totalGroup;
    }

}
