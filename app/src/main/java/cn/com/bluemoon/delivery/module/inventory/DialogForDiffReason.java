package cn.com.bluemoon.delivery.module.inventory;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;

/**
 * Created by summer on 2016/4/1.
 */

public class DialogForDiffReason {
    Context context;
    Dialogcallback dialogcallback;
    Dialog dialog;
    TextView title;
    TextView txtDiffReason;
    TextView txtDiffReasonDetail;
    Button btnOk;

    /**
     * init the dialog
     *
     * @return
     */
    public DialogForDiffReason(Context con, String orderDiffReason, String orderDiffReasonDetail) {
        this.context = con;
        dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_diff_reason);
        title = (TextView) dialog.findViewById(R.id.title);
        txtDiffReason = (TextView) dialog.findViewById(R.id.txt_diff_reason);
        txtDiffReasonDetail = (TextView) dialog.findViewById(R.id.txt_diff_reason_detail);
        btnOk =(Button) dialog.findViewById(R.id.btn_ok);
        initListener();


        txtDiffReason.setText(orderDiffReason);
        txtDiffReasonDetail.setText(orderDiffReasonDetail);
    }

    private void initListener() {

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogcallback.dialogdo(txtDiffReason.getText().toString());
                dismiss();
            }
        });

    }

    public interface Dialogcallback {
        public void dialogdo(String string);
    }

    public void setDialogCallback(Dialogcallback dialogcallback) {
        this.dialogcallback = dialogcallback;
    }


    public void setContent(String content) {
        title.setText(content);
    }


    public String getText() {
        return "";
    }

    public void show() {
        dialog.show();
    }

    public void hide() {
        dialog.hide();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}