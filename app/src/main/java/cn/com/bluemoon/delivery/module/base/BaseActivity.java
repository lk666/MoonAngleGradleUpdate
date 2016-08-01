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
import cn.com.bluemoon.delivery.module.base.interf.BaseViewInterface;
import cn.com.bluemoon.delivery.module.base.interf.DialogControl;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;
import cn.com.bluemoon.lib.utils.LibViewUtil;


/**
 * 基础Activity，实现了一些公共方法
 * Created by lk on 2016/6/14.
 */
public abstract class BaseActivity extends Activity implements DialogControl, BaseViewInterface {

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

    final AsyncHttpResponseHandler mainHandler = new WithContextTextHttpResponseHandler(
            HTTP.UTF_8, this) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(getDefaultTag(), "mainHandler requestCode:" + getRequestCode() + " --> " +
                    "result = " + responseString);
            hideWaitDialog();
            try {
                ResultBase result = JSON.parseObject(responseString,
                        ResultBase.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    onSuccessResponse(getRequestCode(), responseString);
                } else {
                    onErrorResponse(getRequestCode(), result);
                }
            } catch (Exception e) {
                LogUtils.e(getDefaultTag(), e.getMessage());
                PublicUtil.showToastServerBusy();
                onFailureResponse(getRequestCode());
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            LogUtils.e(getDefaultTag(), throwable.getMessage());
            hideWaitDialog();
            PublicUtil.showToastServerOvertime();
            onFailureResponse(getRequestCode());
        }
    };

    ///////////// 工具方法 ////////////////

    /**
     * 在调用DeliveryApi的方法时使用，如： DeliveryApi.getEmp(requestCode, ClientStateManager.getLoginToken
     * (this),
     * "80474765", getMainHandler());
     */
    final protected AsyncHttpResponseHandler getMainHandler() {
        return mainHandler;
    }

    /**
     * 默认TAG
     *
     * @return getClass().getSimpleName()
     */
    final protected String getDefaultTag() {
        return this.getClass().getSimpleName();
    }


    final protected void longToast(String msg) {
        LibViewUtil.longToast(this, msg);
    }

    final protected void toast(String msg) {
        LibViewUtil.toast(this, msg);
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
    protected void onErrorResponse(int requestCode, ResultBase result) {
        DialogUtil.showErrorMsg(this, result);
    }

    /**
     * 请求失败
     */
    protected void onFailureResponse(int requestCode) {
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

    /**
     * 请求成功
     */
    protected abstract void onSuccessResponse(int requestCode, String jsonString);
}
