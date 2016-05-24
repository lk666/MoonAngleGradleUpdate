/** 
 * Description:
 * Copyright: Copyright (c) 2015
 * Company: BLUE MOON
 * @author: Eric Liang 
 * @version 1.0
 * @date: 2015/12/17
 */
package cn.com.bluemoon.delivery.utils;

import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.widget.EditText;

public class MaxLengthWatcher implements TextWatcher {
	
	private int maxLen = 0;
	private String msg = null;
	private EditText editText = null;
	public MaxLengthWatcher(int maxLen, String msg, EditText editText) {
		this.maxLen = maxLen;
		this.msg = msg;
		this.editText = editText;
	}

	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		
	}

	
	@Override
	public void onTextChanged(CharSequence s, int start, int before,
			int count) {
		Editable editable = editText.getText();
		int len = editable.length();
		if (len > maxLen) {
			PublicUtil.showToast(msg);
			int selEndIndex = Selection.getSelectionEnd(editable);
			String str = editable.toString();
			String newStr = str.substring(0, maxLen);
			editText.setText(newStr);
			editable = editText.getText();
			
			int newLen = editable.length();
			if (selEndIndex > newLen) {
				selEndIndex = editable.length();
			}
			Selection.setSelection(editable, selEndIndex);
		}
		
	}

	
	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}
}
