package cn.com.bluemoon.delivery.app.api.model.team;

import java.util.List;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;

/**
 * Created by bm on 2016/6/23.
 */
public class ResultPersonnelAreaList extends ResultBase {
    /** 人员编码 */
    private String empCode;
    /** 人员名称 */
    private String empName;
    /** 结果数组对象 */
    private List<PersonnelArea> itemList;
    /** 最后一条记录时间戳 */
    private long timestamp;
    /** 总户数 */
    private int totalFamily;

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
    public List<PersonnelArea> getItemList() {
        return itemList;
    }
    public void setItemList(List<PersonnelArea> itemList) {
        this.itemList = itemList;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public int getTotalFamily() {
        return totalFamily;
    }
    public void setTotalFamily(int totalFamily) {
        this.totalFamily = totalFamily;
    }

}
