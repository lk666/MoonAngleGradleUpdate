package cn.com.bluemoon.delivery.module.base;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.utils.Constants;
import cn.com.bluemoon.delivery.utils.LogUtils;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibViewUtil;

/**
 * 基于PullToRefreshListView的基础刷新activity，自动显示空数据页面和网络错误页面
 */
public abstract class BasePullToRefreshListViewActivity<ADAPTER extends BaseListAdapter, ITEM
        extends Object> extends
        BaseActionBarActivity {

    /**
     * 错误页面View
     */
    private View errorView;
    /**
     * 空数据页面View
     */
    private View emptyView;

    /**
     * 下拉刷新listview
     */
    private PullToRefreshListView ptrlv;

    /**
     * 列表adapter
     */
    private ADAPTER adapter;

    /**
     * 列表数据
     */
    private List<ITEM> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_to_refresh_list_view);

        setIntentData();
        initView();
        refresh();
    }

    /**
     * 设置intent数据
     */
    protected void setIntentData() {
    }

    private void initView() {
        ptrlv = (PullToRefreshListView) findViewById(R.id.ptrlv);
        ptrlv.setMode(getMode());
        initPullToRefreshListView(ptrlv);

        ptrlv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getMore();
            }
        });

        adapter = getNewAdapter();
        ptrlv.setAdapter(adapter);
        LibViewUtil.setViewVisibility(ptrlv, View.GONE);
    }

    /**
     * 产生adapter
     *
     * @return 列表的数据adapter
     */
    protected abstract ADAPTER getNewAdapter();

    /**
     * 加载更多
     */
    private void getMore() {
        LibViewUtil.setChildEnableRecursion(ptrlv, false);
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
                List dataList = getGetMoreList(result);
                // 判断数据是否为空
                if (dataList == null || dataList.isEmpty()) {
//                    showEmptyView();
                } else {
                    setGetMore(dataList);
                }
            }
        }));
    }

    /**
     * 设置加载更多数据请求成功的列表数据
     */
    private void setGetMore(List list) {
        if (this.list != null) {
            this.list.addAll(list);
        } else {
            setAdapterList(list);
        }
        adapter.notifyDataSetChanged();
    }

    private void setAdapterList(List list) {
        this.list = list;
        adapter.setList(this.list);
    }

    /**
     * 获取加载更多数据返回，处理请求成功的数据（数据不为空），进而得到列表数据
     *
     * @param result 继承ResultBase的classType数据，不为null，也非空数据
     * @return 列表数据
     */
    protected abstract List<ITEM> getGetMoreList(Object result);

    /**
     * 具体调用加载更多数据时的DeliveryApi的方法，格式应如： DeliveryApi.getEmp(ClientStateManager.getLoginToken(this),
     * "80474765", handler);
     *
     * @param handler DeliveryApi的方法中的AsyncHttpResponseHandler参数
     */
    protected abstract void invokeGetMoreDeliveryApi(AsyncHttpResponseHandler handler);

    /**
     * 设置列表其他属性，如设置分割线等
     */
    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
//        ptrlv.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
//        ptrlv.setDividerDrawable(getResources().getDrawable(R.drawable.div_left_padding_16));
//        ptrlv.getRefreshableView().setDividerHeight(getResources().getDimensionPixelSize(R.dimen
//                .div_height));
    }

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
    private void showRefreshView() {
        LibViewUtil.setViewVisibility(errorView, View.GONE);
        LibViewUtil.setViewVisibility(emptyView, View.GONE);
        LibViewUtil.setViewVisibility(ptrlv, View.VISIBLE);
    }

    /**
     * 获取界面数据（刷新界面），显示dialog
     */
    final protected void refresh() {
        showProgressDialog();
        getData();
    }

    /**
     * 获取界面数据（刷新界面）
     */
    private void getData() {
        LibViewUtil.setChildEnableRecursion(ptrlv, false);
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
                List dataList = getGetDataList(result);
                // 判断数据是否为空
                if (dataList == null || dataList.isEmpty()) {
                    showEmptyView();
                } else {
                    setGetData(dataList);
                    showRefreshView();
                }
            }
        }));
    }

    /**
     * 获取刷新数据返回，处理请求成功的数据（数据不为空），进而得到列表数据
     *
     * @param result 继承ResultBase的classType数据，不为null，也非空数据
     * @return 列表数据
     */
    protected abstract List<ITEM> getGetDataList(Object result);

    /**
     * 设置刷新请求成功的列表数据
     */
    private void setGetData(List list) {
        if (this.list == null) {
            setAdapterList(list);
        } else {
            this.list.clear();
            this.list.addAll(list);
        }
        adapter.notifyDataSetChanged();
    }

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
        LibViewUtil.setViewVisibility(ptrlv, View.GONE);
    }

    /**
     * 获取空数据页面的layout的id
     */
    protected abstract int getEmptyViewLayoutId();

    /**
     * 设置空数据页面
     *
     * @param emptyView
     */
    protected abstract void initEmptyViewEvent(View emptyView);

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
        LibViewUtil.setViewVisibility(ptrlv, View.GONE);
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
    private AsyncHttpResponseHandler createScrollViewRefreshResponseHandler(
            final IRefreshHttpResponseHandler callback) {
        return new WithClassTextHttpResponseHandler(HTTP.UTF_8, getResultClass()) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString, Class
                    classType) {
                if (ptrlv == null) {
                    return;
                }
                LogUtils.d(getDefaultTag(), "baseExtendHandler result = " + responseString);
                ptrlv.onRefreshComplete();
                dismissProgressDialog();
                LibViewUtil.setChildEnableRecursion(ptrlv, true);
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
                            PublicUtil.showErrorMsg(BasePullToRefreshListViewActivity.this,
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
                if (ptrlv == null) {
                    return;
                }
                LogUtils.e(getDefaultTag(), throwable.getMessage());
                ptrlv.onRefreshComplete();
                dismissProgressDialog();
                LibViewUtil.setChildEnableRecursion(ptrlv, true);

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
