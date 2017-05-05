package cn.com.bluemoon.delivery.ui.selectordialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;

import java.util.List;

import cn.com.bluemoon.delivery.R;

/**
 * 单条件选择控件。确定取消文本按钮
 */
public class TextSingleOptionSelectDialog extends SingleOptionSelectDialog {

    /**
     * 构造函数
     *
     * @param title    标题
     * @param dataList 数据
     * @param defIndex 默认选中项
     * @param listener 点击按钮时的回调
     */
    public TextSingleOptionSelectDialog(Context context, String title, List<String> dataList,
                                        int defIndex, OnButtonClickListener listener) {
        super(context, title, dataList, defIndex, listener);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.pop_text_btn_single_option_select;
    }

    @Override
    protected void initView() {
        super.initView();
        getWindow().setBackgroundDrawable(new ColorDrawable(getContext().getResources().getColor
                (R.color.transparent2)));
    }
}
