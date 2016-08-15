package cn.com.bluemoon.delivery.module.base;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshListView;

/**
 * 基于PullToRefreshListView的基础刷新activity，自动显示空数据页面和网络错误页面
 * Created by lk on 2016/7/26.
 */
public abstract class BasePullToRefreshListViewActivity<ADAPTER extends BaseListAdapter, ITEM
        extends Object> extends BasePullToRefreshActivity implements OnListItemClickListener {

    /**
     * 列表adapter
     */
    private ADAPTER adapter;

    /**
     * 列表数据
     */
    private List<ITEM> list;

    protected PullToRefreshListView ptrlv;

    @Override
    final protected int getLayoutId() {
        return R.layout.activity_pull_to_refresh_list_view;
    }

    @Override
    final protected void initPtr(PullToRefreshBase ptr) {
        ptrlv = (PullToRefreshListView) ptr;
        initPullToRefreshListView(ptrlv);
        adapter = getNewAdapter();
        list = new ArrayList<>();
        adapter.setList(list);
        ptrlv.setAdapter(adapter);
    }

    @Override
    final protected void setGetMore(ResultBase result) {
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
        this.list.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    final protected void setGetData(ResultBase result) {
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
     * 设置刷新请求成功的列表数据
     */
    private void setGetDataList(List list) {
        this.list.clear();
        this.list.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    final protected int getPtrId() {
        return R.id.ptrlv;
    }

    @Override
    final protected int getHeadViewStubId() {
        return R.id.viewstub_head;
    }

    ///////////// 工具方法 ////////////////
    final protected List<ITEM> getList() {
        return list;
    }

    ///////////// 可选重写 ////////////////

    /**
     * 设置列表其他属性，如设置分割线等
     */
    protected void initPullToRefreshListView(PullToRefreshListView ptrlv) {
//        ptrlv.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
//        ptrlv.setDividerDrawable(getResources().getDrawable(R.drawable.div_left_padding_16));
//        ptrlv.getRefreshableView().setDividerHeight(getResources().getDimensionPixelSize(R.dimen
//                .div_height));
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
}
