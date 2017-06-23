package cn.com.bluemoon.delivery.module.hr.personinfo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodManager;

import cn.com.bluemoon.lib_widget.module.form.BMFieldText1View;

/**
 * 修改手机号
 */

public class ModifyPhoneEditText extends BMFieldText1View {

    public ModifyPhoneEditText(Context context) {
        super(context);
    }

    public ModifyPhoneEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void showIM() {
        etContent.requestFocus();
        InputMethodManager imm = (InputMethodManager) etContent.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }
}