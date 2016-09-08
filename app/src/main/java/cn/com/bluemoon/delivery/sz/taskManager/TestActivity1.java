package cn.com.bluemoon.delivery.sz.taskManager;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.base.BaseTabActivity;

/**
 * Created by Wan.N
 * Date       2016/9/7
 * Desc      用于测试的activity
 */
public class TestActivity1 extends BaseTabActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sz_activity_test1);
        initView();
    }


    public void initView() {
        SzTaskEvaluateFragment fragment = new SzTaskEvaluateFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.container1, fragment).commit();
    }

}
