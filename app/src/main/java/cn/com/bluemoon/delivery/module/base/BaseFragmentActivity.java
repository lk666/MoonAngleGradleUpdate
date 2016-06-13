package cn.com.bluemoon.delivery.module.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.umeng.analytics.MobclickAgent;

import cn.com.bluemoon.delivery.manager.ActivityManager;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

// TODO: lk 2016/6/12 修改目录结构，先规划再动手
// TODO: lk 2016/6/12 所有的activity继承基类，解决内存泄漏
// TODO: lk 2016/6/12 删除kjframe，换用imageloader+butterknife 
// TODO: lk 2016/6/12 可考虑换用logutil 

/**
 * 基础FragmentActivity，实现了一些公共方法
 * Created by lk on 2016/6/3.
 */
public class BaseFragmentActivity extends FragmentActivity implements IShowDialog{

    private CommonProgressDialog progressDialog;

    /**
     * 默认TAG
     * @return getClass().getSimpleName()
     */
    protected String getDefaultTag() {
        return this.getClass().getSimpleName();
    }

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

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getDefaultTag());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getDefaultTag());
    }

    @Override
    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new CommonProgressDialog(this);
        }

        progressDialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
