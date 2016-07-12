package cn.com.bluemoon.delivery.module.base;

import android.os.Bundle;
import android.widget.ScrollView;

import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshScrollView;

/**
 * 下拉刷新普通页面，自动显示空数据页面和网络错误页面
 */
public abstract class RefreshableActivity extends BaseActionBarActivity {

    private PullToRefreshScrollView ptrsv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        ptrsv = (PullToRefreshScrollView) findViewById(getPullToRefreshScrollerId());
        ptrsv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        ptrsv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
            }
        });
    }

    /**
     * setContentView(id);使用
     *
     * @return id
     */
    protected abstract int getLayoutId();

    /**
     * @return PullToRefreshScrollView的id
     */
    protected abstract int getPullToRefreshScrollerId();

    /**
     * 获取界面数据（刷新界面）
     */
    protected abstract void getData();
}
