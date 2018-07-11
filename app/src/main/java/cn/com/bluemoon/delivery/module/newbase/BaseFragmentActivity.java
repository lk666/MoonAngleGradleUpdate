package cn.com.bluemoon.delivery.module.newbase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;

import butterknife.BindView;
import cn.com.bluemoon.delivery.BuildConfig;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.ApiHttpClient;
import cn.com.bluemoon.delivery.utils.DialogUtil;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;
import cn.com.bluemoon.liblog.LogUtils;
import cn.com.bluemoon.liblog.NetLogDialog;

/**
 * 基础Activity，不处理任何除fragment交互等的逻辑。
 * Activity应该是模块的入口，所有子模块都应是fragmtent。
 * 子模块以栈的形式管理（类似activity），子模块之间的交互通过eventbus进行。
 */
public abstract class BaseFragmentActivity extends FragmentActivity {
    /**
     * Activity的基础模块的容器，一般是如首页的tab等，类似模块的“main activity”的，single instance启动方式的“主页”。
     * 一般来说，此fragmtent模块应该是其他子模块的总入口，且不适合在回退时需要重新绘制。
     * 此Fragment容器只能有一层fragment，即不可addToBackStack
     * 总的来说，此容器就是用来存放Activity模块的主fragmtent的，此fragmtent不会被放到栈中，即不会被重绘。
     */
    @BindView(R.id.fl_content)
    FrameLayout flContent;
    /**
     * 栈节点的容器，所有非主的子fragment模块都应该在此容器上堆叠。
     */
    @BindView(R.id.fl_mask)
    FrameLayout flMask;

    private static final String TAG_MAIN = "MAIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushOneActivity(this);
        onBeforeSetContentLayout(savedInstanceState);

        setContentView(R.layout.activity_new_base_fragment);

        //                // TODO 沉浸式，是否使用也应写到配置
        //                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        //                    Window window = this.getWindow();
        //                    WindowManager.LayoutParams layoutParams = window.getAttributes();
        //                    layoutParams.flags |= WindowManager.LayoutParams
        // .FLAG_TRANSLUCENT_STATUS;
        //                    window.setAttributes(layoutParams);
        //                    mActionBar.setImmerse();
        //                }
        //         StatusBarUtil.StatusBarLightMode(this,1); // 设置状态栏字体颜色

        // Icepick.restoreInstanceState(this, savedInstanceState);

        showLifeCycle("onCreate");
        Fragment main = getSupportFragmentManager().findFragmentById(R.id.fl_content);
        if (main == null) {
            main = getMainFragment();
        }
        setMainFragment(main);
    }

    /**
     * 设置主fragment
     */
    private void setMainFragment(Fragment fragment) {
        if (fragment == null) {
            return;
        }
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();

        // 设置跳转动画
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);

        // 不压入后退栈
        ft.replace(R.id.fl_mask, fragment).commitAllowingStateLoss();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        showLifeCycle("onRestart");
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        showLifeCycle("onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        showLifeCycle("onPause");
    }

    @Override
    protected void onDestroy() {
        hideWaitDialog();
        ApiHttpClient.cancelAll(this);
        ActivityManager.getInstance().popOneActivity(this);

        super.onDestroy();
        showLifeCycle("onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();
        showLifeCycle("onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        showLifeCycle("onStop");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        showLifeCycle("onSaveInstanceState");
        // Icepick.saveInstanceState(this, outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        showLifeCycle("onRestoreInstanceState");
        // Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        showLifeCycle("onConfigurationChanged");
    }

    /**
     * activity与所属fragment是否显示生命周期log
     */
    boolean isShowLifeCycle() {
        return BuildConfig.DEBUG;
    }

    private void showLifeCycle(String info) {
        if (isShowLifeCycle()) {
            Log.d("LIFE_CYCLE", getClass().getSimpleName() + "-->" + info);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    // debug模式下，点击物理menu键显示最近10条网络日志弹窗
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (!BuildConfig.RELEASE && keyCode == KeyEvent.KEYCODE_MENU) {
            new NetLogDialog().show(getSupportFragmentManager(), "NET_LOG");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 相当于手动点击后退键，回到上一个界面
     *
     * @return 回退是否成功（后退栈数量 > 0）
     */
    private boolean popFragment() {
        final int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackCount > 0) {
            getSupportFragmentManager().popBackStack();
            return true;
        }
        return false;
    }

    //////////////// 可选重写 ////////////////

    /**
     * 在oncreate后执行，一般为获取intentata
     */
    protected void onBeforeSetContentLayout(Bundle savedInstanceState) {
    }

    //////////////// 工具方法 ////////////////

    /**
     * 设置软键盘
     */
    final public void setSoftInputMode(int mode) {
        getWindow().setSoftInputMode(mode);
    }

    ////// toast //////
    final public void longToast(String msg) {
        ViewUtil.longToast(msg);
    }

    final public void toast(String msg) {
        ViewUtil.toast(msg);
    }

    final public void longToast(int resId) {
        ViewUtil.longToast(resId);
    }

    final public void toast(int resId) {
        ViewUtil.toast(resId);
    }

    ////// 转菊花dialog //////
    private ProgressDialog waitDialog;

    final public void hideWaitDialog() {
        if (waitDialog != null) {
            try {
                waitDialog.dismiss();
                waitDialog = null;
            } catch (Exception ex) {
                ex.printStackTrace();
                LogUtils.e("关闭dialog失败：" + ex.getMessage());
            }
        }
    }

    final public void showWaitDialog() {
        showWaitDialog(true);
    }

    /**
     * @param isCancelable 是否可以手动结束转菊花
     */
    final protected void showWaitDialog(boolean isCancelable) {
        showWaitDialog(R.string.data_loading, R.layout.dialog_progress, isCancelable);
    }

    final public void showWaitDialog(int resId, int viewId) {
        showWaitDialog(resId, viewId, true);
    }

    final public void showWaitDialog(String message, int viewId) {
        showWaitDialog(message, viewId, true);
    }

    final protected void showWaitDialog(int resId, int viewId, boolean isCancelable) {
        showWaitDialog(getString(resId), viewId, isCancelable);
    }

    final protected void showWaitDialog(String message, int viewId, boolean
            isCancelable) {
        if (waitDialog == null) {
            waitDialog = DialogUtil.getWaitDialog(this);
        }

        waitDialog.setMessage(message);
        waitDialog.setCancelable(isCancelable);
        waitDialog.show();
        if (viewId != 0) {
            waitDialog.setContentView(viewId);
        }
    }

    ////// 通用fragment操作 //////

    /**
     * 相当于startActivity，进入一个新的fragment
     */
    public void pushFragment(Fragment fragment, String tag) {
        if (fragment == null) {
            return;
        }
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();

        // 设置跳转动画
        // 压入后退栈中
        ft.setCustomAnimations(R.anim.fragment_slide_right_in,
                R.anim.fragment_slide_left_out,
                R.anim.fragment_slide_left_in,
                R.anim.fragment_slide_right_out)
                .replace(R.id.fl_mask, fragment, tag).addToBackStack(null)
                .commitAllowingStateLoss();
    }

    /**
     * 相当于startActivity，进入一个新的fragment
     */
    public void pushFragment(Fragment fragment) {
        if (fragment == null) {
            return;
        }
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();

        // 设置跳转动画
        // 压入后退栈中
        ft.setCustomAnimations(R.anim.fragment_slide_right_in,
                R.anim.fragment_slide_left_out,
                R.anim.fragment_slide_left_in,
                R.anim.fragment_slide_right_out)
                .replace(R.id.fl_mask, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    /**
     * 相当于手动点击后退键，回到上一个界面
     */
    public void handleBack() {
        if (!popFragment()) {
            finish();
        }
    }

    /**
     * 回退到底部，即显示主页（{@link #flContent}）
     */
    public void popFragmentsToBase() {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * 获取当前栈顶的fragment，即当前activity显示的fragment
     */
    public Fragment getCurrentTopFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            return getSupportFragmentManager().findFragmentById(R.id.fl_mask);
        }
        return getSupportFragmentManager().findFragmentById(R.id.fl_content);
    }

    ///////////// 必须重写 ////////////////

    /**
     * 获取当前栈顶的fragment，即当前activity显示的fragment
     */
    protected abstract Fragment getMainFragment();
}