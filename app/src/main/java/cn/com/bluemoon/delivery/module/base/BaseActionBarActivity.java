package cn.com.bluemoon.delivery.module.base;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cn.com.bluemoon.delivery.async.listener.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;

/**
 * 具有自定义actionBar的基础Activity
 * Created by lk on 2016/6/14.
 */
public abstract class BaseActionBarActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCustomActionBar();
    }

    private void initCustomActionBar() {
        new CommonActionBar(getActionBar(), new IActionBarListener() {

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
        setCustomActionBar();
    }

    /**
     * ActionBar的其他设置，如右边按钮的初始化
     */
    protected void setCustomActionBar() {
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
        BaseActionBarActivity.this.finish();
    }

    /**
     * ActionBar右键点击
     */
    protected void ontActionBarBtnRightClick() {

    }
}
