package cn.com.bluemoon.delivery.module.base;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;

/**
 * 具有自定义actionBar的基础Activity
 * Created by lk on 2016/6/14.
 */
public abstract class BaseActionBarActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCustomActionBar();
    }

    private void initCustomActionBar() {
        new CommonActionBar(getActionBar(), new IActionBarListener() {

            @Override
            public void onBtnRight(View v) {
                ontActionBarBtnRightClick();
            }

            @Override
            public void onBtnLeft(View v) {
                ontActionBarBtnLeftClick();
            }

            @Override
            public void setTitle(TextView v) {
                v.setText(getActionBarTitleRes());
            }

        });
        setCustomActionBar();
    }

    /**
     * ActionBar的其他设置，如右边按钮的初始化
     */
    protected void setCustomActionBar() {
    }

    /**
     * 获取ActionBar标题文字
     *
     * @return R.string.*
     */
    protected abstract int getActionBarTitleRes();

    /**
     * ActionBar左键点击
     */
    protected void ontActionBarBtnLeftClick() {
        BaseActionBarActivity.this.finish();
    }

    /**
     * ActionBar右键点击
     */
    protected void ontActionBarBtnRightClick() {

    }

    // TODO: lk 2016/6/29 改为工厂模式，删掉此处
    @Deprecated
    protected void onResponseSuccess(String responseString) {
        ResultBase result = JSON.parseObject(responseString,
                ResultBase.class);
        if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
            PublicUtil.showToast(result.getResponseMsg());
            finish();
        }
    }

    @Deprecated
    protected AsyncHttpResponseHandler baseHandler = new TextHttpResponseHandler(
            HTTP.UTF_8) {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            LogUtils.d(getDefaultTag(), "baseHandler result = " + responseString);
            dismissProgressDialog();
            try {
                ResultBase result = JSON.parseObject(responseString,
                        ResultBase.class);
                if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                    onResponseSuccess(responseString);
                } else {
                    PublicUtil.showErrorMsg(BaseActionBarActivity.this, result);
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
