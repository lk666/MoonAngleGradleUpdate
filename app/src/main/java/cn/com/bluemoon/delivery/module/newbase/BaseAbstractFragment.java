package cn.com.bluemoon.delivery.module.newbase;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * fragment继承层次：0（顶层）
 * 基础Fragment，所属activity必须为{@link BaseFragmentActivity}，
 * 显示生命周期log，对接父类接口。
 * 建议使用newInstance(Bundle)的初始化方式
 */
public abstract class BaseAbstractFragment extends Fragment {
    protected BaseFragmentActivity aty;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        showLifeCycle("onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLifeCycle("onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        aty = (BaseFragmentActivity) getActivity();
        View v = inflater.inflate(getLayoutId(), container, false);
        showLifeCycle("onCreateView");
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        showLifeCycle("onStart");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showLifeCycle("onActivityCreated");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showLifeCycle("onViewCreated");
    }

    @Override
    public void onResume() {
        super.onResume();
        showLifeCycle("onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        showLifeCycle("onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        showLifeCycle("onStop");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        showLifeCycle("onSaveInstanceState");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        showLifeCycle("onDestroyView");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        showLifeCycle("onViewStateRestored");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        showLifeCycle("onHiddenChanged:" + hidden);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        showLifeCycle("onDestroy");
    }

    @Override
    public void onDetach() {
        showLifeCycle("onDetach");
        super.onDetach();
    }

    private void showLifeCycle(String info) {
        if (aty != null && aty.isShowLifeCycle()) {
            Log.d("LIFE_CYCLE", getClass().getSimpleName() + "-->" + info);
        }
    }

    ///////////// 必须重写 ////////////////

    /**
     * 设置布局文件layout
     */
    protected abstract int getLayoutId();

    ///////////// 对接activity工具方法 ////////////////

    /**
     * 设置软键盘
     */
    final public void setSoftInputMode(int mode) {
        if (aty != null) {
            aty.setSoftInputMode(mode);
        }
    }

    ////// toast //////
    final public void longToast(String msg) {
        if (aty != null) {
            aty.longToast(msg);
        }
    }

    final public void toast(String msg) {
        if (aty != null) {
            aty.toast(msg);
        }
    }

    final public void longToast(int resId) {
        if (aty != null) {
            aty.longToast(resId);
        }
    }

    final public void toast(int resId) {
        if (aty != null) {
            aty.toast(resId);
        }
    }

    ////// 转菊花dialog TODO 取消时取消请求//////
    final public void hideWaitDialog() {
        if (aty != null) {
            aty.hideWaitDialog();
        }
    }

    final public void showWaitDialog() {
        if (aty != null) {
            aty.showWaitDialog();
        }
    }

    /**
     * @param isCancelable 是否可以手动结束转菊花
     */
    final public void showWaitDialog(boolean isCancelable) {
        if (aty != null) {
            aty.showWaitDialog(isCancelable);
        }
    }

    final public void showWaitDialog(int resId, int viewId) {
        if (aty != null) {
            aty.showWaitDialog(resId, viewId);
        }
    }

    final public void showWaitDialog(String message, int viewId) {
        if (aty != null) {
            aty.showWaitDialog(message, viewId);
        }
    }

    final public void showWaitDialog(int resId, int viewId, boolean isCancelable) {
        if (aty != null) {
            aty.showWaitDialog(resId, viewId, isCancelable);
        }
    }

    ////// 通用fragment操作 //////

    /**
     * 后退
     */
    protected void back() {
        aty.handleBack();
    }

    /**
     * 相当于startActivity，进入一个新的fragment
     */
    public void pushFragment(Fragment fragment, String tag) {
        aty.pushFragment(fragment, tag);
    }

    /**
     * 相当于startActivity，进入一个新的fragment
     */
    public void pushFragment(Fragment fragment) {
        aty.pushFragment(fragment);
    }

    /**
     * 回退到底部，即显示主页
     */
    public void popFragmentsToBase() {
        aty.popFragmentsToBase();
    }

    /**
     * 获取当前栈顶的fragment，即当前activity显示的fragment
     */
    public Fragment getCurrentTopFragment() {
        return aty.getCurrentTopFragment();
    }
}
