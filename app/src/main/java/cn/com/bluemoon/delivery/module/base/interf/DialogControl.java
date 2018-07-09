package cn.com.bluemoon.delivery.module.base.interf;

import cn.com.bluemoon.delivery.ui.WaitingDialog;

/**
 * Created by lk on 2016/6/13.
 */
public interface DialogControl {
    void hideWaitDialog();

    WaitingDialog showWaitDialog();

    WaitingDialog showWaitDialog(boolean isCancelable);

    WaitingDialog showWaitDialog(int resId, int viewId);

    WaitingDialog showWaitDialog(String text, int viewId);
}
