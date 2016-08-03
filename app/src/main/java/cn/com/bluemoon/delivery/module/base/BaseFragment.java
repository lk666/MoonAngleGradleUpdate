package cn.com.bluemoon.delivery.module.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.app.api.ApiHttpClient;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.interf.BaseMainInterface;
import cn.com.bluemoon.delivery.module.base.interf.BaseViewInterface;
import cn.com.bluemoon.delivery.module.base.interf.DialogControl;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;

/**
 * 基础Fragment，必须属于{@link BaseTabActivity}
 * Created by lk on 2016/7/29.
 */
public abstract class BaseFragment extends Fragment implements BaseMainInterface, BaseViewInterface {

    private BaseTabActivity aty;
    private View mainView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        aty = (BaseTabActivity) getActivity();
        onBeforeCreateView();
        initCustomActionBar();

        View v = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, v);
        mainView = v;
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        initData();
    }

    private void initCustomActionBar() {
        if (!TextUtils.isEmpty(getTitleString())) {
            CommonActionBar titleBar = new CommonActionBar(aty.getActionBar(),
                    new IActionBarListener() {

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApiHttpClient.cancelAll(aty);
    }

    ///////////// 工具方法 ////////////////
    final protected View getMainView() {
        return mainView;
    }

    /**
     * 获取Activity
     */
    final protected BaseTabActivity getBaseTabActivity() {
        return aty;
    }


    final protected void longToast(String msg) {
        aty.longToast(msg);
    }

    final protected void toast(String msg) {
        aty.toast(msg);
    }

    /**
     * 默认TAG
     *
     * @return getClass().getSimpleName()
     */
    final protected String getDefaultTag() {
        return this.getClass().getSimpleName();
    }

    @Override
    final public void hideWaitDialog() {
        aty.hideWaitDialog();
    }

    @Override
    final public ProgressDialog showWaitDialog() {
        return aty.showWaitDialog();
    }

    @Override
    final public ProgressDialog showWaitDialog(int resId, int viewId) {
        return aty.showWaitDialog(resId, viewId);
    }

    @Override
    final public ProgressDialog showWaitDialog(String text, int viewId) {
        return aty.showWaitDialog(text, viewId);
    }

    final protected ProgressDialog showWaitDialog(String message, int viewId, boolean
            isCancelable) {
        return aty.showWaitDialog(message, viewId, isCancelable);
    }

    private final AsyncHttpResponseHandler mainHandler = new WithContextTextHttpResponseHandler(
            HTTP.UTF_8, aty) {

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

    /**
     * 在调用DeliveryApi的方法时使用，如： DeliveryApi.getEmp(requestCode, ClientStateManager.getLoginToken
     * (this),
     * "80474765", getMainHandler());
     */
    final public AsyncHttpResponseHandler getMainHandler() {
        return mainHandler;
    }

    ///////////// 可选重写 ////////////////

    /**
     * 在oncreateView后执行，一般为获取getArguments();的数据
     */
    protected void onBeforeCreateView() {
    }

    /**
     * 返回为null或者空字符串，则不设置ActionBar(若要不显示actionbar，须在主题中声明)
     */
    protected String getTitleString() {
        return null;
    }

    /**
     * 左键点击事件，一般不需重写
     */
    protected void onActionBarBtnLeftClick() {
        aty.setResult(Activity.RESULT_CANCELED);
        aty.finish();
    }

    /**
     * 右键点击事件
     */
    protected void onActionBarBtnRightClick() {
    }

    /**
     * 设置自定义ActionBar，如右图标
     */
    protected void setActionBar(CommonActionBar titleBar) {
    }

    /**
     * 请求返回非OK
     */
    protected void onErrorResponse(int requestCode, ResultBase result) {
        PublicUtil.showErrorMsg(aty, result);
    }

    /**
     * 请求失败
     */
    protected void onFailureResponse(int requestCode) {
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
