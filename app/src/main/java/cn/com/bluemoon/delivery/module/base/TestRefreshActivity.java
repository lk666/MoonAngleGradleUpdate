package cn.com.bluemoon.delivery.module.base;

import android.app.Activity;
import android.os.Bundle;

import cn.com.bluemoon.delivery.R;

public class TestRefreshActivity extends BaseRefreshableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_refresh);
    }

    @Override
    protected int getActionBarTitleRes() {
        return 0;
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected int getPullToRefreshScrollerId() {
        return 0;
    }

    @Override
    protected void getData() {

    }
}
