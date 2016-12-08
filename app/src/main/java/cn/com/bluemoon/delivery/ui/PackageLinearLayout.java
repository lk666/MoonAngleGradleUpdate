package cn.com.bluemoon.delivery.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;

/**
 * Created by ljl on 2016/12/8.
 */
public class PackageLinearLayout extends LinearLayout {

    private TextView txtName;
    private TextView txtNum;

    public PackageLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public PackageLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PackageLinearLayout(Context context) {
        super(context);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.list_item_order_package, this, true);
        txtName = (TextView) findViewById(R.id.txt_content);
        txtNum = (TextView) findViewById(R.id.txt_num);
    }

    public void setData(String produceName, String num) {
        txtName.setText(produceName);
        txtNum.setText(num);
    }
}
