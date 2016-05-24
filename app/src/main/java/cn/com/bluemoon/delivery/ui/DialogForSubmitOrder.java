package cn.com.bluemoon.delivery.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;

/**
 * Created by summer on 2016/4/1.
 */

public class DialogForSubmitOrder {
    Context context;
    Dialogcallback dialogcallback;
    Dialog dialog;
    TextView title;

    TextView txtCommenNameShould;
    TextView txtShouldOrderNums;
    TextView txtCommenNameReal;
    TextView txtRealOrderNums;
    TextView txtDiffOrderNums;
    TextView txtCommenNameRealTotalMoney;
    TextView txtRealTotalMoney;
    TextView txtCommenNameTip;

    Button positiveButton;
    Button negativeButton;

    String type;//deliver or  receive


    /**
     * init the dialog
     *
     * @return
     */
    public DialogForSubmitOrder(Context ctx,String type,String should,String real,String diff,String totalMoney) {
        this.context = ctx;
        dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_submit_view);
        title = (TextView) dialog.findViewById(R.id.title);
        txtCommenNameShould = (TextView) dialog.findViewById(R.id.txt_commenName_should);
        txtShouldOrderNums = (TextView) dialog.findViewById(R.id.txt_should_order_nums);
        txtCommenNameReal = (TextView) dialog.findViewById(R.id.txt_commenName_real);
        txtRealOrderNums = (TextView) dialog.findViewById(R.id.txt_real_order_nums);
        txtDiffOrderNums = (TextView) dialog.findViewById(R.id.txt_diff_order_nums);
        txtCommenNameRealTotalMoney = (TextView) dialog.findViewById(R.id.txt_commenName_real_total_money);
        txtRealTotalMoney = (TextView) dialog.findViewById(R.id.txt_real_total_money);
        txtCommenNameTip = (TextView) dialog.findViewById(R.id.txt_commenName_tip);

        positiveButton =(Button) dialog.findViewById(R.id.positiveButton);
        negativeButton =(Button) dialog.findViewById(R.id.negativeButton);
        initListener();

        if("deliver".equals(type)){
            txtCommenNameShould.setText(context.getString(R.string.detail_order_deliver_should));
            txtCommenNameReal.setText(context.getString(R.string.detail_order_deliver_real));
            txtCommenNameRealTotalMoney.setText(context.getString(R.string.order_real_deliver_money));
            txtCommenNameTip.setText(context.getString(R.string.order_deliver_submit_message_tip));

        }else{
            txtCommenNameShould.setText(context.getString(R.string.detail_order_receive_should));
            txtCommenNameReal.setText(context.getString(R.string.detail_order_receive_real));
            txtCommenNameRealTotalMoney.setText(context.getString(R.string.order_real_receive_money));
            txtCommenNameTip.setText(context.getString(R.string.order_recive_submit_message_tip));
        }

        txtShouldOrderNums.setText(should);
        txtRealOrderNums.setText(real);
        txtDiffOrderNums.setText(diff);
        txtRealTotalMoney.setText(totalMoney);
    }

    private void initListener() {

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogcallback.dialogdo("");
                dismiss();
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    /**
     * Get the Text of the EditText
     */
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