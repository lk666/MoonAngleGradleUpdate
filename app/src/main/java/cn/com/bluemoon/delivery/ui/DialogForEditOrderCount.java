package cn.com.bluemoon.delivery.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.utils.PublicUtil;

/**
 * Created by summer on 2016/4/1.
 */

public class DialogForEditOrderCount {
    Context context;
    Dialogcallback dialogcallback;
    Dialog dialog;
    Button negativeButton, positiveButton;
    TextView title;
    EditText etAmount;
    ImageView imgReduce;
    ImageView imgAdd;

    int staticNums = 0;
    int NUMS = 0;
    int position = 0;
    boolean isDeliver = false;
    int diffNum = 0;

    /**
     * init the dialog
     *
     * @return
     */
    public DialogForEditOrderCount(Context con, int pos, int nums, int diffNum, boolean isDeliver) {
        this.context = con;
        this.NUMS = diffNum;
        this.staticNums = nums;
        this.position = pos;
        this.isDeliver = isDeliver;
        this.diffNum = diffNum;


        dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_edit_order_nums);
        title = (TextView) dialog.findViewById(R.id.title);
        imgAdd = (ImageView) dialog.findViewById(R.id.img_add);
        imgReduce = (ImageView) dialog.findViewById(R.id.img_reduce);
        positiveButton = (Button) dialog.findViewById(R.id.positiveButton);
        negativeButton = (Button) dialog.findViewById(R.id.negativeButton);
        etAmount = (EditText) dialog.findViewById(R.id.et_amount);
        etAmount.setText(NUMS + "");
        if (NUMS == staticNums) {
            imgAdd.setImageResource(R.mipmap.add_disable3);
        } else if (NUMS == 0) {
            imgReduce.setImageResource(R.mipmap.minus_disable3);
        }
        initListener();
        if (isDeliver) {
            title.setText(context.getResources().getString(R.string.text_real_deliver_nums_title));
        } else {
            title.setText(context.getResources().getString(R.string.text_real_receive_nums_title));
        }

    }


    private void initListener() {

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    NUMS = Integer.valueOf(etAmount.getText().toString()) + 1;
                    if (NUMS >= staticNums) {
                        imgAdd.setImageResource(R.mipmap.add_disable3);
                        imgAdd.setEnabled(false);

                        etAmount.setText(staticNums + "");
                    } else {
                        imgAdd.setImageResource(R.mipmap.add_normal3);
                        etAmount.setText(NUMS + "");
                        imgAdd.setEnabled(true);
                    }
                    imgReduce.setImageResource(R.mipmap.minus_normal3);
                    imgReduce.setEnabled(true);

                } catch (Exception e) {
                    etAmount.setText(String.valueOf(staticNums));
                    imgAdd.setEnabled(false);
                    imgAdd.setImageResource(R.mipmap.add_disable3);

                    imgReduce.setImageResource(R.mipmap.minus_normal3);
                    imgReduce.setEnabled(true);
                    PublicUtil.showToast(context, context.getResources().getString(R.string.text_nums_out_big_tip));
                }
            }

        });
        imgReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    NUMS = Integer.valueOf(etAmount.getText().toString()) - 1;
                    if (NUMS <= 0) {
                        etAmount.setText(0 + "");
                        imgReduce.setImageResource(R.mipmap.minus_disable3);
                        imgReduce.setEnabled(false);
                        imgAdd.setImageResource(R.mipmap.add_normal3);
                        imgAdd.setEnabled(true);
                    } else if (NUMS >= staticNums) {
                        imgAdd.setImageResource(R.mipmap.add_disable3);
                        imgAdd.setEnabled(false);
                        etAmount.setText(staticNums + "");
                    } else {
                        imgReduce.setImageResource(R.mipmap.minus_normal3);
                        etAmount.setText(NUMS + "");
                        imgReduce.setEnabled(true);
                        imgAdd.setImageResource(R.mipmap.add_normal3);
                        imgAdd.setEnabled(true);
                    }


                } catch (Exception e) {
                    etAmount.setText("0");
                    imgReduce.setEnabled(false);
                    imgReduce.setImageResource(R.mipmap.minus_disable3);

                    imgAdd.setImageResource(R.mipmap.add_normal3);
                    imgAdd.setEnabled(true);
                    PublicUtil.showToast(context, context.getResources().getString(R.string.text_invalid_data_tip));
                }

            }


        });


        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    if (staticNums < Integer.parseInt(etAmount.getText().toString())
                            || Integer.parseInt(etAmount.getText().toString()) < 0) {

                        PublicUtil.showToast(context, context.getResources().getString(R.string.text_invalid_data_tip));
                        return;
                    }

                } catch (Exception ex) {
                    PublicUtil.showToast(context, context.getResources().getString(R.string.text_invalid_data_tip));
                    return;

                }

                dialogcallback.dialogdo(position, etAmount.getText().toString(), staticNums);
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
        public void dialogdo(int pos, String string, int nums);
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
        return etAmount.getText().toString();
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