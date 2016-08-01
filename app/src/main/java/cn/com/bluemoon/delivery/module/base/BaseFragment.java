package cn.com.bluemoon.delivery.module.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.module.base.interf.DialogControl;
import cn.com.bluemoon.delivery.module.base.interf.IActionBarListener;
import cn.com.bluemoon.delivery.ui.CommonActionBar;

/**
 * 基础Fragment，必须属于{@link BaseTabActivity}
 * Created by lk on 2016/7/29.
 */
public abstract class BaseFragment extends Fragment implements DialogControl {

    private BaseTabActivity aty;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        aty = (BaseTabActivity) getActivity();
        onBeforeCreateView();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        initCustomActionBar();
        View v = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }

    private void initCustomActionBar() {
        if (!TextUtils.isEmpty(getTitleString())) {
            CommonActionBar titleBar = new CommonActionBar(aty.getActionBar(),
                    new IActionBarListener() {

                        @Override
                        public void onBtnRight(View v) {
                            onActionBarBtnRightClick();
                        }

                        @Override
                        public void onBtnLeft(View v) {
                            onActionBarBtnLeftClick();
                        }

                        @Override
                        public void setTitle(TextView v) {
                            v.setText(getTitleString());
                        }

                    });
            setActionBar(titleBar);
        }
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
    public void onDestroy() {
        super.onDestroy();
    }

    ///////////// 工具方法 ////////////////

    /**
     * 获取Activity
     */
    final protected BaseTabActivity getBaseTabActivity() {
        return aty;
    }


    final protected void longToast(String msg) {
        aty.longToast(msg);
    }

    final protected void toast(String msg) {
        aty.toast(msg);
    }

    /**
     * 默认TAG
     *
     * @return getClass().getSimpleName()
     */
    final protected String getDefaultTag() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void hideWaitDialog() {
        aty.hideWaitDialog();
    }

    @Override
    public ProgressDialog showWaitDialog() {
        return aty.showWaitDialog();
    }

    @Override
    public ProgressDialog showWaitDialog(int resId, int viewId) {
        return aty.showWaitDialog(resId, viewId);
    }

    @Override
    public ProgressDialog showWaitDialog(String text, int viewId) {
        return aty.showWaitDialog(text, viewId);
    }

    final protected ProgressDialog showWaitDialog(String message, int viewId, boolean
            isCancelable) {
        return aty.showWaitDialog(message, viewId, isCancelable);
    }

    ///////////// 可选重写 ////////////////

    /**
     * 在oncreateView后执行，一般为获取getArguments();的数据
     */
    protected void onBeforeCreateView() {
    }

    /**
     * 返回为null或者空字符串，则不设置ActionBar(若要不显示actionbar，须在主题中声明)
     */
    protected String getTitleString() {
        return null;
    }

    /**
     * 左键点击事件，一般不需重写
     */
    protected void onActionBarBtnLeftClick() {
        aty.setResult(Activity.RESULT_CANCELED);
        aty.finish();
    }

    /**
     * 右键点击事件
     */
    protected void onActionBarBtnRightClick() {
    }

    /**
     * 设置自定义ActionBar，如右图标
     */
    protected void setActionBar(CommonActionBar titleBar) {
    }

    ///////////// 必须重写 ////////////////

    /**
     * 设置布局文件layout，一般都要重写
     */
    protected abstract int getLayoutId();
}
