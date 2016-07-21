package cn.com.bluemoon.delivery.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

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
import cn.com.bluemoon.delivery.module.clothing.collect.withoutorder.ActivityDetailActivity;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;


/**
 * 基础Activity，实现了一些公共方法
 * Created by lk on 2016/6/14.
 */
public abstract class BaseActivity extends Activity implements DialogControl, BaseViewInterface,IActionBarListener {

    private boolean isVisible;
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
        ActivityManager.getInstance().pushOneActivity(this);
        onBeforeSetContentLayout();

        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        }
        mInflater = getLayoutInflater();
        initCustomActionBar();
        // 通过注解绑定控件
        ButterKnife.bind(this);

        init(savedInstanceState);
        initView();
        initData();
        isVisible = true;
    }

    protected void initCustomActionBar() {
        if(getTitleResourceId()!=0) {
            new CommonActionBar(getActionBar(), this);
        }
    }

    protected  int getTitleResourceId(){
        return 0;
    }

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
        if(isFinishing()){

        }
    }


    @Override
    public ProgressDialog showWaitDialog() {
        return showWaitDialog(R.string.data_loading, R.layout.dialog_progress);
    }

    @Override
    public ProgressDialog showWaitDialog(int resId,int viewId) {
        return showWaitDialog(getString(resId),viewId);
    }

    @Override
    public ProgressDialog showWaitDialog(String message,int viewId) {
        if (isVisible) {
            if (waitDialog == null) {
                waitDialog = DialogUtil.getWaitDialog(this, message,viewId);
            }
            if (waitDialog != null) {
                waitDialog.setMessage(message);
                if(viewId!=0){
                    waitDialog.setContentView(viewId);
                }
                waitDialog.show();
            }
            return waitDialog;
        }
        return null;
    }

    @Override
    public void hideWaitDialog() {
        if (isVisible && waitDialog != null) {
            try {
                waitDialog.dismiss();
                waitDialog = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    AsyncHttpResponseHandler mainHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(getDefaultTag(), "mainHandler requestCode:"+ getRequestCode() +" --> result = " + responseString);
            hideWaitDialog();
            try {
                ResultBase result = JSON.parseObject(responseString,
                        ResultBase.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    onSuccessResponse(getRequestCode(),responseString);
                } else {
                    onErrorResponse(getRequestCode(),responseString);
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
            hideWaitDialog();
            PublicUtil.showToastServerOvertime();
        }
    };


     abstract void onSuccessResponse(int requestCode,String jsonString);

     abstract  void onErrorResponse(int requestCode,String jsonString);

}
