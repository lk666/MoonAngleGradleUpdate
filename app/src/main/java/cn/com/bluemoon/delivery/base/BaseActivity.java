package cn.com.bluemoon.delivery.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ApiHttpClient;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.interf.BaseViewInterface;
import cn.com.bluemoon.delivery.interf.DialogControl;
import cn.com.bluemoon.delivery.manager.ActivityManager;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.utils.LibViewUtil;


/**
 * 基础Activity，实现了一些公共方法
 * Created by lk on 2016/6/14.
 */
public abstract class BaseActivity extends Activity implements DialogControl, BaseViewInterface {

    protected Activity aty;
    private ProgressDialog waitDialog;
    protected LayoutInflater mInflater;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApiHttpClient.cancelAll(this);
        ActivityManager.getInstance().popOneActivity(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.aty = this;
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

    protected void initCustomActionBar() {
        if (!TextUtils.isEmpty(getTitleString())) {
            CommonActionBar titleBar = new CommonActionBar(getActionBar(), new IActionBarListener() {

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

    /**
     * 设置自定义ActionBar，如右图标
     */
    protected void setActionBar(CommonActionBar titleBar) {

    }

    /**
     * 返回为null或者空字符串，则不设置ActionBar
     * @return
     */
    protected String getTitleString() {
        return null;
    }

    protected void onActionBarBtnLeftClick() {
        this.setResult(Activity.RESULT_CANCELED);
        this.finish();
    }

    protected void onActionBarBtnRightClick() {

    }

    /**
     * 设置布局文件layout
     * @return
     */
    protected int getLayoutId() {
        return 0;
    }

    protected View inflateView(int resId) {
        return mInflater.inflate(resId, null);
    }

    protected void onBeforeSetContentLayout() {
    }


    protected void init(Bundle savedInstanceState) {
    }

    protected void longToast(String msg) {
        LibViewUtil.longToast(this, msg);
    }

    protected void toast(String msg) {
        LibViewUtil.toast(this, msg);
    }

    /**
     * 默认TAG
     *
     * @return getClass().getSimpleName()
     */
    protected String getDefaultTag() {
        return this.getClass().getSimpleName();
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

    /**
     * 展示默认等待dialog
     * @return
     */
    @Override
    public ProgressDialog showWaitDialog() {
        return showWaitDialog(true);
    }
    /**
     * 展示默认等待dialog
     * @return
     */
    public ProgressDialog showWaitDialog(boolean isCancelable) {
        return showWaitDialog(R.string.data_loading, R.layout.dialog_progress,isCancelable);
    }

    @Override
    public ProgressDialog showWaitDialog(int resId, int viewId) {
        return showWaitDialog(resId, viewId,true);
    }

    protected ProgressDialog showWaitDialog(int resId, int viewId,boolean isCancelable) {
        return showWaitDialog(getString(resId), viewId,isCancelable);
    }

    @Override
    public ProgressDialog showWaitDialog(String message, int viewId) {
        return showWaitDialog(message, viewId,true);
    }

    protected ProgressDialog showWaitDialog(String message, int viewId,boolean isCancelable) {
        if (waitDialog == null) {
            waitDialog = DialogUtil.getWaitDialog(this, message, viewId);
        }
        if (waitDialog != null) {
            waitDialog.setMessage(message);
            if (viewId != 0) {
                waitDialog.setContentView(viewId);
            }
            waitDialog.setCancelable(isCancelable);
            waitDialog.show();
        }
        return waitDialog;
    }

    /**
     * 关闭等待dialog
     * @return
     */
    @Override
    public void hideWaitDialog() {
        if (waitDialog != null) {
            try {
                waitDialog.dismiss();
                waitDialog = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    protected AsyncHttpResponseHandler getMainHandler(){
        return mainHandler;
    }

    final AsyncHttpResponseHandler mainHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(getDefaultTag(), "mainHandler requestCode:" + getRequestCode() + " --> result = " + responseString);
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
                onErrorResponse(getRequestCode());
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            LogUtils.e(getDefaultTag(), throwable.getMessage());
            hideWaitDialog();
            PublicUtil.showToastServerOvertime();
            onErrorResponse(getRequestCode());
        }
    };


    abstract void onSuccessResponse(int requestCode, String jsonString);

    protected void onErrorResponse(int requestCode,ResultBase result){
        DialogUtil.showErrorMsg(aty, result);
    }

    protected void onErrorResponse(int requestCode){

    }


}
