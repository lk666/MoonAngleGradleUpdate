package cn.com.bluemoon.delivery.module.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import cn.com.bluemoon.delivery.manager.ActivityManager;

// TODO: lk 2016/6/12 修改目录结构，先规划再动手
// TODO: lk 2016/6/12 所有的activity继承基类，解决内存泄漏
// TODO: lk 2016/6/12 删除kjframe，换用imageloader+butterknife 
// TODO: lk 2016/6/12 可考虑换用logutil 

/**
 * 基础FragmentActivity，实现了一些公共方法
 * Created by lk on 2016/6/3.
 */
public class BaseFragmentActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushOneActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getInstance().popOneActivity(this);
    }
}
