package cn.com.bluemoon.delivery.module.base;

import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;

// TODO: lk 2016/7/14 缺少一种有head的刷新基类

/**
 * 基于PullToRefreshListView的基础刷新activity，自动显示空数据页面和网络错误页面
 */
public abstract class BasePullToRefreshListViewActivity<ADAPTER extends BaseListAdapter, ITEM
        extends Object> extends BasePullToRefreshActivity {
    /**
     * 列表adapter
     */
    private ADAPTER adapter;

    /**
     * 列表数据
     */
    private List<ITEM> list;

    /**
     * 下拉刷新listview
     */
    private PullToRefreshListView ptrlv;

    @Override
    final protected int getLayoutId() {
        return R.layout.activity_pull_to_refresh_list_view;
    }

    @Override
    final protected void initPtr(PullToRefreshBase ptr) {
        ptrlv = (PullToRefreshListView) ptr;
        initPullToRefreshListView(ptrlv);
        adapter = getNewAdapter();
        ptrlv.setAdapter(adapter);
    }

    /**
     * 产生adapter
     *
     * @return 列表的数据adapter
     */
    protected abstract ADAPTER getNewAdapter();

    @Override
    final protected void setGetMore(Object result) {
        List dataList = getGetMoreList(result);
        // 判断数据是否为空
        if (dataList == null || dataList.isEmpty()) {
//                    showEmptyView();
        } else {
            setGetMoreList(dataList);
        }
    }

    /**
     * 设置加载更多数据请求成功的列表数据
     */
    private void setGetMoreList(List list) {
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
     * 设置列表其他属性，如设置分割线等
     */
    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
//        ptrlv.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
//        ptrlv.setDividerDrawable(getResources().getDrawable(R.drawable.div_left_padding_16));
//        ptrlv.getRefreshableView().setDividerHeight(getResources().getDimensionPixelSize(R.dimen
//                .div_height));
    }

    @Override
    final protected void setGetData(Object result) {
        List dataList = getGetDataList(result);
        // 判断数据是否为空
        if (dataList == null || dataList.isEmpty()) {
            showEmptyView();
        } else {
            setGetDataList(dataList);
            showRefreshView();
        }
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
    private void setGetDataList(List list) {
        if (this.list == null) {
            setAdapterList(list);
        } else {
            this.list.clear();
            this.list.addAll(list);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    final protected int getPtrId() {
        return R.id.ptrlv;
    }

    @Override
    final protected PullToRefreshBase.Mode getMode() {
        return PullToRefreshBase.Mode.BOTH;
    }
}
