package cn.com.bluemoon.delivery.module.base;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ScrollView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
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

    /**
     * 错误页面View
     */
    private View errorView;
    /**
     * 空数据页面View
     */
    private View emptyView;
    /**
     * 实际的内容页面View
     */
    private View contentView;
    /**
     * 下拉刷新view
     */
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

        showContentView();
        LibViewUtil.setViewVisibility(ptrsv, View.GONE);
    }

    /**
     * 设置内容页
     */
    private void showContentView() {
        if (contentView == null) {
            int layoutId = getContentViewLayoutId();
            View viewStub = findViewById(R.id.viewstub_content);
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
     * 设置inflate后的具体内容的view/viewgroup
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
            public void onResponseSuccess(Object result) {
                // 判断数据是否为空
                if (isDataEmpty(result)) {
                    showEmptyView();
                } else {
                    setData(result);
                }
            }
        }));
    }

    /**
     * 设置请求成功的数据（数据不为空）
     *
     * @param result 继承ResultBase的classType数据，不为null，也非空数据
     * @return
     */
    protected abstract void setData(Object result);

    /**
     * 判断数据是否为empty
     *
     * @param result 继承ResultBase的classType数据，不为null
     * @return
     */
    protected abstract boolean isDataEmpty(Object result);

    // TODO: lk 2016/7/13 enable测试

    /**
     * 显示空数据页
     */
    private void showEmptyView() {
        if (emptyView == null) {
            int layoutId = getEmptyViewLayoutId();
            final View viewStub = findViewById(R.id.viewstub_empty);
            if (viewStub != null) {
                final ViewStub stub = (ViewStub) viewStub;
                stub.setLayoutResource(layoutId);
                emptyView = stub.inflate();
                initEmptyViewEvent(emptyView);
            }
        }

        LibViewUtil.setViewVisibility(emptyView, View.VISIBLE);
        LibViewUtil.setViewVisibility(errorView, View.GONE);
        LibViewUtil.setViewVisibility(ptrsv, View.GONE);
    }

    /**
     * 获取空数据页面的layout的id
     */
    protected abstract int getEmptyViewLayoutId();

    /**
     * 设置空数据页面
     *
     * @param errorView
     */
    protected abstract void initEmptyViewEvent(View errorView);

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
        if (errorView == null) {
            int layoutId = getErrorViewLayoutId();
            View viewStub = findViewById(R.id.viewstub_error);
            if (viewStub != null) {
                final ViewStub stub = (ViewStub) viewStub;
                stub.setLayoutResource(layoutId);
                errorView = stub.inflate();
                initErrorViewEvent(errorView);
            }

        }

        LibViewUtil.setViewVisibility(emptyView, View.GONE);
        LibViewUtil.setViewVisibility(errorView, View.VISIBLE);
        LibViewUtil.setViewVisibility(ptrsv, View.GONE);
    }


    /**
     * 获取错误页面的layout的id
     */
    protected abstract int getErrorViewLayoutId();

    /**
     * 设置错误页面
     *
     * @param errorView
     */
    protected abstract void initErrorViewEvent(View errorView);

    /**
     * 创建一个用于下拉刷新详情的拓展AsyncHttpResponseHandler
     *
     * @param callback
     * @return
     */
    protected AsyncHttpResponseHandler createScrollViewRefreshResponseHandler(
            final IRefreshHttpResponseHandler callback) {
        return new WithClassTextHttpResponseHandler(HTTP.UTF_8, getResultClass()) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString, Class
                    classType) {
                if (ptrsv == null) {
                    return;
                }
                LogUtils.d(getDefaultTag(), "baseExtendHandler result = " + responseString);
                ptrsv.onRefreshComplete();
                LibViewUtil.setChildEnableRecursion(ptrsv, true);
                try {
                    Object obj = classType.newInstance();
                    obj = JSON.parseObject(responseString, classType);
                    if (obj instanceof ResultBase) {
                        ResultBase result = (ResultBase) obj;
                        if (result.getResponseCode() == Constants.RESPONSE_RESULT_SUCCESS) {
                            if (callback != null) {
                                callback.onResponseSuccess(obj);
                            }
                        } else {
                            PublicUtil.showErrorMsg(BasePullToRefreshScrollViewActivity.this,
                                    result);
                        }
                    } else {
                        LogUtils.e(getDefaultTag(), "baseExtendHandler result type error:" +
                                responseString + "," + classType);
                        PublicUtil.showToastServerBusy();
                        if (callback != null) {
                            callback.onResponseException(responseString, new Exception
                                    ("baseExtendHandler result type error:" +
                                            responseString + "," + classType));
                        }
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
                LibViewUtil.setChildEnableRecursion(ptrsv, true);

                if (callback != null) {
                    callback.onResponseFailure(statusCode, headers, responseString, throwable);
                }
                PublicUtil.showToastServerOvertime();

            }
        };
    }

    /**
     * 获取刷新页面获取内容数据时的服务器返回类型
     */
    protected abstract Class getResultClass();

    /**
     * 封装AsyncHttpResponseHandler的回调(刷新用)
     */
    public interface IRefreshHttpResponseHandler {
        /**
         * 响应成功的后续步骤回调
         *
         * @param result 继承ResultBase的classType数据，不为null
         */
        void onResponseSuccess(Object result);

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
