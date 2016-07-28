package cn.com.bluemoon.delivery.module.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;

/**
 * 具有自定义actionBar的基础Activity
 * Created by lk on 2016/6/14.
 */
@Deprecated
public abstract class BaseActionBarActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCustomActionBar();
    }

    // TODO: lk 2016/7/6 事件定义不灵活
    protected void initCustomActionBar() {
        CommonActionBar titleBar = new CommonActionBar(getActionBar(), new IActionBarListener() {

            @Override
            public void onBtnRight(View v) {
                ontActionBarBtnRightClick();
            }

            @Override
            public void onBtnLeft(View v) {
                ontActionBarBtnLeftClick();
            }

            @Override
            public void setTitle(TextView v) {
                v.setText(getActionBarTitleRes());
            }

        });

        setActionBar(titleBar);
    }

    /**
     * 设置标题栏其他属性，如右边按钮的图片等
     */
    protected void setActionBar(CommonActionBar titleBar) {

    }

    /**
     * 获取ActionBar标题文字
     *
     * @return R.string.*
     */
    protected abstract int getActionBarTitleRes();

    /**
     * ActionBar左键点击
     */
    protected void ontActionBarBtnLeftClick() {
        BaseActionBarActivity.this.setResult(Activity.RESULT_CANCELED);
        BaseActionBarActivity.this.finish();
    }

    /**
     * ActionBar右键点击
     */
    protected void ontActionBarBtnRightClick() {

    }
}
