package cn.com.bluemoon.delivery.app.api;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;

import org.apache.http.Header;

import java.util.HashMap;
import java.util.Map;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.BuildConfig;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.personalinfo.ResultGetAddressInfo;
import cn.com.bluemoon.delivery.app.api.model.personalinfo.ResultGetAddressInfo.AddressInfoBean;
import cn.com.bluemoon.delivery.app.api.model.personalinfo.ResultGetFamilyInfo.FamilyListBean;
import cn.com.bluemoon.delivery.app.api.model.personalinfo.ResultGetInterstInfo;
import cn.com.bluemoon.delivery.module.base.WithContextTextHttpResponseHandler;
import cn.com.bluemoon.delivery.utils.Constants;

/**
 * HR项目API
 */
public class HRApi extends BaseApi {

    /***********************
     * 2.0行政区域相关
     ********************************/

	/* 2.0.1 级联查询全国行政区域 */
    /* 返回： ResultArea */
    public static void getRegionSelect(String pid, String type,
                                       WithContextTextHttpResponseHandler handler) {

        Map<String, String> params = new HashMap<>();
        String jsonString = "{}";
        if (!(pid == null && type == null)) {
            params.put("pid", pid);
            params.put("type", type);
            jsonString = JSONObject.toJSONString(params);
        }

        String url = String.format("bmhr-control/personInfo/getRegionSelectSAP%s",
                ApiClientHelper.getParamUrl());

        ApiHttpClient.post(AppContext.getInstance(), url, jsonString, handler);
    }

    /**
     * 校验密码
     *
     * @param password 密码 String
     * @param token    token身份检验码 String
     */
    public static void checkPassword(String password, String token,
                                     WithContextTextHttpResponseHandler handler) {
        if (TextUtils.isEmpty(token) || TextUtils.isEmpty(password)) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1],
                    (byte[]) null,
                    new Exception(AppContext.getInstance().getString(R.string.error_local_param)
                            + ":" + (null == password ? " null=password" : "") + (null == token ?
                            " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("password", password);
        params.put(TOKEN, token);
        postRequest("校验密码", params, "bmhr-control/personInfo/checkPassword%s", handler);
    }

    /**
     * 获取个人基本信息
     *
     * @param token token身份检验码 String
     */
    public static void getBaseInfo(String token, WithContextTextHttpResponseHandler handler) {
        if (null == token) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1],
                    (byte[]) null, new Exception(AppContext.getInstance().getString(R.string
                            .error_local_param) + ":" + (null == token ? " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        postRequest(" 获取个人基本信息", params, "bmhr-control/personInfo/getBaseInfo%s", handler);
    }


    /**
     * 获取住址信息
     *
     * @param type  请求地址类型 String
     * @param token token身份检验码 String
     */
    public static void getAddressInfo(String type, String token,
                                      WithContextTextHttpResponseHandler handler) {
        if (TextUtils.isEmpty(token) || TextUtils.isEmpty(type)) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1],
                    (byte[]) null,
                    new Exception(AppContext.getInstance().getString(R.string.error_local_param)
                            + ":" + (null == type ? " null=type" : "") + (null == token ?
                            " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put(TOKEN, token);
        postRequest("Get Address Info", params, "bmhr-control/personInfo/getAddressInfo%s",
                handler);
    }


    /**
     * 获取兴趣爱好和特长
     *
     * @param token token身份检验码 String
     */
    public static void getInterest(String token,
                                   WithContextTextHttpResponseHandler handler) {
        if (TextUtils.isEmpty(token)) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1],
                    (byte[]) null,
                    new Exception(AppContext.getInstance().getString(R.string.error_local_param)
                            + ":" + (null == token ?
                            " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        postRequest("Get interest", params, "bmhr-control/personInfo/getInterest%s", handler);
    }

    /**
     * 编辑兴趣爱好和特长
     *
     * @param token token身份检验码 String
     * @param bean  技术爱好/特长 ResultGetInterstInfo
     */
    public static void saveInterest(ResultGetInterstInfo bean, String token,
                                    WithContextTextHttpResponseHandler handler) {
        if (TextUtils.isEmpty(token)) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1],
                    (byte[]) null,
                    new Exception(AppContext.getInstance().getString(R.string.error_local_param)
                            + ":" + (null == token ?
                            " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("interest", bean.interest);
        params.put("otherHobby", bean.otherHobby);
        params.put("performExperience", bean.performExperience);
        params.put("specialty", bean.specialty);
        postRequest("Save interest", params, "bmhr-control/personInfo/saveInterest%s", handler);
    }

    /**
     * 获取家庭情况
     *
     * @param token token身份检验码 String
     */
    public static void getFamilyInfo(String token,
                                     WithContextTextHttpResponseHandler handler) {
        if (TextUtils.isEmpty(token)) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1],
                    (byte[]) null,
                    new Exception(AppContext.getInstance().getString(R.string.error_local_param)
                            + ":" + (null == token ?
                            " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        postRequest("Get family info", params, "bmhr-control/personInfo/getFamilyInfo%s", handler);
    }

    /**
     * 新增/编辑家庭成员
     *
     * @param bean  家庭信息 FamilyListBean
     * @param token 身份检验码 String 必填
     */
    public static void saveOrUpdateFamily(boolean hasBirthday, FamilyListBean bean, String token,
                                          WithContextTextHttpResponseHandler handler) {
        if (TextUtils.isEmpty(token)
                || TextUtils.isEmpty(bean.gender) || TextUtils.isEmpty(bean.name)
                || TextUtils.isEmpty(bean.relationship) || TextUtils.isEmpty(bean.surname)) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1],
                    (byte[]) null,
                    new Exception(AppContext.getInstance().getString(R.string.error_local_param)
                            + ":" + (null == token ?
                            " null=token" : "") + (null == bean.gender ?
                            " null=gender" : "") + (null == bean.name ?
                            " null=name" : "") + (null == bean.relationship ?
                            " null=relationship" : "") + (null == bean.surname ?
                            " null=surname" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        if (hasBirthday) {
            params.put("birthday", bean.birthday);
        }
        if (!TextUtils.isEmpty(bean.familyId)) {
            params.put("familyId", Integer.valueOf(bean.familyId));
        }
        params.put("gender", bean.gender);
        params.put("menberPosition", bean.menberPosition);
        params.put("name", bean.name);
        params.put("relationship", bean.relationship);
        params.put("surname", bean.surname);
        params.put("workPlace", bean.workPlace);
        postRequest("Save or update family", params,
                "bmhr-control/personInfo/saveOrUpdateFamily%s", handler);
    }

    /**
     * 获取联系方式信息
     *
     * @param token token身份检验码 String
     */
    public static void getContactInfo(String token, WithContextTextHttpResponseHandler handler) {
        if (TextUtils.isEmpty(token)) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1],
                    (byte[]) null, new Exception(AppContext.getInstance().getString(R.string
                            .error_local_param) + ":" + (null == token ? " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        postRequest("获取联系方式信息", params, "bmhr-control/personInfo/getContactInfo%s", handler);
    }

    /**
     * 保存联系方式信息
     *
     * @param contactMobile    紧急联系人手机号 String
     * @param contactMobile2   紧急联系人手机号2 String
     * @param contactName      紧急联系人姓名 String
     * @param contactName2     紧急联系人姓名2 String
     * @param contactRelation  紧急联系人关系 String
     * @param contactRelation2 紧急联系人关系2 String
     * @param emailPers        个人邮箱 String
     * @param officePhone      办公分机号 String
     * @param officePlace      办公点 String
     * @param officeSeat       办公座位号 String
     * @param token            token身份检验码 String
     * @param weichat          微信号 String
     */
    public static void saveContact(String contactMobile, String contactMobile2, String
            contactName, String contactName2, String contactRelation, String contactRelation2,
                                   String emailPers, String officePhone, String officePlace,
                                   String officeSeat, String token, String weichat,
                                   WithContextTextHttpResponseHandler handler) {
        if (TextUtils.isEmpty(token) || TextUtils.isEmpty(contactMobile)
                || TextUtils.isEmpty(contactName) || TextUtils.isEmpty(contactRelation)) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1],
                    (byte[]) null,
                    new Exception(AppContext.getInstance().getString(R.string.error_local_param)
                            + ":" + (null == contactMobile ? " null=contactMobile" : "") +
                            (null == contactName ? " null=contactName" : "") +
                            (null == contactRelation ? " null=contactRelation" : "") +
                            (null == token ? " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("contactMobile", contactMobile);
        params.put("contactMobile2", contactMobile2);
        params.put("contactName", contactName);
        params.put("contactName2", contactName2);
        params.put("contactRelation", contactRelation);
        params.put("contactRelation2", contactRelation2);
        params.put("emailPers", emailPers);
        params.put("officePhone", officePhone);
        params.put("officePlace", officePlace);
        params.put("officeSeat", officeSeat);
        params.put(TOKEN, token);
        params.put("weichat", weichat);
        postRequest("保存联系方式信息", params, "bmhr-control/personInfo/saveContact%s", handler);
    }

    /**
     * 获取验证码
     *
     * @param mobile 手机号码 String
     * @param token  token身份检验码 String
     */
    public static void getVerifyCode(String mobile, String token,
                                     WithContextTextHttpResponseHandler handler) {
        if (null == mobile || null == token) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1],
                    (byte[]) null, new Exception(AppContext.getInstance().getString(R.string
                            .error_local_param) + ":" + (null == mobile ? " null=mobile" : "") +
                            (null == token ? " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("mobile", mobile);
        params.put(TOKEN, token);
        postRequest("获取验证码", params, "bmhr-control/personInfo/getVerifyCode%s", handler);
    }

    /**
     * 保存手机号码
     *
     * @param mobile   手机号码 String
     * @param token    token身份检验码 String
     * @param valiCode 验证码 String
     */
    public static void saveMobile(String mobile, String token, String valiCode,
                                  WithContextTextHttpResponseHandler handler) {
        if (null == mobile || null == token || null == valiCode) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1],
                    (byte[]) null, new Exception(AppContext.getInstance().getString(R.string
                            .error_local_param) + ":" + (null == mobile ? " null=mobile" : "") +
                            (null == token ? " null=token" : "") + (null == valiCode ? " " +
                            "null=valiCode" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("mobile", mobile);
        params.put(TOKEN, token);
        params.put("valiCode", valiCode);
        postRequest("保存手机号码", params, "bmhr-control/personInfo/saveMobile%s", handler);
    }

    /**
     * 保存住址信息
     *
     */
    public static void saveAddress(AddressInfoBean bean, String address, String waitCart, String type, String token,
                                   WithContextTextHttpResponseHandler handler) {
        if (null == token) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1],
                    (byte[]) null, new Exception(AppContext.getInstance().getString(R.string
                            .error_local_param) + ":"  +
                            (null == token ? " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("provinceName", bean.provinceName);
        params.put("provinceCode", bean.provinceCode);
        params.put("cityName", bean.cityName);
        params.put("cityCode", bean.cityCode);
        params.put("countyCode", bean.countyCode);
        params.put("countyName", bean.countyName);
        params.put("address", address);
        params.put(TOKEN, token);
        params.put("carWait", waitCart);
        params.put("type", type);
        postRequest("保存住址信息", params, "bmhr-control/personInfo/saveAddress%s", handler);
    }

}
