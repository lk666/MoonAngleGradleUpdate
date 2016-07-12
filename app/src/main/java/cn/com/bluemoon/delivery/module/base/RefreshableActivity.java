package cn.com.bluemoon.delivery.module.base;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ImageView;
import android.widget.ScrollView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.utils.PublicUtil;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshBase;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshScrollView;

public class RefreshableActivity extends BaseActionBarActivity {

    @Bind(R.id.ptrsv)
    PullToRefreshScrollView ptrsv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refreshable);
        ButterKnife.bind(this);

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

    private void getData() {
        ptrsv.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrsv.onRefreshComplete();
                PublicUtil.showToast("加载数据完成");
            }
        }, 5000);
    }

    @Override
    protected int getActionBarTitleRes() {
        return R.string.title_clothing_book_in;
    }

}
