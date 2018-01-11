package cn.com.bluemoon.delivery.app.api;

import android.text.TextUtils;
import android.util.Base64;

import org.apache.http.Header;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.base.WithContextTextHttpResponseHandler;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.lib.utils.LibFileUtil;

/**
 * 电子合同
 * Created by ljl on 2018/1/10.
 */
public class ContractApi extends BaseApi {

    /**
     * 4.进行实名认证
     *
     * @param token token身份检验码 String
     */
    public static void doRealNameCheck(String token,
                                       WithContextTextHttpResponseHandler handler) {
        if (TextUtils.isEmpty(token)) {
            onError(handler);
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        postMockRequest("进行实名认证", params, "bmhr-control/contract/doRealNameCheck%s", handler);
    }

    /**
     * 3.检查人员是否已经实名认证
     *
     * @param token token身份检验码 String
     */
    public static void checkPersonReal(String token,
                                       WithContextTextHttpResponseHandler handler) {
        if (TextUtils.isEmpty(token)) {
            onError(handler);
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        postMockRequest("检查人员是否已经实名认证", params, "bmhr-control/contract/checkPersonReal%s", handler);
    }

    /**
     * 2.获取合同详情
     *
     * @param token      token身份检验码 String
     * @param contractId 合同编码
     */
    public static void getContractDetail(String token, String contractId,
                                         WithContextTextHttpResponseHandler handler) {
        if (isEmpty(token, contractId)) {
            onError(handler);
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("contractId", contractId);
        postMockRequest("获取合同详情", params, "bmhr-control/contract/getContractDetail%s", handler);
    }

    /**
     * 5.获得pdf的签名的定位信息
     *
     * @param token      token身份检验码 String
     * @param contractId 合同编码
     */
    public static void getPDFPosition(String token, String contractId,
                                      WithContextTextHttpResponseHandler handler) {
        if (isEmpty(token, contractId)) {
            onError(handler);
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("contractId", contractId);
        postMockRequest("获得pdf的签名的定位信息", params, "bmhr-control/contract/getPDFPosition%s", handler);
    }

    /**
     * 6.合同签署发送短信验证码
     * @param token token身份检验码 String
     */
    public static void sendSmsBySign(String token, WithContextTextHttpResponseHandler handler) {
        if (isEmpty(token)) {
            onError(handler);
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        postMockRequest("合同签署发送短信验证码", params, "bmhr-control/contract/sendSmsBySign%s", handler);
    }

    /**
     * 7.合同签署
     * @param token token身份检验码 String
     */
    public static void doContractSign(String token, String validCode, String filePath,
                                      WithContextTextHttpResponseHandler handler) {
        if (isEmpty(token,validCode,filePath)) {
            onError(handler);
            return;
        }
        //验证文件路径是否正确
        File file = new File(filePath);
        if(!file.exists()){
            onError(handler);
            return;
        }
        //这种方式转码不会出现"\n"，默认的是有的。
        String signFile = Base64.encodeToString(LibFileUtil.getBytes(file), Base64.NO_WRAP);

        Map<String, Object> params = new HashMap<>();
        params.put(TOKEN, token);
        params.put("vailCode", validCode);
        params.put("signFile", signFile);
        postMockRequest("合同签署", params, "bmhr-control/contract/doContractSign%s", handler);
    }
}
