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

public class DialogForSubmitOrder {
    Context context;
    DialogCallback dialogcallback;
    Dialog dialog;
    TextView title;

    TextView txtCommonNameShould;
    TextView txtShouldOrderNum;
    TextView txtCommonNameReal;
    TextView txtRealOrderNum;
    TextView txtDiffOrderNum;
    TextView txtCommonNameRealTotalMoney;
    TextView txtRealTotalMoney;
    TextView txtCommonNameTip;

    Button positiveButton;
    Button negativeButton;

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
        txtCommonNameShould = (TextView) dialog.findViewById(R.id.txt_commenName_should);
        txtShouldOrderNum = (TextView) dialog.findViewById(R.id.txt_should_order_nums);
        txtCommonNameReal = (TextView) dialog.findViewById(R.id.txt_commenName_real);
        txtRealOrderNum = (TextView) dialog.findViewById(R.id.txt_real_order_nums);
        txtDiffOrderNum = (TextView) dialog.findViewById(R.id.txt_diff_order_nums);
        txtCommonNameRealTotalMoney = (TextView) dialog.findViewById(R.id.txt_commenName_real_total_money);
        txtRealTotalMoney = (TextView) dialog.findViewById(R.id.txt_real_total_money);
        txtCommonNameTip = (TextView) dialog.findViewById(R.id.txt_commenName_tip);

        positiveButton =(Button) dialog.findViewById(R.id.positiveButton);
        negativeButton =(Button) dialog.findViewById(R.id.negativeButton);
        initListener();

        if("deliver".equals(type)){
            txtCommonNameShould.setText(context.getString(R.string.detail_order_deliver_should));
            txtCommonNameReal.setText(context.getString(R.string.detail_order_deliver_real));
            txtCommonNameRealTotalMoney.setText(context.getString(R.string.order_real_deliver_money));
            txtCommonNameTip.setText(context.getString(R.string.order_deliver_submit_message_tip));

        }else{
            txtCommonNameShould.setText(context.getString(R.string.detail_order_receive_should));
            txtCommonNameReal.setText(context.getString(R.string.detail_order_receive_real));
            txtCommonNameRealTotalMoney.setText(context.getString(R.string.order_real_receive_money));
            txtCommonNameTip.setText(context.getString(R.string.order_recive_submit_message_tip));
        }

        txtShouldOrderNum.setText(should);
        txtRealOrderNum.setText(real);
        txtDiffOrderNum.setText(diff);
        txtRealTotalMoney.setText(totalMoney);
    }

    private void initListener() {

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogcallback.dialogDo("");
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

    public interface DialogCallback {
         void dialogDo(String string);
    }

    public void setDialogCallback(DialogCallback dialogcallback) {
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

    public void dismiss() {
        dialog.dismiss();
    }
}