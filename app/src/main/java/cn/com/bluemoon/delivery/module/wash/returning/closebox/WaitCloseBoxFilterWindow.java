package cn.com.bluemoon.delivery.module.wash.returning.closebox;

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

import cn.com.bluemoon.delivery.R;

/**
 * 待封箱筛选弹窗
 */
public class WaitCloseBoxFilterWindow extends PopupWindow {
    private CheckBox cbWaitInbox;

    private Context contextt;
    private FilterListener listener;

    /**
     * 是否显示待封箱
     */
    private boolean waitInbox = true;

    public WaitCloseBoxFilterWindow(Context context, boolean waitInbox, FilterListener listener) {
        this.contextt = context;
        this.listener = listener;
        this.waitInbox = waitInbox;
        init();
    }

    private void setWaitInbox(boolean waitInbox) {
        this.waitInbox = waitInbox;
        if (getContentView() != null) {
            cbWaitInbox.setChecked(waitInbox);
        }
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(contextt);
        View view = inflater.inflate(R.layout.dialog_wait_close_box_filter, null);

        LinearLayout llMain = (LinearLayout) view.findViewById(R.id.ll_main);

        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(view);

        setBackgroundDrawable(new ColorDrawable(contextt.getResources().getColor(R.color
                .transparent_65)));

        llMain.startAnimation(AnimationUtils.loadAnimation(contextt, R.anim.push_top_in));

        Button btnConfirm = (Button) view.findViewById(R.id.btn_confirm);
        Button btnReset = (Button) view.findViewById(R.id.btn_reset);
        cbWaitInbox = (CheckBox) view.findViewById(R.id.cb_waitInbox);

        cbWaitInbox.setChecked(waitInbox);
        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cbWaitInbox.setChecked(waitInbox);
            }
        });

        btnConfirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener == null) {
                    return;
                }
                waitInbox = cbWaitInbox.isChecked();
                listener.onOkClick(waitInbox);
                dismiss();
            }
        });
    }

    public void showPopwindow(View popStart) {
        showAsDropDown(popStart);
    }

    public interface FilterListener {
        void onOkClick(boolean waitInbox);
    }
}
