package cn.com.bluemoon.delivery.module.base;

import android.app.Activity;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.manager.ActivityManager;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

/**
 * 基础Activity，实现了一些公共方法
 * Created by lk on 2016/6/14.
 */
public abstract class BaseActivity extends Activity implements IShowDialog {

    private CommonProgressDialog progressDialog;

    /**
     * 默认TAG
     *
     * @return getClass().getSimpleName()
     */
    protected String getDefaultTag() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushOneActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getInstance().popOneActivity(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getDefaultTag());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getDefaultTag());
    }

    @Override
    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new CommonProgressDialog(this);
        }

        progressDialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    // TODO: lk 2016/7/1 都换成这种形式

    /**
     * 创建一个通用的AsyncHttpResponseHandler
     *
     * @param callback
     * @return
     */
    protected AsyncHttpResponseHandler createResponseHandler(final IHttpResponseHandler callback) {
        return new TextHttpResponseHandler(
                HTTP.UTF_8) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                LogUtils.d(getDefaultTag(), "baseHandler result = " + responseString);
                dismissProgressDialog();
                try {
                    ResultBase result = JSON.parseObject(responseString,
                            ResultBase.class);
                    if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                        if (callback != null) {
                            callback.onResponseSuccess(responseString);
                        }
                    } else {
                        PublicUtil.showErrorMsg(BaseActivity.this, result);
                    }
                } catch (Exception e) {
                    LogUtils.e(getDefaultTag(), e.getMessage());
                    PublicUtil.showToastServerBusy();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                LogUtils.e(getDefaultTag(), throwable.getMessage());
                dismissProgressDialog();
                PublicUtil.showToastServerOvertime();
            }
        };
    }

    /**
     * 封装AsyncHttpResponseHandler的回调
     */
    public interface IHttpResponseHandler {
        /**
         * 响应成功的后续步骤回调
         *
         * @param responseString
         */
        void onResponseSuccess(String responseString);
    }
}
