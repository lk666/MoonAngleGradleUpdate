package cn.com.bluemoon.delivery.module.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ApiHttpClient;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.module.base.interf.BaseMainInterface;
import cn.com.bluemoon.delivery.module.base.interf.BaseViewInterface;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.module.base.interf.IHttpRespone;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;


/**
 * 基础Activity，实现了一些公共方法
 * Created by lk on 2016/6/14.
 */
public abstract class BaseActivity extends Activity implements BaseMainInterface, BaseViewInterface,
        IHttpRespone {

    private ProgressDialog waitDialog;
    protected LayoutInflater mInflater;

    @Override
    protected void onDestroy() {
        hideWaitDialog();
        super.onDestroy();
        ApiHttpClient.cancelAll(this);
        ActivityManager.getInstance().popOneActivity(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushOneActivity(this);
        onBeforeSetContentLayout();
        initCustomActionBar();
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        }
        mInflater = getLayoutInflater();
        // 通过注解绑定控件
        ButterKnife.bind(this);

        init(savedInstanceState);
        initView();
        initData();
    }

    private void initCustomActionBar() {
        if (!TextUtils.isEmpty(getTitleString())) {
            CommonActionBar titleBar = new CommonActionBar(getActionBar(), new IActionBarListener
                    () {

                @Override
                public void onBtnRight(View v) {
                    onActionBarBtnRightClick();
                }

                @Override
                public void onBtnLeft(View v) {
                    onActionBarBtnLeftClick();
                }

                @Override
                public void setTitle(TextView v) {
                    v.setText(getTitleString());
                }

            });
            setActionBar(titleBar);
        }
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

    private AsyncHttpResponseHandler getHandler(int requestcode, Class clazz,
                                                final IHttpRespone iHttpRespone) {
        WithContextTextHttpResponseHandler handler = new WithContextTextHttpResponseHandler(
                HTTP.UTF_8, this, requestcode, clazz) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (iHttpRespone == null) {
                    return;
                }
                LogUtils.d(getDefaultTag(), "mainHandler requestCode:" + getReqCode() + " -->" +
                        " " + "result = " + responseString);
                hideWaitDialog();
                try {
                    Object resultObj = getClazz().newInstance();
                    resultObj = JSON.parseObject(responseString, getClazz());
                    if (resultObj instanceof ResultBase) {
                        ResultBase resultBase = (ResultBase) resultObj;
                        if (resultBase.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                            iHttpRespone.onSuccessResponse(getReqCode(),
                                    responseString, resultBase);
                        } else {
                            iHttpRespone.onErrorResponse(getReqCode(), resultBase);
                        }
                    } else {
                        throw new IllegalArgumentException();
                    }
                } catch (Exception e) {
                    LogUtils.e(getDefaultTag(), e.getMessage());
                    PublicUtil.showToastServerBusy();
                    iHttpRespone.onFailureResponse(getReqCode());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                if (iHttpRespone == null) {
                    return;
                }
                LogUtils.e(getDefaultTag(), throwable.getMessage());
                hideWaitDialog();
                PublicUtil.showToastServerOvertime();
                iHttpRespone.onFailureResponse(getReqCode());
            }
        };
        return handler;
    }

    ///////////// 工具方法 ////////////////

    /**
     * 获取token
     */
    final protected String getToken() {
        return ClientStateManager.getLoginToken(this);
    }

    /**
     * 在调用DeliveryApi的方法时使用
     */
    @Override
    final public AsyncHttpResponseHandler getNewHandler(final int requestcode, final Class clazz) {
        return getHandler(requestcode, clazz, this);
    }

    /**
     * 默认TAG
     *
     * @return getClass().getSimpleName()
     */
    final protected String getDefaultTag() {
        return this.getClass().getSimpleName();
    }

    /**
     * toast提示方法
     *
     * @param msg
     */
    final protected void longToast(String msg) {
        ViewUtil.longToast(msg);
    }

    final protected void toast(String msg) {
        ViewUtil.toast(msg);
    }

    final protected void longToast(int resId) {
        ViewUtil.longToast(resId);
    }

    final protected void toast(int resId) {
        ViewUtil.toast(resId);
    }

    final protected View inflateView(int resId) {
        return mInflater.inflate(resId, null);
    }

    /**
     * 展示默认等待dialog
     */
    @Override
    final public ProgressDialog showWaitDialog() {
        return showWaitDialog(true);
    }

    final protected ProgressDialog showWaitDialog(boolean isCancelable) {
        return showWaitDialog(R.string.data_loading, R.layout.dialog_progress, isCancelable);
    }

    @Override
    final public ProgressDialog showWaitDialog(int resId, int viewId) {
        return showWaitDialog(resId, viewId, true);
    }

    final protected ProgressDialog showWaitDialog(int resId, int viewId, boolean isCancelable) {
        return showWaitDialog(getString(resId), viewId, isCancelable);
    }

    @Override
    final public ProgressDialog showWaitDialog(String message, int viewId) {
        return showWaitDialog(message, viewId, true);
    }

    final protected ProgressDialog showWaitDialog(String message, int viewId, boolean
            isCancelable) {
        if (waitDialog == null) {
            waitDialog = DialogUtil.getWaitDialog(this);
        }

        waitDialog.setMessage(message);
        waitDialog.setCancelable(isCancelable);
        waitDialog.show();
        if (viewId != 0) {
            waitDialog.setContentView(viewId);
        }
        return waitDialog;
    }

    /**
     * 关闭等待dialog
     */
    @Override
    final public void hideWaitDialog() {
        if (waitDialog != null) {
            try {
                waitDialog.dismiss();
                waitDialog = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    ///////////// 可选重写 ////////////////

    /**
     * 在oncreate后执行，一般为获取intentata
     */
    protected void onBeforeSetContentLayout() {
    }

    /**
     * 可用于保存和还原界面缓存数据
     */
    protected void init(Bundle savedInstanceState) {
    }

    /**
     * 请求返回非OK
     */
    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        PublicUtil.showErrorMsg(this, result);
    }

    /**
     * 请求失败
     */
    @Override
    public void onFailureResponse(int requestCode) {
    }

    /**
     * 返回为null或者空字符串，则不设置ActionBar(若要不显示actionbar，须在主题中声明)
     */
    protected String getTitleString() {
        return null;
    }

    /**
     * 设置自定义ActionBar，如右图标
     */
    protected void setActionBar(CommonActionBar titleBar) {
    }

    /**
     * 左键点击事件，一般不需重写
     */
    protected void onActionBarBtnLeftClick() {
        this.setResult(Activity.RESULT_CANCELED);
        this.finish();
    }

    /**
     * 右键点击事件
     */
    protected void onActionBarBtnRightClick() {
    }

    ///////////// 必须重写 ////////////////

    /**
     * 设置布局文件layout，一般都要重写
     */
    protected abstract int getLayoutId();
}
