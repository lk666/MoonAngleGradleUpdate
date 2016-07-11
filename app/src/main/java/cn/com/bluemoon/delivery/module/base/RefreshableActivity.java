package cn.com.bluemoon.delivery.module.base;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.lib.pulltorefresh.PullToRefreshScrollView;

public class RefreshableActivity extends Activity {

    @Bind(R.id.iv_content)
    ImageView ivContent;
    @Bind(R.id.ptrsv)
    PullToRefreshScrollView ptrsv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refreshable);
        ButterKnife.bind(this);

//        ptrsv.setOn
    }
}
