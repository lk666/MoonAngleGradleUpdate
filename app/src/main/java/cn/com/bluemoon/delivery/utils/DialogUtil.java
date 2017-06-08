package cn.com.bluemoon.delivery.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.Gravity;

import cn.com.bluemoon.delivery.AppContext;
import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.utils.manager.ActivityManager;
import cn.com.bluemoon.delivery.utils.manager.CacheManager;
import cn.com.bluemoon.lib.utils.LibDialogUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.ImageDialog;
import cn.com.bluemoon.lib.view.QRCodeDialog;

public class DialogUtil extends LibDialogUtil {


    public static QRCodeDialog showCodeDialog(Activity context, String title,
                                              String code, String str) {
        return showCodeDialog(context, title, null, code, str, null, PublicUtil.getPhotoPath(),
                null);
    }

    public static ImageDialog showPictureDialog(Activity context, String imgUrl) {
        return showPictureDialog(context, null, imgUrl, PublicUtil.getPhotoPath(), null);
    }

    public static ImageDialog showPictureDialog(Activity context, Bitmap bit) {
        return showPictureDialog(context, bit, null, PublicUtil.getPhotoPath(), null);
    }

    /**
     * 获取一个dialog
     */
    public static CommonAlertDialog.Builder getSimpleDialog(Context context) {
        return new CommonAlertDialog.Builder(context);
    }

    /**
     * 获取一个dialog
     *
     * @param context
     * @param title
     * @param message
     * @param okString
     * @param cancelString
     * @param onOkClickListener
     * @param onCancelClickListener
     * @return
     */
    public static CommonAlertDialog.Builder getCommonDialog(Context context,
                                                            String title,
                                                            String message,
                                                            String okString,
                                                            String cancelString,
                                                            DialogInterface.OnClickListener
                                                                    onOkClickListener,
                                                            DialogInterface.OnClickListener
                                                                    onCancelClickListener) {
        CommonAlertDialog.Builder builder = new CommonAlertDialog.Builder(context);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        builder.setMessage(message);
        builder.setPositiveButton(okString, onOkClickListener);
        if (!TextUtils.isEmpty(cancelString)) {
            builder.setNegativeButton(cancelString, onCancelClickListener);
        }
        return builder;
    }

    /**
     * 获取一个dialog
     *
     * @param context
     * @param title
     * @param message
     * @param okString
     * @param onOkClickListener
     * @return
     */
    public static CommonAlertDialog.Builder getMessageDialog(Context context,
                                                             String title,
                                                             String message,
                                                             String okString,
                                                             DialogInterface.OnClickListener
                                                                     onOkClickListener) {
        CommonAlertDialog.Builder builder = new CommonAlertDialog.Builder(context);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        builder.setMessage(message);
        builder.setPositiveButton(okString, onOkClickListener);
        return builder;
    }

    public static CommonAlertDialog.Builder getMsgDialog(Context context, String message) {
        return getMessageDialog(context, null, message, AppContext.getInstance().getString(R
                .string.btn_ok), null);
    }

    /**
     * 获取默认取消按钮的对话框
     *
     * @param context
     * @param title
     * @param message
     * @param okString
     * @param onOkClickListener
     * @return
     */
    public static CommonAlertDialog.Builder getCommonDialog(Context context,
                                                            String title,
                                                            String message,
                                                            String okString,
                                                            DialogInterface.OnClickListener
                                                                    onOkClickListener) {
        return getCommonDialog(context, title, message, context.getString(R.string
                .btn_cancel_space), okString, null, onOkClickListener);
    }

    /**
     * 显示客服对话框
     *
     * @param aty
     */
    public static void showServiceDialog(final Activity aty) {
        CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(aty);
        dialog.setTitle(aty.getString(R.string.service_dialog_title));
        dialog.setMessageSize(14);
        dialog.setMessage(aty.getString(R.string.service_call)
                + "\n"
                + aty.getString(R.string.service_weixin));
        dialog.setPositiveButton(aty.getString(R.string.service_weixin_btn),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PublicUtil.showWeixinApp(aty, aty.getString(R.string.no_weixin));
                    }
                });
        dialog.setNegativeButton(aty.getString(R.string.service_call_btn),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PublicUtil.callPhone(aty, Constants.SERVICE_PHONE);
                    }
                });
        dialog.show();
    }

    /**
     * 显示客服对话框2
     *
     * @param aty
     */
    public static void showServiceDialog2(final Activity aty) {
        CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(aty);
        dialog.setTitle(aty.getString(R.string.service_dialog_title2));
        dialog.setMessage(aty.getString(R.string.service_call2));
        dialog.setTxtGravity(Gravity.CENTER);
        dialog.setNegativeButton(R.string.btn_ok_space,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PublicUtil.callPhone(aty, Constants.SERVICE_PHONE);
                    }
                });
        dialog.setPositiveButton(R.string.btn_cancel_space, null);
        dialog.show();
    }

    public static CommonAlertDialog.Builder getExitDialog(final Activity aty) {
        CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(aty);
        dialog.setTitle(R.string.app_name);
        dialog.setMessage(R.string.exit_app_msg);
        dialog.setPositiveButton(R.string.btn_ok_space,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        aty.finish();
                        ActivityManager.getInstance().finishAllActivity();
                    }
                });
        dialog.setNegativeButton(R.string.btn_cancel_space, null);
        return dialog;
    }


    /**
     * 显示promrt对话框
     */
    public static void showInfoDialog(Context context, String content, String txtLeftBtn,
                                      String txtRightBtn,
                                      DialogInterface.OnClickListener listenerLeftBtn,
                                      DialogInterface.OnClickListener listenerRightBtn) {
        CommonAlertDialog.Builder dialog = new CommonAlertDialog.Builder(context);

        dialog.setMessageSize(14);
        dialog.setMessage(content);
        dialog.setPositiveButton(txtLeftBtn, listenerLeftBtn);
        dialog.setNegativeButton(txtRightBtn, listenerRightBtn);
        dialog.show();
    }
}
