package cn.com.bluemoon.delivery.sz.util;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class PageJumps {

	public static void PageJumps(Context context, Class<?> descClass, Bundle bundle) {
		Class<?> mClass = context.getClass();
		if (mClass == descClass) {
			return;
		}
		try {
			Intent intent = new Intent();
			intent.setClass(context, descClass);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			if (bundle != null) {
				intent.putExtras(bundle);
			}
			((Activity) context).startActivity(intent);
//			((Activity) context).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);//从右到左�?��
	     	} catch (Exception e) {
	     		e.printStackTrace();
		}
	}
	public static void PageJumpsAlpha(Context context, Class<?> descClass, Bundle bundle) {
		Class<?> mClass = context.getClass();
		if (mClass == descClass) {
			return;
		}
		try {
			Intent intent = new Intent();
			intent.setClass(context, descClass);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			if (bundle != null) {
				intent.putExtras(bundle);
			}
			((Activity) context).startActivity(intent);
//			((Activity) context).overridePendingTransition(R.anim.alpha_finish_show, R.anim.alpha_finish_close);//从右到左�?��
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void PageJumps(Context context, Class<?> descClass, Bundle bundle,int requestCode) {
		Class<?> mClass = context.getClass();
		if (mClass == descClass) {
			return;
		}
		try {
			Intent intent = new Intent();
			intent.putExtra("requestCode", requestCode+"");
			intent.setClass(context, descClass);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			if (bundle != null) {
				intent.putExtras(bundle);
			}
			((Activity) context).startActivityForResult(intent, requestCode);
//			((Activity) context).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);//从右到左�?��
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void PageJumps_Down(Context context, Class<?> descClass, Bundle bundle,int requestCode) {
		Class<?> mClass = context.getClass();
		if (mClass == descClass) {
			return;
		}
		try {
			Intent intent = new Intent();
			intent.putExtra("requestCode", requestCode+"");
			intent.setClass(context, descClass);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			if (bundle != null) {
				intent.putExtras(bundle);
			}
			((Activity) context).startActivityForResult(intent, requestCode);
//			((Activity) context).overridePendingTransition(R.anim.popshow_anim, R.anim.pophidden_anim);//从上到下
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void finish(Context context) {
			((Activity) context).finish();
//			((Activity) context).overridePendingTransition(R.anim.finish_down1, R.anim.finish_down2);
	}
	public static void finishBase(Context context) {
		((Activity) context).finish();
//		((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}
	public static void finish(Context context, Intent intent,int resultCode) {
		if (00!=resultCode) {
			((Activity) context).setResult(resultCode, intent);
			((Activity) context).finish();
//			((Activity) context).overridePendingTransition(R.anim.finish_down1, R.anim.finish_down2);
		}
	}
	
	
	
}
