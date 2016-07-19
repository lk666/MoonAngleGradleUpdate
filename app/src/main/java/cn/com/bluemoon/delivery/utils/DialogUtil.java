package cn.com.bluemoon.delivery.utils;

import android.app.Activity;
import android.graphics.Bitmap;

import cn.com.bluemoon.delivery.R;
import cn.com.bluemoon.lib.callback.CodeDialogCallback;
import cn.com.bluemoon.lib.callback.ImageDialogCallback;
import cn.com.bluemoon.lib.qrcode.utils.BarcodeUtil;
import cn.com.bluemoon.lib.view.ImageDialog;
import cn.com.bluemoon.lib.view.QRCodeDialog;

public class DialogUtil {

	public static QRCodeDialog showCodeDialog(Activity context,String title,
			String codeUrl,String code,String str,String tips,CodeDialogCallback cb)
	{
			QRCodeDialog codeDialog = new QRCodeDialog();
		if(!codeDialog.isVisible())
		{
			codeDialog.setLoadString(context.getString(R.string.data_loading));
			if(title!=null) codeDialog.setTitle(title);
			if(codeUrl!=null)codeDialog.setCodeUrl(codeUrl);
			if(str!= null) codeDialog.setString(str);
			if(code!=null) codeDialog.setBitmap(BarcodeUtil.createQRCode(code));
			if(tips!=null) codeDialog.setContent(tips);
			if(cb!=null) codeDialog.setCallback(cb);
			codeDialog.setSavePath(PublicUtil.getPhotoPath());
			codeDialog.show(context.getFragmentManager(), "dialog");
		}
		return codeDialog;
	}

	public static QRCodeDialog showCodeDialog(Activity context,String title,
			String code,String str){
		return showCodeDialog(context, title, null, code, str, null, null);
	}

	public static ImageDialog showPictureDialog(Activity context,Bitmap bm,String imgUrl,ImageDialogCallback cb)
	{
		ImageDialog picDialog = new ImageDialog();
		picDialog.setCallback(cb);
		if (!picDialog.isVisible()) {
			picDialog.setLoadString(context.getString(R.string.data_loading));
			if (bm != null) {
				picDialog.setBitmap(bm);
			} else {
				if (imgUrl != null)
					picDialog.setCodeUrl(imgUrl);
			}
			picDialog.setSavePath(PublicUtil.getPhotoPath());
			picDialog.show(context.getFragmentManager(), "dialog");
		}
		return picDialog;
	}

	public static ImageDialog showPictureDialog(Activity context,String imgUrl){
		return showPictureDialog(context, null, imgUrl, null);
	}

	public static ImageDialog showPictureDialog(Activity context,Bitmap bit){
		return showPictureDialog(context, bit, null, null);
	}

}
