/** 
 * Description:
 * Copyright: Copyright (c) 2015
 * Company: BLUE MOON
 * @author: Eric Liang 
 * @version 1.0
 * @date: 2015/12/9
 */
package cn.com.bluemoon.delivery.ui;


import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class ClearFocusEditText extends EditText {

	public ClearFocusEditText(Context context) {
		super(context);
	}

	public ClearFocusEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClearFocusEditText(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    }

	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		if (android.os.Build.MODEL.startsWith("MX") && keyCode == KeyEvent.KEYCODE_BACK) {
			 clearFocus();
		}
		return super.onKeyPreIme(keyCode, event);
	}

}
