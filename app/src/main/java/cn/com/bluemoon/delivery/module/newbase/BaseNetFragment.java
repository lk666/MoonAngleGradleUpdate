package cn.com.bluemoon.delivery.module.newbase;

import com.alibaba.fastjson.JSON;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.protocol.HTTP;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.WithContextTextHttpResponseHandler;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.liblog.NetLogUtils;
// TODO: lk 2017/4/24 考虑使用代理或适配器

/**
 * fragment继承层次：3（0顶层）
 * 网络抽象实现类。使用asyHttp实现网络
 */
public abstract class BaseNetFragment extends BaseActionFragment implements IHttpResponse {

    @Override
    public void onDestroy() {
        cancelRequest();
        super.onDestroy();
    }

    private void cancelRequest() {
        //TODO 会影响其他fragment请求
        //ApiHttpClient.cancelAll(getContext());
    }

    //////////////// 工具方法 ////////////////

    /**
     * 在调用DeliveryApi的方法时使用
     */
    final public WithContextTextHttpResponseHandler getNewHandler(final int requestCode, final
    Class clazz) {
        return getHandler(requestCode, clazz, this, true);
    }

    /**
     * 在调用DeliveryApi的方法时使用
     */
    final public WithContextTextHttpResponseHandler getNewHandler(final int requestCode, final
    Class clazz, boolean isShowDialog) {
        return getHandler(requestCode, clazz, this, isShowDialog);
    }

    private WithContextTextHttpResponseHandler getHandler(int requestCode, Class clazz,
                                                          final IHttpResponse iHttpResponse,
                                                          final boolean isShowDialog) {
        WithContextTextHttpResponseHandler handler = new WithContextTextHttpResponseHandler(
                HTTP.UTF_8, getContext(), requestCode, clazz) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (iHttpResponse == null) {
                    return;
                }
                if (isShowDialog) {
                    hideWaitDialog();
                }
                try {
                    Object resultObj;
                    resultObj = JSON.parseObject(responseString, getClazz());
                    if (resultObj instanceof ResultBase) {
                        ResultBase resultBase = (ResultBase) resultObj;
                        if (resultBase.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                            NetLogUtils.dNetResponse(Constants.TAG_HTTP_RESPONSE_SUCCESS, getUuid(),
                                    System.currentTimeMillis(), responseString);
                            iHttpResponse.onSuccessResponse(getReqCode(),
                                    responseString, resultBase);
                        } else {
                            NetLogUtils.eNetResponse(Constants.TAG_HTTP_RESPONSE_NOT_SUCCESS,
                                    getUuid(), System
                                            .currentTimeMillis(), responseString, new Exception
                                            ("resultBase.getResponseCode() = " + resultBase
                                                    .getResponseCode() + "-->" + responseString));
                            if (isShowDialog) {
                                hideWaitDialog();
                            }
                            iHttpResponse.onErrorResponse(getReqCode(), resultBase);
                        }
                    } else {
                        throw new Exception
                                ("转换ResultBase失败-->" + responseString);
                    }
                } catch (Exception e) {
                    NetLogUtils.eNetResponse(Constants.TAG_HTTP_RESPONSE_EXCEPTION, getUuid(),
                            System.currentTimeMillis(), responseString, e);
                    if (isShowDialog) {
                        hideWaitDialog();
                    }
                    ViewUtil.toastBusy();
                    iHttpResponse.onSuccessException(getReqCode(), e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                NetLogUtils.eNetResponse(Constants.TAG_HTTP_RESPONSE_FAILURE, getUuid(), System
                        .currentTimeMillis(), responseString, throwable);

                if (iHttpResponse == null) {
                    return;
                }
                hideWaitDialog();
                iHttpResponse.onFailureResponse(getReqCode(), throwable);
            }
        };
        return handler;
    }

    //////////////// 可选重写 ////////////////

    /**
     * 请求返回非OK
     */
    public void onErrorResponse(int requestCode, ResultBase result) {
        PublicUtil.showErrorMsg(getActivity(), result);
    }

    /**
     * 请求失败
     */
    @Override
    public void onFailureResponse(int requestCode, Throwable t) {
        PublicUtil.showToastServerOvertime();
    }

    @Override
    public void onSuccessException(int requestCode, Throwable t) {
        PublicUtil.showToastServerBusy();
    }
}
