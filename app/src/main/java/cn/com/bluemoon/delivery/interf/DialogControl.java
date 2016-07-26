package cn.com.bluemoon.delivery.interf;

import android.app.ProgressDialog;

/**
 * Created by lk on 2016/6/13.
 */
public interface DialogControl {
    void hideWaitDialog();

    ProgressDialog showWaitDialog();

    ProgressDialog showWaitDialog(int resId, int viewId);

    ProgressDialog showWaitDialog(String text, int viewId);
}
