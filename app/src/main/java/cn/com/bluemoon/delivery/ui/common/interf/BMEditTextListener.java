package cn.com.bluemoon.delivery.ui.common.interf;

import android.text.Editable;
import android.widget.EditText;

/**
 * Created by bm on 2017/6/4.
 */

public abstract class BMEditTextListener {

    public abstract void afterTextChanged(Editable s);

    public void onRightClick(EditText et, boolean isCleanable) {
        if (isCleanable) {
            et.setText("");
        }
    }
}
