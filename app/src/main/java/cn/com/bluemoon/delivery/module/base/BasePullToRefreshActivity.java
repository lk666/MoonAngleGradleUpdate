package cn.com.bluemoon.delivery.module.base;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ListView;

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
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonEmptyView;

// TODO: lk 2016/7/14 实现类似的fragment的刷新基类
// TODO: lk 2016/7/14 缺少一种Mode.PULL_FROM_START的刷新基类

/**
 * 刷新类Activity/Fragment公共逻辑
 * Created by lk on 2016/7/14.
 */
@Deprecated
public abstract class BasePullToRefreshActivity extends BaseActionBarActivity {
    /**
     * 错误页面View
     */
    private View errorView;
    /**
     * 空数据页面View
     */
    private View emptyView;

    /**
     * 刷新view
     */
    private PullToRefreshBase ptr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        setIntentData();
        initView();
        refresh();
    }

    /**
     * 获取Layout的id
     */
    protected abstract int getLayoutId();

    /**
     * 设置intent数据
     */
    protected void setIntentData() {
    }

    private void initView() {
        ptr = (PullToRefreshBase) findViewById(getPtrId());
        ptr.setMode(getMode());

        ptr.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getMore();
            }
        });

        initPtr(ptr);

        LibViewUtil.setViewVisibility(ptr, View.GONE);
    }

    /**
     * 初始化PullToRefresh控件的其它属性
     */
    protected abstract void initPtr(PullToRefreshBase ptr);

    /**
     * 获取pulltorefresh控件id
     */
    protected abstract int getPtrId();

    /**
     * 加载更多
     */
    private void getMore() {
        LibViewUtil.setChildEnableRecursion(ptr, false);
        invokeGetMoreDeliveryApi(createScrollViewRefreshResponseHandler(new IRefreshHttpResponseHandler() {

            @Override
            public void onResponseException(String responseString, Exception e) {
//                showNetErrorView();
            }

            @Override
            public void onResponseFailure(int statusCode, Header[] headers, String
                    responseString, Throwable throwable) {
//                showNetErrorView();
            }

            @Override
            public void onResponseSuccess(Object result) {
                setGetMore(result);
            }
        }));
    }

    /**
     * 设置加载更多数据请求成功的数据
     */
    protected abstract void setGetMore(Object result);

    /**
     * 具体调用加载更多数据时的DeliveryApi的方法，格式应如： DeliveryApi.getEmp(ClientStateManager.getLoginToken(this),
     * "80474765", handler);
     *
     * @param handler DeliveryApi的方法中的AsyncHttpResponseHandler参数
     */
    protected abstract void invokeGetMoreDeliveryApi(AsyncHttpResponseHandler handler);

    /**
     * 获取列表刷新方式
     *
     * @return 一般为{@link PullToRefreshBase.Mode#BOTH}、
     * {@link PullToRefreshBase.Mode#PULL_FROM_START}或{@link PullToRefreshBase.Mode#PULL_FROM_END}
     */
    protected abstract PullToRefreshBase.Mode getMode();

    /**
     * 显示内容页
     */
    protected void showRefreshView() {
        LibViewUtil.setViewVisibility(errorView, View.GONE);
        LibViewUtil.setViewVisibility(emptyView, View.GONE);
        LibViewUtil.setViewVisibility(ptr, View.VISIBLE);
    }

    /**
     * 获取界面数据（刷新界面），显示dialog
     */
    private void refresh() {
        showProgressDialog();
        getData();
    }

    /**
     * 获取界面数据（刷新界面）
     */
    private void getData() {
        LibViewUtil.setChildEnableRecursion(ptr, false);
        invokeGetDataDeliveryApi(createScrollViewRefreshResponseHandler(new IRefreshHttpResponseHandler() {

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
                setGetData(result);
            }
        }));
    }

    /**
     * 设置刷新请求成功的数据
     */
    abstract protected void setGetData(Object result);

    /**
     * 显示空数据页
     */
    protected void showEmptyView() {
        if (emptyView == null) {
            int layoutId = R.layout.viewstub_wrapper;
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
        LibViewUtil.setViewVisibility(ptr, View.GONE);
    }

    /**
     * 设置空数据页面
     *
     * @param emptyView
     */
    private void initEmptyViewEvent(View emptyView) {
        // TODO: lk 2016/7/14 空白页
        CommonEmptyView empty = new CommonEmptyView(this);
        empty.setEmptyListener(new CommonEmptyView.EmptyListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        ((ViewGroup) emptyView).addView(empty);
    }

    /**
     * 具体调用刷新数据时的DeliveryApi的方法，格式应如： DeliveryApi.getEmp(ClientStateManager.getLoginToken(this),
     * "80474765", handler);
     *
     * @param handler DeliveryApi的方法中的AsyncHttpResponseHandler参数
     */
    protected abstract void invokeGetDataDeliveryApi(AsyncHttpResponseHandler handler);

    /**
     * 显示错误页
     */
    private void showNetErrorView() {
        if (errorView == null) {
            int layoutId = R.layout.viewstub_wrapper;
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
        LibViewUtil.setViewVisibility(ptr, View.GONE);
    }

    /**
     * 设置错误页面
     *
     * @param errorView
     */
    private void initErrorViewEvent(View errorView) {
        // TODO: lk 2016/7/14 错误页
        CommonEmptyView error = new CommonEmptyView(this);
        error.setEmptyListener(new CommonEmptyView.EmptyListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        ((ViewGroup) errorView).addView(error);
    }

    /**
     * 创建一个用于刷新详情的拓展AsyncHttpResponseHandler
     *
     * @param callback
     * @return
     */
    private AsyncHttpResponseHandler createScrollViewRefreshResponseHandler(
            final IRefreshHttpResponseHandler callback) {
        return new WithClassTextHttpResponseHandler(HTTP.UTF_8, getResultClass()) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString, Class
                    classType) {
                if (ptr == null) {
                    return;
                }
                LogUtils.d(getDefaultTag(), "createScrollViewRefreshResponseHandler result = " +
                        responseString);
                ptr.onRefreshComplete();
                dismissProgressDialog();
                LibViewUtil.setChildEnableRecursion(ptr, true);
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
                            PublicUtil.showErrorMsg(BasePullToRefreshActivity.this, result);
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
                if (ptr == null) {
                    return;
                }
                LogUtils.e(getDefaultTag(), throwable.getMessage());
                ptr.onRefreshComplete();
                dismissProgressDialog();
                LibViewUtil.setChildEnableRecursion(ptr, true);

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