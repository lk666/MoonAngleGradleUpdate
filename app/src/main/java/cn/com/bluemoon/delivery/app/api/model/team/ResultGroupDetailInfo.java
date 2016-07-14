package cn.com.bluemoon.delivery.app.api.model.team;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2016/6/23.
 */
public class ResultGroupDetailInfo extends ResultBase {
    /** 实际总人数 */
    private int actualTotalPopulation;
    /** 小组/社区编码 */
    private String bpCode;
    /** 小组名称 */
    private String bpName;
    /** 全职总人数 */
    private int fullTimeNumber;
    /** 小组/社区数组对象 */
    private List<GroupDetail> itemList;
    /** 兼职总人数 */
    private int partTimeNumber;
    /** 规划总人数 */
    private int planTotalPopulation;
    /** 最后一条记录时间戳 */
    private long timestamp;

    public int getActualTotalPopulation() {
        return actualTotalPopulation;
    }

    public void setActualTotalPopulation(int actualTotalPopulation) {
        this.actualTotalPopulation = actualTotalPopulation;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getPlanTotalPopulation() {
        return planTotalPopulation;
    }

    public void setPlanTotalPopulation(int planTotalPopulation) {
        this.planTotalPopulation = planTotalPopulation;
    }

    public int getPartTimeNumber() {
        return partTimeNumber;
    }

    public void setPartTimeNumber(int partTimeNumber) {
        this.partTimeNumber = partTimeNumber;
    }

    public List<GroupDetail> getItemList() {
        return itemList;
    }

    public void setItemList(List<GroupDetail> itemList) {
        this.itemList = itemList;
    }

    public int getFullTimeNumber() {
        return fullTimeNumber;
    }

    public void setFullTimeNumber(int fullTimeNumber) {
        this.fullTimeNumber = fullTimeNumber;
    }

    public String getBpName() {
        return bpName;
    }

    public void setBpName(String bpName) {
        this.bpName = bpName;
    }

    public String getBpCode() {
        return bpCode;
    }

    public void setBpCode(String bpCode) {
        this.bpCode = bpCode;
    }
}
