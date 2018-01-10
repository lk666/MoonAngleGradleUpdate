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
 * 电子合同
 * Created by ljl on 2018/1/10.
 */
public class ContractApi extends BaseApi {

    /**
     * 4.进行实名认证
     * @param token    token身份检验码 String
     */
    public static void doRealNameCheck(String token,
                                     WithContextTextHttpResponseHandler handler) {
        if (TextUtils.isEmpty(token)) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1],
                    (byte[]) null,
                    new Exception(AppContext.getInstance().getString(R.string.error_local_param)
                            + ":" + (null == token ?  " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        postMockRequest("进行实名认证", params, "bmhr-control/contract/doRealNameCheck%s", handler);
    }

    /**
     * 3.检查人员是否已经实名认证
     * @param token    token身份检验码 String
     */
    public static void checkPersonReal(String token,
                                     WithContextTextHttpResponseHandler handler) {
        if (TextUtils.isEmpty(token)) {
            handler.onFailure(Constants.RESPONSE_RESULT_LOCAL_PARAM_ERROR, new Header[1],
                    (byte[]) null,
                    new Exception(AppContext.getInstance().getString(R.string.error_local_param)
                            + ":" + (null == token ?  " null=token" : "")));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        postMockRequest("检查人员是否已经实名认证", params, "bmhr-control/contract/checkPersonReal%s", handler);
    }

}
