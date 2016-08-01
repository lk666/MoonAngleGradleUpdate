package cn.com.bluemoon.delivery.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;

import org.apache.commons.lang3.StringUtils;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.delivery.app.api.model.ResultBase;
import cn.com.bluemoon.delivery.module.account.LoginActivity;
import cn.com.bluemoon.lib.utils.LibDialogUtil;
import cn.com.bluemoon.lib.utils.LibViewUtil;
import cn.com.bluemoon.lib.view.CommonAlertDialog;
import cn.com.bluemoon.lib.view.ImageDialog;
import cn.com.bluemoon.lib.view.QRCodeDialog;

public class DialogUtil extends LibDialogUtil{



	public static QRCodeDialog showCodeDialog(Activity context,String title,
			String code,String str){
		return showCodeDialog(context, title, null, code, str, null, PublicUtil.getPhotoPath(),null);
	}

	public static ImageDialog showPictureDialog(Activity context,String imgUrl){
		return showPictureDialog(context, null, imgUrl, PublicUtil.getPhotoPath(), null);
	}

	public static ImageDialog showPictureDialog(Activity context,Bitmap bit){
		return showPictureDialog(context, bit, null, PublicUtil.getPhotoPath(),null);
	}

	/**
	 * 显示账户过期对话框
	 *
	 * @param context
	 */
	public static void showMessageTokenExpire(final Activity context) {
		new CommonAlertDialog.Builder(context)
				.setCancelable(false)
				.setMessage(context.getString(R.string.token_out))
				.setPositiveButton(R.string.btn_ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(context, LoginActivity.class);
								context.startActivity(intent);
								context.finish();
							}
						}).show();
	}

	public static void showErrorMsg(Activity context, ResultBase resultBase) {
		if (Constants.RESPONSE_RESULT_TOKEN_EXPIRED == resultBase
				.getResponseCode()) {
			showMessageTokenExpire(context);
		} else {
			String msg = Constants.ERROR_MAP.get(resultBase.getResponseCode());
			if (StringUtils.isEmpty(msg)) {
				LibViewUtil.toast(context, resultBase.getResponseMsg());
			} else {
				LibViewUtil.toast(context, msg);
			}
		}
	}



}
