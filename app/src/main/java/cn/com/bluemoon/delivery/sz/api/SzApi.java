package cn.com.bluemoon.delivery.sz.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.entity.ByteArrayEntity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.BuildConfig;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.sz.bean.taskManager.DailyPerformanceInfoBean;
import cn.com.bluemoon.delivery.sz.bean.taskManager.RateDataInfoBean;
import cn.com.bluemoon.delivery.sz.util.LogUtil;
import cn.com.bluemoon.delivery.utils.StringUtil;

/**
 * Created by dujiande on 2016/8/11.
 */
public class SzApi {

    public static  String URL = "http://192.168.237.2/%s";

    public static AsyncHttpClient client = null;

    public static String CLIENT = "android";
    private static String FORMAT = "json";
    private static String APP_TYPE = "moonAngel";

    private static boolean isDebug = false;


    static {
        client = new AsyncHttpClient();
        client.setConnectTimeout(20000);
        client.setResponseTimeout(20000);

    }

	public static String getAbsoluteApiUrl(String partUrl) {
		String url = String.format(BuildConfig.API_URL_SZ, partUrl);
		 LogUtil.d("getAbsoluteApiUrl BASE_URL:" + url);
		return url;
	}

	public static String getMockUrl(String partUrl) {
		 LogUtil.d("getAbsoluteApiUrl BASE_URL:" + String.format(BuildConfig.MOCK_URL_SZ, partUrl));
		return String.format(BuildConfig.MOCK_URL_SZ, partUrl);
	}

    /*  获取用户单日日程列表 */
    /*
        optStaffNum	操作员工编号	string
        scheduleDay	排期日期	string
        scheduleType	-1 对应所有，日程类型【 0-任务日程，1-会议日程】
        staffNum	员工编号	string
        token	token身份检验码	string	必填
     */
    public static void userSchDay(String optStaffNum, String scheduleDay, String scheduleType,
                                  String staffNum,
                                  String token,
                                  AsyncHttpResponseHandler handler) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("optStaffNum", optStaffNum);
        params.put("scheduleDay", scheduleDay);
        params.put("scheduleType", scheduleType);
        params.put("uid", staffNum);
        params.put("token", token);
//        String url = HOST + "userSchDay";
//        client.post(AppContext.getInstance(), url, getEntity(params), "application/json", handler);
    }

    private static ByteArrayEntity getEntity(Map<String, Object> params) {
        String jsonString = JSONObject.toJSONString(params);
        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(jsonString.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return entity;
    }

    public static void msgMainType(String optStaffNum,
                                   AsyncHttpResponseHandler handler) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("msgType", null);
        params.put("uid", optStaffNum);
        params.put("token", ClientStateManager.getLoginToken());
//        String url = HOST + "msgMainType";
//        client.post(AppContext.getInstance(), url, getEntity(params), "application/json", handler);
    }

    public static void userMsgList(String uid, int msgType,
                                   AsyncHttpResponseHandler handler) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("msgType", msgType);
        params.put("uid", uid);
        params.put("token", ClientStateManager.getLoginToken());
//        String url = HOST + "userMsgList";
//        client.post(AppContext.getInstance(), url, getEntity(params), "application/json", handler);
    }

    /****************************************
     * 任务管理系统api
     **************************************/
    /**
     * desc:满足 新增 和 修改 任务数据
     * <p/>
     * submitData 工作任务数据 <单日绩效数据> object asignJobs 里至少保留一条数据
     * token 用户token string
     * type 0:新增 1:修改 string
     */
    public static void submitDayJobsApi(DailyPerformanceInfoBean submitData, String type,
                                     AsyncHttpResponseHandler handler) {
        if (submitData == null || StringUtil.isEmpty(type)) {
            return;
        }
        if (submitData.getAsignJobs() == null) {
            return;
        }
        if (submitData.getAsignJobs().size() < 1) {
            return;
        }
        HashMap<String, Object> params = new HashMap<>();
        params.putAll(SzApiClientHelper.getParamUrl());
        params.put("submitData", submitData);
        params.put("type", type);
        String url = "app/work/submitDayJobsApi";
		if (isDebug)
			URL=getMockUrl(url);
		else
			URL=getAbsoluteApiUrl(url);
        requestLog(params,URL);
        client.post(AppContext.getInstance(), URL,
                        getEntity(params), "application/json", handler);
    }

    /**
     * desc  提交评价接口
     * <p/>
     * rateData 评价数据[] array<object>
     * work_day_id 任务日计划ID string
     */
    public static void submitDayJobsRating(List<RateDataInfoBean> rateData, String work_day_id,
                                           AsyncHttpResponseHandler handler) {
        HashMap<String, Object> params = new HashMap<>();
        params.putAll(SzApiClientHelper.getParamUrl());
        params.put("rateData", rateData);
        params.put("work_day_id", work_day_id);

        String url = "app/work/submitDayJobsRatingApi";
		if (isDebug)
			URL=getMockUrl(url);
		else
			URL=getAbsoluteApiUrl(url);
		requestLog(params,URL);
		client.post(AppContext.getInstance(), URL,
				getEntity(params), "application/json", handler);
    }

    /**
     * desc 获取模糊查询员工联系人接口
     * <p/>
     * queryStr 模糊查询关键字 string
     */
    public static void searchByKeyword(String queryStr, AsyncHttpResponseHandler handler) {
        HashMap<String, Object> params = new HashMap<>();
        params.putAll(SzApiClientHelper.getParamUrl());
        params.put("queryStr", queryStr);
        String url = "app/user/search";
		if (isDebug)
			URL=getMockUrl(url);
		else
			URL=getAbsoluteApiUrl(url);
		requestLog(params,URL);
		client.post(AppContext.getInstance(), URL,
				getEntity(params), "application/json", handler);
    }

    /**
     * desc 获得任务评价列表
     * <p/>
     * currentPage 当前页(从1开始) string
     * pageSize 每页数量 string
     * type 0:待评价 1:已评价
     */
    public static void getRateJobsList(String currentPage, String pageSize, String type, AsyncHttpResponseHandler handler) {
        HashMap<String, Object> params = new HashMap<>();
        params.putAll(SzApiClientHelper.getParamUrl());
        params.put("currentPage", currentPage);
        params.put("pageSize", pageSize);
        params.put("type", type);
        params.put("token", ClientStateManager.getLoginToken());
        String url ="app/work/getRateJobsListApi";
		if (isDebug)
			URL=getMockUrl(url);
		else
			URL=getAbsoluteApiUrl(url);
		requestLog(params,URL);
		client.post(AppContext.getInstance(), URL,
				getEntity(params), "application/json", handler);
    }



    /**
     * desc 通过员工工号查询员工信息及直接上级信息
     * <p/>
     * account 用户ID string
     * appType [必填] app的类型 string [必填] app的类型: 培训平台:trainingSys 日程会议管理: scheduleSys 工作任务管理:moonAngel
     * token 用户token string
     *
     */
    public static void getuserinfo(String account, AsyncHttpResponseHandler handler) {
        HashMap<String, Object> params = new HashMap<>();
        params.putAll(SzApiClientHelper.getParamUrl());
        params.put("account", account);
        String url = "app/work/getuserinfo";
		if (isDebug)
			URL=getMockUrl(url);
		else
			URL=getAbsoluteApiUrl(url);
		requestLog(params,URL);
		client.post(AppContext.getInstance(), URL,
				getEntity(params), "application/json", handler);
    }



    /********************Task api @author jiangyh*****************************/
    /**
     * 获得单日工作任务列表&月度绩效积分组合接口
     *
     * @param date
     * @param type 0:全部 1:任务列表 2:月度绩效
     */
    public static void getWorkDetailsApi(String date, int type,
                                         AsyncHttpResponseHandler handler) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("date", date);
        params.put("type", type );
        params.put("token", ClientStateManager.getLoginToken());
        params.putAll(SzApiClientHelper.getParamUrl());
        String url = "app/work/getWorkDetailsApi";
		if (isDebug)
			URL=getMockUrl(url);
		else
			URL=getAbsoluteApiUrl(url);
		requestLog(params,URL);
		client.post(AppContext.getInstance(), URL,
				getEntity(params), "application/json", handler);
    }

    public static void requestLog(Map<String, Object> params,String url){
        LogUtil.e("========url========");
        LogUtil.e(url);
        LogUtil.e("========request========");
        LogUtil.e(JSON.toJSONString(params));
    }

}
