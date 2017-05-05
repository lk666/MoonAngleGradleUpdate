package cn.com.bluemoon.delivery.module.wash.returning.pack;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.TextArea;
import cn.com.bluemoon.delivery.ui.selectordialog.SingleOptionSelectDialog;
import cn.com.bluemoon.delivery.utils.ViewUtil;
import cn.com.bluemoon.lib.view.selectordialog.ISecectedItem;
import cn.com.bluemoon.lib.view.selectordialog.SelectOptionsDialog;

/**
 * 待封箱筛选弹窗
 */
public class WaitPackFilterWindow extends PopupWindow {
    private CheckBox cbWaitInbox;
    private TextView txtRegion;
    private Context context;
    private FilterListener listener;
    private List<String> list;
    private String region;

    /**
     * 是否显示待封箱
     */
    private boolean waitInbox = true;

    public WaitPackFilterWindow(Context context, List<String> list,String region,boolean
            waitInbox,
                                FilterListener listener) {
        this.context = context;
        this.listener = listener;
        this.waitInbox = waitInbox;
        this.list = list;
        this.region = region;
        init();
    }

    private void setWaitInbox(boolean waitInbox) {
        this.waitInbox = waitInbox;
        if (getContentView() != null) {
            cbWaitInbox.setChecked(waitInbox);
        }
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_wait_pack_filter, null);

        LinearLayout llMain = (LinearLayout) view.findViewById(R.id.ll_main);

        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(view);

        setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color
                .transparent_65)));

        llMain.startAnimation(AnimationUtils.loadAnimation(context, R.anim.push_top_in));

        txtRegion = (TextView) view.findViewById(R.id.txt_region);
        Button btnConfirm = (Button) view.findViewById(R.id.btn_confirm);
        Button btnReset = (Button) view.findViewById(R.id.btn_reset);
        cbWaitInbox = (CheckBox) view.findViewById(R.id.cb_waitInbox);

        cbWaitInbox.setChecked(waitInbox);
        txtRegion.setText(region);
        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        txtRegion.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectView();
            }
        });

        btnReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cbWaitInbox.setChecked(false);
                txtRegion.setText("");
            }
        });

        btnConfirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener == null) {
                    return;
                }
                waitInbox = cbWaitInbox.isChecked();
                region = txtRegion.getText().toString();
                listener.onOkClick(region, waitInbox);
                dismiss();
            }
        });
    }

    private void showSelectView() {
        if (list == null || list.size() == 0) {
            ViewUtil.toast(context.getString(R.string.pack_area_select_error));
            return;
        }
        int index = list.size() > 2 ? 1 : 0;
        new SingleOptionSelectDialog(context, "",
                list, index, new SingleOptionSelectDialog.OnButtonClickListener() {
            @Override
            public void onOKButtonClick(int index, String text) {
                txtRegion.setText(text);
            }

            @Override
            public void onCancelButtonClick() {

            }
        }).show();

    }

    public void showPopwindow(View popStart) {
        showAsDropDown(popStart);
    }

    public interface FilterListener {
        void onOkClick(String str, boolean waitInbox);
    }
}
