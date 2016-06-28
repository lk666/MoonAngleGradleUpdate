package cn.com.bluemoon.delivery.app.api.model.team;

/**
 * Created by bm on 2016/6/22.
 */
public class TeamGroup {
    /** 小组实际人口 */
    private int actualPopulation;
    /** 小组编码 */
    private String bpCode;
    /** 小组名称 */
    private String bpName;
    /** 组长 */
    private String chargeName;
    /** 小组规划人口 */
    private int planPopulation;

    public int getActualPopulation() {
        return actualPopulation;
    }
    public void setActualPopulation(int actualPopulation) {
        this.actualPopulation = actualPopulation;
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
    public String getChargeName() {
        return chargeName;
    }
    public void setChargeName(String chargeName) {
        this.chargeName = chargeName;
    }
    public int getPlanPopulation() {
        return planPopulation;
    }
    public void setPlanPopulation(int planPopulation) {
        this.planPopulation = planPopulation;
    }

}
