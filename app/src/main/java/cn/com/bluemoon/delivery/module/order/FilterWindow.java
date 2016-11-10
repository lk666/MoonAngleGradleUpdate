package cn.com.bluemoon.delivery.module.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import org.apache.commons.lang3.StringUtils;

import cn.com.bluemoon.delivery.R;


public class FilterWindow extends PopupWindow {

    private Context mContext;

    public FilterWindow(Context context,OkListener listener) {
        mContext = context;
        Init(listener);
    }

    private void Init(final OkListener listener) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_order_filter, null);

        final EditText nameEt = (EditText) view.findViewById(R.id.et_name);
        final EditText addressEt = (EditText) view.findViewById(R.id.et_address);

        LinearLayout ll_popup = (LinearLayout) view
                .findViewById(R.id.layout_order_filter);

        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(view);
        setBackgroundDrawable(mContext.getResources().getDrawable(
                R.drawable.bg_transparent));

        ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.push_top_in));

        Button okBtn = (Button) view.findViewById(R.id.btn_confirm);
        Button cancleBtn = (Button) view.findViewById(R.id.btn_cancle);


        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        okBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String name = nameEt.getText().toString();
                String address = addressEt.getText().toString();
                listener.comfireClick(name, address);
                dismiss();
            }
        });

        cancleBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                nameEt.setText("");
                addressEt.setText("");
            }
        });
    }

    public interface OkListener {
        void comfireClick(String name, String address);
    }

}
