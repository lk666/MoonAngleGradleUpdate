package cn.com.bluemoon.delivery.app.api.model.clothing.collect;

/**
 * Created by liangjiangli on 2018/5/3.
 */

public class DispachInfo {

    private String dispachType;	//派单类型	string	@mock=$order('ADMIN_DISPACH','ANGEL_TRANSFER')
    private String opCode;	//操作人编号	string
    private String opName;	//操作人姓名	string
    private long opTime;	//操作时间	number
    private String remark;	//备注	string

    public String getDispachType() {
        return dispachType;
    }

    public void setDispachType(String dispachType) {
        this.dispachType = dispachType;
    }

    public String getOpCode() {
        return opCode;
    }

    public void setOpCode(String opCode) {
        this.opCode = opCode;
    }

    public String getOpName() {
        return opName;
    }

    public void setOpName(String opName) {
        this.opName = opName;
    }

    public long getOpTime() {
        return opTime;
    }

    public void setOpTime(long opTime) {
        this.opTime = opTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
