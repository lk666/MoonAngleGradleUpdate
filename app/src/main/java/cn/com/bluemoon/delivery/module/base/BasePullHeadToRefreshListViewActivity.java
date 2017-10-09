package cn.com.bluemoon.delivery.module.base;

import android.view.LayoutInflater;
import android.widget.AbsListView;

/**
 * 基于PullToRefreshListView的基础刷新activity，自动显示空数据页面和网络错误页面，头可滚动
 * Created by lk on 2016/7/26.
 */
public abstract class BasePullHeadToRefreshListViewActivity extends BasePullToRefreshListViewActivity {

    @Override
    final protected void initHeadView() {
        int layoutId = getHeadLayoutId();
        if (layoutId != 0) {
            head = LayoutInflater.from(this).inflate(layoutId, ptrlv, false);

            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView
                    .LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
            head.setLayoutParams(layoutParams);
            ptrlv.getRefreshableView().addHeaderView(head);

            initHeadViewEvent(head);
        }
    }
}
