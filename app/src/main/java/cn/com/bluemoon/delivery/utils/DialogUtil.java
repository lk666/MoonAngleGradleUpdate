package cn.com.bluemoon.delivery.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.text.Html;

import org.apache.http.util.TextUtils;

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
			QRCodeDialog codeDialog = new QRCodeDialog(context);
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
		ImageDialog picDialog = new ImageDialog(context,cb);
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


	/***
	 * 获取一个dialog
	 * @param context context
	 * @return
	 */
	public static AlertDialog.Builder getDialog(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		return builder;
	}

	/***
	 * 获取一个耗时等待对话框
	 * @param context context
	 * @param message message
	 * @return
	 */
	public static ProgressDialog getWaitDialog(Context context, String message,int viewId) {
		ProgressDialog waitDialog = new ProgressDialog(context);
		if (!TextUtils.isEmpty(message)) {
			waitDialog.setMessage(message);
			if(viewId!=0) {
				waitDialog.setContentView(viewId);
			}
		}
		return waitDialog;
	}

	/***
	 * 获取一个信息对话框，注意需要自己手动调用show方法显示
	 * @param context
	 * @param message
	 * @param onClickListener
	 * @return
	 */
	public static AlertDialog.Builder getMessageDialog(Context context, String message, DialogInterface.OnClickListener onClickListener) {
		AlertDialog.Builder builder = getDialog(context);
		builder.setMessage(message);
		builder.setPositiveButton(context.getResources().getString(R.string.btn_ok_with_space), onClickListener);
		return builder;
	}
    
	public static AlertDialog.Builder getMessageDialog(Context context, String message) {
		return getMessageDialog(context, message, null);
	}

	public static AlertDialog.Builder getConfirmDialog(Context context, String message, DialogInterface.OnClickListener onClickListener) {
		AlertDialog.Builder builder = getDialog(context);
		builder.setMessage(Html.fromHtml(message));
		builder.setPositiveButton(context.getResources().getString(R.string.btn_ok_with_space), onClickListener);
		builder.setNegativeButton(context.getResources().getString(R.string.btn_cancel_with_space), null);
		return builder;
	}

	public static AlertDialog.Builder getConfirmDialog(Context context, String message, DialogInterface.OnClickListener onOkClickListener, DialogInterface.OnClickListener onCancelClickListener) {
		AlertDialog.Builder builder = getDialog(context);
		builder.setMessage(message);
		builder.setPositiveButton(context.getResources().getString(R.string.btn_ok_with_space), onOkClickListener);
		builder.setNegativeButton(context.getResources().getString(R.string.btn_cancel_with_space), onCancelClickListener);
		return builder;
	}

	public static AlertDialog.Builder getConfirmDialog(Context context,
													   String message,
													   String okString,
													   String cancelString,
													   DialogInterface.OnClickListener onOkClickListener,
													   DialogInterface.OnClickListener onCancelClickListener) {
		return getConfirmDialog(context, "", message, okString, cancelString, onOkClickListener, onCancelClickListener);
	}

	public static AlertDialog.Builder getConfirmDialog(Context context,
													   String title,
													   String message,
													   String okString,
													   String cancelString,
													   DialogInterface.OnClickListener onOkClickListener,
													   DialogInterface.OnClickListener onCancelClickListener) {
		AlertDialog.Builder builder = getDialog(context);
		if (!TextUtils.isEmpty(title)) {
			builder.setTitle(title);
		}
		builder.setMessage(message);
		builder.setPositiveButton(okString, onOkClickListener);
		builder.setNegativeButton(cancelString, onCancelClickListener);
		return builder;
	}

	public static AlertDialog.Builder getSelectDialog(Context context, String title, String[] arrays, DialogInterface.OnClickListener onClickListener) {
		AlertDialog.Builder builder = getDialog(context);
		builder.setItems(arrays, onClickListener);
		if (!TextUtils.isEmpty(title)) {
			builder.setTitle(title);
		}
		builder.setPositiveButton(context.getResources().getString(R.string.btn_cancel_with_space), null);
		return builder;
	}

	public static AlertDialog.Builder getSelectDialog(Context context, String[] arrays, DialogInterface.OnClickListener onClickListener) {
		return getSelectDialog(context, "", arrays, onClickListener);
	}

	public static AlertDialog.Builder getSingleChoiceDialog(Context context, String title, String[] arrays, int selectIndex, DialogInterface.OnClickListener onClickListener) {
		AlertDialog.Builder builder = getDialog(context);
		builder.setSingleChoiceItems(arrays, selectIndex, onClickListener);
		if (!TextUtils.isEmpty(title)) {
			builder.setTitle(title);
		}
		builder.setNegativeButton(context.getResources().getString(R.string.btn_cancel_with_space), null);
		return builder;
	}

	public static AlertDialog.Builder getSingleChoiceDialog(Context context, String[] arrays, int selectIndex, DialogInterface.OnClickListener onClickListener) {
		return getSingleChoiceDialog(context, "", arrays, selectIndex, onClickListener);
	}

}
