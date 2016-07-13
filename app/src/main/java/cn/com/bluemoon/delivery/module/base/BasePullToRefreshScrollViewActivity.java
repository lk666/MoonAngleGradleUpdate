package cn.com.bluemoon.delivery.module.base;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ScrollView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.app.api.model.clothing.ResultUserInfo;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshScrollView;
import cn.com.bluemoon.lib.utils.LibViewUtil;

/**
 * 下拉刷新普通页面，自动显示空数据页面和网络错误页面
 */
public abstract class BasePullToRefreshScrollViewActivity extends BaseActionBarActivity {

    private View errorView;
    private View emptyView;
    private View contentView;
    private PullToRefreshScrollView ptrsv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_to_refresh_scroll_view);

        setIntentData();
        initView();
        getData();
    }

    /**
     * 设置intent数据
     */
    protected void setIntentData() {
    }

    private void initView() {
        ptrsv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
            }
        });

        LibViewUtil.setViewVisibility(ptrsv, View.GONE);
    }

    /**
     * 设置内容页
     */
    private void showContentView() {
        if (contentView == null) {
            int layoutId = getContentViewLayoutId();
            final View viewStub = findViewById(R.id.viewstub_content);
            if (viewStub != null) {
                final ViewStub stub = (ViewStub) viewStub;
                stub.setLayoutResource(layoutId);
                contentView = stub.inflate();
                initContentView(contentView);
            }
        }

        LibViewUtil.setViewVisibility(errorView, View.GONE);
        LibViewUtil.setViewVisibility(emptyView, View.GONE);
        LibViewUtil.setViewVisibility(contentView, View.VISIBLE);
    }

    /**
     * 设置inflate后的具体内容view/viewgroup
     *
     * @param contentView
     */
    protected abstract void initContentView(View contentView);

    /**
     * 获取具体内容的layout的id
     */
    protected abstract int getContentViewLayoutId();

    /**
     * 获取界面数据（刷新界面）
     */
    final protected void getData() {
        LibViewUtil.setChildEnableRecursion(ptrsv, false);
        invokeDeliveryApi(createScrollViewRefreshResponseHandler(new IRefreshHttpResponseHandler() {

            @Override
            public void onResponseException(String responseString, Exception e) {
                showNetErrorView();
            }

            @Override
            public void onResponseFailure(int statusCode, Header[] headers, String
                    responseString, Throwable throwable) {
                showNetErrorView();
            }

            @Override
            public void onResponseSuccess(String responseString) {
                ResultUserInfo result = JSON.parseObject(responseString,
                        ResultUserInfo.class);
                // 判断数据是否为空
                if (is) {
                    showEmptyView();
                } else {
                    setData(result);
                }
                is = !is;
            }
        })));
    }

    /**
     * 具体调用 DeliveryApi的方法，格式应如： DeliveryApi.getEmp(ClientStateManager.getLoginToken(this),
     * "80474765", handler);
     *
     * @param handler DeliveryApi的方法中的AsyncHttpResponseHandler参数
     */
    protected abstract void invokeDeliveryApi(AsyncHttpResponseHandler handler);

    /**
     * 显示错误页
     */
    private void showNetErrorView() {
        try {
            if (errorView == null) {
                int layoutId = R.layout.view_error;
                if (layoutId != 0) {
                    final View viewStub = findViewById(R.id.viewstub_error);
                    if (viewStub != null) {
                        final ViewStub stub = (ViewStub) viewStub;
                        stub.setLayoutResource(layoutId);
                        errorView = stub.inflate();
                        setErrorViewEvent(errorView);
                    }
                }
            }

            LibViewUtil.setViewVisibility(emptyView, View.GONE);
            LibViewUtil.setViewVisibility(errorView, View.VISIBLE);
            LibViewUtil.setViewVisibility(ptrsv, View.GONE);
        } catch (Exception e) {
            LogUtils.e(getDefaultTag(), e.getMessage());
        }
    }

    /**
     * 设置错误页面
     * @param viewstubError
     */
    private void initErrorViewEvent(View errorView) {
        viewstubError.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
    }

    /**
     * 创建一个用于下拉刷新详情的拓展AsyncHttpResponseHandler
     *
     * @param callback
     * @return
     */
    protected AsyncHttpResponseHandler createScrollViewRefreshResponseHandler(
            final IRefreshHttpResponseHandler callback) {
        return new TextHttpResponseHandler(
                HTTP.UTF_8) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (ptrsv == null) {
                    return;
                }
                LogUtils.d(getDefaultTag(), "baseExtendHandler result = " + responseString);
                ptrsv.onRefreshComplete();
                setChildEnableRecursion(ptrsv, true);
                try {
                    ResultBase result = JSON.parseObject(responseString,
                            ResultBase.class);
                    if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                        if (callback != null) {
                            callback.onResponseSuccess(responseString);
                        }
                    } else {
                        PublicUtil.showErrorMsg(TestRefreshActivity.this, result);
                    }
                } catch (Exception e) {
                    LogUtils.e(getDefaultTag(), e.getMessage());
                    PublicUtil.showToastServerBusy();
                    if (callback != null) {
                        callback.onResponseException(responseString, e);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                if (ptrsv == null) {
                    return;
                }
                LogUtils.e(getDefaultTag(), throwable.getMessage());
                ptrsv.onRefreshComplete();
                setChildEnableRecursion(ptrsv, true);

                if (callback != null) {
                    callback.onResponseFailure(statusCode, headers, responseString, throwable);
                }
                PublicUtil.showToastServerOvertime();

            }
        };
    }


    /**
     * 封装AsyncHttpResponseHandler的回调(刷新用)
     */
    public interface IRefreshHttpResponseHandler extends IHttpResponseHandler {

        /**
         * 响应成功，onSuccess,但抛错的情况
         */
        void onResponseException(String responseString, Exception e);

        /**
         * onFailure的情况
         */
        void onResponseFailure(int statusCode, Header[] headers, String responseString, Throwable
                throwable);
    }
}
