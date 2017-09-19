package cn.com.bluemoon.delivery.module.newbase.pulltorefresh;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ListView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.newbase.BaseFragment;
import cn.com.bluemoon.delivery.module.newbase.view.BaseTitleBar;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonEmptyView;

/**
 * 基础下拉刷新上拉加载Fragment，layout只包含一个不滚动的头部、一个PullToRefresh控件和
 * 一个空白页、 一个错误页
 * Created by lk on 2016/8/1.
 */
public abstract class BasePullToRefreshFragment<TITLE_BAR extends BaseTitleBar> extends
        BaseFragment<TITLE_BAR> {

    /**
     * 头，固定在顶部，开始时显示，与刷新view是分离的
     */
    protected View head;

    /**
     * 错误页面View
     */
    protected View errorView;

    /**
     * 空数据页面View
     */
    protected View emptyView;

    protected static final int HTTP_REQUEST_CODE_GET_MORE = 0x1000;
    protected static final int HTTP_REQUEST_CODE_GET_DATA = 0x1001;

    /**
     * 刷新view
     */
    private PullToRefreshBase ptr;
    private String emptyMsg;

    /**
     * 初始时是否显示头部
     */
    protected boolean isShowHeaderInit() {
        return true;
    }

    /**
     * 数据空白时是否显示头部
     */
    protected boolean isShowHeaderEmpty() {
        return true;
    }

    /**
     * 数据错误时是否显示头部
     */
    protected boolean isShowHeaderError() {
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        return v;
    }

    protected void setMode(PullToRefreshBase.Mode mode) {
        ptr.setMode(mode);
    }

    @Override
    protected void initContentView(View mainView) {
        errorView = null;
        emptyView = null;
        emptyMsg = null;

        // 列表
        ptr = (PullToRefreshBase) mainView.findViewById(getPtrId());
        if (ptr != null) {
            setMode(getMode() == null ? PullToRefreshBase.Mode.DISABLED : getMode());
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
        }

        initHeadView();
        // 头部
        if (head != null) {
            initHeadViewEvent(head);
        }
        LibViewUtil.setViewVisibility(ptr, View.GONE);
        if (isShowHeaderInit()) {
            setHeadViewVisibility(View.VISIBLE);
        } else {
            setHeadViewVisibility(View.GONE);
        }
    }

    /**
     * 初始化头部View
     */
    protected void initHeadView() {
        int layoutId = getHeadLayoutId();
        if (layoutId == 0) {
            return;
        }

        if (getHeadViewStubId() != 0) {
            final View viewStub = mainView.findViewById(getHeadViewStubId());
            if (viewStub != null) {
                final ViewStub stub = (ViewStub) viewStub;
                stub.setLayoutResource(layoutId);
                head = stub.inflate();
            }
        }
    }

    ///////////// 可选重写 ////////////////

    /**
     * 获取头部在主layout中的viewstub的Id
     */
    protected int getHeadViewStubId() {
        return 0;
    }

    /**
     * 获取头部的layoutId，不使用头部时返回0
     */
    protected int getHeadLayoutId() {
        return 0;
    }

    /**
     * 设置头部
     */
    protected void initHeadViewEvent(View headView) {
    }

    /**
     * 设置错误页面
     */
    protected void initErrorViewEvent(View errorView) {
        CommonEmptyView error = new CommonEmptyView(getContext());
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
     */
    protected void initEmptyViewEvent(View emptyView) {
        CommonEmptyView empty = new CommonEmptyView(getActivity());
        empty.setEmptyListener(new CommonEmptyView.EmptyListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
        ((ViewGroup) emptyView).addView(empty);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        errorView = null;
        emptyView = null;
        emptyMsg = null;
    }

    ///////////// 必须重写 ////////////////

    /**
     * 获取列表刷新方式
     *
     * @return 一般为{@link PullToRefreshBase.Mode#BOTH}、
     * {@link PullToRefreshBase.Mode#PULL_FROM_START}
     * 或{@link PullToRefreshBase.Mode#PULL_FROM_END}
     */
    protected abstract PullToRefreshBase.Mode getMode();

    /**
     * 初始化PullToRefresh控件的其它属性
     */
    protected abstract void initPtr(PullToRefreshBase ptr);

    /**
     * 获取pulltorefresh控件id
     */
    protected abstract int getPtrId();

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
    protected void getMore() {
        // LibViewUtil.setChildEnableRecursion(ptr, false);
        invokeGetMoreDeliveryApi(HTTP_REQUEST_CODE_GET_MORE);
    }

    /**
     * 获取界面数据（刷新界面）
     */
    protected void getData() {
        // LibViewUtil.setChildEnableRecursion(ptr, false);
        invokeGetDataDeliveryApi(HTTP_REQUEST_CODE_GET_DATA);
    }

    /**
     * 获取到数据时，显示内容页
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
            View viewStub = mainView.findViewById(R.id.viewstub_error);
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

        if (isShowHeaderError()) {
            setHeadViewVisibility(View.VISIBLE);
        } else {
            setHeadViewVisibility(View.GONE);
        }
    }

    //
    //    /**
    //     * 设置空数据页显示信息，建议在
    //     */
    //    final protected void setEmptyViewMsg(String msg) {
    //        emptyMsg = msg;
    //
    //        if (emptyView != null) {不一定是getChildAt(0)
    //            ((EmptyView) ((ViewGroup) emptyView).getChildAt(0)).setContentText(emptyMsg);
    //        }
    //    }

    /**
     * 显示空数据页
     */
    protected void showEmptyView() {
        if (emptyView == null) {
            int layoutId = R.layout.viewstub_wrapper;
            final View viewStub = mainView.findViewById(R.id.viewstub_empty);
            if (viewStub != null) {
                final ViewStub stub = (ViewStub) viewStub;
                stub.setLayoutResource(layoutId);
                emptyView = stub.inflate();
                initEmptyViewEvent(emptyView);
            }
        }

        //        if (!TextUtils.isEmpty(emptyMsg)) {
        //            ((EmptyView) ((ViewGroup) emptyView).getChildAt(0)).setContentText(emptyMsg);
        //        }

        LibViewUtil.setViewVisibility(emptyView, View.VISIBLE);
        LibViewUtil.setViewVisibility(errorView, View.GONE);
        LibViewUtil.setViewVisibility(ptr, View.GONE);

        if (isShowHeaderEmpty()) {
            setHeadViewVisibility(View.VISIBLE);
        } else {
            setHeadViewVisibility(View.GONE);
        }
    }

    /**
     * 获取空数据页文案
     */
    protected String getEmptyMsg() {
        return getString(R.string.empty_hint3, getTitleString() == null ? "" : getTitleString());
    }

    @Override
    public void onSuccessResponse(int requestCode, String jsonString, ResultBase result) {
        switch (requestCode) {
            // 刷新数据
            case HTTP_REQUEST_CODE_GET_DATA:
                ptr.onRefreshComplete();
                // LibViewUtil.setChildEnableRecursion(ptr, true);
                setGetData(result);
                break;
            // 加载更多数据
            case HTTP_REQUEST_CODE_GET_MORE:
                ptr.onRefreshComplete();
                // LibViewUtil.setChildEnableRecursion(ptr, true);
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
                ViewUtil.toastBusy();
                ptr.onRefreshComplete();
                // LibViewUtil.setChildEnableRecursion(ptr, true);
                showNetErrorView();
                break;
            // 加载更多数据
            case HTTP_REQUEST_CODE_GET_MORE:
                ViewUtil.toastBusy();
                ptr.onRefreshComplete();
                // LibViewUtil.setChildEnableRecursion(ptr, true);
                break;
            default:
                super.onSuccessException(requestCode, t);
                break;
        }
    }

    @Override
    public void onFailureResponse(int requestCode, Throwable t) {
        switch (requestCode) {
            // 刷新数据
            case HTTP_REQUEST_CODE_GET_DATA:
                ViewUtil.toastOvertime();
                ptr.onRefreshComplete();
                // LibViewUtil.setChildEnableRecursion(ptr, true);
                showNetErrorView();
                break;
            // 加载更多数据
            case HTTP_REQUEST_CODE_GET_MORE:
                ViewUtil.toastOvertime();
                ptr.onRefreshComplete();
                // LibViewUtil.setChildEnableRecursion(ptr, true);
                break;
            default:
                super.onFailureResponse(requestCode, t);
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
                // LibViewUtil.setChildEnableRecursion(ptr, true);
                showNetErrorView();
                break;
            // 加载更多数据
            case HTTP_REQUEST_CODE_GET_MORE:
                PublicUtil.showErrorMsg(getActivity(), result);
                ptr.onRefreshComplete();
                // LibViewUtil.setChildEnableRecursion(ptr, true);
                break;
            default:
                super.onErrorResponse(requestCode, result);
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
}
