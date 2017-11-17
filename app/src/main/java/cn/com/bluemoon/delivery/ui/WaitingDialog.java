package cn.com.bluemoon.delivery.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.WindowManager;
import android.widget.TextView;

import cn.com.bluemoon.delivery.R;

/**
 * loading弹窗
 *
 * @author LK
 */
public class WaitingDialog extends Dialog {


    public WaitingDialog(Context context) {
        super(context, R.style.Dialog);
        initView();
    }

    /**
     * 初始化界面与控件
     */
    private void initView() {
        //设置Windows沉浸式
//        StatusBarUtil.immersive(this.getWindow());

        setContentView(R.layout.dialog_progress);
        // 保证全屏宽，因为默认宽高为WRAP_CONTENT
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(lp);
    }

    public void setMessage(String message) {
        TextView tvMessage = (TextView) findViewById(R.id.message);
        if (tvMessage != null) {
            tvMessage.setText(message);
        }
    }
}