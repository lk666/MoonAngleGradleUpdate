package cn.com.bluemoon.delivery.module.newbase;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.module.newbase.view.BaseTitleBar;
import cn.com.bluemoon.delivery.module.newbase.view.CommonActionBar;
import cn.com.bluemoon.delivery.module.newbase.view.IActionBarListener;

/**
 * fragment继承层次：4（0顶层）
 * 普通实现类，使用最基本的titleBar，可以此为蓝本使用不同的titleBar
 */
public abstract class BaseFragment<TITLE_BAR extends BaseTitleBar> extends BaseNetFragment {
    private TITLE_BAR titleBar;

    public TITLE_BAR getTitleBar() {
        return titleBar;
    }

    @Override
    protected int getTitleLayoutId() {
        return R.layout.view_common_title_bar;
    }

    @Override
    protected void initTitleBarView(View title) {
        super.initTitleBarView(title);

        titleBar = (TITLE_BAR) title;

        if (titleBar == null || !(titleBar instanceof CommonActionBar)) {
            return;
        }

        CommonActionBar commonActionBar = (CommonActionBar) titleBar;

        if (!TextUtils.isEmpty(getTitleString())) {
            commonActionBar.setBackgroundColor(getResources().getColor(R.color.white));
            ViewGroup.LayoutParams lp = commonActionBar.getLayoutParams();
            lp.height = (int) getResources().getDimension(R.dimen.new_base_title);
            commonActionBar.setLayoutParams(lp);

            commonActionBar.setVisibility(View.VISIBLE);
            commonActionBar.setListener(new IActionBarListener() {
                @Override
                public void onBtnRight(View v) {
                    onActionBarBtnRightClick();
                }

                @Override
                public void onBtnLeft(View v) {
                    onActionBarBtnLeftClick();
                }
            });
            commonActionBar.getTitleView().setText(getTitleString());

            commonActionBar.getImgLeftView().setVisibility(View.VISIBLE);
            commonActionBar.getTvRightView().setVisibility(View.GONE);
            commonActionBar.getImgRightView().setVisibility(View.GONE);
        } else {
            commonActionBar.setVisibility(View.GONE);
        }

        // 其他初始化操作可以 @Override  initTitleBarView方法并在super.initTitleBarView(title);之后进行
    }

    /**
     * 返回为null或者空字符串，则不设置ActionBar(若要不显示actionbar，须在主题中声明)
     * 只在titlebar为CommonActionBar时生效
     */
    protected String getTitleString() {
        return null;
    }

    /**
     * 左键点击事件，一般不需重写
     * 只在titlebar为CommonActionBar时生效
     */
    protected void onActionBarBtnLeftClick() {
        back();
    }

    /**
     * 右键点击事件
     * 只在titlebar为CommonActionBar时生效
     */
    protected void onActionBarBtnRightClick() {
    }
}
