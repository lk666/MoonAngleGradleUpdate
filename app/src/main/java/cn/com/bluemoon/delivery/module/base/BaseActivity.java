package cn.com.bluemoon.delivery.module.base;

import android.app.Activity;
import android.os.Bundle;

import com.umeng.analytics.MobclickAgent;

import cn.com.bluemoon.delivery.manager.ActivityManager;
import cn.com.bluemoon.lib.view.CommonProgressDialog;

/**
 * 基础Activity，实现了一些公共方法
 * Created by lk on 2016/6/14.
 */
public abstract class BaseActivity extends Activity implements IShowDialog {

    private CommonProgressDialog progressDialog;

    /**
     * 默认TAG
     *
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
