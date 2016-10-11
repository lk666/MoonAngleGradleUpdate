package cn.com.bluemoon.delivery.ui.selectordialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.bluemoon.delivery.R;

/**
 * 单条件选择控件
 */
public class SingleOptionSelectDialog extends Dialog {

    @Bind(R.id.btn_cancle)
    ImageView btnCancle;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.btn_ok)
    ImageView btnOk;
    @Bind(R.id.wheel)
    SimpleWheelView wheel;
    /**
     * 设置数据
     */
    private ArrayList<String> dataList;

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
    public SingleOptionSelectDialog(Context context, String title, ArrayList<String> dataList,
                                    int defIndex, OnButtonClickListener listener) {
        super(context, R.style.Dialog);
        this.dataList = dataList;
        this.title = title;
        this.listener = listener;
        this.defIndex = defIndex;
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_single_option_select);

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

    @OnClick({R.id.btn_cancle, R.id.btn_ok, R.id.rl_main})
    public void onClick(View view) {
        switch (view.getId()) {
            // 取消
            case R.id.btn_cancle:
                if (listener != null) {
                    dismiss();
                    listener.onCancleButtonClick();
                }
                break;
            case R.id.btn_ok:
                if (listener != null) {
                    dismiss();
                    listener.onOKButtonClick(defIndex, dataList.get(defIndex));
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
        void onCancleButtonClick();
    }
}
