package cn.com.bluemoon.delivery.module.newbase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.common.ClientStateManager;

/**
 * fragment继承层次：1（0顶层）
 * 基础Fragment，基本界面操作实现等(不包含与activity的交互、网络)
 */
public abstract class BaseActionFragment extends BaseAbstractFragment {

    protected View titleBar;
    protected View mainView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isUseEventBus()) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    final protected int getLayoutId() {
        return R.layout.fragment_base;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View main = super.onCreateView(inflater, container, savedInstanceState);

        // 标题栏
        if (getTitleLayoutId() > 0) {
            View viewStub = main.findViewById(R.id.vs_title_bar);
            if (viewStub != null) {
                final ViewStub stub = (ViewStub) viewStub;
                stub.setLayoutResource(getTitleLayoutId());
                titleBar = stub.inflate();
            }
        }

        // 实际内容
        if (getContentLayoutId() > 0) {
            View viewStub = main.findViewById(R.id.vs_content);
            if (viewStub != null) {
                final ViewStub stub = (ViewStub) viewStub;
                stub.setLayoutResource(getContentLayoutId());
                mainView = stub.inflate();
            }
        }

        return main;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onGetArguments();
        ButterKnife.bind(this, view);
        initTitleBarView(titleBar);
        initContentView(mainView);
        initData();
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
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isUseEventBus()) {
            EventBus.getDefault().unregister(this);
        }
    }

    //////////////// 可选重写 ////////////////

    /**
     * 获取标题栏layoutId，不使用标题时，返回0
     */
    protected int getTitleLayoutId() {
        return 0;
    }

    /**
     * 初始化标题栏
     * 初始化控件属性，不应在此设置控件数据
     * view状态恢复建议在此处执行
     */
    protected void initTitleBarView(View titleBar) {
    }

    /**
     * 在onCreateView后执行，一般为获取getArguments()的数据
     */
    protected void onGetArguments() {
    }

    /**
     * 是否有使用EventBus，默认不使用
     */
    protected boolean isUseEventBus() {
        return false;
    }

    //////////////// 工具方法 ////////////////

    /**
     * 获取token
     */
    final protected String getToken() {
        return ClientStateManager.getLoginToken();
    }

    /**
     * 默认TAG
     *
     * @return getClass().getSimpleName()
     */
    final protected String getDefaultTag() {
        return this.getClass().getSimpleName();
    }

    ///////////// 必须重写 ////////////////

    /**
     * 设置初始数据，并发起初始网络请求。
     * 实例状态恢复建议在此处执行
     */
    protected abstract void initData();

    /**
     * 实际内容layoutId（不含标题）
     */
    protected abstract int getContentLayoutId();

    /**
     * 初始化实际内容layoutId（不含标题）
     * 初始化控件属性，不应在此设置控件数据
     * view状态恢复建议在此处执行
     */
    protected abstract void initContentView(View mainView);
}
