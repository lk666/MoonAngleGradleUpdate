package cn.com.bluemoon.delivery.module.base;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ListView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonEmptyView;

/**
 * 基础下拉刷新上拉加载Fragment，layout最好只包含一个不滚动的头部、一个PullToRefresh控件和一个空白页、一个错误页
 * Created by lk on 2016/8/1.
 */
public abstract class BasePullToRefreshFragment extends BaseFragment {

    /**
     * 头，开始不显示，
     */
    protected View head;

    /**
     * 错误页面View
     */
    private View errorView;
    /**
     * 空数据页面View
     */
    private View emptyView;

    private static final int HTTP_REQUEST_CODE_GET_MORE = 0x1000;
    private static final int HTTP_REQUEST_CODE_GET_DATA = 0x1001;

    /**
     * 刷新view
     */
    private PullToRefreshBase ptr;
    private String emptyMsg;

    @Override
    final public void initView() {
        errorView = null;
        emptyView = null;
        emptyMsg = null;

        ptr = (PullToRefreshBase) getMainView().findViewById(getPtrId());
        ptr.setMode(getMode() == null ? PullToRefreshBase.Mode.DISABLED : getMode());

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
        initHeadView();

        LibViewUtil.setViewVisibility(ptr, View.GONE);
        setHeadViewVisibility(View.GONE);
    }

    /**
     * 初始化头部
     */
    protected void initHeadView() {
        if (getHeadLayoutId() != 0 && getHeadViewStubId() != 0) {
            int layoutId = getHeadLayoutId();
            final View viewStub = getMainView().findViewById(getHeadViewStubId());
            if (viewStub != null) {
                final ViewStub stub = (ViewStub) viewStub;
                stub.setLayoutResource(layoutId);
                head = stub.inflate();
                initHeadViewEvent(head);
            }
        }
    }

    /**
     * 设置错误页面
     */
    private void initErrorViewEvent(View errorView) {
        CommonEmptyView error = new CommonEmptyView(getBaseTabActivity());
        error.setEmptyListener(new CommonEmptyView.EmptyListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
        ((ViewGroup) errorView).addView(error);
    }

    /**
     * 设置空数据页面
     *
     * @param emptyView
     */
    private void initEmptyViewEvent(View emptyView) {
        CommonEmptyView empty = new CommonEmptyView(getActivity());
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
     * 设置头可见性
     */
    final protected void setHeadViewVisibility(int visibility) {
        LibViewUtil.setViewVisibility(head, visibility);
    }

    /**
     * 加载更多
     */
    final protected void getMore() {
        LibViewUtil.setChildEnableRecursion(ptr, false);
        invokeGetMoreDeliveryApi(HTTP_REQUEST_CODE_GET_MORE);
    }

    /**
     * 获取界面数据（刷新界面）
     */
    final protected void getData() {
        LibViewUtil.setChildEnableRecursion(ptr, false);
        invokeGetDataDeliveryApi(HTTP_REQUEST_CODE_GET_DATA);
    }

    /**
     * 显示内容页
     */
    protected void showRefreshView() {
        LibViewUtil.setViewVisibility(errorView, View.GONE);
        LibViewUtil.setViewVisibility(emptyView, View.GONE);
        LibViewUtil.setViewVisibility(ptr, View.VISIBLE);
    }

    /**
     * 显示错误页
     */
    protected void showNetErrorView() {
        if (errorView == null) {
            int layoutId = R.layout.viewstub_wrapper;
            View viewStub = getMainView().findViewById(R.id.viewstub_error);
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
     * 设置空数据页显示信息，建议在
     */
    final protected void setEmptyViewMsg(String msg) {
        emptyMsg = msg;

        if (emptyView != null) {
            ((CommonEmptyView) ((ViewGroup) emptyView).getChildAt(0)).setContentText(emptyMsg);
        }
    }

    /**
     * 显示空数据页
     */
    protected void showEmptyView() {
        if (emptyView == null) {
            int layoutId = R.layout.viewstub_wrapper;
            final View viewStub = getMainView().findViewById(R.id.viewstub_empty);
            if (viewStub != null) {
                final ViewStub stub = (ViewStub) viewStub;
                stub.setLayoutResource(layoutId);
                emptyView = stub.inflate();
                initEmptyViewEvent(emptyView);
            }
        }

        if (!TextUtils.isEmpty(emptyMsg)) {
            ((CommonEmptyView) ((ViewGroup) emptyView).getChildAt(0)).setContentText(emptyMsg);
        }

        LibViewUtil.setViewVisibility(emptyView, View.VISIBLE);
        LibViewUtil.setViewVisibility(errorView, View.GONE);
        LibViewUtil.setViewVisibility(ptr, View.GONE);
    }

    ///////////// 可选重写 ////////////////

    /**
     * 获取头部的layoutId
     */
    protected int getHeadLayoutId() {
        return 0;
    }

    /**
     * 获取头部在主layout中的viewstub的Id
     */
    protected int getHeadViewStubId() {
        return 0;
    }

    /**
     * 设置头部
     */
    protected void initHeadViewEvent(View headView) {
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            // 刷新数据
            case HTTP_REQUEST_CODE_GET_DATA:
                ptr.onRefreshComplete();
                LibViewUtil.setChildEnableRecursion(ptr, true);
                setGetData(result);
                break;
            // 加载更多数据
            case HTTP_REQUEST_CODE_GET_MORE:
                ptr.onRefreshComplete();
                LibViewUtil.setChildEnableRecursion(ptr, true);
                setGetMore(result);
                break;
            default:
                break;
        }
    }

    @Override
    public void onSuccessException(int requestCode, Throwable t) {
        switch (requestCode) {
            // 刷新数据
            case HTTP_REQUEST_CODE_GET_DATA:
                PublicUtil.showToastServerBusy();
                ptr.onRefreshComplete();
                LibViewUtil.setChildEnableRecursion(ptr, true);
                showNetErrorView();
                break;
            // 加载更多数据
            case HTTP_REQUEST_CODE_GET_MORE:
                PublicUtil.showToastServerBusy();
                ptr.onRefreshComplete();
                LibViewUtil.setChildEnableRecursion(ptr, true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onFailureResponse(int requestCode, Throwable t) {
        switch (requestCode) {
            // 刷新数据
            case HTTP_REQUEST_CODE_GET_DATA:
                PublicUtil.showToastServerOvertime();
                ptr.onRefreshComplete();
                LibViewUtil.setChildEnableRecursion(ptr, true);
                showNetErrorView();
                break;
            // 加载更多数据
            case HTTP_REQUEST_CODE_GET_MORE:
                PublicUtil.showToastServerOvertime();
                ptr.onRefreshComplete();
                LibViewUtil.setChildEnableRecursion(ptr, true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onErrorResponse(int requestCode, ResultBase result) {
        switch (requestCode) {
            // 刷新数据
            case HTTP_REQUEST_CODE_GET_DATA:
                PublicUtil.showErrorMsg(getActivity(), result);
                ptr.onRefreshComplete();
                LibViewUtil.setChildEnableRecursion(ptr, true);
                showNetErrorView();
                break;
            // 加载更多数据
            case HTTP_REQUEST_CODE_GET_MORE:
                PublicUtil.showErrorMsg(getActivity(), result);
                ptr.onRefreshComplete();
                LibViewUtil.setChildEnableRecursion(ptr, true);
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
     * 具体调用刷新数据时的DeliveryApi的方法
     *
     * @param requestCode DeliveryApi的方法中的requestCode参数
     */
    protected abstract void invokeGetDataDeliveryApi(int requestCode);

    /**
     * 具体调用加载更多数据时的DeliveryApi的方法
     *
     * @param requestCode DeliveryApi的方法中的requestCode参数
     */
    protected abstract void invokeGetMoreDeliveryApi(int requestCode);

    /**
     * 设置刷新请求成功的数据
     *
     * @param result ResultBase类的子类对象，resultcode为OK
     */
    abstract protected void setGetData(ResultBase result);

    /**
     * 设置加载更多数据请求成功的数据
     *
     * @param result ResultBase类的子类对象，resultcode为OK
     */
    protected abstract void setGetMore(ResultBase result);
}
