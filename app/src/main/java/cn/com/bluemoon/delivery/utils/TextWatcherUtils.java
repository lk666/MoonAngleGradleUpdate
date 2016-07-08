package cn.com.bluemoon.delivery.utils;

import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by LIANGJIANGLI on 2016/7/6.
 */
public class TextWatcherUtils {

    public static void setMaxLengthWatcher(final EditText editText, final int maxLen, final String msg) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                Editable editable = editText.getText();
                int len = editable.length();
                if (len > maxLen) {
                    if (StringUtils.isNotBlank(msg)) {
                        PublicUtil.showToast(msg);
                    }
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

            }
        });
    }

    public static void setMaxNumberWatcher(final EditText editText, final int maxNum, final int maxNum2, final String msg) {
        editText.addTextChangedListener(new TextWatcher() {
            private String beforeStr;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeStr = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                Editable editable = editText.getText();
                int len = editable.length();
                String ss = editable.toString();
                String num = null;
                String num2 = null;
                String[] nums = ss.split("\\.");
                if (nums.length > 1 && StringUtils.isNotBlank(nums[1])) {
                    num2 = nums[1];
                }
                if (nums == null || nums.length == 0) {
                    num = ss;
                } else {
                    num = nums[0];
                }

                if (num.length() > maxNum || (num2 != null && num2.length() > maxNum2)) {
                    if (StringUtils.isNotBlank(msg)) {
                        PublicUtil.showToast(msg);
                    }
                    int selEndIndex = Selection.getSelectionEnd(editable);
                    String str = editable.toString();
                    editText.setText(beforeStr);
                    editable = editText.getText();

                    int newLen = editable.length();
                    if (selEndIndex > newLen) {
                        selEndIndex = editable.length();
                    } else {
                        selEndIndex--;
                    }
                    Selection.setSelection(editable, selEndIndex);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}
