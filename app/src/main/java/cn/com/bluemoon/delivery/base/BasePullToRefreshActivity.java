package cn.com.bluemoon.delivery.base;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonEmptyView;

/**
 * 基础下拉刷新上拉加载Activity，layout最好只包含一个PullToRefresh控件和一个空白页、一个错误页
 * Created by lk on 2016/7/26.
 */
public abstract class BasePullToRefreshActivity extends BaseActivity {
    /**
     * 错误页面View
     */
    private View errorView;
    /**
     * 空数据页面View
     */
    private View emptyView;

    private static final int HTTP_REQUEST_CODE_GET_MORE = 0x11;
    private static final int HTTP_REQUEST_CODE_GET_DATA = 0x10;

    /**
     * 刷新view
     */
    private PullToRefreshBase ptr;

    @Override
    final public void initView() {
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
     * 获取界面数据（刷新界面）
     */
    private void getData() {
        LibViewUtil.setChildEnableRecursion(ptr, false);
        invokeGetDataDeliveryApi(HTTP_REQUEST_CODE_GET_DATA, getMainHandler());
    }

    /**
     * 设置错误页面
     */
    private void initErrorViewEvent(View errorView) {
        CommonEmptyView error = new CommonEmptyView(this);
        error.setEmptyListener(new CommonEmptyView.EmptyListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
        ((ViewGroup) errorView).addView(error);
    }

    /**
     * 加载更多
     */
    private void getMore() {
        LibViewUtil.setChildEnableRecursion(ptr, false);
        invokeGetMoreDeliveryApi(HTTP_REQUEST_CODE_GET_MORE, getMainHandler());
    }


    /**
     * 设置空数据页面
     *
     * @param emptyView
     */
    private void initEmptyViewEvent(View emptyView) {
        CommonEmptyView empty = new CommonEmptyView(this);
        empty.setEmptyListener(new CommonEmptyView.EmptyListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
        ((ViewGroup) emptyView).addView(empty);
    }

    ///////////// 工具方法 ////////////////
    /**
     * 显示内容页
     */
    final protected void showRefreshView() {
        LibViewUtil.setViewVisibility(errorView, View.GONE);
        LibViewUtil.setViewVisibility(emptyView, View.GONE);
        LibViewUtil.setViewVisibility(ptr, View.VISIBLE);
    }


    /**
     * 显示错误页
     */
    final protected void showNetErrorView() {
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
     * 显示空数据页
     */
    final protected void showEmptyView() {
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


    ///////////// 可选重写 ////////////////
    @Override
    protected void onSuccessResponse(int requestCode, String jsonString) {
        ptr.onRefreshComplete();
        LibViewUtil.setChildEnableRecursion(ptr, true);
        switch (requestCode) {
            // 刷新数据
            case HTTP_REQUEST_CODE_GET_DATA:
                setGetData(jsonString);
                break;
            // 加载更多数据
            case HTTP_REQUEST_CODE_GET_MORE:
                setGetMore(jsonString);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onFailureResponse(int requestCode) {
        super.onFailureResponse(requestCode);
        ptr.onRefreshComplete();
        LibViewUtil.setChildEnableRecursion(ptr, true);
        switch (requestCode) {
            // 刷新数据
            case HTTP_REQUEST_CODE_GET_DATA:
                showNetErrorView();
                break;
            default:
                break;
        }
        showNetErrorView();
    }

    @Override
    protected void onErrorResponse(int requestCode, ResultBase result) {
        super.onErrorResponse(requestCode, result);
        ptr.onRefreshComplete();
        LibViewUtil.setChildEnableRecursion(ptr, true);
        switch (requestCode) {
            // 刷新数据
            case HTTP_REQUEST_CODE_GET_DATA:
                showNetErrorView();
                break;
            default:
                break;
        }
    }

    /**
     * 获取界面数据（刷新界面），显示dialog。若要使dialog不可手动取消，可重写此方法
     */
    @Override
    public void initData() {
        showWaitDialog();
        getData();
    }


    ///////////// 工具方法 ////////////////


    ///////////// 必须重写 ////////////////

    /**
     * 初始化PullToRefresh控件的其它属性
     */
    protected abstract void initPtr(PullToRefreshBase ptr);

    /**
     * 获取pulltorefresh控件id
     */
    protected abstract int getPtrId();

    /**
     * 获取列表刷新方式
     *
     * @return 一般为{@link PullToRefreshBase.Mode#BOTH}、
     * {@link PullToRefreshBase.Mode#PULL_FROM_START}或{@link PullToRefreshBase.Mode#PULL_FROM_END}
     */
    protected abstract PullToRefreshBase.Mode getMode();

    /**
     * 具体调用刷新数据时的DeliveryApi的方法，格式应如： DeliveryApi.getEmp(requestCode,
     * ClientStateManager.getLoginToken(this), "80474765", handler);
     *
     * @param requestCode DeliveryApi的方法中的requestCode参数
     * @param handler     DeliveryApi的方法中的AsyncHttpResponseHandler参数
     */
    protected abstract void invokeGetDataDeliveryApi(int requestCode, AsyncHttpResponseHandler
            handler);

    /**
     * 设置刷新请求成功的数据
     *
     * @param result ResultBase类的json字符串，resultcode为OK
     */
    abstract protected void setGetData(String result);

    /**
     * 具体调用加载更多数据时的DeliveryApi的方法，格式应如： DeliveryApi.getEmp(ClientStateManager.getLoginToken(this),
     * "80474765", handler);
     *
     * @param requestCode DeliveryApi的方法中的requestCode参数
     * @param handler     DeliveryApi的方法中的AsyncHttpResponseHandler参数
     */
    protected abstract void invokeGetMoreDeliveryApi(int requestCode, AsyncHttpResponseHandler
            handler);

    /**
     * 设置加载更多数据请求成功的数据
     */
    protected abstract void setGetMore(String result);
}
