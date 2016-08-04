package cn.com.bluemoon.delivery.utils;

import android.app.Activity;
import android.graphics.Bitmap;

import cn.com.bluemoon.lib.utils.LibDialogUtil;
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



}
