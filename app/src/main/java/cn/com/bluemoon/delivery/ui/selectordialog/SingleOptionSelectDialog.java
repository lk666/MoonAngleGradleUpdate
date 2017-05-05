package cn.com.bluemoon.delivery.ui.selectordialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;

/**
 * 单条件选择控件
 */
public class SingleOptionSelectDialog extends Dialog {

    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.wheel)
    SimpleWheelView wheel;
    /**
     * 设置数据
     */
    private List<String> dataList;

    private OnButtonClickListener listener;

    private String title;
    private int defIndex;
    private int selectedIndex = -1;

    /**
     * 构造函数
     *
     * @param title    标题
     * @param dataList 数据
     * @param defIndex 默认选中项
     * @param listener 点击按钮时的回调
     */
    public SingleOptionSelectDialog(Context context, String title, List<String> dataList,
                                    int defIndex, OnButtonClickListener listener) {
        super(context, R.style.Translucent_Dialog);
        this.dataList = dataList;
        this.title = title;
        this.listener = listener;
        this.defIndex = defIndex;
        initView();
    }

    protected int getLayoutId() {
        return R.layout.dialog_single_option_select;
    }

    protected void initView() {
        setContentView(getLayoutId());

        // 保证全屏宽，因为默认宽高为WRAP_CONTENT
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(lp);

        getWindow().setWindowAnimations(R.style.DialogAnimation);

        ButterKnife.bind(this);

        tvTitle.setText(title);
        initSelectView();
    }

    /**
     * 初始化选择列表控件
     */
    private void initSelectView() {
        selectedIndex = defIndex;
        wheel.initData(dataList, selectedIndex);
        wheel.setOnSelectListener(new OnSelectChangedListener() {
            @Override
            public void onEndSelected(int index, Object text) {
                selectedIndex = index;
            }
        });
    }

    @OnClick({R.id.btn_cancel, R.id.btn_ok, R.id.rl_main})
    public void onClick(View view) {
        switch (view.getId()) {
            // 取消
            case R.id.btn_cancel:
                if (listener != null) {
                    dismiss();
                    listener.onCancelButtonClick();
                }
                break;
            case R.id.btn_ok:
                if (listener != null) {
                    dismiss();
                    listener.onOKButtonClick(selectedIndex, dataList.get(selectedIndex));
                }
                break;
            case R.id.rl_main:
                this.dismiss();
                break;
        }
    }

    /**
     * 点击按钮时的回调接口
     */
    public interface OnButtonClickListener {
        /**
         * 点击确定时的回调
         */
        void onOKButtonClick(int index, String text);

        /**
         * 点击取消按钮的回调
         */
        void onCancelButtonClick();
    }
}
