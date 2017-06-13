package cn.com.bluemoon.delivery.module.newbase.pulltorefresh;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.base.BaseListAdapter;
import cn.com.bluemoon.delivery.module.base.OnListItemClickListener;
import cn.com.bluemoon.delivery.module.newbase.view.BaseTitleBar;
import cn.com.bluemoon.delivery.module.newbase.view.CommonActionBar;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;
import cn.com.bluemoon.lib.utils.LibViewUtil;

/**
 * 基于PullToRefreshListView的基础刷新fragment，自动显示空数据页面和网络错误页面。
 */
public abstract class BasePullToRefreshListViewFragment<ADAPTER extends
        BasePullToRefreshListViewFragment.BaseListAdapter, ITEM
        extends Object, TITLE_BAR extends BaseTitleBar> extends BasePullToRefreshFragment<TITLE_BAR>
        implements OnListItemClickListener, CommonActionBar.OnTitleBarClickLister {
    /**
     * 列表adapter
     */
    private ADAPTER adapter;

    /**
     * 列表数据
     */
    private List<ITEM> list;

    private PullToRefreshListView ptrlv;
    protected View flNoMore;

    /**
     * 模式是否支持上拉加载
     */
    private boolean modeCanPullUp = false;
    /**
     * 当getMore返回空时为false，表示是否可以继续getMore
     */
    protected boolean canGetMore = false;
    /**
     * 是否正在发送getMore请求
     */
    private boolean isGettingMore = false;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_pull_to_refresh_list_view;
    }

    @Override
    final protected void initHeadView() {
        if (isHeaderFixed()) {
            super.initHeadView();
        } else {
            // 头部滚动时，viewstub_head是没有作用的
            int layoutId = getHeadLayoutId();
            if (layoutId != 0) {
                head = LayoutInflater.from(getContext()).inflate(layoutId, ptrlv, false);
                AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView
                        .LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
                head.setLayoutParams(layoutParams);
                ptrlv.getRefreshableView().addHeaderView(head);
            }
        }
    }

    @Override
    final protected void initPtr(PullToRefreshBase ptr) {
        ptrlv = (PullToRefreshListView) ptr;
        ptrlv.getLoadingLayoutProxy(false, true).setReleaseLabel(
                getString(R.string.refresh_from_bottom_release_label));
        initPullToRefreshListView(ptrlv);
        adapter = getNewAdapter();
        list = new ArrayList<>();
        adapter.setList(list);
        ptrlv.setAdapter(adapter);

        flNoMore = getNoMoreView();
        if (flNoMore != null) {
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView
                    .LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
            flNoMore.setLayoutParams(layoutParams);
            ptrlv.getRefreshableView().addFooterView(flNoMore);
            flNoMore.setVisibility(View.GONE);
        }
    }

    @Override
    final protected void setGetMore(ResultBase result) {
        List dataList = getGetMoreList(result);

        // 判断数据是否为空
        if (dataList == null || dataList.isEmpty()) {
            canGetMore = false;
            setNoMoreViewVisibility(View.VISIBLE);
            isGettingMore = false;
            Log.e("sdsadasd", "setGetMore数据为空" + "--isGettingMore=" + isGettingMore);
            adapter.notifyDataSetChanged();
        } else {
            isGettingMore = false;
            canGetMore = true;
            Log.e("sdsadasd", "setGetMore数据不为空" + "--isGettingMore=" + isGettingMore);
            setGetMoreList(dataList);
        }
    }

    private void setNoMoreViewVisibility(int visibility) {
        if (modeCanPullUp && isShowNoMoreView() && flNoMore != null) {
            flNoMore.setVisibility(visibility);
        }
    }

    @Override
    public void onDoubleClick() {
        if (ptrlv != null && list != null && !list.isEmpty()) {
            ptrlv.getRefreshableView().setSelection(0);
        }
    }

    @Override
    protected void initTitleBarView(View title) {
        super.initTitleBarView(title);

        if (title == null || !(title instanceof CommonActionBar)) {
            return;
        }
        ((CommonActionBar) title).setDoubleClickListener(this);
    }

    /**
     * 设置加载更多数据请求成功的列表数据
     */
    private void setGetMoreList(List list) {
        this.list.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    final protected void setGetData(ResultBase result) {
        List dataList = getGetDataList(result);
        // 判断数据是否为空
        if (dataList == null || dataList.isEmpty()) {
            canGetMore = false;
            showEmptyView();
        } else {
            canGetMore = true;
            setGetDataList(dataList);
            showRefreshView();
        }
    }

    // 上拉加载改为到倒数第二个时自动加载
    @Override
    final protected void setMode(PullToRefreshBase.Mode mode) {
        modeCanPullUp = false;
        if (mode.equals(PullToRefreshBase.Mode.BOTH)) {
            modeCanPullUp = true;
            mode = PullToRefreshBase.Mode.PULL_FROM_START;
        } else if (mode.equals(PullToRefreshBase.Mode.PULL_FROM_END)) {
            modeCanPullUp = true;
            mode = PullToRefreshBase.Mode.DISABLED;
        } else {
            modeCanPullUp = false;
        }

        super.setMode(mode);
    }

    @Override
    protected void showEmptyView() {
        super.showEmptyView();
        setNoMoreViewVisibility(View.GONE);

        // 空白时显示列表头
        if (isShowHeaderEmpty() && !isHeaderFixed()) {
            LibViewUtil.setViewVisibility(ptrlv, View.VISIBLE);
            list.clear();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void showNetErrorView() {
        super.showNetErrorView();
        setNoMoreViewVisibility(View.GONE);

        // 错误时显示列表头
        if (isShowHeaderError() && !isHeaderFixed()) {
            LibViewUtil.setViewVisibility(ptrlv, View.VISIBLE);
            list.clear();
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置刷新请求成功的列表数据
     */
    private void setGetDataList(List list) {
        this.list.clear();
        this.list.addAll(list);
        adapter.notifyDataSetChanged();
        this.ptrlv.getRefreshableView().scrollTo(0, 0);
    }

    @Override
    final protected int getPtrId() {
        return R.id.ptrlv;
    }

    @Override
    final protected int getHeadViewStubId() {
        return R.id.viewstub_head;
    }

    /**
     * 加载更多
     */
    final protected void getMore() {
        isGettingMore = true;
        Log.e("sdsadasd", "invokeGetMoreDeliveryApi" + "--isGettingMore=" + isGettingMore);
        super.getMore();
    }

    /**
     * 获取界面数据（刷新界面）
     */
    final protected void getData() {
        if (modeCanPullUp) {
            isGettingMore = false;
            canGetMore = false;
        }
        setNoMoreViewVisibility(View.GONE);
        super.getData();
    }
    ///////////// 工具方法 ////////////////

    final protected List<ITEM> getList() {
        return list;
    }

    final protected ADAPTER getAdapter() {
        return adapter;
    }

    ///////////// 可选重写 ////////////////

    /**
     * 是否在没有更多数据时显示底部没有更多数据view
     */
    protected boolean isShowNoMoreView() {
        return true;
    }

    /**
     * 返回没有更多数据时的view（其实就是列表的尾部）。返回null不显示没有更多数据view。
     * 当需要同时显示尾部和没有更多数据view时，
     * 请在{@link #initPullToRefreshListView(PullToRefreshListView)}中添加
     */
    protected View getNoMoreView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.view_no_more, ptrlv, false);
    }

    /**
     * 设置列表其他属性，如设置分割线等
     */
    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
        ptrlv.getRefreshableView().setDivider(null);
        ptrlv.getRefreshableView().setDividerHeight(0);
        ptrlv.getRefreshableView().setSelector(R.color.transparent);
    }

    /**
     * 头部是否固定，即不随列表滚动
     */
    protected boolean isHeaderFixed() {
        return false;
    }

    ///////////// 必须重写 ////////////////

    /**
     * 产生adapter
     *
     * @return 列表的数据adapter
     */
    protected abstract ADAPTER getNewAdapter();

    /**
     * 获取加载更多数据返回，处理请求成功的数据（数据不为空），进而得到列表数据
     *
     * @param result ResultBase类的子类对象，resultcode为OK
     * @return 列表数据
     */
    protected abstract List<ITEM> getGetMoreList(ResultBase result);

    /**
     * 获取刷新数据返回，处理请求成功的数据（数据不为空），进而得到列表数据
     *
     * @param result ResultBase类的子类对象，resultcode为OK
     * @return 列表数据
     */
    protected abstract List<ITEM> getGetDataList(ResultBase result);

    /**
     * 基础列表adapter
     * Created by lk on 2016/6/14.
     */
    public abstract class BaseListAdapter extends
            cn.com.bluemoon.delivery.module.base.BaseListAdapter<ITEM> {

        public BaseListAdapter(Context context, OnListItemClickListener listener) {
            super(context, listener);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = super.getView(position, convertView, parent);
            if (v != null) {
                //   倒数第二个发加载更多请求
                Log.e("sdsadasd", "getView" + "--isGettingMore=" + isGettingMore + ", " +
                        "position = " + position + ",size = " + list.size() +
                        ", canGetMore = " + canGetMore);
                if (canGetMore && !isGettingMore && (position + 3 > list.size())) {
                    isGettingMore = true;
                    getMore();
                }
            }
            return v;
        }
    }
}
