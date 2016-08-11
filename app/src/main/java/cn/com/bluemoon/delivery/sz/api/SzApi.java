package cn.com.bluemoon.delivery.sz.api;

import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.HashMap;
import java.util.Map;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.app.api.ApiHttpClient;

/**
 * Created by dujiande on 2016/8/11.
 */
public class SzApi {

    private static final String HOST="http://192.168.236.1:9002/mockjsdata/15/";

    /*  获取用户单日日程列表 */
    /*
        optStaffNum	操作员工编号	string
        scheduleDay	排期日期	string
        scheduleType	-1 对应所有，日程类型【 0-任务日程，1-会议日程】
        staffNum	员工编号	string
        token	token身份检验码	string	必填
     */
    public static void userSchDay(String optStaffNum,String scheduleDay,int scheduleType,
                                  String staffNum,
                                  String token,
                                   AsyncHttpResponseHandler handler) {
        Map<String, Object> params = new HashMap<String,Object>();
        params.put("optStaffNum",optStaffNum);
        params.put("scheduleDay",scheduleDay);
        params.put("scheduleType",scheduleType);
        params.put("staffNum",staffNum);
        params.put("token",token);
        String jsonString = JSONObject.toJSONString(params);
        String url = HOST+"userSchDay";
        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }
}
