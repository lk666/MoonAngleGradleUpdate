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
import cn.com.bluemoon.lib.view.selectordialog.ISecectedItem;
import cn.com.bluemoon.lib.view.selectordialog.SelectOptionsDialog;

/**
 * 待封箱筛选弹窗
 */
public class WaitPackFilterWindow extends PopupWindow {
    private CheckBox cbWaitInbox;
    private TextView txtRegion;
    private Context contextt;
    private FilterListener listener;
    private List<TextArea> list;

    /**
     * 是否显示待封箱
     */
    private boolean waitInbox = true;

    public WaitPackFilterWindow(Context context, List<String> list, boolean waitInbox,
                                FilterListener listener) {
        this.contextt = context;
        this.listener = listener;
        this.waitInbox = waitInbox;
        setList(list);
        init();
    }

    private void setList(List<String> strs) {
        list = new ArrayList<>();
        for (String str : strs) {
            list.add(new TextArea(str + list.size()));
        }
    }

    private void setWaitInbox(boolean waitInbox) {
        this.waitInbox = waitInbox;
        if (getContentView() != null) {
            cbWaitInbox.setChecked(waitInbox);
        }
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(contextt);
        View view = inflater.inflate(R.layout.dialog_wait_pack_filter, null);

        LinearLayout llMain = (LinearLayout) view.findViewById(R.id.ll_main);

        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(view);

        setBackgroundDrawable(new ColorDrawable(contextt.getResources().getColor(R.color
                .transparent_65)));

        llMain.startAnimation(AnimationUtils.loadAnimation(contextt, R.anim.push_top_in));

        txtRegion = (TextView) view.findViewById(R.id.txt_region);
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
        txtRegion.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectView();
            }
        });

        btnReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cbWaitInbox.setChecked(waitInbox);
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
                listener.onOkClick(txtRegion.getText().toString(), waitInbox);
                dismiss();
            }
        });
    }

    private void showSelectView(){
        List<TextArea>[] iList = new ArrayList[1];
        iList[0] = list;
        int index = list.size()>5?2:list.size()/2-1;
        new SelectOptionsDialog(contextt, 5, iList, new
                int[]{index}, new
                SelectOptionsDialog.ISelectOptionsDialog() {
                    @Override
                    public void OnSelectedChanged(List<ISecectedItem> selectedObj) {

                    }

                    @Override
                    public void onOutsideClick() {

                    }

                    @Override
                    public void onOKButtonClick(List<ISecectedItem> selectedObj) {
                        if (selectedObj != null) {
                            String str = selectedObj.get(0).getShowText();
                            txtRegion.setText(str);
                        }
                    }

                    @Override
                    public void onClearButtonClick() {

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
