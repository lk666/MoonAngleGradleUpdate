package cn.com.bluemoon.delivery.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.lib.utils.LibDialogUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.ImageDialog;
import cn.com.bluemoon.lib.view.QRCodeDialog;

public class DialogUtil extends LibDialogUtil {


    public static QRCodeDialog showCodeDialog(Activity context, String title,
                                              String code, String str) {
        return showCodeDialog(context, title, null, code, str, null, PublicUtil.getPhotoPath(), null);
    }

    public static ImageDialog showPictureDialog(Activity context, String imgUrl) {
        return showPictureDialog(context, null, imgUrl, PublicUtil.getPhotoPath(), null);
    }

    public static ImageDialog showPictureDialog(Activity context, Bitmap bit) {
        return showPictureDialog(context, bit, null, PublicUtil.getPhotoPath(), null);
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
