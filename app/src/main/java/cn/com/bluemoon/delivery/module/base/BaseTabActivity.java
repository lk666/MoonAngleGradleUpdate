package cn.com.bluemoon.delivery.module.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.common.ClientStateManager;
import cn.com.bluemoon.delivery.entity.ArgumentTabState;
import cn.com.bluemoon.delivery.entity.TabState;
import cn.com.bluemoon.delivery.module.base.interf.BaseMainInterface;
import cn.com.bluemoon.delivery.module.base.interf.BaseViewInterface;
import cn.com.bluemoon.delivery.module.base.interf.IHttpRespone;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;

/**
 * 基础FragmentActivity，用于各fragment集合的界面
 * Created by lk on 2016/6/3.
 */
public class BaseTabActivity extends FragmentActivity implements BaseViewInterface,
        BaseMainInterface, IHttpRespone {

    /**
     * List<OldTabState>，tab数据
     */
    public final static String EXTRA_TAB_STATES = "EXTRA_TAB_STATES";

    @Bind(android.R.id.tabhost)
    FragmentTabHost tabhost;

    private ProgressDialog waitDialog;
    private LayoutInflater layoutInflater;

    private List<TabState> tabs;
    private List<TextView> amountTvs;

    protected static void actionStart(Context context, ArrayList<TabState> tabs, Class clazz) {
        Intent intent = new Intent(context, clazz);
        intent.putExtra(EXTRA_TAB_STATES, tabs);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushOneActivity(this);

        onBeforeSetContentLayout();
        setContentView(R.layout.extract_tab);
        // 通过注解绑定控件
        ButterKnife.bind(this);
        layoutInflater = LayoutInflater.from(this);

        initView();
        initData();
    }

    private void onBeforeSetContentLayout() {
        tabs = (List<TabState>) getIntent().getSerializableExtra(EXTRA_TAB_STATES);
        if (tabs == null) {
            tabs = new ArrayList<>();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getInstance().popOneActivity(this);
    }

    @Override
    public void initView() {
        tabhost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        tabhost.getTabWidget().setDividerDrawable(null);
        amountTvs = new ArrayList<>();
    }

    @Override
    public void initData() {
        for (int i = 0; i < tabs.size(); i++) {
            TabState ts = tabs.get(i);
            TabHost.TabSpec tabSpec = tabhost.newTabSpec(getResources()
                    .getString(ts.getContent()))
                    .setIndicator(getTabItemView(ts.getImage(),
                            getResources().getString(ts.getContent())));

            Bundle bundle = new Bundle();
            if (ts instanceof ArgumentTabState) {
                bundle.putSerializable(BaseFragment.EXTRA_BUNDLE_DATA,
                        ((ArgumentTabState) ts).getBundleData());
            }

            tabhost.addTab(tabSpec, ts.getClazz(), bundle);
        }
    }

    private View getTabItemView(int resId, String content) {
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(resId);
        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(content);
        amountTvs.add(textView);
        return view;
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
     * 设置角标数量
     */
    public void setAmount(int index, int amount) {
        if (amountTvs == null || index > amountTvs.size() || index < 0) {
            return;
        }

        if (amount > 0) {
            amountTvs.get(index).setText(String.valueOf(amount));
            amountTvs.get(index).setVisibility(View.VISIBLE);
        } else {
            amountTvs.get(index).setVisibility(View.GONE);
        }
    }

    final protected void longToast(String msg) {
        ViewUtil.longToast(this, msg);
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
     * 默认TAG
     */
    protected String getDefaultTag() {
        return this.getClass().getSimpleName();
    }

    /**
     * 请求成功
     */
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase resultBase) {

    }

    /**
     * 请求返回非OK
     */
    public void onErrorResponse(int requestCode, ResultBase result) {
        PublicUtil.showErrorMsg(this, result);
    }

    /**
     * 请求失败
     */
    public void onFailureResponse(int requestCode) {
    }
}
