package cn.com.bluemoon.delivery.app.api;

import android.text.TextUtils;

import org.apache.http.Header;

import java.util.HashMap;
import java.util.Map;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.personalinfo.ResultGetFamilyInfo.FamilyListBean;
import cn.com.bluemoon.delivery.app.api.model.personalinfo.ResultGetInterstInfo;
import cn.com.bluemoon.delivery.module.base.WithContextTextHttpResponseHandler;
import cn.com.bluemoon.delivery.utils.Constants;

/**
 * HR项目API
 */
public class HRApi extends BaseApi {

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
     * 获取住址信息
     *
     * @param type 请求地址类型 String
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
        postRequest("Get Address Info", params, "bmhr-control/personInfo/getAddressInfo%s", handler);
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
     * @param bean 技术爱好/特长 ResultGetInterstInfo
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
     * @param bean 家庭信息 FamilyListBean
     * @param token 身份检验码 String 必填
     */
    public static void saveOrUpdateFamily(FamilyListBean bean ,String token,
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
        params.put("birthday", bean.birthday);
        params.put("familyId", Integer.valueOf(bean.familyId));
        params.put("gender", bean.gender);
        params.put("menberPosition", bean.menberPosition);
        params.put("name", bean.name);
        params.put("relationship", bean.relationship);
        params.put("surname", bean.surname);
        params.put("workPlace", bean.workPlace);
        postRequest("Save or update family", params, "bmhr-control/personInfo/saveOrUpdateFamily%s", handler);
    }
}
