package cn.com.bluemoon.delivery.app.api;

import android.text.TextUtils;

import org.apache.http.Header;

import java.util.HashMap;
import java.util.Map;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
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
}
