package cn.com.bluemoon.delivery.app.api.model.wash.enterprise;

/**
 * Created by liangjiangli on 2017/5/4.
 */

public class QueryInfo {
    public QueryInfo(long startTime, long endTime, String enterpriseCode, String branchCode, String queryCode) {
        this.branchCode = branchCode;
        this.endTime = endTime;
        this.enterpriseCode = enterpriseCode;
        this.queryCode = queryCode;
        this.startTime = startTime;
    }
    public String branchCode;
    public long endTime;	//订单创建时间	number
    public String enterpriseCode;	//企业编码	string
    public String queryCode;	//员工姓名/员工分机号/员工手机号	string
    public long startTime;

}
