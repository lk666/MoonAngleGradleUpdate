package cn.com.bluemoon.delivery.base;

/**
 * 基于PullToRefreshListView的基础刷新activity，自动显示空数据页面和网络错误页面
 * Created by lk on 2016/7/26.
 */
public class BasePullToRefreshListViewActivity <ADAPTER extends BaseListAdapter, ITEM
        extends Object> extends BaseActivity {








    @Override
    void onSuccessResponse(int requestCode, String jsonString) {

    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }
}
