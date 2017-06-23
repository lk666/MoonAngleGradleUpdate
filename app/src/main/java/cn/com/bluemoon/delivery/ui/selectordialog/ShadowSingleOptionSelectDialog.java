package cn.com.bluemoon.delivery.ui.selectordialog;

import android.content.Context;

import java.util.List;

import cn.com.bluemoon.delivery.R;

/**
 * 单条件选择控件。确定取消文本按钮。样式：去掉遮罩
 */
public class ShadowSingleOptionSelectDialog extends SingleOptionSelectDialog {

    /**
     * 构造函数
     *
     * @param title    标题
     * @param dataList 数据
     * @param defIndex 默认选中项
     * @param listener 点击按钮时的回调
     */
    public ShadowSingleOptionSelectDialog(Context context, String title, List<String> dataList,
                                          int defIndex, OnButtonClickListener listener) {
        super(context, title, dataList, defIndex, listener);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.pop_shadow_btn_single_option_select;
    }

}
